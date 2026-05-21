package gui;

import user.AuthService;
import user.User;

import javax.swing.*;
import java.awt.*;

public class LoginDialog extends JDialog {

    private User authenticatedUser; // utilisateur connecté après succès

    public LoginDialog(JFrame parent, AuthService authService) {
        super(parent, "Connexion", true);
        this.authenticatedUser = null;

        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField idField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Se connecter");
        JButton cancelButton = new JButton("Annuler");

        formPanel.add(new JLabel("User ID :"));
        formPanel.add(idField);
        formPanel.add(new JLabel("Password :"));
        formPanel.add(passwordField);
        formPanel.add(loginButton);
        formPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> {
            String id = idField.getText().trim();
            String password = new String(passwordField.getPassword());

            // Vérification des champs vides
            if (id.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Veuillez remplir l'identifiant et le mot de passe.",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            // Recherche de l'utilisateur dans le fichier déjà chargé
            User user = authService.login(id, password);

            if (user == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Identifiant ou mot de passe incorrect.",
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE
                );
            } else {
                authenticatedUser = user;
                dispose();
            }
        });

        cancelButton.addActionListener(e -> {
            authenticatedUser = null;
            dispose();
        });

        getRootPane().setDefaultButton(loginButton);
        pack();
        setResizable(false);
        setLocationRelativeTo(parent);
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}