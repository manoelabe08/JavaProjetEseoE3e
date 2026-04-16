package task_managment;

import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;

import Enum.TaskStatus;
import user.Engineer;
import user.User;
import custom_exeptions.*;

public class TaskManager {
    private HashMap<String, Task> tasks;
    private HashMap<String, User> users;
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
        if (!user.fullAccess) {
            throw new InvalidRoleException("User does not have permission to create tasks");
        }
        this.tasks.put(task.getTaskId(), task);
        this.taskQueue.add(task);
        task.addToHistory("taskcreated by " + user.getName(), user);
    }

    public void removeTask(String taskId, User user) throws InvalidRoleException, TaskNotFoundException {
        if (!user.partialAccess) {
            throw new InvalidRoleException("User does not have permission to delete tasks");
        }
        for (Task task : this.tasks.values()) {
            if (task.getTaskId().equals(taskId)) {
                this.tasks.remove(taskId);
                this.taskQueue.remove(task);
                task.addToHistory("task deleted by " + user.getName(), user);
                break;
            }
            throw new TaskNotFoundException("task not found ");
        }
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

    public void addDependencies(Task task, Task dependency, User user)
            throws CircularDependencyException, InvalidRoleException {
        if (!user.partialAccess) {
            throw new InvalidRoleException("User does not have permission to change status");
        }
        if (DetectCircularDependency(task, dependency)) {
            throw new CircularDependencyException(
                    "Circular dependency detected between " + task.getTaskId() + " and " + dependency.getTaskId());
        }
        task.getDependencies().add(dependency);
        task.updateStatus(TaskStatus.BLOCKED, user);
    }

    public TaskStatus monitoreProgress(Task task, User user) throws InvalidRoleException {
        if (user.partialAccess) {
            task.addToHistory("monitored progress", user);
            return task.getStatus();
        }
        task.addToHistory(user.getName() + " tried to monitor progress", user);
        throw new InvalidRoleException("User does not have permission to monitor progress");
    }

    public void assignTask(Task task, Engineer assignedEngineer) throws InvalidRoleException {
        if (!assignedEngineer.partialAccess) {
            throw new InvalidRoleException("User does not have permission to assign tasks");
        }
        task.setAssignee(assignedEngineer);
        task.updateStatus(TaskStatus.IN_PROGRESS, assignedEngineer);
    }
    public void completeTask(Task task, User user){
        task.setstatus(TaskStatus.DONE);
        task.addToHistory("completed by " + user.getName(), user);
        for (Task t : this.tasks.values()) {
            if (t.getDependencies().contains(task)){
                t.getDependencies().remove(task);
                if (t.getDependencies().isEmpty() ){
                    t.setstatus(TaskStatus.IN_PROGRESS);
                    t.addToHistory("dependency completed starting :", user);
                }
            }
        }
    }
    public void printInProgressTasks() {
        System.out.println("In Progress Tasks:");
        for (Task task : this.inProgressTasks ) {
                System.out.println("- " + task.getTaskId() + ": " + task.getStatus());
            }
        
    }
    public void findTask(String taskId) throws TaskNotFoundException {
        if (this.tasks.containsKey(taskId)) {
            Task task = this.tasks.get(taskId);
            System.out.println("Task ID: " + task.getTaskId());
            System.out.println("Status: " + task.getStatus());
            System.out.println("Priority: " + task.getPriority());
            System.out.println("Dependencies: ");
            for (Task dep : task.getDependencies()) {
                System.out.println("- " + dep.getTaskId());
            }
        } else {
            throw new TaskNotFoundException("Task with ID " + taskId + " not found");
        }
    }
    public void updateTask(Task task,Task newTask , User user) throws InvalidRoleException {
        if (!user.partialAccess) {
            throw new InvalidRoleException("User does not have permission to update tasks");
        }
        task.addToHistory("updated by " + user.getName(), user);
        task = newTask;
    }
}