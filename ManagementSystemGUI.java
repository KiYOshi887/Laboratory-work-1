import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Класс Employee для управления данными сотрудника и отслеживания времени работы
class Employee {
    private String name;
    private int id;
    private Map<Task, TimeLog> timeLogs = new HashMap<>();

    public Employee(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void logTime(Task task, int hours) {
        timeLogs.computeIfAbsent(task, k -> new TimeLog()).addTime(hours);
    }

    public int getTotalTimeForTask(Task task) {
        return timeLogs.getOrDefault(task, new TimeLog()).getTotalTime();
    }
}

// Класс Project, который содержит список сотрудников и задач
class Project {
    private String projectName;
    private List<Employee> employees = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();

    public Project(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}

// Класс Task для управления задачами, связанного с проектами и сотрудниками
class Task {
    private String description;
    private Employee assignedEmployee;
    private boolean isCompleted;

    public Task(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void assignTo(Employee employee) {
        this.assignedEmployee = employee;
    }

    public void markAsCompleted() {
        isCompleted = true;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }
}

// Класс TimeLog для учета рабочего времени сотрудников
class TimeLog {
    private int totalTime;

    public void addTime(int hours) {
        totalTime += hours;
    }

    public int getTotalTime() {
        return totalTime;
    }
}

// Класс Department для управления группами сотрудников и их проектами
class Department {
    private String departmentName;
    private List<Employee> employees = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();

    public Department(String departmentName) {
        this.departmentName = departmentName;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Employee> getEmployees() {
        return employees;
    }
}

// GUI-класс для управления проектами и сотрудниками
public class ManagementSystemGUI extends JFrame {
    private Department department;
    private JTextArea outputArea;
    private JTextField employeeNameField, employeeIdField, projectNameField, taskDescriptionField, logHoursField;
    private JComboBox<String> employeeComboBox, projectComboBox, taskComboBox;

    public ManagementSystemGUI() {
        department = new Department("Development");

        setTitle("Система управления сотрудниками и проектами");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель ввода данных
        JPanel inputPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Ввод данных"));

        // Поля для ввода данных сотрудника
        inputPanel.add(new JLabel("Имя сотрудника:"));
        employeeNameField = new JTextField();
        inputPanel.add(employeeNameField);

        inputPanel.add(new JLabel("ID сотрудника:"));
        employeeIdField = new JTextField();
        inputPanel.add(employeeIdField);

        // Поле для ввода названия проекта
        inputPanel.add(new JLabel("Название проекта:"));
        projectNameField = new JTextField();
        inputPanel.add(projectNameField);

        // Поле для ввода описания задачи
        inputPanel.add(new JLabel("Описание задачи:"));
        taskDescriptionField = new JTextField();
        inputPanel.add(taskDescriptionField);

        // Поле для ввода времени
        inputPanel.add(new JLabel("Часы для логирования:"));
        logHoursField = new JTextField();
        inputPanel.add(logHoursField);

        add(inputPanel, BorderLayout.NORTH);

        // Панель для выбора сотрудников и проектов
        JPanel comboPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        employeeComboBox = new JComboBox<>();
        projectComboBox = new JComboBox<>();
        taskComboBox = new JComboBox<>();

        comboPanel.add(new JLabel("Выберите сотрудника:"));
        comboPanel.add(employeeComboBox);

        comboPanel.add(new JLabel("Выберите проект:"));
        comboPanel.add(projectComboBox);

        add(comboPanel, BorderLayout.CENTER);

        // Панель для кнопок действий
        JPanel actionPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JButton addEmployeeButton = new JButton("Добавить сотрудника");
        addEmployeeButton.addActionListener(new AddEmployeeAction());
        actionPanel.add(addEmployeeButton);

        JButton addProjectButton = new JButton("Добавить проект");
        addProjectButton.addActionListener(new AddProjectAction());
        actionPanel.add(addProjectButton);

        JButton addTaskButton = new JButton("Добавить задачу");
        addTaskButton.addActionListener(new AddTaskAction());
        actionPanel.add(addTaskButton);

        JButton assignTaskButton = new JButton("Назначить задачу");
        assignTaskButton.addActionListener(new AssignTaskAction());
        actionPanel.add(assignTaskButton);

        JButton logTimeButton = new JButton("Логировать время");
        logTimeButton.addActionListener(new LogTimeAction());
        actionPanel.add(logTimeButton);

        JButton generateReportButton = new JButton("Создать отчет");
        generateReportButton.addActionListener(new GenerateReportAction());
        actionPanel.add(generateReportButton);

        add(actionPanel, BorderLayout.SOUTH);

        // Область вывода отчета
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        add(scrollPane, BorderLayout.EAST);

        setVisible(true);
    }

    // Обработчик добавления сотрудника
    private class AddEmployeeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = employeeNameField.getText();
            int id = Integer.parseInt(employeeIdField.getText());
            Employee employee = new Employee(name, id);
            department.addEmployee(employee);
            employeeComboBox.addItem(name);
            outputArea.append("Добавлен сотрудник: " + name + " (ID: " + id + ")\n");
        }
    }

