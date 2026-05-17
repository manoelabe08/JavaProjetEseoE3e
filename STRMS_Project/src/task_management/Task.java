package task_management;

import enums.PriorityLevel;
import enums.TaskCategory;
import enums.TaskStatus;
import exceptions.InvalidTaskStateException;
import user.Engineer;
import user.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task implements Comparable<Task> {
    private final String taskId;
    private String title;
    private String description;
    private PriorityLevel priority;
    private TaskStatus status;
    private TaskCategory category;
    private LocalDate deadline;
    private Engineer assignedEngineer;
    private final List<Task> dependencies;
    private final List<TaskHistoryEntry> history;

    public Task(String taskId, String title, String description,
                PriorityLevel priority, TaskCategory category, LocalDate deadline) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.priority = priority;
        this.category = category;
        this.deadline = deadline;
        this.status = TaskStatus.TODO;
        this.dependencies = new ArrayList<>();
        this.history = new ArrayList<>();
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public TaskCategory getCategory() {
        return category;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public Engineer getAssignedEngineer() {
        return assignedEngineer;
    }

    public void setAssignedEngineer(Engineer assignedEngineer, User user) {
        this.assignedEngineer = assignedEngineer;
        addHistoryEntry("Assigned to engineer " + assignedEngineer.getName(), user);
    }

    public List<Task> getDependencies() {
        return dependencies;
    }

    public List<TaskHistoryEntry> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void addDependency(Task dependency, User user) {
        if (!dependencies.contains(dependency)) {
            dependencies.add(dependency);
            refreshBlockedStatus();
            addHistoryEntry("Dependency added: " + dependency.getTaskId(), user);
        }
    }

    public boolean hasUnfinishedDependencies() {
        for (Task dependency : dependencies) {
            if (dependency.getStatus() != TaskStatus.DONE) {
                return true;
            }
        }
        return false;
    }

    public void refreshBlockedStatus() {
        if (status != TaskStatus.DONE) {
            if (hasUnfinishedDependencies()) {
                status = TaskStatus.BLOCKED;
            } else if (status == TaskStatus.BLOCKED) {
                status = TaskStatus.TODO;
            }
        }
    }

    public void updateStatus(TaskStatus newStatus, User user) throws InvalidTaskStateException {
        if (status == TaskStatus.DONE && newStatus != TaskStatus.DONE) {
            throw new InvalidTaskStateException("A task in DONE state cannot return to another state.");
        }

        if (newStatus == TaskStatus.IN_PROGRESS && hasUnfinishedDependencies()) {
            throw new InvalidTaskStateException("Task cannot start because dependencies are not completed.");
        }

        this.status = newStatus;
        addHistoryEntry("Status changed to " + newStatus, user);
    }

    public void addHistoryEntry(String action, User user) {
        history.add(new TaskHistoryEntry(LocalDateTime.now(), user, action));
    }

    public void addLoadedHistoryEntry(TaskHistoryEntry entry) {
        history.add(entry);
    }

    public void clearHistory() {
        history.clear();
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(other.priority.getValue(), this.priority.getValue());
    }

    @Override
    public String toString() {
        return taskId + " | " + title + " | " + priority + " | " + status;
    }
}