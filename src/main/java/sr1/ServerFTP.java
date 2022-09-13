package sr1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * La classe ServerFTP gère la connexion entre les clients ftp et ce serveur ftp
 *
 * @author pochet
 * @author michot
 */
public class ServerFTP extends Thread {

    private ServerSocket serverSocket;
    private final String currentDirectory;
    private final int port;


    ServerFTP(String currentDirectory, int port) {
        this.serverSocket = null;
        this.currentDirectory = currentDirectory;
        this.port = port;
    }

    /**
     * connection permet la connexion au serveur FTP tout au long du temps d'exécution
     * @throws IOException exception
     */
    protected void connection() throws IOException {

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
