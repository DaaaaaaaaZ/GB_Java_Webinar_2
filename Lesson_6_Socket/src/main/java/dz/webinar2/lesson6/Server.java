package dz.webinar2.lesson6;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        Socket socket;

        try (ServerSocket serverSocket = new ServerSocket(7777)) {
            System.out.println("Ожидаем подключение ...");
            socket = serverSocket.accept();
            System.out.println("Клиент подключен");
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            final DataOutputStream copyOut = out;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                        String messageOut;
                        while (true) {
                            messageOut = br.readLine();
                            if (!messageOut.isEmpty() && !messageOut.isBlank()) {
                                System.out.println("Сервер: " + messageOut);
                                out.writeUTF(messageOut);
                                out.flush();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            String messageIn;

            while (true) {
                messageIn = in.readUTF();
                System.out.println("Клиент: " + messageIn);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
