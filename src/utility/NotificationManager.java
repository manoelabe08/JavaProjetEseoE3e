package utility;

import enums.NotificationType;
import task_management.Task;
import user.User;

/**
 * Responsible for handling and dispatching notifications to system users.
 * Supports multiple notification channels based on the NotificationType enum.
 */
public class NotificationManager {

    /**
     * Dispatches a notification to a specific user regarding a task update.
     * * @param user    The user receiving the notification.
     * @param task    The task associated with the event.
     * @param type    The delivery method (EMAIL, SMS, CONSOLE).
     * @param message The content of the notification.
     */
    public void sendNotification(User user, Task task, NotificationType type, String message) {
        String formattedAlert = String.format("ALERT [%s] to %s: Task %s - %s", 
                type.name(), 
                user.getName(), 
                task.getTaskId(), 
                message);

        // Simulation of sending the notification. 
        // In a real system, this would integrate with Email/SMS APIs.
        System.out.println(formattedAlert);
    }
}