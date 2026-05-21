package user;
/**
 * Represents a set of user credentials for system authentication.
 * Binds a specific User instance to a secure password.
 */
public class UserAccount {
    private final User user;     // Objet utilisateur
    private String password;     // Mot de passe associé

    public UserAccount(User user, String password) {
        this.user = user;
        this.password = password;
    }
/**
     * Retrieves the User object associated with this account.
     */
    public User getUser() {
        return user;
    }
/**
     * Validates an input password against the stored password.
     * @param inputPassword The password attempting to authenticate.
     * @return True if the password matches, false otherwise.
     */
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