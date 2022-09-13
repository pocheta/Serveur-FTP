package sr1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class ClientThread extends Thread {

    private final Socket socket;
    private Socket dataConnection;

    private ServerSocket dataSocket;

    private final PrintWriter printWriter;
    private PrintWriter dataWriter;

    private final BufferedReader bufferedReader;
    private BufferedReader dataReader;

    TransferType transferMode = TransferType.ASCII;

    private final FTPCommands ftpCommands;

    ClientThread(Socket socket, String currentDirectory) throws IOException {
        this.socket = socket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.printWriter = new PrintWriter(socket.getOutputStream(), true);
        this.printWriter.println("220 (vsFTPd 3.0.3)");
        this.ftpCommands = new FTPCommands(this, printWriter, currentDirectory);

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

            } catch (Exception e) {
                // ERREUR
            }
        }

    }

    int createServerSocket() {
        int port = -1;
        try {
            dataSocket = new ServerSocket(0);
            port =  dataSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return port;
    }

    void openDataConnectionPassive() {

        try {
            dataConnection = dataSocket.accept();
            dataWriter = new PrintWriter(dataConnection.getOutputStream(), true);
            dataReader = new BufferedReader(new InputStreamReader(dataConnection.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    void closeDataConnection() {
        try {
            dataWriter.close();
            dataConnection.close();
            if (dataSocket != null) {
                dataSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        dataWriter = null;
        dataConnection = null;
        dataSocket = null;
    }

    void sendMsgToClient(String msg) {
        printWriter.println(msg);
    }

    void sendDataMsgToClient(String msg) {
        dataWriter.println(msg);
    }

    String getDataFromClient() throws IOException {
        String res = "";
        String line = dataReader.readLine();
        while(line != null) {
            res +=line;
            line = dataReader.readLine();
        }
        return res;
    }
}
