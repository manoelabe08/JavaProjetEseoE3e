package task_management;

import enums.PriorityLevel;
import enums.TaskCategory;
import enums.TaskStatus;
import exceptions.CircularDependencyException;
import exceptions.DependencyNotCompletedException;
import exceptions.DuplicateTaskException;
import exceptions.InvalidRoleException;
import exceptions.InvalidTaskStateException;
import exceptions.TaskNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.Admin;
import user.Engineer;
import user.Manager;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private TaskManager taskManager;
    private Admin admin;
    private Manager manager;
    private Engineer engineer;
    private Engineer otherEngineer;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        taskManager = new TaskManager();

        admin = new Admin("A1", "Admin", "admin@test.com");
        manager = new Manager("M1", "Manager", "manager@test.com");
        engineer = new Engineer("E1", "Engineer 1", "eng1@test.com");
        otherEngineer = new Engineer("E2", "Engineer 2", "eng2@test.com");

        taskManager.addUser(admin);
        taskManager.addUser(manager);
        taskManager.addUser(engineer);
        taskManager.addUser(otherEngineer);

        task1 = new Task("T1", "Task 1", "Desc 1",
                PriorityLevel.HIGH, TaskCategory.DEVELOPMENT, LocalDate.now().plusDays(3));
        task2 = new Task("T2", "Task 2", "Desc 2",
                PriorityLevel.MEDIUM, TaskCategory.TESTING, LocalDate.now().plusDays(5));
    }

    @Test
    void adminShouldAddTask() throws Exception {
        taskManager.addTask(task1, admin);

        assertEquals(task1, taskManager.findTask("T1"));
        assertEquals(1, taskManager.listTasks().size());
    }

    @Test
    void nonAuthorizedUserShouldNotAddTask() {
        assertThrows(InvalidRoleException.class, () -> taskManager.addTask(task1, manager));
        assertThrows(InvalidRoleException.class, () -> taskManager.addTask(task1, engineer));
    }

    @Test
    void shouldRejectDuplicateTask() throws Exception {
        taskManager.addTask(task1, admin);

        assertThrows(DuplicateTaskException.class, () -> taskManager.addTask(task1, admin));
    }

    @Test
    void managerShouldAssignTask() throws Exception {
        taskManager.addTask(task1, admin);

        taskManager.assignTask("T1", engineer, manager);

        assertEquals(engineer, taskManager.findTask("T1").getAssignedEngineer());
        assertEquals(1, engineer.getAssignedTasks().size());
    }

    @Test
    void unauthorizedUserShouldNotAssignTask() throws Exception {
        taskManager.addTask(task1, admin);

        assertThrows(InvalidRoleException.class, () -> taskManager.assignTask("T1", engineer, engineer));
    }

    @Test
    void assignedEngineerShouldStartTask() throws Exception {
        taskManager.addTask(task1, admin);
        taskManager.assignTask("T1", engineer, admin);

        taskManager.startTask("T1", engineer);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.findTask("T1").getStatus());
        assertTrue(taskManager.getInProgressTasks().contains(task1));
    }

    @Test
    void unassignedEngineerShouldNotStartTask() throws Exception {
        taskManager.addTask(task1, admin);
        taskManager.assignTask("T1", engineer, admin);

        assertThrows(InvalidRoleException.class, () -> taskManager.startTask("T1", otherEngineer));
    }

    @Test
    void shouldNotStartTaskWhenDependencyNotCompleted() throws Exception {
        taskManager.addTask(task1, admin);
        taskManager.addTask(task2, admin);
        taskManager.assignTask("T1", engineer, admin);
        taskManager.addDependency("T1", "T2", admin);

        assertThrows(DependencyNotCompletedException.class, () -> taskManager.startTask("T1", engineer));
        assertEquals(TaskStatus.BLOCKED, taskManager.findTask("T1").getStatus());
    }

    @Test
    void assignedEngineerShouldCompleteTask() throws Exception {
        taskManager.addTask(task1, admin);
        taskManager.assignTask("T1", engineer, admin);
        taskManager.startTask("T1", engineer);

        taskManager.completeTask("T1", engineer);

        assertEquals(TaskStatus.DONE, taskManager.findTask("T1").getStatus());
        assertFalse(taskManager.getInProgressTasks().contains(task1));
    }

    @Test
    void shouldAddDependency() throws Exception {
        taskManager.addTask(task1, admin);
        taskManager.addTask(task2, admin);

        taskManager.addDependency("T1", "T2", admin);

        assertEquals(1, taskManager.findTask("T1").getDependencies().size());
    }

    @Test
    void shouldRejectSelfDependency() throws Exception {
        taskManager.addTask(task1, admin);

        assertThrows(CircularDependencyException.class,
                () -> taskManager.addDependency("T1", "T1", admin));
    }

    @Test
    void shouldRejectCircularDependency() throws Exception {
        Task task3 = new Task("T3", "Task 3", "Desc 3",
                PriorityLevel.LOW, TaskCategory.DOCUMENTATION, LocalDate.now().plusDays(6));

        taskManager.addTask(task1, admin);
        taskManager.addTask(task2, admin);
        taskManager.addTask(task3, admin);

        taskManager.addDependency("T1", "T2", admin);
        taskManager.addDependency("T2", "T3", admin);

        assertThrows(CircularDependencyException.class,
                () -> taskManager.addDependency("T3", "T1", admin));
    }

    @Test
    void shouldThrowWhenTaskNotFound() {
        assertThrows(TaskNotFoundException.class, () -> taskManager.findTask("UNKNOWN"));
    }
}