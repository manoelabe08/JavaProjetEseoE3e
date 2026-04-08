public class Manager extends User{

    public Manager(String name, String email, String uID) {
        super(name, email, uID);
    }
        public boolean canCreateTask(){
        return false;
    }
        public boolean canMonitorProgress(){
        return true;
    }
    public boolean canRemoveTask(){
        return true;
    }



}
