package task_management;

import java.time.LocalDateTime;
import user.User;
/**
 * Maintains an immutable record of a single modification made to a specific task.
 * Ensures traceability by tracking what changed, when it occurred, and who performed it.
 */
public class TaskHistoryEntry {
    private final LocalDateTime timestamp;
    private final User performedBy;
    private final String action;
/**
     * Constructs a new history entry.
     *
     * @param timestamp   The exact date and time the action occurred.
     * @param performedBy The user who initiated the action.
     * @param action      A textual description of the modification.
     */
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