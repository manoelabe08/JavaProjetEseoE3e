import java.util.ArrayList;

public class Engineer extends User {


    private ArrayList<Task> assignedTasks;

    public Engineer(String name, String email, String uID) {
        super(name, email, uID);
        this.assignedTasks = new ArrayList<>();
    }
    public Engineer(String name, String email, String uID, ArrayList<Task> assignedTasks) {
        super(name, email, uID);
        this.assignedTasks = assignedTasks;
    }

    public void addTask(Task task){
        this.assignedTasks.add(task);
    }
    public void removeTask(Task task){
        assignedTasks.remove(task);
    }

    public boolean canCreateTask(){
        return false;
    }
        public boolean canMonitorProgress(){
        return false;
    }


}
