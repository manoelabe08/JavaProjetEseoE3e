package task_management;

import enums.PriorityLevel;
import enums.TaskCategory;
import enums.TaskStatus;
import exceptions.InvalidTaskStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import user.Admin;
import user.Engineer;
import user.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    private User admin;
    private Engineer engineer;
    private Task task;
    private Task dependency;

    @BeforeEach
    void setUp() {
        admin = new Admin("A1", "Admin", "admin@test.com");
        engineer = new Engineer("E1", "Eng", "eng@test.com");
        task = new Task(
                "T1",
                "Main task",
                "Description",
                PriorityLevel.HIGH,
                TaskCategory.DEVELOPMENT,
                LocalDate.now().plusDays(5)
        );
        dependency = new Task(
                "T2",
                "Dependency",
                "Dependency description",
                PriorityLevel.MEDIUM,
                TaskCategory.TESTING,
                LocalDate.now().plusDays(2)
        );
    }

    @Test
    void shouldInitializeTaskWithTodoStatus() {
        assertEquals("T1", task.getTaskId());
        assertEquals(TaskStatus.TODO, task.getStatus());
        assertTrue(task.getDependencies().isEmpty());
        assertTrue(task.getHistory().isEmpty());
    }

    @Test
    void shouldAssignEngineerAndAddHistoryEntry() {
        task.setAssignedEngineer(engineer, admin);

        assertEquals(engineer, task.getAssignedEngineer());
        assertEquals(1, task.getHistory().size());
        assertTrue(task.getHistory().get(0).toString().contains("Assigned to engineer"));
    }

    @Test
    void shouldAddDependencyAndRefreshBlockedStatus() {
        task.addDependency(dependency, admin);

        assertEquals(1, task.getDependencies().size());
        assertTrue(task.hasUnfinishedDependencies());
        assertEquals(TaskStatus.BLOCKED, task.getStatus());
    }

    @Test
    void shouldReturnToTodoWhenDependencyIsDone() throws InvalidTaskStateException {
        task.addDependency(dependency, admin);
        dependency.updateStatus(TaskStatus.DONE, admin);

        task.refreshBlockedStatus();

        assertEquals(TaskStatus.TODO, task.getStatus());
    }

    @Test
    void shouldThrowWhenStartingTaskWithUnfinishedDependencies() {
        task.addDependency(dependency, admin);

        InvalidTaskStateException ex = assertThrows(
                InvalidTaskStateException.class,
                () -> task.updateStatus(TaskStatus.IN_PROGRESS, admin)
        );

        assertTrue(ex.getMessage().contains("dependencies"));
    }

    @Test
    void shouldNotAllowDoneTaskToReturnToAnotherState() throws InvalidTaskStateException {
        task.updateStatus(TaskStatus.DONE, admin);

        InvalidTaskStateException ex = assertThrows(
                InvalidTaskStateException.class,
                () -> task.updateStatus(TaskStatus.TODO, admin)
        );

        assertTrue(ex.getMessage().contains("DONE"));
    }

    @Test
    void shouldAddHistoryEntryWhenStatusChanges() throws InvalidTaskStateException {
        task.updateStatus(TaskStatus.IN_PROGRESS, admin);

        assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        assertEquals(1, task.getHistory().size());
        assertTrue(task.getHistory().get(0).toString().contains("Status changed to"));
    }

    @Test
    void shouldCompareTasksByPriority() {
        Task high = new Task(
                "T3", "High", "Desc",
                PriorityLevel.HIGH, TaskCategory.DEVELOPMENT, LocalDate.now().plusDays(1)
        );
        Task low = new Task(
                "T4", "Low", "Desc",
                PriorityLevel.LOW, TaskCategory.DEVELOPMENT, LocalDate.now().plusDays(1)
        );

        assertTrue(high.compareTo(low) < 0 || low.compareTo(high) > 0);
    }

    @Test
    void shouldClearHistory() {
        task.addHistoryEntry("Manual action", admin);
        assertFalse(task.getHistory().isEmpty());

        task.clearHistory();

        assertTrue(task.getHistory().isEmpty());
    }
}