import enums.NotificationType;
import enums.PriorityLevel;
import enums.TaskCategory;
import task_management.Dashboard;
import task_management.Task;
import task_management.TaskManager;
import utility.NotificationManager;
import utility.ReportGenerator;
import user.Admin;
import user.Engineer;
import user.Manager;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        try {
            TaskManager managerSystem = new TaskManager();

            Admin admin = new Admin("A1", "Alice", "alice@strms.com");
            Manager manager = new Manager("M1", "Bob", "bob@strms.com");
            Engineer engineer = new Engineer("E1", "Charlie", "charlie@strms.com");

            managerSystem.addUser(admin);
            managerSystem.addUser(manager);
            managerSystem.addUser(engineer);

            Task task1 = new Task(
                    "T1",
                    "Implement login page",
                    "Create authentication UI",
                    PriorityLevel.HIGH,
                    TaskCategory.FEATURE,
                    LocalDate.now().plusDays(5)
            );

            Task task2 = new Task(
                    "T2",
                    "Create database schema",
                    "Prepare tables for authentication",
                    PriorityLevel.CRITICAL,
                    TaskCategory.FEATURE,
                    LocalDate.now().plusDays(7)
            );

            managerSystem.addTask(task1, admin);
            managerSystem.addTask(task2, admin);

            managerSystem.addDependency("T2", "T1", manager);
            managerSystem.assignTask("T1", engineer, manager);
            managerSystem.startTask("T1", engineer);
            managerSystem.completeTask("T1", engineer);

            managerSystem.assignTask("T2", engineer, manager);

            NotificationManager notificationManager = new NotificationManager();
            notificationManager.sendNotification(engineer, "Task T2 is now ready.", NotificationType.CONSOLE);

            Dashboard dashboard = new Dashboard(managerSystem);
            dashboard.displayStatistics();

            ReportGenerator reportGenerator = new ReportGenerator(managerSystem);
            System.out.println(reportGenerator.generateReport());

            managerSystem.saveTasksToFile("data/tasks.txt");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
