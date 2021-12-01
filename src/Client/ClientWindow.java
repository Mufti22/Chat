//
// Source code recreated from a .class file by Eclipse
// (powered by Malik Fomin)
//
package Client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientWindow extends JFrame {
    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 3443;
    private Socket clientSocket;
    private Scanner inMessage;
    private PrintWriter outMessage;
    private String clientName = "";

    private JTextField jtfMessage;
    private JTextField jtfName;
    private JTextArea jtaTextAreaMessage;

    public String getClientName() {
        return this.clientName;
    }

    public ClientWindow() {
        try {
            clientSocket = new Socket(SERVER_HOST, SERVER_PORT);
            inMessage = new Scanner(clientSocket.getInputStream());
            outMessage = new PrintWriter(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        setBounds(700, 400, 700, 600);
        setTitle("Окно чата");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jtaTextAreaMessage = new JTextArea();
        jtaTextAreaMessage.setEditable(false);
        jtaTextAreaMessage.setLineWrap(true);
        JScrollPane jsCrollPane = new JScrollPane(jtaTextAreaMessage);
        add(jsCrollPane, BorderLayout.CENTER);
        JLabel jlNumberOfClients = new JLabel("Онлайн: ");
        add(jlNumberOfClients, BorderLayout.NORTH);
        JPanel bottomPanel = new JPanel(new BorderLayout());
        add(bottomPanel, BorderLayout.SOUTH);
        JButton jbSendMessage = new JButton("Отправить");
        bottomPanel.add(jbSendMessage, BorderLayout.EAST);
        jtfMessage = new JTextField("Введите сообщение: ");
        bottomPanel.add(jtfMessage, BorderLayout.CENTER);
        jtfName = new JTextField("Ваше имя: ");
        bottomPanel.add(jtfName, BorderLayout.WEST);

        // кнопка
        jbSendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!jtfMessage.getText().trim().isEmpty() && !jtfName.getText().trim().isEmpty()) {
                    clientName = jtfName.getText();
                    sendMsg();
                    jtfMessage.grabFocus();
                }
            }
        });

        // очистка сообщения
        jtfMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfMessage.setText("");
            }
        });
        // очистка имени
        jtfName.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                jtfName.setText("");
            }
        });

        // работа
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (inMessage.hasNext()) {
                            String inMes = inMessage.nextLine();
                            String clientsInChat = "Онлайн = ";
                            if (inMes.indexOf(clientsInChat) == 0) {
                                jlNumberOfClients.setText(inMes);
                            } else {
                                jtaTextAreaMessage.append(inMes);
                                jtaTextAreaMessage.append("\n");
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }).start();

        // закрыть окно
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                super.windowClosing(event);
                try {
                    if (!clientName.isEmpty() && clientName != "Ваше имя: ") {
                        outMessage.println(clientName + " покинул чат");
                    } else {
                        outMessage.println("Пользователь покинул чат");
                    }
                    outMessage.println("/выйти");
                    outMessage.flush();
                    outMessage.close();
                    inMessage.close();
                    clientSocket.close();
                } catch (IOException e) {

                }
            }
        });

        setVisible(true);
    }

    public void sendMsg() {
        String messageStr = jtfName.getText() + ": " + jtfMessage.getText();
        outMessage.println(messageStr);
        outMessage.flush();
        jtfMessage.setText("");
    }
}

