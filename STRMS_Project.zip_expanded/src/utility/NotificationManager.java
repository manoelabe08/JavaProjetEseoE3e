package utility;

import enums.NotificationType;
import user.User;

public class NotificationManager {

    public void sendNotification(User user, String message, NotificationType type) {
        switch (type) {
            case EMAIL:
                System.out.println("[EMAIL] To: " + user.getEmail() + " | " + message);
                break;
            case SMS:
                System.out.println("[SMS] To: " + user.getName() + " | " + message);
                break;
            case CONSOLE:
                System.out.println("[CONSOLE] " + user.getName() + " -> " + message);
                break;
            default:
                System.out.println("[UNKNOWN] " + message);
        }
    }
}
