    package exceptions;
/**
 * Thrown when an operation attempts to link tasks in a way that creates 
 * a circular loop, preventing task resolution.
 */
    public class CircularDependencyException extends Exception {
        public CircularDependencyException(String message) {
            super(message);
        }
    }
