package user;



public class Admin extends User {

    
    public Admin(String name, String email, String uID) {
        super(name, email, uID);
        this.canCreateTask = true;
        this.canMonitorProgress = true;
        this.canRemoveTask = true;
        this.canChangeStatus = true;
    }

}
