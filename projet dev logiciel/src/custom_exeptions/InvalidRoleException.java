package custom_exeptions;


public class InvalidRoleException extends Exception {
    public InvalidRoleException(String message) {
        super(message);
    }

}
