package utility;

import task_management.Task;
import task_management.TaskManager;

public class ReportGenerator implements Reportable {
    private final TaskManager taskManager;

    public ReportGenerator(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public String generateReport() {
        StringBuilder builder = new StringBuilder();
        builder.append("=== STRMS TASK REPORT ===").append(System.lineSeparator());

        for (Task task : taskManager.listTasks()) {
            builder.append(task.toString()).append(System.lineSeparator());
        }

        return builder.toString();
    }
}
