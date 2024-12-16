package chapter05;

import java.io.*;

public class MissionB {

    public static void main(String[] args) throws IOException, InterruptedException {
        PipedOutputStream output = new PipedOutputStream();
        PipedInputStream input = new PipedInputStream(output);

        Writer writer = new Writer(output);
        Reader reader = new Reader(input);

        Thread[] threads = new Thread[]{writer, reader};

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static class Writer extends Thread {

        private static final String MESSAGE = "Rubber duck";

        private final OutputStream output;

        public Writer(OutputStream output) {
            super("Writer");
            this.output = output;
        }

        @Override
        public void run() {
            String threadName = getName();

            try {
                System.out.printf("%s: Sending rubber duck...%n", threadName);
                output.write(MESSAGE.getBytes());
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class Reader extends Thread {

        private static final int BUFFER_SIZE = 1024;
        private static final int EOF = -1;

        private final InputStream input;

        public Reader(InputStream conn) {
            super("Reader");
            this.input = conn;
        }

        @Override
        public void run() {
            try {
                int bytesRead;
                String threadName = getName();
                byte[] buffer = new byte[BUFFER_SIZE];

                System.out.printf("%s: Reading...%n", threadName);

                while ((bytesRead = input.read(buffer)) != EOF) {
                    String message = new String(buffer, 0, bytesRead);
                    System.out.printf("%s: Received: %s%n", threadName, message);
                }

                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
