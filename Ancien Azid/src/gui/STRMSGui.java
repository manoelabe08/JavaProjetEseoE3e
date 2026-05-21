package gui;

import enums.PriorityLevel;
import enums.TaskCategory;
import exceptions.CircularDependencyException;
import exceptions.DependencyNotCompletedException;
import exceptions.DuplicateTaskException;
import exceptions.FilePersistenceException;
import exceptions.InvalidRoleException;
import exceptions.InvalidTaskStateException;
import exceptions.TaskNotFoundException;
import task_management.Task;
import task_management.TaskHistoryEntry;
import task_management.TaskManager;
import user.Admin;
import user.AuthService;
import user.Engineer;
import user.Manager;
import user.User;
import user.UserAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Fenêtre principale de l'application STRMS.
 * Cette version gère :
 * - les tâches
 * - l'historique
 * - les utilisateurs
 * - le chargement/sauvegarde des utilisateurs depuis un fichier txt
 * - le changement de mot de passe
 */
public class STRMSGui extends JFrame {

    // Gestionnaire métier des tâches
    private final TaskManager taskManager;

    // Service d'authentification (comptes + mots de passe)
    private final AuthService authService;

    // Liste locale des utilisateurs visibles dans l'application
    private final List<User> localUsers;

    // Utilisateur connecté
    private final User currentUser;

    // Composants liés au tableau des tâches
    private final DefaultTableModel taskTableModel;
    private final JTable taskTable;

    // Zone texte pour afficher l'historique d'une tâche
    private final JTextArea historyArea;

    // ComboBox utilisées dans l'interface
    private final JComboBox<String> taskComboBox;
    private final JComboBox<String> dependencyTaskComboBox;
    private final JComboBox<String> dependencyOnComboBox;
    private final JComboBox<String> engineerComboBox;
    private final JComboBox<String> startTaskComboBox;
    private final JComboBox<String> completeTaskComboBox;
    private final JComboBox<String> historyTaskComboBox;

    // Onglets principaux
    private final JTabbedPane tabs;

    /**
     * Constructeur principal.
     * @param loggedUser utilisateur connecté
     * @param authService service d'authentification déjà chargé
     */
    public STRMSGui(User loggedUser, AuthService authService) {
        this.taskManager = new TaskManager();
        this.authService = authService;
        this.localUsers = new ArrayList<>();
        this.currentUser = loggedUser;

        setTitle("STRMS - Smart Task & Resource Management System - Connected: " + currentUser.getName());
        setSize(1200, 720);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();

        // Modèle du tableau des tâches
        taskTableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Priority", "Category", "Status", "Deadline", "Engineer"}, 0
        );
        taskTable = new JTable(taskTableModel);

        // Zone historique
        historyArea = new JTextArea();
        historyArea.setEditable(false);

        // Initialisation des ComboBox
        taskComboBox = new JComboBox<>();
        dependencyTaskComboBox = new JComboBox<>();
        dependencyOnComboBox = new JComboBox<>();
        engineerComboBox = new JComboBox<>();
        startTaskComboBox = new JComboBox<>();
        completeTaskComboBox = new JComboBox<>();
        historyTaskComboBox = new JComboBox<>();

        // Charge les utilisateurs depuis AuthService dans l'interface et dans TaskManager
        loadUsersFromAuthService();

        // Création des onglets
        tabs.add("Users", createUsersPanel());
        tabs.add("Tasks", createTasksPanel());
        tabs.add("Assign", createAssignPanel());
        tabs.add("Dependencies", createDependencyPanel());
        tabs.add("Execution", createExecutionPanel());
        tabs.add("History", createHistoryPanel());
        tabs.add("Dashboard", createDashboardPanel());
        tabs.add("Persistence", createPersistencePanel());

        // Bouton logout
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JPanel container = new JPanel(new BorderLayout());
        container.add(tabs, BorderLayout.CENTER);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topRightPanel.add(logoutButton);
        container.add(topRightPanel, BorderLayout.NORTH);

        setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);

        refreshAllComboBoxes();
        refreshTaskTable();
        applyPermissions();
    }

    /**
     * Recharge les utilisateurs depuis AuthService
     * puis les injecte dans localUsers et TaskManager.
     */
    private void loadUsersFromAuthService() {
        localUsers.clear();

        for (UserAccount account : authService.getAllAccounts()) {
            User user = account.getUser();
            localUsers.add(user);
            taskManager.addUser(user);
        }
    }

    /**
     * Onglet gestion des utilisateurs.
     * Permet :
     * - ajouter un utilisateur
     * - sauvegarder les users dans un fichier
     * - charger les users depuis un fichier
     * - changer le mot de passe de l'utilisateur connecté
     */
    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Partie formulaire d'ajout
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField passwordField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Manager", "Engineer"});
        JButton addButton = new JButton("Add User");

        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.add(new JLabel("User ID:"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Password:"));
        form.add(passwordField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);
        form.add(new JLabel(""));
        form.add(addButton);

        // Zone d'affichage des utilisateurs
        JTextArea usersArea = new JTextArea();
        usersArea.setEditable(false);

        // Boutons supplémentaires
        JButton saveUsersButton = new JButton("Save Users");
        JButton loadUsersButton = new JButton("Load Users");
        JButton changePasswordButton = new JButton("Change My Password");

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionsPanel.add(saveUsersButton);
        actionsPanel.add(loadUsersButton);
        actionsPanel.add(changePasswordButton);

        // Ajouter un utilisateur
        addButton.addActionListener(e -> {
            if (!currentUser.canCreateTask()) {
                showError("You do not have permission to add users.");
                return;
            }

            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = passwordField.getText().trim();
            String role = (String) roleBox.getSelectedItem();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showError("Please fill all user fields.");
                return;
            }

            User user;
            switch (role) {
                case "Admin":
                    user = new Admin(id, name, email);
                    break;
                case "Manager":
                    user = new Manager(id, name, email);
                    break;
                default:
                    user = new Engineer(id, name, email);
                    break;
            }

            localUsers.add(user);
            taskManager.addUser(user);
            authService.addAccount(user, password);

            refreshUsersArea(usersArea);
            refreshAllComboBoxes();

            idField.setText("");
            nameField.setText("");
            emailField.setText("");
            passwordField.setText("");
        });

        // Sauvegarder les utilisateurs dans un fichier texte
        saveUsersButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save users file");
            chooser.setSelectedFile(new File("data/users.txt"));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    authService.saveAccountsToFile(chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Users saved successfully.");
                } catch (FilePersistenceException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        // Charger les utilisateurs depuis un fichier externe
        loadUsersButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load users file");
            chooser.setSelectedFile(new File("data/users.txt"));

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    authService.loadAccountsFromFile(chooser.getSelectedFile().getAbsolutePath());

                    // Recharge localUsers et TaskManager à partir des comptes chargés
                    loadUsersFromAuthService();

                    refreshUsersArea(usersArea);
                    refreshAllComboBoxes();

                    JOptionPane.showMessageDialog(this, "Users loaded successfully.");
                } catch (FilePersistenceException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        // Modifier le mot de passe de l'utilisateur connecté
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());

        panel.add(form, BorderLayout.NORTH);
        panel.add(actionsPanel, BorderLayout.CENTER);
        panel.add(new JScrollPane(usersArea), BorderLayout.SOUTH);

        refreshUsersArea(usersArea);
        return panel;
    }

    /**
     * Ouvre une petite boîte de dialogue pour changer le mot de passe.
     */
    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JPasswordField oldPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();

        panel.add(new JLabel("Old password:"));
        panel.add(oldPasswordField);
        panel.add(new JLabel("New password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm password:"));
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String oldPassword = new String(oldPasswordField.getPassword()).trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("All password fields are required.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("New password and confirmation do not match.");
                return;
            }

            boolean success = authService.changePassword(currentUser.getUID(), oldPassword, newPassword);

            if (success) {
                JOptionPane.showMessageDialog(this, "Password changed successfully.");
            } else {
                showError("Old password is incorrect.");
            }
        }
    }

    /**
     * Onglet de création et affichage des tâches.
     */
    private JPanel createTasksPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JComboBox<PriorityLevel> priorityBox = new JComboBox<>(PriorityLevel.values());
        JComboBox<TaskCategory> categoryBox = new JComboBox<>(TaskCategory.values());
        JTextField deadlineField = new JTextField("2026-06-10");
        JButton addButton = new JButton("Create Task");

        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.add(new JLabel("Task ID:"));
        form.add(idField);
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("Description:"));
        form.add(descField);
        form.add(new JLabel("Priority:"));
        form.add(priorityBox);
        form.add(new JLabel("Category:"));
        form.add(categoryBox);
        form.add(new JLabel("Deadline (YYYY-MM-DD):"));
        form.add(deadlineField);
        form.add(new JLabel(""));
        form.add(addButton);

        addButton.addActionListener(e -> {
            if (!currentUser.canCreateTask()) {
                showError("You do not have permission to create tasks.");
                return;
            }

            try {
                Task task = new Task(
                        idField.getText().trim(),
                        titleField.getText().trim(),
                        descField.getText().trim(),
                        (PriorityLevel) priorityBox.getSelectedItem(),
                        (TaskCategory) categoryBox.getSelectedItem(),
                        LocalDate.parse(deadlineField.getText().trim())
                );

                taskManager.addTask(task, currentUser);
                refreshTaskTable();
                refreshAllComboBoxes();

                idField.setText("");
                titleField.setText("");
                descField.setText("");
            } catch (DuplicateTaskException | InvalidRoleException ex) {
                showError(ex.getMessage());
            } catch (Exception ex) {
                showError("Invalid input: " + ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Onglet d'assignation.
     */
    private JPanel createAssignPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton assignButton = new JButton("Assign Task");

        panel.add(new JLabel("Task:"));
        panel.add(taskComboBox);
        panel.add(new JLabel("Engineer:"));
        panel.add(engineerComboBox);
        panel.add(new JLabel(""));
        panel.add(assignButton);

        assignButton.addActionListener(e -> {
            if (!currentUser.canAssignTask()) {
                showError("You do not have permission to assign tasks.");
                return;
            }

            try {
                String taskId = (String) taskComboBox.getSelectedItem();
                Engineer engineer = getEngineerById((String) engineerComboBox.getSelectedItem());

                if (taskId == null || engineer == null) {
                    showError("Please select a task and an engineer.");
                    return;
                }

                taskManager.assignTask(taskId, engineer, currentUser);
                refreshTaskTable();
                JOptionPane.showMessageDialog(this, "Task assigned successfully.");
            } catch (TaskNotFoundException | InvalidRoleException ex) {
                showError(ex.getMessage());
            }
        });

        return panel;
    }

    /**
     * Onglet de dépendances.
     */
    private JPanel createDependencyPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton addDependencyButton = new JButton("Add Dependency");

        panel.add(new JLabel("Task:"));
        panel.add(dependencyTaskComboBox);
        panel.add(new JLabel("Depends on:"));
        panel.add(dependencyOnComboBox);
        panel.add(new JLabel(""));
        panel.add(addDependencyButton);

        addDependencyButton.addActionListener(e -> {
            if (!currentUser.canAssignTask()) {
                showError("You do not have permission to manage dependencies.");
                return;
            }

            try {
                String taskId = (String) dependencyTaskComboBox.getSelectedItem();
                String dependencyId = (String) dependencyOnComboBox.getSelectedItem();

                if (taskId == null || dependencyId == null) {
                    showError("Please select both tasks.");
                    return;
                }

                taskManager.addDependency(taskId, dependencyId, currentUser);
                refreshTaskTable();
                JOptionPane.showMessageDialog(this, "Dependency added.");
            } catch (TaskNotFoundException | CircularDependencyException | InvalidRoleException ex) {
                showError(ex.getMessage());
            }
        });

        return panel;
    }

    /**
     * Onglet d'exécution.
     */
    private JPanel createExecutionPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton startButton = new JButton("Start Task");
        JButton completeButton = new JButton("Complete Task");

        panel.add(new JLabel("Task to start:"));
        panel.add(startTaskComboBox);
        panel.add(new JLabel(""));
        panel.add(startButton);

        panel.add(new JLabel("Task to complete:"));
        panel.add(completeTaskComboBox);
        panel.add(new JLabel(""));
        panel.add(completeButton);

        startButton.addActionListener(e -> {
            if (!currentUser.canWorkOnTask()) {
                showError("You do not have permission to start tasks.");
                return;
            }

            try {
                String taskId = (String) startTaskComboBox.getSelectedItem();
                Task task = taskManager.findTask(taskId);
                Engineer engineer = task.getAssignedEngineer();

                if (engineer == null) {
                    showError("No engineer assigned to this task.");
                    return;
                }

                if (!engineer.getUID().equals(currentUser.getUID())) {
                    showError("You can only start your own assigned tasks.");
                    return;
                }

                taskManager.startTask(taskId, engineer);
                refreshTaskTable();
                JOptionPane.showMessageDialog(this, "Task started.");
            } catch (TaskNotFoundException | InvalidRoleException |
                     DependencyNotCompletedException | InvalidTaskStateException ex) {
                showError(ex.getMessage());
            }
        });

        completeButton.addActionListener(e -> {
            if (!currentUser.canWorkOnTask()) {
                showError("You do not have permission to complete tasks.");
                return;
            }

            try {
                String taskId = (String) completeTaskComboBox.getSelectedItem();
                Task task = taskManager.findTask(taskId);
                Engineer engineer = task.getAssignedEngineer();

                if (engineer == null) {
                    showError("No engineer assigned to this task.");
                    return;
                }

                if (!engineer.getUID().equals(currentUser.getUID())) {
                    showError("You can only complete your own assigned tasks.");
                    return;
                }

                taskManager.completeTask(taskId, engineer);
                refreshTaskTable();
                JOptionPane.showMessageDialog(this, "Task completed.");
            } catch (TaskNotFoundException | InvalidRoleException | InvalidTaskStateException ex) {
                showError(ex.getMessage());
            }
        });

        return panel;
    }

    /**
     * Onglet historique.
     */
    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadHistoryButton = new JButton("Show History");
        JButton saveCurrentHistoryButton = new JButton("Save Selected Task History");
        JButton loadAllHistoriesButton = new JButton("Load Histories");

        top.add(new JLabel("Task:"));
        top.add(historyTaskComboBox);
        top.add(loadHistoryButton);
        top.add(saveCurrentHistoryButton);
        top.add(loadAllHistoriesButton);

        loadHistoryButton.addActionListener(e -> {
            try {
                String taskId = (String) historyTaskComboBox.getSelectedItem();

                if (taskId == null) {
                    showError("Select a task.");
                    return;
                }

                Task task = taskManager.findTask(taskId);
                historyArea.setText("");

                for (TaskHistoryEntry entry : task.getHistory()) {
                    historyArea.append(entry.toString() + "\n");
                }
            } catch (TaskNotFoundException ex) {
                showError(ex.getMessage());
            }
        });

        saveCurrentHistoryButton.addActionListener(e -> {
            String taskId = (String) historyTaskComboBox.getSelectedItem();

            if (taskId == null) {
                showError("Select a task.");
                return;
            }

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save selected task history");
            chooser.setSelectedFile(new File("task_" + taskId + "_history.txt"));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    taskManager.saveTaskHistoryToFile(taskId, chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Task history saved.");
                } catch (TaskNotFoundException | FilePersistenceException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        loadAllHistoriesButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load task histories");

            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    taskManager.loadTaskHistoriesFromFile(chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "Task histories loaded.");
                    historyArea.setText("");
                } catch (FilePersistenceException | TaskNotFoundException ex) {
                    showError(ex.getMessage());
                }
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Onglet dashboard.
     */
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea dashboardArea = new JTextArea();
        dashboardArea.setEditable(false);
        JButton refreshButton = new JButton("Refresh Dashboard");

        refreshButton.addActionListener(e -> {
            if (!currentUser.canGenerateReport()) {
                showError("You do not have permission to view reports.");
                return;
            }

            StringBuilder builder = new StringBuilder();
            builder.append("=== DASHBOARD ===\n");
            builder.append("Connected user: ")
                    .append(currentUser.getName())
                    .append(" [")
                    .append(currentUser.getClass().getSimpleName())
                    .append("]\n");
            builder.append("Total tasks: ").append(taskManager.listTasks().size()).append("\n");
            builder.append("In progress: ").append(taskManager.getInProgressTasks().size()).append("\n\n");

            for (Task task : taskManager.listTasks()) {
                builder.append(task.toString()).append("\n");
            }

            dashboardArea.setText(builder.toString());
        });

        panel.add(refreshButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(dashboardArea), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Onglet persistance des tâches.
     */
    private JPanel createPersistencePanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField fileField = new JTextField("data/tasks.txt");
        JButton saveButton = new JButton("Save Tasks");
        JButton loadButton = new JButton("Load Tasks");

        panel.add(new JLabel("File path:"));
        panel.add(fileField);
        panel.add(saveButton);
        panel.add(loadButton);

        saveButton.addActionListener(e -> {
            if (!currentUser.canDeleteTask()) {
                showError("You do not have permission to save tasks.");
                return;
            }

            try {
                taskManager.saveTasksToFile(fileField.getText().trim());
                JOptionPane.showMessageDialog(this, "Tasks saved.");
            } catch (FilePersistenceException ex) {
                showError(ex.getMessage());
            }
        });

        loadButton.addActionListener(e -> {
            if (!currentUser.canDeleteTask()) {
                showError("You do not have permission to load tasks.");
                return;
            }

            try {
                taskManager.loadTasksFromFile(fileField.getText().trim());
                refreshTaskTable();
                refreshAllComboBoxes();
                JOptionPane.showMessageDialog(this, "Tasks loaded.");
            } catch (FilePersistenceException ex) {
                showError(ex.getMessage());
            }
        });

        return panel;
    }

    /**
     * Active/désactive les onglets selon les permissions.
     */
    private void applyPermissions() {
        setTabEnabled("Users", currentUser.canCreateTask());
        setTabEnabled("Tasks", currentUser.canCreateTask());
        setTabEnabled("Assign", currentUser.canAssignTask());
        setTabEnabled("Dependencies", currentUser.canAssignTask());
        setTabEnabled("Execution", currentUser.canWorkOnTask());
        setTabEnabled("History", true);
        setTabEnabled("Dashboard", currentUser.canGenerateReport());
        setTabEnabled("Persistence", currentUser.canDeleteTask());
    }

    /**
     * Active ou désactive un onglet par son titre.
     */
    private void setTabEnabled(String tabTitle, boolean enabled) {
        int index = tabs.indexOfTab(tabTitle);
        if (index != -1) {
            tabs.setEnabledAt(index, enabled);
        }
    }

    /**
     * Recharge le tableau des tâches.
     */
    private void refreshTaskTable() {
        taskTableModel.setRowCount(0);

        for (Task task : taskManager.listTasks()) {
            taskTableModel.addRow(new Object[]{
                    task.getTaskId(),
                    task.getTitle(),
                    task.getPriority(),
                    task.getCategory(),
                    task.getStatus(),
                    task.getDeadline(),
                    task.getAssignedEngineer() == null ? "None" : task.getAssignedEngineer().getName()
            });
        }
    }

    /**
     * Recharge toutes les ComboBox.
     */
    private void refreshAllComboBoxes() {
        taskComboBox.removeAllItems();
        dependencyTaskComboBox.removeAllItems();
        dependencyOnComboBox.removeAllItems();
        startTaskComboBox.removeAllItems();
        completeTaskComboBox.removeAllItems();
        historyTaskComboBox.removeAllItems();
        engineerComboBox.removeAllItems();

        for (Task task : taskManager.listTasks()) {
            String id = task.getTaskId();
            taskComboBox.addItem(id);
            dependencyTaskComboBox.addItem(id);
            dependencyOnComboBox.addItem(id);
            startTaskComboBox.addItem(id);
            completeTaskComboBox.addItem(id);
            historyTaskComboBox.addItem(id);
        }

        for (User user : localUsers) {
            if (user instanceof Engineer) {
                engineerComboBox.addItem(user.getUID());
            }
        }
    }

    /**
     * Recharge la zone texte des utilisateurs.
     */
    private void refreshUsersArea(JTextArea usersArea) {
        usersArea.setText("");

        for (User user : localUsers) {
            usersArea.append(user.toString() + "\n");
        }
    }

    /**
     * Recherche un ingénieur par identifiant.
     */
    private Engineer getEngineerById(String id) {
        for (User user : localUsers) {
            if (user instanceof Engineer && user.getUID().equals(id)) {
                return (Engineer) user;
            }
        }
        return null;
    }

    /**
     * Déconnexion puis retour à l'écran de login.
     */
    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Do you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            dispose();

            SwingUtilities.invokeLater(() -> {
                LoginDialog loginDialog = new LoginDialog(null, authService);
                loginDialog.setVisible(true);

                User authenticatedUser = loginDialog.getAuthenticatedUser();

                if (authenticatedUser != null) {
                    new STRMSGui(authenticatedUser, authService).setVisible(true);
                } else {
                    System.exit(0);
                }
            });
        }
    }

    /**
     * Affiche une boîte d'erreur.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Point d'entrée principal.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();

            try {
                // Essaie de charger les utilisateurs depuis un fichier externe
                authService.loadAccountsFromFile("data/users.txt");
            } catch (FilePersistenceException e) {
                // Si le fichier n'existe pas encore, on crée des comptes par défaut
                Admin admin = new Admin("A1", "Alice", "alice@strms.com");
                Manager manager = new Manager("M1", "Bob", "bob@strms.com");
                Engineer engineer = new Engineer("E1", "Charlie", "charlie@strms.com");

                authService.addAccount(admin, "admin123");
                authService.addAccount(manager, "manager123");
                authService.addAccount(engineer, "engineer123");

                try {
                    authService.saveAccountsToFile("data/users.txt");
                } catch (FilePersistenceException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            LoginDialog loginDialog = new LoginDialog(null, authService);
            loginDialog.setVisible(true);

            User authenticatedUser = loginDialog.getAuthenticatedUser();

            if (authenticatedUser != null) {
                new STRMSGui(authenticatedUser, authService).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}