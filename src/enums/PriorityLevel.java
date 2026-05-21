package enums;
/**
 * Defines the urgency and importance of a task within the system.
 * Each level is associated with an integer value to facilitate priority-based 
 * scheduling and comparisons in queues.
 */
public enum PriorityLevel {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int value;

    PriorityLevel(int value) {
        this.value = value;
    }
/**
     * Gets the numeric weight of the priority level.
     * @return the integer priority value (higher means more urgent)
     */
    public int getValue() {
        return value;
    }
}
