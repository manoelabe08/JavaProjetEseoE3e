
package user;

public class Manager extends User{

    public Manager(String name, String email, String uID) {
        super(name, email, uID);
        this.canCreateTask = false;
        this.canMonitorProgress = true;
        this.canRemoveTask = true;
        this.canChangeStatus = true;
    }


}
