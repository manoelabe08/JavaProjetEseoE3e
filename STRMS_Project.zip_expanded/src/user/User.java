package user;

public abstract class User {
    private final String id;
    private final String name;
    private final String email;

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

    public abstract boolean canCreateTask();
    public abstract boolean canDeleteTask();
    public abstract boolean canAssignTask();
    public abstract boolean canUpdateTask();
    public abstract boolean canGenerateReport();
    public abstract boolean canWorkOnTask();

    @Override
    public String toString() {
        return id + " - " + name + " (" + email + ")";
    }
}
