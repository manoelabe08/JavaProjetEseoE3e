package exceptions;
/**
 * Thrown during task creation if the specified unique identifier (UID) 
 * already exists within the system repository.
 */
public class DuplicateTaskException extends Exception {
    public DuplicateTaskException(String message) {
        super(message);
    }
}
