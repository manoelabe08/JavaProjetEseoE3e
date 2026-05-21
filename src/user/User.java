package user;
/**
 * An abstract superclass representing all user roles within the STRMS system.
 * It encapsulates common user attributes and defines abstract methods to enforce 
 * role-based access control (RBAC) across the application.
 */
public abstract class User {
    private final String id;
    private final String name;
    private final String email;
/**
     * Constructs a new User.
     *
     * @param uid   The unique identifier for the user.
     * @param name  The full name of the user.
     * @param email The contact email address.
     */
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getUID() {
        return id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
// Abstract methods defining role-based permissions

    /** Checks if the user has permission to create new tasks. */
    public abstract boolean canCreateTask();
    /** Checks if the user has permission to delete existing tasks. */
    public abstract boolean canDeleteTask();
    /** Checks if the user has permission to assign tasks. */
    public abstract boolean canAssignTask();
    /** Checks if the user has permission to modify task details or add dependencies. */
    public abstract boolean canUpdateTask();
    /** Checks if the user has permission to generate reports. */
    public abstract boolean canGenerateReport();
    /** Checks if the user has permission to execute and complete tasks. */
    public abstract boolean canWorkOnTask();

    @Override
    public String toString() {
        return id + " - " + name + " (" + email + ")";
    }
}