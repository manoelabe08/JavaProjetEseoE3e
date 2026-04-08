public class Admin extends User {
    
    public Admin(String name, String email, String uID) {
        super(name, email, uID);
    }

    public boolean canCreateTask() {
        return true;
    }

    public boolean canMonitorProgress() {
        return true;
    }

    public boolean canRemoveTask() {
        return true;
    }

}
