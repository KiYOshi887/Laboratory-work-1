import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// Классы данных
class Course {
    private String name;
    private String description;
    private Instructor instructor;
    private List<Student> enrolledStudents;

    public Course(String name, String description, Instructor instructor) {
        this.name = name;
        this.description = description;
        this.instructor = instructor;
        this.enrolledStudents = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public List<Student> getEnrolledStudents() {
        return enrolledStudents;
    }

    public void enrollStudent(Student student) {
        enrolledStudents.add(student);
    }
}

class Student {
    private String name;
    private String email;

    public Student(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

class Instructor {
    private String name;
    private String schedule;

    public Instructor(String name, String schedule) {
        this.name = name;
        this.schedule = schedule;
    }

    public String getName() {
        return name;
    }

    public String getSchedule() {
        return schedule;
    }
}

class EducationSystem {
    private List<Course> courses;
    private List<Student> students;
    private List<Instructor> instructors;

    public EducationSystem() {
        this.courses = new ArrayList<>();
        this.students = new ArrayList<>();
        this.instructors = new ArrayList<>();
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Student> getStudents() {
        return students;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addInstructor(Instructor instructor) {
        instructors.add(instructor);
    }
}

// Основное приложение
public class EducationSystemApp extends JFrame {
    private EducationSystem educationSystem;

    private JTable coursesTable;
    private JTable studentsTable;
    private JTable instructorsTable;

    private DefaultTableModel coursesTableModel;
    private DefaultTableModel studentsTableModel;
    private DefaultTableModel instructorsTableModel;

    public EducationSystemApp() {
        educationSystem = new EducationSystem();
        setTitle("Система управления образовательными курсами");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Курсы", createCoursesPanel());
        tabbedPane.add("Студенты", createStudentsPanel());
        tabbedPane.add("Преподаватели", createInstructorsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createCoursesPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица курсов
        coursesTableModel = new DefaultTableModel(new String[]{"Название", "Описание", "Преподаватель"}, 0);
        coursesTable = new JTable(coursesTableModel);

        // Форма добавления курсов
        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        JTextField courseNameInput = new JTextField();
        JTextField courseDescriptionInput = new JTextField();
        JComboBox<String> instructorComboBox = new JComboBox<>();
        JButton addCourseButton = new JButton("Добавить курс");

        formPanel.add(new JLabel("Название"));
        formPanel.add(courseNameInput);
        formPanel.add(new JLabel("Описание"));
        formPanel.add(courseDescriptionInput);
        formPanel.add(new JLabel("Преподаватель"));
        formPanel.add(instructorComboBox);
        formPanel.add(new JLabel());
        formPanel.add(addCourseButton);

        // Обновление выпадающего списка преподавателей
        for (Instructor instructor : educationSystem.getInstructors()) {
            instructorComboBox.addItem(instructor.getName());
        }

        addCourseButton.addActionListener(e -> {
            String name = courseNameInput.getText();
            String description = courseDescriptionInput.getText();
            String instructorName = (String) instructorComboBox.getSelectedItem();

            if (name.isEmpty() || description.isEmpty() || instructorName == null) {
                JOptionPane.showMessageDialog(this, "Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Instructor instructor = educationSystem.getInstructors().stream()
                    .filter(i -> i.getName().equals(instructorName))
                    .findFirst()
                    .orElse(null);

            if (instructor != null) {
                Course course = new Course(name, description, instructor);
                educationSystem.addCourse(course);
                coursesTableModel.addRow(new Object[]{name, description, instructor.getName()});
                courseNameInput.setText("");
                courseDescriptionInput.setText("");
            }
        });

        panel.add(new JScrollPane(coursesTable), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица студентов
        studentsTableModel = new DefaultTableModel(new String[]{"Имя", "Email"}, 0);
        studentsTable = new JTable(studentsTableModel);

        // Форма добавления студентов
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        JTextField studentNameInput = new JTextField();
        JTextField studentEmailInput = new JTextField();
        JButton addStudentButton = new JButton("Добавить студента");

        formPanel.add(new JLabel("Имя"));
        formPanel.add(studentNameInput);
        formPanel.add(new JLabel("Email"));
        formPanel.add(studentEmailInput);
        formPanel.add(new JLabel());
        formPanel.add(addStudentButton);

        addStudentButton.addActionListener(e -> {
            String name = studentNameInput.getText();
            String email = studentEmailInput.getText();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Student student = new Student(name, email);
            educationSystem.addStudent(student);
            studentsTableModel.addRow(new Object[]{name, email});
            studentNameInput.setText("");
            studentEmailInput.setText("");
        });

        panel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInstructorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица преподавателей
        instructorsTableModel = new DefaultTableModel(new String[]{"Имя", "Расписание"}, 0);
        instructorsTable = new JTable(instructorsTableModel);

        // Форма добавления преподавателей
        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        JTextField instructorNameInput = new JTextField();
        JTextField instructorScheduleInput = new JTextField();
        JButton addInstructorButton = new JButton("Добавить преподавателя");

        formPanel.add(new JLabel("Имя"));
        formPanel.add(instructorNameInput);
        formPanel.add(new JLabel("Расписание"));
        formPanel.add(instructorScheduleInput);
        formPanel.add(new JLabel());
        formPanel.add(addInstructorButton);

        addInstructorButton.addActionListener(e -> {
            String name = instructorNameInput.getText();
            String schedule = instructorScheduleInput.getText();

            if (name.isEmpty() || schedule.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля!", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Instructor instructor = new Instructor(name, schedule);
            educationSystem.addInstructor(instructor);
            instructorsTableModel.addRow(new Object[]{name, schedule});
            instructorNameInput.setText("");
            instructorScheduleInput.setText("");
        });

        panel.add(new JScrollPane(instructorsTable), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EducationSystemApp app = new EducationSystemApp();
            app.setVisible(true);
        });
    }
}
 