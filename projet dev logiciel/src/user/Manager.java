
package user;

public class Manager extends User {

    public Manager(String name, String email, String uID) {
        super(name, email, uID);
        this.fullAccess = false;
        this.partialAccess = true;
    }

}
