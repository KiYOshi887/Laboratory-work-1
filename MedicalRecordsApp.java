import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Классы данных
class Patient {
    int patientId;
    String name;
    LocalDate birthDate;
    String medicalHistory;

    public Patient(int patientId, String name, LocalDate birthDate, String medicalHistory) {
        this.patientId = patientId;
        this.name = name;
        this.birthDate = birthDate;
        this.medicalHistory = medicalHistory;
    }
}

class Doctor {
    int doctorId;
    String name;
    String specialization;
    String schedule;

    public Doctor(int doctorId, String name, String specialization, String schedule) {
        this.doctorId = doctorId;
        this.name = name;
        this.specialization = specialization;
        this.schedule = schedule;
    }
}

class Appointment {
    int appointmentId;
    Patient patient;
    Doctor doctor;
    LocalDate date;

    public Appointment(int appointmentId, Patient patient, Doctor doctor, LocalDate date) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.date = date;
    }
}

class Clinic {
    List<Patient> patients = new ArrayList<>();
    List<Doctor> doctors = new ArrayList<>();
    List<Appointment> appointments = new ArrayList<>();
    int nextPatientId = 1;
    int nextDoctorId = 1;
    int nextAppointmentId = 1;

    public Patient addPatient(String name, LocalDate birthDate, String medicalHistory) {
        Patient patient = new Patient(nextPatientId++, name, birthDate, medicalHistory);
        patients.add(patient);
        return patient;
    }

    public Doctor addDoctor(String name, String specialization, String schedule) {
        Doctor doctor = new Doctor(nextDoctorId++, name, specialization, schedule);
        doctors.add(doctor);
        return doctor;
    }

    public Appointment addAppointment(Patient patient, Doctor doctor, LocalDate date) {
        Appointment appointment = new Appointment(nextAppointmentId++, patient, doctor, date);
        appointments.add(appointment);
        return appointment;
    }
}

// Основное приложение
public class MedicalRecordsApp extends JFrame {
    private Clinic clinic = new Clinic();
    private JTable patientTable, doctorTable, appointmentTable;
    private JComboBox<String> patientCombo, doctorCombo;
    private JTextField patientNameField, doctorNameField, doctorSpecField;
    private JTextArea patientHistoryField, doctorScheduleField;
    private JSpinner patientBirthDateSpinner, appointmentDateSpinner;

    public MedicalRecordsApp() {
        setTitle("Система управления медицинскими записями");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Пациенты", createPatientsTab());
        tabbedPane.add("Врачи", createDoctorsTab());
        tabbedPane.add("Приемы", createAppointmentsTab());
        tabbedPane.add("Отчеты", createReportsTab());

        add(tabbedPane);
    }

    private JPanel createPatientsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(1, 3));
        patientNameField = new JTextField();
        patientHistoryField = new JTextArea();
        patientBirthDateSpinner = new JSpinner(new SpinnerDateModel());
        ((JSpinner.DefaultEditor) patientBirthDateSpinner.getEditor()).getTextField().setEditable(false);
        JButton addPatientButton = new JButton("Добавить пациента");

        formPanel.add(new JLabel("Имя:"));
        formPanel.add(patientNameField);
        formPanel.add(new JLabel("Дата рождения:"));
        formPanel.add(patientBirthDateSpinner);
        formPanel.add(new JLabel("Медицинская история:"));
        formPanel.add(new JScrollPane(patientHistoryField));
        formPanel.add(addPatientButton);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Имя", "Дата рождения", "Медицинская история"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        patientTable = new JTable(tableModel);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        addPatientButton.addActionListener(e -> {
            String name = patientNameField.getText();
            String history = patientHistoryField.getText();
            LocalDate birthDate = LocalDate.now(); // Реализуйте ввод даты через spinner

            if (name.isEmpty() || history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            Patient patient = clinic.addPatient(name, birthDate, history);
            tableModel.addRow(new Object[]{patient.patientId, patient.name, patient.birthDate, patient.medicalHistory});
            patientCombo.addItem(patient.name);
            JOptionPane.showMessageDialog(this, "Пациент добавлен.");
        });

        return panel;
    }

    private JPanel createDoctorsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(1, 3));
        doctorNameField = new JTextField();
        doctorSpecField = new JTextField();
        doctorScheduleField = new JTextArea();
        JButton addDoctorButton = new JButton("Добавить врача");

        formPanel.add(new JLabel("Имя:"));
        formPanel.add(doctorNameField);
        formPanel.add(new JLabel("Специализация:"));
        formPanel.add(doctorSpecField);
        formPanel.add(new JLabel("Расписание:"));
        formPanel.add(new JScrollPane(doctorScheduleField));
        formPanel.add(addDoctorButton);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Имя", "Специализация", "Расписание"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        doctorTable = new JTable(tableModel);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        addDoctorButton.addActionListener(e -> {
            String name = doctorNameField.getText();
            String specialization = doctorSpecField.getText();
            String schedule = doctorScheduleField.getText();

            if (name.isEmpty() || specialization.isEmpty() || schedule.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            Doctor doctor = clinic.addDoctor(name, specialization, schedule);
            tableModel.addRow(new Object[]{doctor.doctorId, doctor.name, doctor.specialization, doctor.schedule});
            doctorCombo.addItem(doctor.name);
            JOptionPane.showMessageDialog(this, "Врач добавлен.");
        });

        return panel;
    }

    private JPanel createAppointmentsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(1, 3));
        patientCombo = new JComboBox<>();
        doctorCombo = new JComboBox<>();
        appointmentDateSpinner = new JSpinner(new SpinnerDateModel());
        JButton addAppointmentButton = new JButton("Записать на прием");

        formPanel.add(new JLabel("Пациент:"));
        formPanel.add(patientCombo);
        formPanel.add(new JLabel("Врач:"));
        formPanel.add(doctorCombo);
        formPanel.add(new JLabel("Дата:"));
        formPanel.add(appointmentDateSpinner);
        formPanel.add(addAppointmentButton);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Пациент", "Врач", "Дата"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        appointmentTable = new JTable(tableModel);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        addAppointmentButton.addActionListener(e -> {
            int patientIndex = patientCombo.getSelectedIndex();
            int doctorIndex = doctorCombo.getSelectedIndex();

            if (patientIndex == -1 || doctorIndex == -1) {
                JOptionPane.showMessageDialog(this, "Выберите пациента и врача.");
                return;
            }

            Patient patient = clinic.patients.get(patientIndex);
            Doctor doctor = clinic.doctors.get(doctorIndex);
            LocalDate date = LocalDate.now(); // Реализуйте ввод через spinner

            Appointment appointment = clinic.addAppointment(patient, doctor, date);
            tableModel.addRow(new Object[]{appointment.appointmentId, appointment.patient.name, appointment.doctor.name, date});
            JOptionPane.showMessageDialog(this, "Прием добавлен.");
        });

        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea reportArea = new JTextArea();
        JButton generateReportButton = new JButton("Создать отчет");

        generateReportButton.addActionListener(e -> {
            StringBuilder report = new StringBuilder("Отчет о приемах:\n\n");

            for (Appointment appointment : clinic.appointments) {
                report.append(String.format("Прием ID: %d\nПациент: %s\nВрач: %s\nДата: %s\n\n",
                        appointment.appointmentId, appointment.patient.name, appointment.doctor.name, appointment.date));
            }

            reportArea.setText(report.toString());
        });

        panel.add(generateReportButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MedicalRecordsApp app = new MedicalRecordsApp();
            app.setVisible(true);
        });
    }
}
