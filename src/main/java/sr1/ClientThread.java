package sr1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * La classe ClientThread gère les différents socket nécessaires à l'acceptation et au fonctionnement des connexions
 * avec les clients ftp
 *
 * @author pochet
 * @author michot
 */
public class ClientThread extends Thread {

    private final Socket socket;
    private Socket dataConnection;

    private ServerSocket dataSocket;

    private final PrintWriter printWriter;
    private PrintWriter dataWriter;

    private final BufferedReader bufferedReader;

    TransferType transferMode = TransferType.ASCII;

    private final FTPCommands ftpCommands;

    ClientThread(Socket socket, String currentDirectory) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.printWriter.println("220 (vsFTPd 3.0.3)");
        this.ftpCommands = new FTPCommands(this, currentDirectory);
    }

    @Override
    public void run() {
        while (true) {
            try {
                String command = bufferedReader.readLine();
                if (command !=null) {
                    System.out.println("------------------------------------------------");
                    System.out.println("Command from " + socket.getRemoteSocketAddress() + " : " + command);
                    ftpCommands.executeCommand(command);
                }
            } catch (IOException | SocketException | DataConnectionException | DirectoryException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * createServerSocket créé un tunnel de données du serveur ftp
     * @return le port du tunnel créé
     * @throws SocketException exception
     */
    protected int createServerSocket() throws SocketException {
        int port = -1;
        try {
            dataSocket = new ServerSocket(0);
            port =  dataSocket.getLocalPort();
        } catch (IOException e) {
            throw new SocketException("Impossible de creer la socket : " + e.getMessage());
        }
        return port;
    }

    /**
     * openDataConnectionPassive ouvre une connexion passive pour le transfert de données
     * @throws DataConnectionException exception
     */
    protected void openDataConnectionPassive() throws DataConnectionException {

        try {
            dataConnection = dataSocket.accept();
            dataWriter = new PrintWriter(dataConnection.getOutputStream(), true);
            BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));
        } catch (IOException e) {
            throw new DataConnectionException("Impossible de creer la socket en mode passif : " + e.getMessage());
        }

    }

    /**
     * openDataConnectionActive ouvre une connexion active pour le transfert de données
     * @param addr addresse ip
     * @param port port
     * @throws DataConnectionException exception
     */
    protected void openDataConnectionActive(String addr, int port) throws DataConnectionException {
        try {
            sendMsgToClient("200 PORT command succssful");
            dataConnection = new Socket(addr, port);
            dataWriter = new PrintWriter(dataConnection.getOutputStream(), true);
            BufferedReader dataReader = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));
        } catch (IOException e){
            throw new DataConnectionException("Impossible de creer la socket en mode actif : " + e.getMessage());
        }
    }

    /**
     * closeDataConnection ferme la connexion de transfert de données
     * @throws DataConnectionException exception
     */
    protected void closeDataConnection() throws DataConnectionException {
        try {
            dataWriter.close();
            dataConnection.close();
            if (dataSocket != null) {
                dataSocket.close();
            }

        } catch (IOException e) {
            throw new DataConnectionException("Impossible de fermer la socket : " + e.getMessage());
        }
        dataWriter = null;
        dataConnection = null;
        dataSocket = null;
    }

    /**
     * getDataConnection getter de dataConnection
     * @return dataConnection
     */
    protected Socket getDataConnection() {
        return dataConnection;
    }

    /**
     * sendMsgToClient envooie un message passé en paramètre au client ftp
     * @param msg message à envoyer
     */
    protected void sendMsgToClient(String msg) {
        printWriter.println(msg);
    }

    /**
     * sendDataMsgToClient envoie un message de donnée passé en paramètre au client ftp
     * @param msg message à envoyer
     */
    protected void sendDataMsgToClient(String msg) {
        dataWriter.println(msg);
    }

}
