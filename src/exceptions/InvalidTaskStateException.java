package exceptions;
/**
 * Thrown when an illegal lifecycle transition is requested on a task, 
 * such as reverting a completed task (DONE) back to another state.
 */
public class InvalidTaskStateException extends Exception {
    public InvalidTaskStateException(String message) {
        super(message);
    }
}
