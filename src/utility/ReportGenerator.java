package utility;

import task_management.Task;
import task_management.TaskManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates detailed text reports for tasks and users currently managed by the system.
 * Implements the Reportable interface.
 */
public class ReportGenerator implements Reportable {
    private final TaskManager taskManager;

    public ReportGenerator(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public String generateReport() {
        StringBuilder report = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        report.append("=========================================\n");
        report.append("       STRMS SYSTEM STATUS REPORT        \n");
        report.append(" Generated on: ").append(dtf.format(LocalDateTime.now())).append("\n");
        report.append("=========================================\n\n");

        report.append("--- TASK SUMMARY ---\n");
        if (taskManager.listTasks().isEmpty()) {
            report.append("No tasks in the system.\n");
        } else {
            for (Task task : taskManager.listTasks()) {
                String assignedTo = task.getAssignedEngineer() != null 
                        ? task.getAssignedEngineer().getName() 
                        : "Unassigned";

                report.append(String.format("[%s] %s - Status: %s - Priority: %s - Assigned to: %s\n",
                        task.getTaskId(),
                        task.getTitle(),
                        task.getStatus(),
                        task.getPriority(),
                        assignedTo));
            }
        }
        
        report.append("\n=========================================\n");
        return report.toString();
    }
}