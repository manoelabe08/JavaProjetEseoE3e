package user;

public class UserAccount {
    private final User user;     // Objet utilisateur
    private String password;     // Mot de passe associé

    public UserAccount(User user, String password) {
        this.user = user;
        this.password = password;
    }

    public User getUser() {
        return user;
    }

    // Vérifie si le mot de passe saisi correspond
    public boolean checkPassword(String inputPassword) {
        return password.equals(inputPassword);
    }

    // Permet de modifier le mot de passe
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

    public String getPassword() {
        return password;
    }
}