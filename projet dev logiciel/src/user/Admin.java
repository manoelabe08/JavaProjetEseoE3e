package user;

public class Admin extends User {

    public Admin(String name, String email, String uID) {
        super(name, email, uID);
        this.fullAccess = true;
        this.partialAccess = true;

    }
}