package sr1;

/**
 * Cette classe permet de relever les exceptions lors de la manipulation des dossiers
 *
 * @author pochet
 * @author michot
 */
public class DirectoryException extends Exception {

    /**
     * DirectoryException
     */
    public DirectoryException() {
        super();
    }

    /**
     * Constructeur DirectoryException avec un message spécifique
     * @param msg message spécifique
     */
    public DirectoryException(String msg) {
        super(msg);
    }
}
