package task_managment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import Enum.TaskStatus;
import user.User;

public class TaskManager {
    private HashMap<String, Task> tasks;
    @SuppressWarnings("unused")
	private HashMap<String, User> users;
    @SuppressWarnings("unused")
	private HashSet<Task> inProgressTasks;
    private PriorityQueue<Task> taskQueue;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.users = new HashMap<>();
        this.inProgressTasks = new HashSet<>();
        this.taskQueue = new PriorityQueue<>((t1, t2) -> t2.getPriority().compareTo(t1.getPriority()));
    }

    public void addTask(Task task) {
        this.tasks.put(task.getTaskId(), task);
        this.taskQueue.add(task);
    }
        public boolean DetectCircularDependency(Task task, Task dependency) {
        if (task.getDependencies().contains(dependency)) {
            return true;
        }
        for (Task dep : task.getDependencies()) {
            if (DetectCircularDependency(dep, dependency)) {
                return true;
            }
        }
        return false;
    }

    public void addDependencies(Task task, Task dependency) {
        task.getDependencies().add(dependency);
        if (DetectCircularDependency(task, dependency)) {
            task.getDependencies().remove(dependency);
            throw new IllegalStateException("Adding this dependency creates a circular dependency");
        }
    }

    public TaskStatus monitoreProgress(Task task, User user) {
        if (user.canMonitorProgress) {
            task.addToHistory("monitored progress", user);
            return task.getStatus();
        }
        task.addToHistory(user.getName() + " tried to monitor progress", user);
        throw new IllegalStateException("User does not have permission to monitor progress");
    }
}