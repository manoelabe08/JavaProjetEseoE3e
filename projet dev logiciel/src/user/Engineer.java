
package user;

import java.util.ArrayList;

import task_managment.Task;

public class Engineer extends User {

    private ArrayList<Task> assignedTasks;

    public Engineer(String name, String email, String uID) {
        super(name, email, uID);
        this.assignedTasks = new ArrayList<>();
        this.fullAccess = false;
        this.partialAccess = false;
    }

    public Engineer(String name, String email, String uID, ArrayList<Task> assignedTasks) {
        super(name, email, uID);
        this.assignedTasks = assignedTasks;
        this.fullAccess = false;
        this.partialAccess = false;
    }

    public void addTask(Task task) {
        this.assignedTasks.add(task);
    }

    public void removeTask(Task task) {
        assignedTasks.remove(task);
    }

}
