package chapter05;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MissionC {

    private static final int PORT = 8080;
    private static final long BOOT_TIME = 1000L;

    public static void main(String[] args) throws InterruptedException {
        Receiver receiver = new Receiver();
        receiver.start();

        Thread.sleep(BOOT_TIME);

        Sender sender = new Sender();
        sender.start();

        receiver.join();
        sender.join();
    }

    private static class Sender extends Thread {

        private static final String LOOP_BACK_ADDRESS = "127.0.0.1";

        public Sender() {
            super("Sender");
        }

        @Override
        public void run() {
            String threadName = getName();
            String[] messages = new String[]{"Hello", " ", "world!"};

            try (
                Socket connection = new Socket(LOOP_BACK_ADDRESS, PORT);
                OutputStream out = connection.getOutputStream()
            ) {
                for (String message : messages) {
                    System.out.printf("%s: Send: '%s'%n", threadName, message);
                    out.write(message.getBytes());
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static class Receiver extends Thread {

        private static final int EOF = -1;
        private static final int BUFFER_SIZE = 1024;

        public Receiver() {
            super("Receiver");
        }

        @Override
        public void run() {
            String name = getName();
            Socket connection;

            try (ServerSocket socket = new ServerSocket(PORT)) {
                connection = socket.accept();
                System.out.printf("%s: Listening for incoming messages...%n", name);

                InputStream in = connection.getInputStream();
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != EOF) {
                    String message = new String(buffer, 0, bytesRead);
                    System.out.printf("%s: Received: '%s'%n", name, message);
                }

                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