    // Обработчик добавления проекта
    private class AddProjectAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String projectName = projectNameField.getText();
            Project project = new Project(projectName);
            department.addProject(project);
            projectComboBox.addItem(projectName);
            outputArea.append("Добавлен проект: " + projectName + "\n");
        }
    }

    // Обработчик добавления задачи
    private class AddTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String description = taskDescriptionField.getText().trim();
            String selectedProject = (String) projectComboBox.getSelectedItem();
    
            // Проверка на выбор проекта
            if (selectedProject == null) {
                outputArea.append("Ошибка: Пожалуйста, выберите проект, к которому хотите добавить задачу.\n");
                return;
            }
    
            // Проверка на описание задачи
            if (description.isEmpty()) {
                outputArea.append("Ошибка: Пожалуйста, введите описание задачи.\n");
                return;
            }
    
            outputArea.append("Попытка найти проект с именем: " + selectedProject + "\n");
    
            // Поиск проекта в списке проектов отдела
            Project project = department.getProjects().stream()
                    .filter(p -> p.getProjectName().equals(selectedProject))
                    .findFirst().orElse(null);
    
            if (project != null) {
                outputArea.append("Проект '" + selectedProject + "' найден. Добавление задачи...\n");
    
                // Добавляем задачу к проекту
                Task task = new Task(description);
                project.addTask(task);
    
                // Проверка: выводим все задачи текущего проекта
                List<Task> tasks = project.getTasks();
                outputArea.append("Текущий список задач проекта '" + selectedProject + "' после добавления задачи:\n");
                for (Task t : tasks) {
                    outputArea.append("- " + t.getDescription() + "\n");
                }
    
                // Проверка всех задач в department
                outputArea.append("Проверка всех задач во всех проектах:\n");
                for (Project p : department.getProjects()) {
                    outputArea.append("Проект: " + p.getProjectName() + "\n");
                    for (Task t : p.getTasks()) {
                        outputArea.append("  - Задача: " + t.getDescription() + "\n");
                    }
                }
    
                // Очищаем и обновляем taskComboBox
                taskComboBox.removeAllItems();
                for (Task t : tasks) {
                    taskComboBox.addItem(t.getDescription());
                }
                outputArea.append("taskComboBox успешно обновлён.\n");
    
                // Перерисовка компонентов
                taskComboBox.revalidate();
                taskComboBox.repaint();
                outputArea.revalidate();
                outputArea.repaint();
    
            } else {
                outputArea.append("Ошибка: Проект " + selectedProject + " не найден в department.\n");
            }
        }
    }
    
    
    
    
    
    
    
    

    // Обработчик назначения задачи сотруднику
    private class AssignTaskAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedEmployee = (String) employeeComboBox.getSelectedItem();
            String selectedTask = (String) taskComboBox.getSelectedItem();

            Employee employee = department.getEmployees().stream()
                    .filter(emp -> emp.getName().equals(selectedEmployee))
                    .findFirst().orElse(null);

            Project project = department.getProjects().stream()
                    .filter(proj -> proj.getTasks().stream()
                            .anyMatch(task -> task.getDescription().equals(selectedTask)))
                    .findFirst().orElse(null);

            if (employee != null && project != null) {
                Task task = project.getTasks().stream()
                        .filter(t -> t.getDescription().equals(selectedTask))
                        .findFirst().orElse(null);

                if (task != null) {
                    task.assignTo(employee);
                    outputArea.append("Назначена задача: " + selectedTask + " сотруднику: " + selectedEmployee + "\n");
                }
            }
        }
    }

    // Обработчик логирования времени на задачу
    private class LogTimeAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedEmployee = (String) employeeComboBox.getSelectedItem();
            String selectedTask = (String) taskComboBox.getSelectedItem();
            int hours = Integer.parseInt(logHoursField.getText());

            Employee employee = department.getEmployees().stream()
                    .filter(emp -> emp.getName().equals(selectedEmployee))
                    .findFirst().orElse(null);

            if (employee != null) {
                Project project = department.getProjects().stream()
                        .filter(proj -> proj.getTasks().stream()
                                .anyMatch(task -> task.getDescription().equals(selectedTask)))
                        .findFirst().orElse(null);

                if (project != null) {
                    Task task = project.getTasks().stream()
                            .filter(t -> t.getDescription().equals(selectedTask))
                            .findFirst().orElse(null);

                    if (task != null) {
                        employee.logTime(task, hours);
                        outputArea.append("Залогировано " + hours + " часов для задачи: " + selectedTask + " сотрудником: " + selectedEmployee + "\n");
                    }
                }
            }
        }
    }

    // Обработчик генерации отчета
    private class GenerateReportAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            outputArea.append("\n=== Отчет по времени для проектов ===\n");
            for (Project project : department.getProjects()) {
                outputArea.append("Проект: " + project.getProjectName() + "\n");
                for (Task task : project.getTasks()) {
                    Employee employee = task.getAssignedEmployee();
                    if (employee != null) {
                        int timeSpent = employee.getTotalTimeForTask(task);
                        outputArea.append("Задача: " + task.getDescription() +
                                ", Назначена: " + employee.getName() +
                                ", Время затрачено: " + timeSpent + " часов\n");
                    }
                }
            }
            outputArea.append("==========================\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagementSystemGUI());
    }
}
