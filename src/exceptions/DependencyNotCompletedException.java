package exceptions;
/**
 * Thrown when a user tries to start or transition a task whose 
 * prerequisite tasks have not reached the DONE status.
 */
public class DependencyNotCompletedException extends Exception {
    public DependencyNotCompletedException(String message) {
        super(message);
    }
}
