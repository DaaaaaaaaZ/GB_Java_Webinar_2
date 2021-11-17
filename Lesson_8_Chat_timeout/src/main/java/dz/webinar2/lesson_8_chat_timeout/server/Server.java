package dz.webinar2.lesson_8_chat_timeout.server;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import static dz.webinar2.lesson_8_chat_timeout.Commands.*;

public class Server {
    private static final int SERVER_PORT = 7777;

    private static final long AUTH_TIMEOUT = 120000; //Таймаут для аутентификации в миллисекундах

    private final CopyOnWriteArrayList<Entry> clientDataList = new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap <String, ClientUnit> clients =
            new ConcurrentHashMap<>(); //String - ник, ClientUnit - его модель
    private final ConcurrentHashMap <Socket, Long> waitingForAuth = new ConcurrentHashMap<>();
    private ServerSocket serverSocket;
    private final ConcurrentLinkedQueue <String> broadcastMsgQueue = new ConcurrentLinkedQueue<>();

    public Server () {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
        } catch (IOException e) {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            System.out.println("Сервер не запускается");
            System.exit(-1);
        }

        clientDataList.add (new Entry("Коля", "login0", "pass0"));
        clientDataList.add (new Entry("Боря", "login1", "pass1"));
        clientDataList.add (new Entry("Костя", "login2", "pass2"));
    }

    public static void main(String[] args) {
        new Server().startServer();
    }

    private void startServer () {
        Socket socket;
        startBroadcastSender ();

        try {
            while (true) {
                socket = serverSocket.accept();
                waitingForAuth.put (socket, System.currentTimeMillis());
                startSocketListener (socket);
            }
        } catch (IOException e) {
            System.out.println("Работа сервера нарушена");
            e.printStackTrace();
        }
    }

    private void startBroadcastSender() {
        Thread broadcastSenderThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    StringBuilder broadcastMsg = new StringBuilder();
                    Iterator it;
                    Socket s;

                    while (true) {
                        if (!broadcastMsgQueue.isEmpty()) {
                            broadcastMsg.append(broadcastMsgQueue.poll());
                            System.out.println(broadcastMsg.toString());
                            for (Map.Entry<String, ClientUnit> client : clients.entrySet()) {
                                try {
                                    client.getValue().getOut().writeUTF(broadcastMsg.toString());
                                    client.getValue().getOut().flush();
                                } catch (IOException e) {
                                    System.out.println("Ошибка при отправке broadcast-сообщения пользователю "
                                            + client.getKey());
                                }
                            }
                        }
                        broadcastMsg.setLength(0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("BroadcastSender остановлен");
                }
            }
        });
        broadcastSenderThread.setDaemon(true);
        broadcastSenderThread.start();
    }

    private void startSocketListener(Socket socket) {
        Thread socketListenerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (
                        DataInputStream in = new DataInputStream(socket.getInputStream());
                        DataOutputStream out = new DataOutputStream(socket.getOutputStream())
                    ) {
                    String inMsg = null;
                    String outMsg = null;
                    String wispNick = null;
                    String clientNick = null;
                    StringBuilder wispMsg = new StringBuilder();
                    String [] inAuthArray = null;
                    String [] inMsgArray = null;
                    boolean isAuthOk = false;
                    String ipAdress = socket.getInetAddress().toString();
                    int port = socket.getPort();
                    long startTimeForAuth = System.currentTimeMillis();

                    while (!isAuthOk) {//Цикл аутентификации
                        try {
                            out.writeUTF("Для аутентификации введите \n" + CMD_AUTH + " login password");
                            out.flush();
                            try {
                                while (in.available() == 0) {
                                    if (System.currentTimeMillis() - startTimeForAuth > AUTH_TIMEOUT) {
                                        out.writeUTF(CMD_AUTH_TIMEOUT);
                                        out.flush();
                                        throw new SocketException("");
                                    }
                                    Thread.sleep(50);
                                }
                            } catch (InterruptedException e) {
                                System.out.println(ipAdress + ": " + port + " не успел пройти аутентификацию");
                                throw new SocketException("");
                            }
                            inMsg = in.readUTF();
                            inAuthArray = inMsg.split(" ");
                            if (inAuthArray.length == 1) {
                                out.writeUTF("Для аутентификации введите \n" + CMD_AUTH + " login password");
                                continue;
                            } else {
                                if (CMD_AUTH.equals(inAuthArray[0])) {
                                    try {
                                        for (Entry clientData : clientDataList) {
                                            if (clientData.getLogin().equals(inAuthArray[1]) &&
                                                    clientData.getPass().equals(inAuthArray[2])) {
                                                if (clients.containsKey(clientData.getNick())) {
                                                    out.writeUTF("Пользователь " + clientData.getNick() +
                                                            " уже в чате");
                                                    break;
                                                }
                                                clientNick = clientData.getNick();
                                                clients.put(clientNick, new ClientUnit(
                                                        socket,
                                                        in,
                                                        out,
                                                        clientNick
                                                ));
                                                isAuthOk = true;
                                                broadcastMsgQueue.add(clientNick + " зашёл в чат");
                                                break;
                                            }
                                        }

                                        if (!isAuthOk) {
                                            out.writeUTF("Для аутентификации введите \n" + CMD_AUTH + " login password");
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        out.writeUTF("Для аутентификации введите \n" + CMD_AUTH + " login password");
                                    }

                                }
                            }
                        } catch (EOFException e) {
                            continue;
                        } catch (UTFDataFormatException e) {
                            out.writeUTF("Ошибка при аутентификации, \nпопробуйте еще раз");
                        }
                    } //Конец цикла аутентификации

                    while (true) {//Рабочий цикл для чтения и отправки сообщений
                        try {
                            inMsg = in.readUTF();
                        } catch (EOFException e) {
                            continue;
                        }
                        if (inMsg.isEmpty() || inMsg.isBlank()) {
                            continue;
                        }

                        inMsgArray = inMsg.split(" ");
                        if (CMD_WISPER.equals (inMsgArray [0])) {
                            try {
                                for (Map.Entry<String, ClientUnit> client : clients.entrySet()) {
                                    if (client.getKey().equals(inMsgArray [1])) {
                                        if (inMsgArray.length > 2) {
                                            wispMsg.append(clientNick + ": ");
                                            for (int i = 2; i < inMsgArray.length; i++) {
                                                wispMsg.append (inMsgArray [i]);
                                                if (i != inMsgArray.length - 1) {
                                                    wispMsg.append (" ");
                                                }
                                            }
                                            client.getValue().getOut().writeUTF(wispMsg.toString());
                                            client.getValue().getOut().flush();
                                            wispMsg.setLength(0);
                                        }
                                    }
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                continue;
                            }
                        } if (CMD_TEST.equals (inMsgArray [0])) {
                            out.writeUTF(CMD_OK);
                        } else {
                            broadcastMsgQueue.add (clientNick + ": " + inMsg);
                        }

                    }//Конец цикла входящий сообщений

                } catch (SocketException e) {
                    if (socket != null) {
                        System.out.println("Сокет " + socket.getInetAddress() + ":" + socket.getPort() + " закрыт");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        socketListenerThread.setDaemon(true);

        socketListenerThread.start();
    }

    private class Entry {
        private final String login;
        private final String pass;
        private final String nick;

        public Entry(String nick, String login, String pass) {
            this.login = login;
            this.pass = pass;
            this.nick = nick;

        }

        public String getLogin() {
            return login;
        }

        public String getPass() {
            return pass;
        }

        public String getNick() {
            return nick;
        }
    }
}