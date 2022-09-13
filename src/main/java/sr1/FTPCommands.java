package sr1;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

class FTPCommands {
    private final ClientThread clientThread;
    private final PrintWriter printWriter;
    private String currentDirectory;
    private final LinkedList<String> lastDirectory;
    private File fileRename;

    private String validUser;
    private String validPassword;

    FTPCommands(ClientThread clientThread, PrintWriter printWriter, String currentDirectory) {
        this.clientThread = clientThread;
        this.currentDirectory = currentDirectory;
        this.printWriter = printWriter;
        this.lastDirectory = new LinkedList<>();
    }

    void executeCommand(String c) throws IOException {
        int index = c.indexOf(' ');
        String command = ((index == -1) ? c.toUpperCase() : (c.substring(0, index)).toUpperCase());
        String args = ((index == -1) ? null : c.substring(index + 1));

        System.out.println("Command: " + command + " Args: " + args);

        switch (command) {
            case "AUTH" -> handleAuth(args);

            case "USER" -> checkUser(args);

            case "PASS" -> checkPassword(args);

            case "PWD" -> sendMsgToClient("257 \"" + currentDirectory + "\" is the current directory");

            case "TYPE" -> handleType(args);

            case "PASV" -> handlePasv();

            case "LIST" -> handleList(args);

            case "CWD" -> handleCwd(args);

            case "CDUP" -> handleCdup();

            case "RNFR" -> handleRnfr(args);

            case "RNTO" -> handleRnto(args);

            case "MKD" -> handleMkd(args);

            case "RMD" -> handleRmd(args);

            case "STOR" -> handleStor(args);

            case "RETR" -> handleRetr(args);

            default -> {
                System.out.println("501 Unknown command");
                sendMsgToClient("501 Unknown command");
            }
        }
    }

    private void handleAuth(String args){
        sendMsgToClient("530 Please login with USER and PASS.");
    }

    private void checkUser(String username) throws IOException {
        Path path = Paths.get("src/main/java/sr1/user.txt");
        BufferedReader bufferedReader = Files.newBufferedReader(path);
        String line = bufferedReader.readLine();

        while (line != null){
            System.out.println(line.split(":")[0] + " = " + username);

            if (username.toLowerCase().equals(line.split(":")[0])) {
                validUser = line.split(":")[0];
                validPassword =  line.split(":")[1];
                sendMsgToClient("331 Please specify the password.");
            }

            line = bufferedReader.readLine();
        }

        if (validUser == null){
            sendMsgToClient("530 User KO");
        }
    }

    private void checkPassword(String password) {
        if (password.equals(validPassword)) {
            sendMsgToClient("230-Welcome to FTP-SERVER");
            sendMsgToClient("230 Login successful");
        } else {
            sendMsgToClient("530 Password KO");
        }
    }

    private void handleType(String mode) {
        if (mode.equalsIgnoreCase("A")) {
            clientThread.transferMode = TransferType.ASCII;
            sendMsgToClient("200 Switching to ASCII mode.");
        } else if (mode.equalsIgnoreCase("I")) {
            clientThread.transferMode = TransferType.BINARY;
            sendMsgToClient("200 Switching to Binary mode.");
        } else {
            sendMsgToClient("504 Type KO");
        }
    }

    private void handlePasv() {
        int dataPort = clientThread.createServerSocket();

        String myIp = "127.0.0.1";
        String myIpSplit[] = myIp.split("\\.");

        int p1 = dataPort / 256;
        int p2 = dataPort % 256;

        sendMsgToClient("227 Entering Passive Mode (" + myIpSplit[0] + "," + myIpSplit[1] + "," + myIpSplit[2] + ","
                + myIpSplit[3] + "," + p1 + "," + p2 + ")");

        clientThread.openDataConnectionPassive();
    }

    private void handleList(String args) {
        File[] dirContent = nlstHelper(args);

        if (dirContent == null) {
            sendMsgToClient("550 File does not exist.");
        } else {
            sendMsgToClient("150 Here comes the directory listing.");

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
            sendMsgToClient("226 Directory send OK.");

        }

    }

    private File[] nlstHelper(String args) {
        // Construct the name of the directory to list.
        String filename = currentDirectory;
        if (args != null) {
            filename = filename + '/' + args;
        }

        // Now get a File object, and see if the name we got exists and is a
        // directory.
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

    private void handleCwd(String args) {
        String replace = args.replace("\\", "/");
        if (replace.contains("/")){
            lastDirectory.add(currentDirectory);
            currentDirectory = replace;
            sendMsgToClient("200 OK");
        }else {
            lastDirectory.add(currentDirectory);
            currentDirectory = currentDirectory + "/" + replace;
            sendMsgToClient("200 OK");
        }
    }

    private void handleCdup(){
        currentDirectory = lastDirectory.getLast();
        lastDirectory.removeLast();
        sendMsgToClient("200 OK");
    }

    private void handleRnfr(String args){
        fileRename = new File(currentDirectory + "//" + args);
        sendMsgToClient("257 \"" + args + "\"");
    }

    private void handleRnto(String args){
        File fileTo = new File(currentDirectory + "//" + args);
        fileRename.renameTo(fileTo);
        sendMsgToClient("257 \"" + args + "\"");
    }

    private void handleMkd(String args) throws IOException {
        Files.createDirectories(Paths.get(currentDirectory + "//" + args));
        sendMsgToClient("257 \"" + args + "\"");
    }

    private void handleRmd(String args) throws IOException {
        Files.deleteIfExists(Paths.get(currentDirectory + "//" + args));
        sendMsgToClient("257 \"" + args + "\"");
    }

    private void handleStor(String args) throws IOException {
        //TODO
        InputStream inputStream = new FileInputStream("/Users/julien/Downloads/BLOCKCHAIN.pdf");

        System.out.println("ICIIIIC"+clientThread.getDataFromClient());

        System.out.println("ARGS: " + args);
        System.out.println("current: " + currentDirectory);
    }

    private void handleRetr(String args){
        //TODO
        File file = new File(args);

        // envoyer vers le client
    }

    private void sendMsgToClient(String msg) {
        printWriter.println(msg);
    }

}
