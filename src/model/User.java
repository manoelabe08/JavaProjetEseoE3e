package model;

public abstract class User {

    // Les attributs — les infos que TOUT utilisateur possède
    private String id;
    private String name;
    private String email;

    // Constructeur — pour créer un utilisateur avec ses infos
    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Méthodes abstraites — chaque rôle décide lui-même
    public abstract boolean canCreateTask(); // Il peut (ou pas) creer une tache
    public abstract boolean canDeleteTask(); // Il peut (ou pas) supprimer une tache
    public abstract boolean canAssignTask(); // Il peut (ou pas) assigner une tache

    // Getters — pour lire les attributs depuis l'extérieur
    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Pour afficher un utilisateur lisiblement
    @Override
    public String toString() {
        return name + " (" + email + ")"; // nous affichons le nom de l'utilisateur et entre parenthese son email
    }
}