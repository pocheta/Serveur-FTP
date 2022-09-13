package sr1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class ServerFTP extends Thread {
    private ServerSocket serverSocket;
    private String currentDirectory;
    private int port;


    ServerFTP(String currentDirectory, int port) {
        this.serverSocket = null;
        this.currentDirectory = currentDirectory;
        this.port = port;
    }

    void connection() throws IOException {

        serverSocket = new ServerSocket(port);
        System.out.println("Server Started on port " + port + " ...");

        while (true) {
            System.out.println("Waiting for the request...");
            Socket client = serverSocket.accept();
            System.out.println("Got a client");
            System.out.println("Client Address " + client.getInetAddress().toString());

            ClientThread ct = new ClientThread(client, currentDirectory);
            ct.start();
        }

    }

}
