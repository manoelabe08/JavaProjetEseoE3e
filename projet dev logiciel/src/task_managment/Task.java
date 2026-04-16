package task_managment;
import java.util.ArrayList;

import Enum.PriorityLevel;
import Enum.TaskStatus;
import user.Engineer;
import user.User;

import java.time.LocalDateTime;

public class Task {
    private String taskId;
    @SuppressWarnings("unused")
	private String title;
    private PriorityLevel priority;
    private TaskStatus status ;
    private ArrayList<Task> dependencies;
    private ArrayList<TaskHistory> history;
    private Engineer assignedEngineer;


    public Task(String taskId, String title, PriorityLevel priority) {
        this.taskId = taskId;
        this.title = title;
        this.priority = priority;
        this.status = TaskStatus.TODO;
        this.dependencies = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public PriorityLevel getPriority() {
        return this.priority;
    }

    public String getTaskId() {
        return this.taskId;
    }

    public void setAssignee(Engineer engineer) {
        this.assignedEngineer = engineer;
        this.history.add(new TaskHistory("assigned to " + engineer.getName(), engineer, LocalDateTime.now()));
    }
    public void setstatus(TaskStatus status) {
        this.status = status;
    }

    public ArrayList<Task> getDependencies() {
        return this.dependencies;
    }
    public TaskStatus getStatus() {
        return this.status;
    }

    public void updateStatus(TaskStatus newStatus,User user) {
        if (user.partialAccess){
            this.status = newStatus;
            this.history.add(new TaskHistory("changed status to " + newStatus, this.assignedEngineer, LocalDateTime.now()));
        }
        else {
            throw new IllegalStateException("User does not have permission to change status");
        }
    }

    public void addToHistory(String action, User user) {
        this.history.add(new TaskHistory(action, user, LocalDateTime.now()));
    }



}