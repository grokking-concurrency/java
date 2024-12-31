package chapter10.missionB;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MissionB {

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    private static class Server {

        private static final int BUFFER_SIZE = 1024;
        private static final String HOST = "127.0.0.1";
        private static final int PORT_NUM = 12345;

        private ServerSocket serverSocket;
        private final Set<SocketHandler> clients = new HashSet<>();

        public void start() {
            try {
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(HOST, PORT_NUM));
                serverSocket.setSoTimeout(200);
                System.out.println("Server listening for incoming connections");

                while (true) {
                    accept();
                    serveAllClients();
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            } finally {
                closeServer();
            }
        }

        private void closeServer() {
            clients.forEach(SocketHandler::close);
            clients.clear();

            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.err.println("Server error with closing: " + e.getMessage());
            }
            System.out.println("Server stopped");
        }

        private void serveAllClients() {
            Iterator<SocketHandler> iterator = clients.iterator();

            while (iterator.hasNext()) {
                SocketHandler client = iterator.next();
                if (!client.handle()) {
                    iterator.remove();
                    client.close();
                }
            }
        }

        private void accept() {
            try {
                Socket client = serverSocket.accept();
                client.setSoTimeout(200);
                clients.add(new SocketHandler(client));
                System.out.println("Connected to " + client.getRemoteSocketAddress());
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                System.err.println("accept: " + e.getMessage());
            }
        }

        private static class SocketHandler {

            private final Socket socket;
            private final byte[] buffer;

            public SocketHandler(Socket socket) {
                this.socket = socket;
                buffer = new byte[BUFFER_SIZE];
            }

            public boolean handle() {
                try {
                    InputStream in = socket.getInputStream();

                    if (in.available() > 0) {
                        int bytesRead = in.read(buffer);

                        if (bytesRead == -1) {
                            return false;
                        }

                        String message = new String(buffer, 0, bytesRead).trim();
                        String response;
                        try {
                            int order = Integer.parseInt(message);
                            response = "Thank you for ordering " + order + " pizzas!\n";
                        } catch (NumberFormatException e) {
                            response = "Wrong number of pizzas, please try again\n";
                        }

                        System.out.println("Sending message to " + socket.getRemoteSocketAddress());
                        socket.getOutputStream().write(response.getBytes());
                    }
                    return true;
                } catch (IOException e) {
                    System.err.println("Socket error: " + e.getMessage());
                    return false;
                }
            }

            public void close() {
                try {
                    if (socket != null && !socket.isClosed()) {
                        System.out.println("Closing " + socket.getRemoteSocketAddress());
                        socket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Error with closing client connection: " + e.getMessage());
                }
            }
        }
    }
}
