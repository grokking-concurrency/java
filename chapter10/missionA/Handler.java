package chapter10.missionA;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Handler extends Thread {

    private final Socket conn;

    public Handler(Socket conn) {
        this.conn = conn;
    }

    @Override
    public void run() {
        System.out.println("Connected to " + conn.getRemoteSocketAddress());

        try (
            Socket socket = conn;
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            String data;

            while ((data = in.readLine()) != null) {
                String responseMessage;

                try {
                    int order = Integer.parseInt(data);
                    responseMessage = "Thank you for ordering " + order + " pizzas!\n";
                } catch (NumberFormatException e) {
                    responseMessage = "Wrong number of pizzas, please try again\n";
                }

                System.out.println("Sending message to " + conn.getRemoteSocketAddress());
                conn.getOutputStream().write(responseMessage.getBytes());
            }
        } catch (IOException e) {
            System.err.println("Handler: " + e.getMessage());
        }

        System.out.println("Connection with " + conn.getRemoteSocketAddress() + " has been closed");
    }
}
