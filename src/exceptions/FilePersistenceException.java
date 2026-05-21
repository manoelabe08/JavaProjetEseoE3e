package exceptions;
/**
 * Thrown when a file input/output operation fails, such as a missing file, 
 * permission restriction, or data corruption during save/load.
 */
public class FilePersistenceException extends Exception {
    public FilePersistenceException(String message) {
        super(message);
    }

    public FilePersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
