package enums;
/**
 * Represents the lifecycle states of a task in the management system.
 * State transitions must follow specific business rules enforced by the controller.
 */
public enum TaskStatus {
    TODO,
    BLOCKED,
    IN_PROGRESS,
    DONE
}
