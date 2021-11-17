package dz.webinar2.lesson_7_chat.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Application {
    private final String SERVER_ADDR = "localhost";
    private final int SERVER_PORT = 7777;

    private Controller controller;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private final ConcurrentLinkedQueue <String> queueToSend = new ConcurrentLinkedQueue<>();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("lesson_7_chat.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Chat");
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        controller = ((Controller) fxmlLoader.getController());
        controller.setClient(this);

        try {
            startChat();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.show();
    }

    private void startChat() throws IOException {
        socket = new Socket(SERVER_ADDR, SERVER_PORT);
        final DataInputStream copyIn = in = new DataInputStream(socket.getInputStream());
        final DataOutputStream copyOut = out = new DataOutputStream(socket.getOutputStream());

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String messageOut;
                    while (true) {
                        if (!queueToSend.isEmpty()) {
                            messageOut = queueToSend.poll();
                            //controller.moveMessage(messageOut, MessageType.CLIENT);
                            copyOut.writeUTF(messageOut);
                            copyOut.flush();
                        }
                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String messageIn;
                    while (true) {
                        messageIn = copyIn.readUTF();
                        if (messageIn != null) {
                            controller.showMessage(messageIn);
                        }
                        Thread.sleep(50);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }

    public void sendMessage(String message) {
        queueToSend.add (message);
    }
}