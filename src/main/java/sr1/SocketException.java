package sr1;

/**
 * Cette classe permet de relever les exceptions des sockets
 *
 * @author pochet
 * @author michot
 */
public class SocketException extends Exception {

    /**
     * SocketException
     */
    public SocketException() {
        super();
    }

    /**
     * Constructeur SocketException avec un message spécifique
     * @param msg message spécifique
     */
    public SocketException(String msg) {
        super(msg);
    }
}
