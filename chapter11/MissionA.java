package chapter11;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class MissionA {

    public static void main(String[] args) {
        ReactorServer server = new ReactorServer();
        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        server.start();
    }

    private static class ReactorServer {

        private static final String ADDRESS = "127.0.0.1";
        private static final int PORT = 12345;
        private static final int BUFFER_SIZE = 1024;

        private final Selector selector;
        private final ServerSocketChannel serverChannel;

        public ReactorServer() {
            try {
                selector = Selector.open();

                serverChannel = ServerSocketChannel.open();
                serverChannel.bind(new InetSocketAddress(ADDRESS, PORT));
                serverChannel.configureBlocking(false);
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);

                System.out.println("Starting up at: " + ADDRESS + ":" + PORT);
            } catch (IOException e) {
                System.err.println("Server Stopped: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }

        public void start() {
            System.out.println("Server listening for incoming connections");

            try {
                while (true) {
                    selector.select();

                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        try {
                            if (key.isAcceptable()) {
                                handleAccept(key);
                            }
                            if (key.isWritable()) {
                                handleWrite(key);
                            }
                            if (key.isReadable()) {
                                handleRead(key);
                            }
                        } catch (IOException e) {
                            key.cancel();
                            try {
                                key.channel().close();
                            } catch (IOException ex) {
                                System.err.println("Error closing channel: " + ex.getMessage());
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Server error: " + e.getMessage());
            }
        }

        private void handleAccept(SelectionKey key) throws IOException {
            ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
            SocketChannel clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);

            System.out.println("Connected to " + clientChannel.getRemoteAddress());

            clientChannel.register(selector, SelectionKey.OP_READ);
        }

        private void handleRead(SelectionKey key) throws IOException {
            SocketChannel client = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);

            int bytesRead = client.read(buffer);
            if (bytesRead == -1) {
                System.out.println("Connection closed by " + client.getRemoteAddress());
                key.cancel();
                client.close();
                return;
            }

            String message = new String(buffer.array(), 0, bytesRead).trim();

            String response;
            try {
                int order = Integer.parseInt(message);
                response = "Thank you for ordering " + order + " pizzas!\n";
            } catch (NumberFormatException e) {
                response = "Wrong number of pizzas, please try again!\n";
            }

            key.attach(response);
            key.interestOps(SelectionKey.OP_WRITE);
        }

        private void handleWrite(SelectionKey key) throws IOException {
            SocketChannel client = (SocketChannel) key.channel();
            String response = (String) key.attachment();

            System.out.println("Sending message to " + client.getRemoteAddress());

            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
            client.write(buffer);

            key.interestOps(SelectionKey.OP_READ);
        }

        public void stop() {
            try {
                selector.close();
                serverChannel.close();
                System.out.println("\nServer stopped.");
            } catch (IOException e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }
    }
}
