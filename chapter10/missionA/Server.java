package chapter10.missionA;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final int PORT = 12345;

    private final ServerSocket serverSocket;

    public Server() {
        System.out.println("Starting up at: " + PORT);

        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            System.err.println("Server stopped.");
            throw new RuntimeException(e);
        }
    }

    public void start() {
        System.out.println("Server listening for incoming connections");

        while (true) {
            try {
                Socket conn = serverSocket.accept();
                System.out.println("Client connection request from " + conn.getRemoteSocketAddress());
                Thread thread = new Handler(conn);
                thread.start();
            } catch (IOException e) {
                System.err.println("Server: " + e.getMessage());
            }
        }
    }
}
