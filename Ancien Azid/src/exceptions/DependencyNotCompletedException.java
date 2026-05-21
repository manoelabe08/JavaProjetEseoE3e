package exceptions;

public class DependencyNotCompletedException extends Exception {
    public DependencyNotCompletedException(String message) {
        super(message);
    }
}
