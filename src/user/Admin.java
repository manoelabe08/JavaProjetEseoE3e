package user;
/**
 * Represents an Administrator in the system.
 * Admins have full privileges, including creating, deleting, assigning, 
 * and updating tasks, as well as generating system-wide reports.
 */
public class Admin extends User {

    public Admin(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public boolean canCreateTask() {
        return true;
    }

    @Override
    public boolean canDeleteTask() {
        return true;
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
        // Admins manage the system but do not execute the technical tasks.
        return false;
    }
}