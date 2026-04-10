import java.util.ArrayList;
import java.time.LocalDateTime;

public class TaskHistory {
    private String action;
    private User user;
    private LocalDateTime timestamp;

    public TaskHistory(String action, User user, LocalDateTime timestamp) {
        this.action = action;
        this.user = user;
        this.timestamp = timestamp;
    }
}