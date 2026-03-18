
import java.time.*;

public class Task {
    private TaskCategory category;
    private TaskStatus status;
    private PriorityLevel priority;
    private String engineer;
    private LocalDate creationDate;


    public Task(TaskCategory category, TaskStatus status, PriorityLevel priority, String engineer) {
        this.category = category;
        this.status = status;
        this.priority = priority;
        this.engineer = engineer;
        
    }

    public TaskCategory getCategory() {
        return category;
    }

    public String getEngineer() {
        return engineer;
    }

    public PriorityLevel getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCategory(TaskCategory category) {
        this.category = category;
    }

    public void setEngineer(String engineer) {
        this.engineer = engineer;
    }

    public void setPriority(PriorityLevel priority) {
        this.priority = priority;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }
}

