package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    private static final String HOST = "localhost";
    private static final int PORT = 3443;
    private Socket clientSocket = null;
    private static int clients_count = 0;

    public ClientHandler(Socket socket, Server server) {
        try {
            clients_count++;
            this.server = server;
            this.clientSocket = socket;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        try {
            while (true) {
                server.sendMessageToAllClients("Пользователь зашел в чат.");
                server.sendMessageToAllClients("Онлайн = " + clients_count);
                break;
            }
            while (true) {
                // от клиента
                if (inMessage.hasNext()) {
                    String clientMessage = inMessage.nextLine();
                    if (clientMessage.equalsIgnoreCase("/выйти")) {
                        break;
                    }

                    System.out.println(clientMessage); // на сервер в консоль
                    server.sendMessageToAllClients(clientMessage); // клиентам
                }
                Thread.sleep(100);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            this.close();
        }
    }

    public void sendMsg(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        server.removeClient(this);
        clients_count--;
        server.sendMessageToAllClients("Онлайн = " + clients_count);
    }
}
