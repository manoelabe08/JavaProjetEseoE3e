package task_management;

import enums.TaskStatus;
import exceptions.CircularDependencyException;
import exceptions.DependencyNotCompletedException;
import exceptions.DuplicateTaskException;
import exceptions.FilePersistenceException;
import exceptions.InvalidRoleException;
import exceptions.InvalidTaskStateException;
import exceptions.TaskNotFoundException;
import file_management.FileManager;
import user.Engineer;
import user.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class TaskManager {
    private final Map<String, Task> tasks;
    private final Map<String, User> users;
    private final Set<Task> inProgressTasks;
    private final PriorityQueue<Task> taskQueue;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.users = new HashMap<>();
        this.inProgressTasks = new HashSet<>();
        this.taskQueue = new PriorityQueue<>();
    }

    public void addUser(User user) {
        users.put(user.getUID(), user);
    }

    public User findUser(String userId) {
        return users.get(userId);
    }

    public void addTask(Task task, User actor) throws InvalidRoleException, DuplicateTaskException {
        if (!actor.canCreateTask()) {
            throw new InvalidRoleException("User does not have permission to create tasks.");
        }

        if (tasks.containsKey(task.getTaskId())) {
            throw new DuplicateTaskException("Task with ID " + task.getTaskId() + " already exists.");
        }

        tasks.put(task.getTaskId(), task);
        taskQueue.offer(task);
        task.addHistoryEntry("Task created", actor);
    }

    public void deleteTask(String taskId, User actor) throws InvalidRoleException, TaskNotFoundException {
        if (!actor.canDeleteTask()) {
            throw new InvalidRoleException("User does not have permission to delete tasks.");
        }

        Task task = findTask(taskId);
        tasks.remove(taskId);
        taskQueue.remove(task);
        inProgressTasks.remove(task);

        for (Task current : tasks.values()) {
            current.getDependencies().remove(task);
            current.refreshBlockedStatus();
        }

        task.addHistoryEntry("Task deleted", actor);
    }

    public Task findTask(String taskId) throws TaskNotFoundException {
        Task task = tasks.get(taskId);
        if (task == null) {
            throw new TaskNotFoundException("Task with ID " + taskId + " not found.");
        }
        return task;
    }

    public Collection<Task> listTasks() {
        return tasks.values();
    }

    public void assignTask(String taskId, Engineer engineer, User actor)
            throws TaskNotFoundException, InvalidRoleException {
        if (!actor.canAssignTask()) {
            throw new InvalidRoleException("User does not have permission to assign tasks.");
        }

        Task task = findTask(taskId);
        task.setAssignedEngineer(engineer, actor);
        engineer.addTask(task);
        task.refreshBlockedStatus();
    }

    public void startTask(String taskId, Engineer engineer)
            throws TaskNotFoundException, InvalidRoleException,
            DependencyNotCompletedException, InvalidTaskStateException {
        Task task = findTask(taskId);

        if (!engineer.canWorkOnTask()) {
            throw new InvalidRoleException("User does not have permission to work on tasks.");
        }

        if (task.getAssignedEngineer() == null ||
                !task.getAssignedEngineer().getUID().equals(engineer.getUID())) {
            throw new InvalidRoleException("This engineer is not assigned to the task.");
        }

        if (task.hasUnfinishedDependencies()) {
            task.refreshBlockedStatus();
            task.addHistoryEntry("Start rejected because dependencies are incomplete", engineer);
            throw new DependencyNotCompletedException("Cannot start task because dependencies are incomplete.");
        }

        task.updateStatus(TaskStatus.IN_PROGRESS, engineer);
        inProgressTasks.add(task);
    }

    public void completeTask(String taskId, Engineer engineer)
            throws TaskNotFoundException, InvalidRoleException, InvalidTaskStateException {
        Task task = findTask(taskId);

        if (task.getAssignedEngineer() == null ||
                !task.getAssignedEngineer().getUID().equals(engineer.getUID())) {
            throw new InvalidRoleException("Only the assigned engineer can complete this task.");
        }

        task.updateStatus(TaskStatus.DONE, engineer);
        inProgressTasks.remove(task);

        for (Task current : tasks.values()) {
            current.refreshBlockedStatus();
        }
    }

    public void updateTaskDescription(String taskId, String newDescription, User actor)
            throws TaskNotFoundException, InvalidRoleException {
        if (!actor.canUpdateTask()) {
            throw new InvalidRoleException("User does not have permission to update tasks.");
        }

        Task task = findTask(taskId);
        task.setDescription(newDescription, actor);
    }

    public void addDependency(String taskId, String dependencyId, User actor)
            throws TaskNotFoundException, CircularDependencyException, InvalidRoleException {
        if (!actor.canUpdateTask()) {
            throw new InvalidRoleException("User does not have permission to add dependencies.");
        }

        Task task = findTask(taskId);
        Task dependency = findTask(dependencyId);

        if (task.getTaskId().equals(dependency.getTaskId())) {
            throw new CircularDependencyException("A task cannot depend on itself.");
        }

        if (detectCircularDependency(dependency, task)) {
            throw new CircularDependencyException("Circular dependency detected.");
        }

        task.addDependency(dependency, actor);
    }

    public void removeDependency(String taskId, String dependencyId, User actor)
            throws TaskNotFoundException, InvalidRoleException {
        if (!actor.canUpdateTask()) {
            throw new InvalidRoleException("User does not have permission to remove dependencies.");
        }

        Task task = findTask(taskId);
        Task dependency = findTask(dependencyId);
        task.removeDependency(dependency, actor);
    }

    private boolean detectCircularDependency(Task current, Task target) {
        if (current.getDependencies().contains(target)) {
            return true;
        }

        for (Task dep : current.getDependencies()) {
            if (detectCircularDependency(dep, target)) {
                return true;
            }
        }

        return false;
    }

    public Set<Task> getInProgressTasks() {
        return inProgressTasks;
    }

    public PriorityQueue<Task> getTaskQueue() {
        return new PriorityQueue<>(taskQueue);
    }

    public void printInProgressTasks() {
        System.out.println("In Progress Tasks:");
        for (Task task : inProgressTasks) {
            System.out.println(task);
        }
    }

    public void saveTasksToFile(String filename) throws FilePersistenceException {
        FileManager fileManager = new FileManager();
        StringBuilder builder = new StringBuilder();

        for (Task task : tasks.values()) {
            String engineerId = task.getAssignedEngineer() == null ? "null" : task.getAssignedEngineer().getUID();
            builder.append(task.getTaskId()).append(";");
            builder.append(task.getTitle()).append(";");
            builder.append(task.getDescription().replace(";", ",")).append(";");
            builder.append(task.getPriority()).append(";");
            builder.append(task.getStatus()).append(";");
            builder.append(task.getCategory()).append(";");
            builder.append(task.getDeadline()).append(";");
            builder.append(engineerId).append(System.lineSeparator());
        }

        fileManager.writeFile(filename, builder.toString());
    }

    public void loadTasksFromFile(String filename) throws FilePersistenceException {
        FileManager fileManager = new FileManager();
        String content = fileManager.readFile(filename);

        if (content.isBlank()) {
            return;
        }

        String[] lines = content.split(System.lineSeparator());

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }

            String[] parts = line.split(";");
            if (parts.length < 8) {
                continue;
            }

            Task task = new Task(
                    parts[0],
                    parts[1],
                    parts[2],
                    enums.PriorityLevel.valueOf(parts[3]),
                    enums.TaskCategory.valueOf(parts[5]),
                    LocalDate.parse(parts[6])
            );

            try {
                task.updateStatus(enums.TaskStatus.valueOf(parts[4]),
                        new user.Admin("SYS", "System", "system@local"));
            } catch (InvalidTaskStateException ignored) {
            }

            tasks.put(task.getTaskId(), task);
            taskQueue.offer(task);
        }
    }
}
