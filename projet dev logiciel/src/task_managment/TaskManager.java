package task_managment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import Enum.TaskStatus;
import user.User;
import custom_exeptions.*;

public class TaskManager {
    private HashMap<String, Task> tasks;
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

    public void addUser(User user) {
        this.users.put(user.getUID(), user);
    }

    public void createTask(Task task, User user) throws InvalidRoleException {
        if (!user.canCreateTask) {
            throw new InvalidRoleException("User does not have permission to create tasks");
        }
        this.tasks.put(task.getTaskId(), task);
        this.taskQueue.add(task);
        task.addToHistory("taskcreated by " + user.getName(), user);
    }

    public void removeTask(String taskId, User user) throws InvalidRoleException, TaskNotFoundException {
        if (!user.canRemoveTask) {
            throw new InvalidRoleException("User does not have permission to delete tasks");
        }
        for (Task task : this.tasks.values()) {
            if (task.getTaskId().equals(taskId)) {
                this.tasks.remove(taskId);
                this.taskQueue.remove(task);
                task.addToHistory("task deleted by " + user.getName(), user);
                break;
            }
        }
        throw new TaskNotFoundException("task not found ");
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

    public void addDependencies(Task task, Task dependency) throws CircularDependencyException {
        if (DetectCircularDependency(task, dependency)) {
            throw new CircularDependencyException(
                    "Circular dependency detected between " + task.getTaskId() + " and " + dependency.getTaskId());
        }
        task.getDependencies().add(dependency);
    }

    public TaskStatus monitoreProgress(Task task, User user) throws InvalidRoleException {
        if (user.canMonitorProgress) {
            task.addToHistory("monitored progress", user);
            return task.getStatus();
        }
        task.addToHistory(user.getName() + " tried to monitor progress", user);
        throw new InvalidRoleException("User does not have permission to monitor progress");
    }

}