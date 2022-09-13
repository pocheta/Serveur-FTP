package sr1;

import java.io.IOException;

/**
 * La classe Main est la classe principale du serveur ftp
 *
 * @author pochet
 * @author michot
 */
public class Main {

    /**
     * main permet la gestion des arguments fournis lors du lancement et exécute la connexion au serveur
     * @param args arguments passés au lancement (chemin du répertoire pour la création du serveur et portdu serveur ftp)
     * @throws IOException exception
     */
    public static void main(String[] args) throws IOException {
        int port = 8888;

        if (args.length < 1) {
            System.err.println("Le dossier source ne peut pas être vide");
            System.exit(1);

        } else {
            String currentDirectory = args[0];

            for (String param : args) {
                if (param.contains("-p")) port = Integer.parseInt(param.split("=")[1]);
            }

            ServerFTP server = new ServerFTP(currentDirectory, port);

            server.connection();
        }
    }
}
