package sr1;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Cette classe permet de gérer les commandes reçu par le client
 * La classe contient tous les méthodes permettant d'éffectuer chaque commande
 *
 * @author pochet
 * @author michot
 */
public class FTPCommands {

    private final ClientThread clientThread;
    private String currentDirectory;
    private final LinkedList<String> lastDirectory;
    private File fileRename;

    private String validUser;
    private String validPassword;

    /**
     * Ce constructeur permet de créer FTPCommands avec en paramètre le client qui va envoyer les commandes a notre
     * serveur ainsi que le répertoire courant
     * @param clientThread
     * @param currentDirectory
     */
    FTPCommands(ClientThread clientThread, String currentDirectory) {
        this.clientThread = clientThread;
        this.currentDirectory = currentDirectory;
        this.lastDirectory = new LinkedList<>();
    }

    /**
     * executeCommand permet de gérer une commande donnée grâce à un switch case
     * @param c commande à exécuter
     * @throws IOException exception
     * @throws SocketException exception
     * @throws DataConnectionException exception
     * @throws DirectoryException exception
     */
    protected void executeCommand(String c) throws IOException, SocketException, DataConnectionException, DirectoryException {
        int index = c.indexOf(' ');
        String command = ((index == -1) ? c.toUpperCase() : (c.substring(0, index)).toUpperCase());
        String args = ((index == -1) ? null : c.substring(index + 1));

        System.out.println("Command: " + command + " Args: " + args);

        switch (command) {
            case "AUTH" -> handleAuth();

            case "USER" -> checkUser(args);

            case "PASS" -> checkPassword(args);

            case "PWD" -> clientThread.sendMsgToClient("257 \"" + currentDirectory + "\" is the current directory");

            case "TYPE" -> handleType(args);

            case "PASV" -> handlePasv();

            case "PORT" -> handlePort(args);

            case "LIST" -> handleList(args);

            case "CWD" -> handleCwd(args);

            case "CDUP" -> handleCdup();

            case "RNFR" -> handleRnfr(args);

            case "RNTO" -> handleRnto(args);

            case "MKD" -> handleMkd(args);

            case "RMD" -> handleRmd(args);

            case "STOR" -> handleStor(args);

            case "RETR" -> handleRetr(args);

            default -> clientThread.sendMsgToClient("501 Unknown command");
        }
    }

    /**
     * handleAuth permet la connexion par user et pass
     */
    private void handleAuth(){
        clientThread.sendMsgToClient("530 Please login with USER and PASS.");
    }

    /**
     * checkUser vérifie si le user donné existe dans le fichier des user
     * @param username
     * @throws IOException
     */
    private void checkUser(String username) throws IOException {
        Path path = Paths.get("src/main/java/sr1/user.txt");
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        String line = bufferedReader.readLine();

        while (line != null){
            System.out.println(line.split(":")[0] + " = " + username);

            if (username.toLowerCase().equals(line.split(":")[0])) {
                validUser = line.split(":")[0];
                validPassword =  line.split(":")[1];
                clientThread.sendMsgToClient("331 Please specify the password.");
            }

            line = bufferedReader.readLine();
        }

        if (validUser == null){
            clientThread.sendMsgToClient("530 User KO");
        }
    }

    /**
     * checkPassword vérifie si le password donné correspond à celui lié à l'utilisateur passé plus tôt
     * @param password
     */
    private void checkPassword(String password) {
        if (password.equals(validPassword)) {
            clientThread.sendMsgToClient("230-Welcome to FTP-SERVER");
            clientThread.sendMsgToClient("230 Login successful");
        } else {
            clientThread.sendMsgToClient("530 Password KO");
        }
    }

    /**
     * handleType permet de gérer l'ASCII et le BINARY
     * @param mode
     */
    private void handleType(String mode) {
        if (mode.equalsIgnoreCase("A")) {
            clientThread.transferMode = TransferType.ASCII;
            clientThread.sendMsgToClient("200 Switching to ASCII mode.");
        } else if (mode.equalsIgnoreCase("I")) {
            clientThread.transferMode = TransferType.BINARY;
            clientThread.sendMsgToClient("200 Switching to Binary mode.");
        } else {
            clientThread.sendMsgToClient("504 Type KO");
        }
    }

    /**
     * handlePasv créé une connexion temporaire pour l'utilisation de commandes spécifiques (mode passif)
     */
    private void handlePasv() throws SocketException, DataConnectionException {
        int dataPort = clientThread.createServerSocket();

        String myIp = "127.0.0.1";
        String myIpSplit[] = myIp.split("\\.");

        int p1 = dataPort / 256;
        int p2 = dataPort % 256;

        clientThread.sendMsgToClient("227 Entering Passive Mode (" + myIpSplit[0] + "," + myIpSplit[1] + "," + myIpSplit[2] + ","
                + myIpSplit[3] + "," + p1 + "," + p2 + ")");

        clientThread.openDataConnectionPassive();
    }

    /**
     * handlePort créé une connexion temporaire pour l'utilisation de commandes spécifiques (mode actif)
     * @param args
     */
    private void handlePort(String args) throws DataConnectionException {
        String[] myIpSplit = args.split(",");

        int p1 = Integer.parseInt(myIpSplit[4]);
        int p2 = Integer.parseInt(myIpSplit[5]);
        int port = p1 * 256 + p2;

        String addr = String.join(".", Arrays.copyOfRange(myIpSplit, 0 ,4));

        clientThread.openDataConnectionActive(addr, port);
    }

