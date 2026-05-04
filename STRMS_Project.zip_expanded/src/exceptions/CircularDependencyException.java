package exceptions;

public class CircularDependencyException extends Exception {
    private static final long serialVersionUID = -7172949966980258419L;

	public CircularDependencyException(String message) {
        super(message);
    }
}
