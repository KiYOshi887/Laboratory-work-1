import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Классы данных
class Venue {
    int venueId;
    String name;
    String address;

    public Venue(int venueId, String name, String address) {
        this.venueId = venueId;
        this.name = name;
        this.address = address;
    }
}

class Event {
    int eventId;
    String name;
    LocalDate date;
    Venue venue;
    String description;
    List<Participant> participants;

    public Event(int eventId, String name, LocalDate date, Venue venue, String description) {
        this.eventId = eventId;
        this.name = name;
        this.date = date;
        this.venue = venue;
        this.description = description;
        this.participants = new ArrayList<>();
    }

    public void addParticipant(Participant participant) {
        participants.add(participant);
    }
}

class Participant {
    int participantId;
    String name;
    String email;

    public Participant(int participantId, String name, String email) {
        this.participantId = participantId;
        this.name = name;
        this.email = email;
    }
}

class Organizer {
    List<Event> events = new ArrayList<>();
    List<Venue> venues = new ArrayList<>();
    List<Participant> participants = new ArrayList<>();
    int nextEventId = 1;
    int nextVenueId = 1;
    int nextParticipantId = 1;

    public Event createEvent(String name, LocalDate date, Venue venue, String description) {
        Event event = new Event(nextEventId++, name, date, venue, description);
        events.add(event);
        return event;
    }

    public Venue addVenue(String name, String address) {
        Venue venue = new Venue(nextVenueId++, name, address);
        venues.add(venue);
        return venue;
    }

    public Participant addParticipant(String name, String email) {
        Participant participant = new Participant(nextParticipantId++, name, email);
        participants.add(participant);
        return participant;
    }
}

// Основное приложение
public class EventManagementApp extends JFrame {
    private Organizer organizer = new Organizer();
    private JTable eventTable, participantTable, venueTable;
    private JComboBox<String> eventVenueCombo, participantEventCombo;
    private JTextField eventNameField, eventDescriptionField, participantNameField, participantEmailField, venueNameField, venueAddressField;

    public EventManagementApp() {
        setTitle("Система учета мероприятий");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.add("Мероприятия", createEventsTab());
        tabbedPane.add("Участники", createParticipantsTab());
        tabbedPane.add("Места", createVenuesTab());
        tabbedPane.add("Отчеты", createReportsTab());

        add(tabbedPane);
    }

    private JPanel createEventsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(2, 5));
        eventNameField = new JTextField();
        eventDescriptionField = new JTextField();
        eventVenueCombo = new JComboBox<>();
        JSpinner eventDateSpinner = new JSpinner(new SpinnerDateModel());
        ((JSpinner.DefaultEditor) eventDateSpinner.getEditor()).getTextField().setEditable(false);
        JButton createEventButton = new JButton("Создать мероприятие");

        formPanel.add(new JLabel("Название:"));
        formPanel.add(eventNameField);
        formPanel.add(new JLabel("Дата:"));
        formPanel.add(eventDateSpinner);
        formPanel.add(new JLabel("Место:"));
        formPanel.add(eventVenueCombo);
        formPanel.add(new JLabel("Описание:"));
        formPanel.add(eventDescriptionField);
        formPanel.add(createEventButton);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Название", "Дата", "Место", "Описание"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        eventTable = new JTable(tableModel);
        panel.add(new JScrollPane(eventTable), BorderLayout.CENTER);

        createEventButton.addActionListener(e -> {
            String name = eventNameField.getText();
            String description = eventDescriptionField.getText();
            Venue venue = organizer.venues.get(eventVenueCombo.getSelectedIndex());
            LocalDate date = LocalDate.now(); // Замените на ввод даты, если нужно.

            if (name.isEmpty() || description.isEmpty() || venue == null) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            Event event = organizer.createEvent(name, date, venue, description);
            tableModel.addRow(new Object[]{event.eventId, event.name, event.date, event.venue.name, event.description});
            participantEventCombo.addItem(event.name); // Добавляем мероприятие в комбобокс участников
            JOptionPane.showMessageDialog(this, "Мероприятие создано.");
        });

        return panel;
    }

    private JPanel createParticipantsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(1, 4));
        participantNameField = new JTextField();
        participantEmailField = new JTextField();
        participantEventCombo = new JComboBox<>();
        JButton addParticipantButton = new JButton("Добавить участника");

        formPanel.add(new JLabel("Имя:"));
        formPanel.add(participantNameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(participantEmailField);
        formPanel.add(new JLabel("Мероприятие:"));
        formPanel.add(participantEventCombo);
        formPanel.add(addParticipantButton);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Имя", "Email", "Мероприятие"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        participantTable = new JTable(tableModel);
        panel.add(new JScrollPane(participantTable), BorderLayout.CENTER);

        addParticipantButton.addActionListener(e -> {
            String name = participantNameField.getText();
            String email = participantEmailField.getText();
            Event event = organizer.events.get(participantEventCombo.getSelectedIndex());

            if (name.isEmpty() || email.isEmpty() || event == null) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            Participant participant = organizer.addParticipant(name, email);
            event.addParticipant(participant);
            tableModel.addRow(new Object[]{participant.participantId, participant.name, participant.email, event.name});
            JOptionPane.showMessageDialog(this, "Участник добавлен.");
        });

        return panel;
    }

    private JPanel createVenuesTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(1, 3));
        venueNameField = new JTextField();
        venueAddressField = new JTextField();
        JButton addVenueButton = new JButton("Добавить место");

        formPanel.add(new JLabel("Название:"));
        formPanel.add(venueNameField);
        formPanel.add(new JLabel("Адрес:"));
        formPanel.add(venueAddressField);
        formPanel.add(addVenueButton);

        panel.add(formPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID", "Название", "Адрес"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        venueTable = new JTable(tableModel);
        panel.add(new JScrollPane(venueTable), BorderLayout.CENTER);

        addVenueButton.addActionListener(e -> {
            String name = venueNameField.getText();
            String address = venueAddressField.getText();

            if (name.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            Venue venue = organizer.addVenue(name, address);
            tableModel.addRow(new Object[]{venue.venueId, venue.name, venue.address});
            eventVenueCombo.addItem(venue.name);
            JOptionPane.showMessageDialog(this, "Место проведения добавлено.");
        });

        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        JButton generateReportButton = new JButton("Создать отчет");

        generateReportButton.addActionListener(e -> {
            StringBuilder report = new StringBuilder("Отчет о мероприятиях и участниках:\n\n");

            for (Event event : organizer.events) {
                report.append(String.format("Мероприятие: %s (ID: %d)\n", event.name, event.eventId));
                report.append(String.format("Дата: %s, Место: %s\n", event.date, event.venue.name));
                report.append(String.format("Описание: %s\n", event.description));
                report.append(String.format("Количество участников: %d\n", event.participants.size()));

                if (event.participants.isEmpty()) {
                    report.append("Список участников: Нет зарегистрированных участников\n");
                } else {
                    report.append("Список участников:\n");
                    for (Participant participant : event.participants) {
                        report.append(String.format("  - %s (Email: %s)\n", participant.name, participant.email));
                    }
                }
                report.append("\n");
            }

            reportArea.setText(report.toString());
        });

        panel.add(generateReportButton, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EventManagementApp app = new EventManagementApp();
            app.setVisible(true);
        });
    }
}
