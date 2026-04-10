abstract public class User {
    private String name;
    private String email;
    private String uID;
    protected boolean canCreateTask;
    protected boolean canMonitorProgress;
    protected boolean canRemoveTask;
    protected boolean canChangeStatus;
    public User(String name, String email, String uID) {
        this.name = name;
        this.email = email;
        this.uID = uID;
        this.canCreateTask = true;
        this.canMonitorProgress = true;
        this.canRemoveTask = true;
        this.canChangeStatus = true;
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
