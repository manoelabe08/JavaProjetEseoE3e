package user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import task_management.Task;

public class Engineer extends User {
    private final List<Task> assignedTasks;

    public Engineer(String id, String name, String email) {
        super(id, name, email);
        this.assignedTasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        if (!assignedTasks.contains(task)) {
            assignedTasks.add(task);
        }
    }

    public void removeTask(Task task) {
        assignedTasks.remove(task);
    }

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
        return true;
    }
}
