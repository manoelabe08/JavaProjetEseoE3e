package custom_exeptions;

public class InvalidTaskStateException extends Exception {
    public InvalidTaskStateException(String message) {
        super(message);
    }

}