    /**
     * handleList permet au client ftp de lister un répertoire
     * @param args
     */
    private void handleList(String args) throws DataConnectionException {
        File[] dirContent = nlstHelper(args);

        if (dirContent == null) {
            clientThread.sendMsgToClient("550 File does not exist.");
        } else {
            clientThread.sendMsgToClient("150 Here comes the directory listing.");

            for (File f : dirContent ) {
                String stringBuilder =
                        (f.isDirectory() ? "d" : "-") +
                        (f.canRead() ? "r" : "-") +
                        (f.canWrite() ? "w" : "-") +
                        (f.canExecute() ? "x" : "-") +
                        "------" +
                        "    " +
                        "1" +
                        " " +
                        "11081" +
                        "    " +
                        "1005" +
                        "            " +
                        f.length() +
                        " Jan 30 00:00 " +
                        f.getName();
                clientThread.sendDataMsgToClient(stringBuilder);
            }

            clientThread.closeDataConnection();
            clientThread.sendMsgToClient("226 Directory send OK.");

        }

    }

    /**
     * nlstHelper renvoie le contenu du dossier à lister dans la méthode handleList
     * @param args
     * @return
     */
    private File[] nlstHelper(String args) {
        String filename = currentDirectory;
        if (args != null) {
            filename = filename + '/' + args;
        }

        File f = new File(filename);

        if (f.exists() && f.isDirectory()) {
            return f.listFiles();
        } else if (f.exists() && f.isFile()) {
            File[] allFiles = new File[1];
            allFiles[0] = f;
            return allFiles;
        } else {
            return null;
        }
    }

    /**
     * handleCwd permet de changer le répertoire courant
     * @param args
     */
    private void handleCwd(String args) {
        String replace = args.replace("\\", "/");
        if (replace.contains("/")){
            lastDirectory.add(currentDirectory);
            currentDirectory = replace;
            clientThread.sendMsgToClient("200 OK");
        }else {
            lastDirectory.add(currentDirectory);
            currentDirectory = currentDirectory + "/" + replace;
            clientThread.sendMsgToClient("200 OK");
        }
    }

    /**
     * handleCdup permet de se déplacer dans le répertoire parent
     */
    private void handleCdup(){
        currentDirectory = lastDirectory.getLast();
        lastDirectory.removeLast();
        clientThread.sendMsgToClient("200 OK");
    }

    /**
     * handleRnfr spécifie l'ancien chemin du fichier à renommer
     * @param args
     */
    private void handleRnfr(String args){
        fileRename = new File(currentDirectory + "//" + args);
        clientThread.sendMsgToClient("257 \"" + args + "\"");
    }

    /**
     * handleRnto spécifie le nouveau chemin du fichier à renommer et le renomme
     * @param args
     */
    private void handleRnto(String args){
        File fileTo = new File(currentDirectory + "//" + args);
        fileRename.renameTo(fileTo);
        clientThread.sendMsgToClient("257 \"" + args + "\"");
    }

    /**
     * handleMkd permet de créer un nouveau répertoire
     * @param args
     * @throws IOException
     */
    private void handleMkd(String args) throws DirectoryException {
        try {
            Files.createDirectories(Paths.get(currentDirectory + "//" + args));
            clientThread.sendMsgToClient("257 \"" + args + "\"");
        } catch (IOException e) {
            throw new DirectoryException("Impossible de creer le répertoire : " + e.getMessage());
        }
    }

    /**
     * handleRmd permet de supprimer un répertoire
     * @param args
     * @throws IOException
     */
    private void handleRmd(String args) throws DirectoryException {
        try {
            Files.deleteIfExists(Paths.get(currentDirectory + "//" + args));
            clientThread.sendMsgToClient("257 \"" + args + "\"");
        }catch (IOException e) {
            throw new DirectoryException("Impossible de supprimer le répertoire : " + e.getMessage());
        }
    }

    /**
     * handleStor permet l'envoi de données vers le serveur ftp
     * @param file
     * @throws IOException
     */
    private void handleStor(String file) throws IOException {
        clientThread.sendMsgToClient("150 Ok to send data.");

        BufferedInputStream bufferedInputStream = new BufferedInputStream(clientThread.getDataConnection().getInputStream());

        byte[] fileBytes = bufferedInputStream.readAllBytes();

        FileOutputStream fileOutputStream = new FileOutputStream(currentDirectory + "/" + file);

        fileOutputStream.write(fileBytes, 0, fileBytes.length);

        bufferedInputStream.close();
        fileOutputStream.close();

        clientThread.sendMsgToClient("226 Directory send OK.");
    }

    /**
     * handleRetr permet le téléchargement de données depuis le serveur ftp
     * @param file
     * @throws IOException
     */
    private void handleRetr(String file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(currentDirectory + "/" + file);

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(clientThread.getDataConnection().getOutputStream());

        byte[] fileBytes = fileInputStream.readAllBytes();
        clientThread.sendMsgToClient("150 Opening BINARY mode");

        bufferedOutputStream.write(fileBytes);
        bufferedOutputStream.close();

        clientThread.sendMsgToClient("226 Transfer complete.");
    }

}
