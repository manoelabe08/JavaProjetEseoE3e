abstract public class User {
    private String name;
    private String email;
    private String uID;
    
    public User(String name, String email, String uID) {
        this.name = name;
        this.email = email;
        this.uID = uID;
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
