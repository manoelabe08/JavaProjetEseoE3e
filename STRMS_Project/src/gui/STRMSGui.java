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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class STRMSGui extends JFrame {

    private final TaskManager taskManager;
    private final List<User> localUsers;
    private final User currentUser;

    private final DefaultTableModel taskTableModel;
    private final JTable taskTable;
    private final JTextArea historyArea;

    private final JComboBox<String> taskComboBox;
    private final JComboBox<String> dependencyTaskComboBox;
    private final JComboBox<String> dependencyOnComboBox;
    private final JComboBox<String> engineerComboBox;
    private final JComboBox<String> startTaskComboBox;
    private final JComboBox<String> completeTaskComboBox;
    private final JComboBox<String> historyTaskComboBox;

    private final JTabbedPane tabs;

    public STRMSGui(User loggedUser) {
        this.taskManager = new TaskManager();
        this.localUsers = new ArrayList<>();
        this.currentUser = loggedUser;

        setTitle("STRMS - Smart Task & Resource Management System - Connected: " + currentUser.getName());
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();

        taskTableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Priority", "Category", "Status", "Deadline", "Engineer"}, 0
        );
        taskTable = new JTable(taskTableModel);

        historyArea = new JTextArea();
        historyArea.setEditable(false);

        taskComboBox = new JComboBox<>();
        dependencyTaskComboBox = new JComboBox<>();
        dependencyOnComboBox = new JComboBox<>();
        engineerComboBox = new JComboBox<>();
        startTaskComboBox = new JComboBox<>();
        completeTaskComboBox = new JComboBox<>();
        historyTaskComboBox = new JComboBox<>();

        seedDefaultUsers();

        tabs.add("Users", createUsersPanel());
        tabs.add("Tasks", createTasksPanel());
        tabs.add("Assign", createAssignPanel());
        tabs.add("Dependencies", createDependencyPanel());
        tabs.add("Execution", createExecutionPanel());
        tabs.add("History", createHistoryPanel());
        tabs.add("Dashboard", createDashboardPanel());
        tabs.add("Persistence", createPersistencePanel());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());

        JPanel container = new JPanel(new BorderLayout());
        container.add(tabs, BorderLayout.CENTER);

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        topRightPanel.add(logoutButton);
        container.add(topRightPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(container, BorderLayout.CENTER);

        refreshAllComboBoxes();
        refreshTaskTable();
        applyPermissions();
    }

    private JPanel createUsersPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"Admin", "Manager", "Engineer"});
        JButton addButton = new JButton("Add User");

        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.add(new JLabel("User ID:"));
        form.add(idField);
        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Email:"));
        form.add(emailField);
        form.add(new JLabel("Role:"));
        form.add(roleBox);
        form.add(new JLabel(""));
        form.add(addButton);

        JTextArea usersArea = new JTextArea();
        usersArea.setEditable(false);

        addButton.addActionListener(e -> {
            if (!currentUser.canCreateTask()) {
                showError("You do not have permission to add users.");
                return;
            }

            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleBox.getSelectedItem();

            if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
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
            refreshUsersArea(usersArea);
            refreshAllComboBoxes();

            idField.setText("");
            nameField.setText("");
            emailField.setText("");
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(usersArea), BorderLayout.CENTER);

        refreshUsersArea(usersArea);
        return panel;
    }

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

                if (task == null) {
                    showError("Task not found.");
                    return;
                }

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

                if (task == null) {
                    showError("Task not found.");
                    return;
                }

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

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton showHistoryButton = new JButton("Show History");
        JButton saveCurrentHistoryButton = new JButton("Save Selected Task History");
        JButton saveAllHistoriesButton = new JButton("Save All Histories");

        top.add(new JLabel("Task:"));
        top.add(historyTaskComboBox);
        top.add(showHistoryButton);
        top.add(saveCurrentHistoryButton);
        top.add(saveAllHistoriesButton);


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

        saveAllHistoriesButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save all task histories");
            chooser.setSelectedFile(new File("all_task_histories.txt"));

            int result = chooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                try {
                    taskManager.saveTaskHistoriesToFile(chooser.getSelectedFile().getAbsolutePath());
                    JOptionPane.showMessageDialog(this, "All task histories saved.");
                } catch (FilePersistenceException ex) {
                    showError(ex.getMessage());
                }
            }
        });
        
        showHistoryButton.addActionListener(e -> {
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
        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        return panel;
    }

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

    private void setTabEnabled(String tabTitle, boolean enabled) {
        int index = tabs.indexOfTab(tabTitle);
        if (index != -1) {
            tabs.setEnabledAt(index, enabled);
        }
    }

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

    private void refreshUsersArea(JTextArea usersArea) {
        usersArea.setText("");
        for (User user : localUsers) {
            usersArea.append(user.toString() + "\n");
        }
    }

    private void seedDefaultUsers() {
        Admin admin = new Admin("A1", "Alice", "alice@strms.com");
        Manager manager = new Manager("M1", "Bob", "bob@strms.com");
        Engineer engineer = new Engineer("E1", "Charlie", "charlie@strms.com");

        localUsers.add(admin);
        localUsers.add(manager);
        localUsers.add(engineer);

        taskManager.addUser(admin);
        taskManager.addUser(manager);
        taskManager.addUser(engineer);
    }

    private Engineer getEngineerById(String id) {
        for (User user : localUsers) {
            if (user instanceof Engineer && user.getUID().equals(id)) {
                return (Engineer) user;
            }
        }
        return null;
    }

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
                AuthService authService = new AuthService();

                Admin admin = new Admin("A1", "Alice", "alice@strms.com");
                Manager manager = new Manager("M1", "Bob", "bob@strms.com");
                Engineer engineer = new Engineer("E1", "Charlie", "charlie@strms.com");

                authService.addAccount(admin, "admin123");
                authService.addAccount(manager, "manager123");
                authService.addAccount(engineer, "engineer123");

                LoginDialog loginDialog = new LoginDialog(null, authService);
                loginDialog.setVisible(true);

                User authenticatedUser = loginDialog.getAuthenticatedUser();

                if (authenticatedUser != null) {
                    new STRMSGui(authenticatedUser).setVisible(true);
                } else {
                    System.exit(0);
                }
            });
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AuthService authService = new AuthService();
            Admin dev = new Admin("gon92brb","azid","azid.attoumani@gmail.com");
            Admin admin = new Admin("A1", "Alice", "alice@strms.com");
            Manager manager = new Manager("M1", "Bob", "bob@strms.com");
            Engineer engineer = new Engineer("E1", "Charlie", "charlie@strms.com");
            
            authService.addAccount(admin, "admin123");
            authService.addAccount(manager, "manager123");
            authService.addAccount(engineer, "engineer123");
            authService.addAccount(dev, "1234");

            LoginDialog loginDialog = new LoginDialog(null, authService);
            loginDialog.setVisible(true);

            User authenticatedUser = loginDialog.getAuthenticatedUser();

            if (authenticatedUser != null) {
                new STRMSGui(authenticatedUser).setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}