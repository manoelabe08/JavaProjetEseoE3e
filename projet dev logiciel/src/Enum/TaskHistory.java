package Enum;

import user.User;

import java.time.LocalDateTime;

public class TaskHistory {
	@SuppressWarnings("unused")
	private String action;
    @SuppressWarnings("unused")
	private User user;
    @SuppressWarnings("unused")
	private LocalDateTime timestamp;

    public TaskHistory(String action, User user, LocalDateTime timestamp) {
        this.action = action;
        this.user = user;
        this.timestamp = timestamp;
    }
}