package user;

import task_management.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Represents an Engineer in the system.
 * Engineers are the only users permitted to execute and complete tasks.
 * They maintain a specific list of tasks assigned to them.
 */
public class Engineer extends User {
    private final List<Task> assignedTasks;

    public Engineer(String id, String name, String email) {
        super(id, name, email);
        this.assignedTasks = new ArrayList<>();
    }
/**
     * Assigns a new task to the engineer's workload.
     * @param task The task to be assigned.
     */
    public void addTask(Task task) {
        if (!assignedTasks.contains(task)) {
            assignedTasks.add(task);
        }
    }
/**
     * Removes a task from the engineer's workload.
     * @param task The task to remove.
     */
    public void removeTask(Task task) {
        assignedTasks.remove(task);
    }
/**
     * Retrieves the list of tasks assigned to this engineer.
     * @return An unmodifiable list of assigned tasks to protect the internal collection.
     */
    public List<Task> getAssignedTasks() {
        return Collections.unmodifiableList(assignedTasks);
    }

    @Override
    public boolean canCreateTask() {
        return false;
    }

    @Override
    public boolean canDeleteTask() {
        return false;
    }

    @Override
    public boolean canAssignTask() {
        return false;
    }

    @Override
    public boolean canUpdateTask() {
        return true;
    }

    @Override
    public boolean canGenerateReport() {
        return false;
    }

    @Override
    public boolean canWorkOnTask() {
        return true;// Only engineers can execute tasks.
    }
}