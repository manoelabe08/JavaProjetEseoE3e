package exceptions;
/**
 * Thrown when a requested task lookup fails because the identifier 
 * does not match any active task in the database.
 */
public class TaskNotFoundException extends Exception {
    public TaskNotFoundException(String message) {
        super(message);
    }
}
