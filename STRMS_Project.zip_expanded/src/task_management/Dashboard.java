package task_management;

import enums.TaskStatus;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Dashboard {
    private final TaskManager taskManager;

    public Dashboard(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    public void displayStatistics() {
        Map<TaskStatus, Integer> statusCount = new HashMap<>();
        Map<String, Integer> tasksPerUser = new HashMap<>();
        int overdueTasks = 0;

        for (Task task : taskManager.listTasks()) {
            statusCount.put(task.getStatus(),
                    statusCount.getOrDefault(task.getStatus(), 0) + 1);

            String userName = task.getAssignedEngineer() == null
                    ? "Unassigned"
                    : task.getAssignedEngineer().getName();

            tasksPerUser.put(userName,
                    tasksPerUser.getOrDefault(userName, 0) + 1);

            if (task.getDeadline() != null
                    && task.getDeadline().isBefore(LocalDate.now())
                    && task.getStatus() != TaskStatus.DONE) {
                overdueTasks++;
            }
        }

        System.out.println("=== DASHBOARD ===");

        System.out.println("Tasks by status:");
        for (Map.Entry<TaskStatus, Integer> entry : statusCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Tasks by user:");
        for (Map.Entry<String, Integer> entry : tasksPerUser.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Overdue tasks: " + overdueTasks);
    }
}
