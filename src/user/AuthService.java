package user;

import exceptions.FilePersistenceException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * Service responsible for user authentication and account management.
 * Handles the persistence of user credentials to a text file.
 */
public class AuthService {
    private final List<UserAccount> accounts;   // Liste des comptes chargés

    public AuthService() {
        this.accounts = new ArrayList<>();
    }
/**
     * Registers a new user account in memory and persists it to the users file.
     *
     * @param user     The User object (Admin, Manager, or Engineer).
     * @param password The associated password.
     */
    // Ajoute un compte manuellement
    public void addAccount(User user, String password) {
        accounts.add(new UserAccount(user, password));
    }
/**
     * Attempts to authenticate a user using their unique ID and password.
     *
     * @param uid      The user identifier.
     * @param password The provided password.
     * @return The authenticated User object, or null if authentication fails.
     */
    // Vérifie l'identifiant et le mot de passe
    public User login(String id, String password) {
        for (UserAccount account : accounts) {
            if (account.getUser().getUID().equals(id) && account.checkPassword(password)) {
                return account.getUser();
            }
        }
        return null;
    }
/**
     * Retrieves all registered user accounts.
     * @return A list of UserAccount objects.
     */
    // Retourne tous les comptes chargés
    public List<UserAccount> getAllAccounts() {
        return accounts;
    }
/**
     * Loads user accounts from the storage file into memory at startup.
     */
    // Charge les comptes depuis un fichier texte
    public void loadAccountsFromFile(String filePath) throws FilePersistenceException {
        accounts.clear(); // on vide l'ancienne liste avant de recharger

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Ignore les lignes vides
                if (line.isEmpty()) {
                    continue;
                }

                String[] parts = line.split(";");

                // On attend exactement 5 champs
                if (parts.length != 5) {
                    continue;
                }

                String id = parts[0].trim();
                String name = parts[1].trim();
                String email = parts[2].trim();
                String password = parts[3].trim();
                String role = parts[4].trim();

                User user;

                // Création du bon type d'utilisateur selon le rôle
                switch (role) {
                    case "Admin":
                        user = new Admin(id, name, email);
                        break;
                    case "Manager":
                        user = new Manager(id, name, email);
                        break;
                    case "Engineer":
                        user = new Engineer(id, name, email);
                        break;
                    default:
                        continue; // rôle inconnu, on ignore la ligne
                }

                // Ajout du compte dans la liste
                accounts.add(new UserAccount(user, password));
            }
        } catch (IOException e) {
            throw new FilePersistenceException("Error loading users: " + e.getMessage());
        }
    }
/**
     * Persists all active accounts to the defined users file.
     * Format: UID;Role;Name;Email;Password
     */
    // Sauvegarde les comptes dans un fichier texte
    public void saveAccountsToFile(String filePath) throws FilePersistenceException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (UserAccount account : accounts) {
                User user = account.getUser();
                String role = user.getClass().getSimpleName();

                writer.write(user.getUID() + ";"
                        + user.getName() + ";"
                        + user.getEmail() + ";"
                        + account.getPassword() + ";"
                        + role);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new FilePersistenceException("Error saving users: " + e.getMessage());
        }
    }

    // Modifie le mot de passe si l'ancien est correct
    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        for (UserAccount account : accounts) {
            if (account.getUser().getUID().equals(userId) && account.checkPassword(oldPassword)) {
                account.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }
}