package user;
/**
 * Represents a Manager in the system.
 * Managers are responsible for assigning tasks to engineers, monitoring progress, 
 * updating task details, and generating reports. They cannot create or delete tasks.
 */
public class Manager extends User {

    public Manager(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public boolean canCreateTask() {
        return false;
    }

    @Override
    public boolean canDeleteTask() {
        return false;
    }

    @Override
    public boolean canAssignTask() {
        return true;
    }

    @Override
    public boolean canUpdateTask() {
        return true;
    }

    @Override
    public boolean canGenerateReport() {
        return true;
    }

    @Override
    public boolean canWorkOnTask() {
        return false;
    }
}