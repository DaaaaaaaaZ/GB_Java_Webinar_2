package dz.webinar2.lesson_8_chat_timeout.client;

import dz.webinar2.lesson_8_chat_timeout.Commands;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Client extends Application {

    private final String SERVER_ADDR = "localhost";

    private final int SERVER_PORT = 7777;
    private Thread inMsgListener;
    private Thread outMsgSender;
    private Thread socketHandler;
    private Boolean isConnectionOn = false;
    private boolean isConnecting = true;

    private Controller controller;

    private long lastTimeMessage;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    private final ConcurrentLinkedQueue <String> queueToSend = new ConcurrentLinkedQueue<>();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("lesson_8_chat.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Chat");
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        controller = fxmlLoader.getController();
        controller.setClient(this);

        startChat();
        stage.show();
    }

    private void setConnectionOn (boolean connectionOn) {
        synchronized (isConnectionOn) {
            isConnectionOn = connectionOn;
        }
    }

    private boolean getConnectionOn () {
        synchronized (isConnectionOn) {
            return isConnectionOn;
        }
    }

    private void startChat() {
        socketHandler = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        if (!getConnectionOn()) {
                            if (!isServerLive ()) {
                                closeSocketIfOpened();
                                socket = new Socket(SERVER_ADDR, SERVER_PORT);
                                in = new DataInputStream(socket.getInputStream());
                                out = new DataOutputStream(socket.getOutputStream());
                                setConnectionOn(true);
                                isConnecting = true;
                                lastTimeMessage = System.currentTimeMillis();
                            }
                        }
                    } catch (ConnectException e) {
                        if (isConnecting) {
                            controller.showMessage("Подключаемся к серверу ...");
                            isConnecting = false;
                        }
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        System.out.println("Поток подключения к серверу остановлен");
                        break;
                    }
                }
            }

            private boolean isServerLive() {
                if (socket == null || in == null || out == null) {
                    return false;
                }

                try {
                    out.writeUTF(Commands.CMD_TEST);
                } catch (Exception e) {
                    return false;
                }

                try {
                    for (int i = 0; i < 5; i++) {
                        if (in.available() > 0) {
                            return true;
                        }
                        out.writeUTF(Commands.CMD_TEST);
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    return false;
                }

                return false;
            }

            private void closeSocketIfOpened() {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        System.out.println("Не удалось закрыть InputStream у сокета");
                    } finally {
                        in = null;
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        System.out.println("Не удалось закрыть OutputStream у сокета");
                    }
                    finally {
                        out = null;
                    }
                }
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.out.println("Не удалось закрыть сокет");
                    } finally {
                        socket = null;
                    }
                }
            }
        });
        socketHandler.setDaemon(true);
        socketHandler.start ();

        outMsgSender = new Thread(new Runnable() {
            @Override
            public void run() {
                String messageOut;
                while (true) {
                    try {
                        if (getConnectionOn()) {
                            if (!queueToSend.isEmpty()) {
                                messageOut = queueToSend.poll();
                                out.writeUTF(messageOut);
                                out.flush();
                            }
                        }
                        Thread.sleep(50);
                    } catch (UTFDataFormatException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println("Поток, отправляющий сообщения, остановлен");
                    } catch (IOException e) {
                        setConnectionOn(false);
                    } catch (Exception e) {
                        setConnectionOn(false);
                    }
                }
            }
        });
        outMsgSender.setDaemon(true);
        outMsgSender.start ();

        inMsgListener = new Thread(new Runnable() {
            @Override
            public void run() {
                String messageIn;
                while (true) {
                    try {
                        if (getConnectionOn()) {
                            if (in.available() > 0) {
                                messageIn = in.readUTF();
                                lastTimeMessage = System.currentTimeMillis();
                                if (messageIn != null) {
                                    if (Commands.CMD_AUTH_TIMEOUT.equals(messageIn)) {
                                        controller.showMessage("Время на аутентификацию прошло.");
                                        throw new Exception("auth timeout");
                                    } else {
                                        controller.showMessage(messageIn);
                                    }
                                }
                            } else if (System.currentTimeMillis() - lastTimeMessage > 10000) {
                                setConnectionOn(false);
                            }
                        }
                        Thread.sleep(50);
                    } catch (UTFDataFormatException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        System.out.println("Поток, ожидающий входящие сообщения, остановлен");
                    } catch (IOException e) {
                        setConnectionOn(false);
                    } catch (Exception e) {
                        setConnectionOn(false);
                    }
                }
            }
        });
        inMsgListener.setDaemon(true);
        inMsgListener.start();
    }

    public static void main(String[] args) {
        launch();
    }

    public void sendMessage(String message) {
        queueToSend.add (message);
    }
}