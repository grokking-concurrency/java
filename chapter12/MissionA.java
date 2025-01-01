package chapter12;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MissionA {

    public static void main(String[] args) {
        try {
            AsyncPizzaServer server = new AsyncPizzaServer();
            Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
            server.start();

            // Keep the main thread alive
            Thread.currentThread().join();
        } catch (IOException | InterruptedException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static class AsyncPizzaServer {

        private static final int BUFFER_SIZE = 1024;
        private static final String HOST = "127.0.0.1";
        private static final int PORT = 12345;

        private final AsynchronousServerSocketChannel serverChannel;
        private final ExecutorService executor;

        static class Kitchen {
            static void cookPizza(int n) {
                System.out.printf("Started cooking %d pizzas%n", n);
                try {
                    TimeUnit.SECONDS.sleep(n);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.printf("Fresh %d pizzas are ready!%n", n);
            }
        }

        public AsyncPizzaServer() throws IOException {
            System.out.printf("Starting up at: %s:%d%n", HOST, PORT);
            this.serverChannel = AsynchronousServerSocketChannel.open();
            this.serverChannel.bind(new InetSocketAddress(HOST, PORT));
            this.executor = Executors.newCachedThreadPool();
        }

        public void start() {
            System.out.println("Server listening for incoming connections");
            acceptConnections();
        }

        private void acceptConnections() {
            serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
                    // Accept next connection
                    serverChannel.accept(null, this);

                    // Handle current connection
                    handleClient(clientChannel);
                }

                @Override
                public void failed(Throwable exc, Void attachment) {
                    System.err.println("Failed to accept connection: " + exc.getMessage());
                }
            });
        }

        private void handleClient(AsynchronousSocketChannel clientChannel) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            readFromClient(clientChannel, buffer);
        }

        private void readFromClient(AsynchronousSocketChannel clientChannel, ByteBuffer buffer) {
            clientChannel.read(buffer, buffer, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    if (result == -1) {
                        closeConnection(clientChannel);
                        return;
                    }

                    buffer.flip();
                    byte[] data = new byte[buffer.remaining()];
                    buffer.get(data);
                    String message = new String(data).trim();

                    processOrder(clientChannel, message);

                    // Prepare for next read
                    buffer.clear();
                    readFromClient(clientChannel, buffer);
                }

                @Override
                public void failed(Throwable exc, ByteBuffer buffer) {
                    closeConnection(clientChannel);
                }
            });
        }

        private void processOrder(AsynchronousSocketChannel clientChannel, String message) {
            try {
                int order = Integer.parseInt(message);
                String response = String.format("Thank you for ordering %d pizzas!%n", order);
                sendResponse(clientChannel, response);

                // Cook pizzas in background
                executor.execute(() -> {
                    Kitchen.cookPizza(order);
                    String completionResponse = String.format("Your order of %d pizzas is ready!%n", order);
                    sendResponse(clientChannel, completionResponse);
                });
            } catch (NumberFormatException e) {
                sendResponse(clientChannel, "Wrong number of pizzas, please try again\n");
            }
        }

        private void sendResponse(AsynchronousSocketChannel clientChannel, String response) {
            try {
                System.out.printf("Sending message to %s%n", clientChannel.getRemoteAddress());
            } catch (IOException e) {
                System.err.println("Error getting remote address: " + e.getMessage());
            }

            ByteBuffer buffer = ByteBuffer.wrap(response.getBytes());
            clientChannel.write(buffer, buffer, new CompletionHandler<>() {
                @Override
                public void completed(Integer result, ByteBuffer buffer) {
                    if (buffer.hasRemaining()) {
                        clientChannel.write(buffer, buffer, this);
                    }
                }

                @Override
                public void failed(Throwable exc, ByteBuffer buffer) {
                    closeConnection(clientChannel);
                }
            });
        }

        private void closeConnection(AsynchronousSocketChannel clientChannel) {
            try {
                System.out.printf("Connection with %s has been closed%n", clientChannel.getRemoteAddress());
                clientChannel.close();
            } catch (IOException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }

        public void stop() {
            try {
                executor.shutdown();
                serverChannel.close();
                System.out.println("\nServer stopped.");
            } catch (IOException e) {
                System.err.println("Error stopping server: " + e.getMessage());
            }
        }
    }
}
