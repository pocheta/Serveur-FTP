package sr1;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Cette classe permet de tester les différentes méthode de la classe ClientThread
 *
 * @author pochet
 * @author michot
 */
public class ClientThreadTest {

    @Test
    public void testUser () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");

        assertEquals(reader.readLine(), "331 Please specify the password.");
    }

    @Test
    public void testKOUser () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "UtilisateurNonExistant");

        assertEquals(reader.readLine(), "530 User KO");
    }

    @Test
    public void testUserAndPassword () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");

        assertEquals(reader.readLine(), "230-Welcome to FTP-SERVER");
        assertEquals(reader.readLine(), "230 Login successful");
    }

    @Test
    public void testUserOKAndPasswordKO () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "FauxMotDePasse");

        assertEquals(reader.readLine(), "530 Password KO");
    }

    @Test
    public void testConnection () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");

        assertEquals(reader.readLine(), "230-Welcome to FTP-SERVER");
    }

}
