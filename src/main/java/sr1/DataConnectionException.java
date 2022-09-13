package sr1;

/**
 * Cette classe permet de relever les exceptions des data sockets
 *
 * @author pochet
 * @author michot
 */
public class DataConnectionException extends Exception {

    /**
     * DataConnectionException
     */
    public DataConnectionException() {
        super();
    }

    /**
     * Constructeur DataConnectionException avec un message spécifique
     * @param msg message spécifique
     */
    public DataConnectionException(String msg) {
        super(msg);
    }
}
