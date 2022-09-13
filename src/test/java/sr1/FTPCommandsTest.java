package sr1;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static org.junit.Assert.assertEquals;

public class FTPCommandsTest {

    @Before
    public void launchServer() throws IOException {
        String[] args = {"C:/Users/touto/Documents", "-p=8888"};
        Main.main(args);
    }

    @Test
    public void testAUTH () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("AUTH TLS");
        assertEquals(reader.readLine(), "530 Please login with USER and PASS.");
    }

    @Test
    public void testTypeASCII () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("TYPE A");
        assertEquals(reader.readLine(), "200 Switching to ASCII mode.");
    }

    @Test
    public void testTypeBinary () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("TYPE I");
        assertEquals(reader.readLine(), "200 Switching to Binary mode.");
    }

    @Test
    public void testTypeKO () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("TYPE X");
        assertEquals(reader.readLine(), "504 Type KO");
    }

    @Test
    public void testCwd () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("CWD C:/Users/touto/Documents");
        assertEquals(reader.readLine(), "200 OK");
    }

    @Test
    public void testCdup () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("CWD C:/Users/touto/Documents/ECOLE");
        reader.readLine();
        printer.println("CDUP ");
        assertEquals(reader.readLine(), "200 OK");
    }

    @Test
    public void testRnfr () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("CWD C:/Users/touto/Documents/ECOLE");
        reader.readLine();
        printer.println("RNFR Nouveau");
        assertEquals(reader.readLine(), "257 \"Nouveau\"");
    }

    @Test
    public void testRnto () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("CWD C:/Users/touto/Documents/ECOLE");
        reader.readLine();
        printer.println("RNFR NouveauDossier");
        reader.readLine();
        printer.println("RNTO Nouveau");
        assertEquals(reader.readLine(), "257 \"Nouveau\"");
    }

    @Test
    public void testMkd () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("CWD C:/Users/touto/Documents/ECOLE");
        reader.readLine();
        printer.println("MKD NouveauDossier");
        assertEquals(reader.readLine(), "257 \"NouveauDossier\"");
    }

    @Test
    public void testRmd () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        printer.println("CWD C:/Users/touto/Documents/ECOLE");
        reader.readLine();
        printer.println("RMD NouveauDossier");
        assertEquals(reader.readLine(), "257 \"NouveauDossier\"");
    }

    @Test
    public void testStor () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        //TODO
    }

    @Test
    public void testRetr () throws IOException {
        Socket socket = new Socket("127.0.0.1", 8888);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter printer = new PrintWriter(socket.getOutputStream(), true);
        reader.readLine();
        printer.println("USER " + "test");
        reader.readLine();
        printer.println("PASS " + "test");
        reader.readLine();
        reader.readLine();

        //TODO
    }
}
