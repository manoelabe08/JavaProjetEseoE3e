package task_management;

import java.time.LocalDateTime;
import user.User;

public class TaskHistoryEntry {
    private final LocalDateTime timestamp;
    private final User performedBy;
    private final String action;

    public TaskHistoryEntry(LocalDateTime timestamp, User performedBy, String action) {
        this.timestamp = timestamp;
        this.performedBy = performedBy;
        this.action = action;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public String getAction() {
        return action;
    }

    @Override
    public String toString() {
        return timestamp + " | " + performedBy.getName() + " | " + action;
    }
}