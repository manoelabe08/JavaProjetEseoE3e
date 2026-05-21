package exceptions;
/**
 * Thrown when an authenticated user attempts to perform an action 
 * that violates role-based access control guidelines.
 */
public class InvalidRoleException extends Exception {
    public InvalidRoleException(String message) {
        super(message);
    }
}
