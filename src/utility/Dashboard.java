package utility;

import enums.TaskStatus;
import task_management.Task;
import task_management.TaskManager;

import java.time.LocalDate;

/**
 * Calculates and displays real-time statistics regarding system tasks.
 * Provides a high-level overview including task distribution and overdue warnings.
 */
public class Dashboard {
    private final TaskManager taskManager;

    public Dashboard(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    /**
     * Computes task metrics and displays the dashboard directly in the console.
     */
    public void displayDashboard() {
        int totalTasks = 0;
        int todoCount = 0;
        int inProgressCount = 0;
        int blockedCount = 0;
        int doneCount = 0;
        int overdueCount = 0;

        LocalDate today = LocalDate.now();

        for (Task task : taskManager.listTasks()) {
            totalTasks++;
            
            switch (task.getStatus()) {
                case TODO -> todoCount++;
                case IN_PROGRESS -> inProgressCount++;
                case BLOCKED -> blockedCount++;
                case DONE -> doneCount++;
            }

            // Check if the task is strictly overdue and not yet completed
            if (task.getStatus() != TaskStatus.DONE && task.getDeadline().isBefore(today)) {
                overdueCount++;
            }
        }

        System.out.println("\n========== STRMS DASHBOARD ==========");
        System.out.println("Total Tasks Managed : " + totalTasks);
        System.out.println("-------------------------------------");
        System.out.println(" TODO        : " + todoCount);
        System.out.println(" BLOCKED     : " + blockedCount);
        System.out.println(" IN PROGRESS : " + inProgressCount);
        System.out.println(" DONE        : " + doneCount);
        System.out.println("-------------------------------------");
        System.out.println(" OVERDUE TASKS: " + overdueCount);
        System.out.println("=====================================\n");
    }
}