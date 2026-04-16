package user;

abstract public class User {
    private String name;
    private String email;
    private String uID;
    public Boolean fullAccess;
    public Boolean partialAccess;

    public User(String name, String email, String uID) {
        this.name = name;
        this.email = email;
        this.uID = uID;
        this.fullAccess = true;
        this.partialAccess = false;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUID() {
        return uID;
    }
}
