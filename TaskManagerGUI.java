import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;

class Task implements Comparable<Task> {
    private String name;
    private String description;
    private int priority;
    private Date dueDate;
    private boolean completed;

    public Task(String name, String description, int priority, Date dueDate) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completed = false;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public int getPriority() {
        return priority;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    @Override
    public String toString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM");
        String taskInfo = (completed ? "✓ " : "○ ") + name + " - " + dateFormat.format(dueDate);
        
        if (description != null && !description.isEmpty()) {
            taskInfo += ": " + description;
        }
        
        return taskInfo;
    }

    @Override
    public int compareTo(Task other) {
        return Integer.compare(other.priority, this.priority); // Сортировка по приоритету (от высокого к низкому)
    }
}

class TaskManagerGUI extends JFrame implements ActionListener {
    private List<Task> tasks;
    private DefaultListModel<Task> taskListModel;
    private JList<Task> taskList;
    private JTextField taskNameField;
    private JTextArea taskDescriptionField;
    private JSpinner dateSpinner;
    private JComboBox<String> priorityField;

    public TaskManagerGUI() {
        tasks = new ArrayList<>();
        setTitle("Планировщик дел");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setLocationRelativeTo(null);

        // Список задач
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane taskScrollPane = new JScrollPane(taskList);
        taskScrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(taskScrollPane, BorderLayout.CENTER);

        // Панель для добавления новой задачи
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Поле названия задачи
        JPanel taskNamePanel = new JPanel(new BorderLayout(5, 5));
        JLabel nameLabel = new JLabel("Название задачи:");
        taskNameField = new JTextField();
        taskNamePanel.add(nameLabel, BorderLayout.NORTH);
        taskNamePanel.add(taskNameField, BorderLayout.CENTER);
        inputPanel.add(taskNamePanel);

        // Поле описания
        JPanel taskDescriptionPanel = new JPanel(new BorderLayout(5, 5));
        JLabel descriptionLabel = new JLabel("Описание:");
        taskDescriptionField = new JTextArea(2, 20);
        taskDescriptionField.setLineWrap(true);
        taskDescriptionField.setWrapStyleWord(true);
        taskDescriptionField.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        taskDescriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        taskDescriptionPanel.add(new JScrollPane(taskDescriptionField), BorderLayout.CENTER);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(taskDescriptionPanel);

        // Поле выбора даты
        JPanel datePanel = new JPanel(new BorderLayout(5, 5));
        JLabel dueDateLabel = new JLabel("Срок выполнения:");
        dateSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "dd-MM-yyyy");
        dateSpinner.setEditor(dateEditor);
        datePanel.add(dueDateLabel, BorderLayout.NORTH);
        datePanel.add(dateSpinner, BorderLayout.CENTER);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(datePanel);

        // Поле выбора приоритета
        JPanel priorityPanel = new JPanel(new BorderLayout(5, 5));
        JLabel priorityLabel = new JLabel("Приоритет:");
        priorityField = new JComboBox<>(new String[]{"Низкий", "Средний", "Высокий"});
        priorityPanel.add(priorityLabel, BorderLayout.NORTH);
        priorityPanel.add(priorityField, BorderLayout.CENTER);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(priorityPanel);

        // Панель кнопок
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Добавить задачу");
        addButton.addActionListener(this);
        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> clearFields());
        JButton deleteButton = new JButton("Удалить задачу");
        deleteButton.addActionListener(e -> deleteSelectedTask());
        buttonPanel.add(cancelButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(addButton);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(buttonPanel);
        add(inputPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String taskName = taskNameField.getText();
        String description = taskDescriptionField.getText();
        int priority = priorityField.getSelectedIndex() + 1;
        Date dueDate = (Date) dateSpinner.getValue();

        Task task = new Task(taskName, description, priority, dueDate);
        tasks.add(task);
        updateTaskList();
        clearFields();
    }

    private void clearFields() {
        taskNameField.setText("");
        taskDescriptionField.setText("");
        dateSpinner.setValue(new Date());
        priorityField.setSelectedIndex(0);
    }

    private void deleteSelectedTask() {
        Task selectedTask = taskList.getSelectedValue();
        if (selectedTask != null) {
            tasks.remove(selectedTask);
            updateTaskList();
        } else {
            JOptionPane.showMessageDialog(this, "Выберите задачу для удаления.");
        }
    }

    private void updateTaskList() {
        tasks.sort(Task::compareTo);  // Сортировка по приоритету
        taskListModel.clear();
        for (Task task : tasks) {
            taskListModel.addElement(task);
        }
    }

    private class TaskCellRenderer extends JPanel implements ListCellRenderer<Task> {
        private JLabel nameLabel;
        private JLabel descriptionLabel;
    
        public TaskCellRenderer() {
            setLayout(new BorderLayout(5, 5));
            nameLabel = new JLabel();
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            
            descriptionLabel = new JLabel();
            descriptionLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            
            add(nameLabel, BorderLayout.NORTH);
            add(descriptionLabel, BorderLayout.SOUTH);
        }
    
        @Override
        public Component getListCellRendererComponent(JList<? extends Task> list, Task task, int index, boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText((task.isCompleted() ? "✓ " : "○ ") + task.getName() + " - " + new SimpleDateFormat("dd MMMM").format(task.getDueDate()));
            descriptionLabel.setText(task.getDescription() != null ? task.getDescription() : "");
    
            // Цвет фона в зависимости от приоритета
            switch (task.getPriority()) {
                case 1:
                    setBackground(new Color(144, 238, 144)); // Зеленый для низкого приоритета
                    break;
                case 2:
                    setBackground(new Color(255, 255, 224)); // Желтый для среднего приоритета
                    break;
                case 3:
                    setBackground(new Color(255, 182, 193)); // Красный для высокого приоритета
                    break;
            }
    
            if (isSelected) {
                setBackground(getBackground().darker());
            }
    
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            return this;
        }
    }
    
    public static void main(String[] args) {
        new TaskManagerGUI();
    }
}
