import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Классы данных
class Product {
    int productId;
    String name;
    double price;
    String description;
    int quantity;
    String category;

    public Product(int productId, String name, double price, String description, int quantity, String category) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.description = description;
        this.quantity = quantity;
        this.category = category;
    }
}

class Customer {
    int customerId;
    String name;
    String email;
    List<Order> orders = new ArrayList<>();

    public Customer(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }
}

class Order {
    static final String[] STATUS_OPTIONS = {"В обработке", "Готов к выдаче"};
    int orderId;
    Customer customer;
    List<OrderItem> products;
    String status;
    String date;

    public Order(int orderId, Customer customer, List<OrderItem> products) {
        this.orderId = orderId;
        this.customer = customer;
        this.products = products;
        this.status = STATUS_OPTIONS[0];
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

class OrderItem {
    Product product;
    int quantity;

    public OrderItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
}

// Основное приложение
public class OnlineStoreApp extends JFrame {
    private List<Product> products = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private List<Order> orders = new ArrayList<>();
    private int nextProductId = 1;
    private int nextCustomerId = 1;
    private int nextOrderId = 1;

    // Конструктор приложения
    public OnlineStoreApp() {
        setTitle("Система управления онлайн-магазином");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Товары", createProductsTab());
        tabbedPane.add("Клиенты", createCustomersTab());
        tabbedPane.add("Заказы", createOrdersTab());
        tabbedPane.add("Отчеты", createReportsTab());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createProductsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Форма добавления товаров
        JPanel formPanel = new JPanel(new GridLayout(2, 6));
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField descField = new JTextField();
        JTextField qtyField = new JTextField();
        JTextField categoryField = new JTextField();
        JButton addProductButton = new JButton("Добавить товар");

        formPanel.add(new JLabel("Название:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Цена:"));
        formPanel.add(priceField);
        formPanel.add(new JLabel("Описание:"));
        formPanel.add(descField);
        formPanel.add(new JLabel("Количество:"));
        formPanel.add(qtyField);
        formPanel.add(new JLabel("Категория:"));
        formPanel.add(categoryField);
        formPanel.add(addProductButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Таблица товаров
        String[] columnNames = {"ID", "Название", "Цена", "Описание", "Количество", "Категория"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable productTable = new JTable(tableModel);
        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        // Событие для добавления товаров
        addProductButton.addActionListener(e -> {
            try {
                String name = nameField.getText();
                double price = Double.parseDouble(priceField.getText());
                String description = descField.getText();
                int quantity = Integer.parseInt(qtyField.getText());
                String category = categoryField.getText();

                Product product = new Product(nextProductId++, name, price, description, quantity, category);
                products.add(product);

                tableModel.addRow(new Object[]{product.productId, product.name, product.price, product.description, product.quantity, product.category});
                JOptionPane.showMessageDialog(this, "Товар добавлен!");

                nameField.setText("");
                priceField.setText("");
                descField.setText("");
                qtyField.setText("");
                categoryField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные значения для цены и количества.");
            }
        });

        return panel;
    }

    private JPanel createCustomersTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Форма добавления клиентов
        JPanel formPanel = new JPanel(new GridLayout(1, 4));
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JButton addCustomerButton = new JButton("Добавить клиента");

        formPanel.add(new JLabel("Имя:"));
        formPanel.add(nameField);
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        formPanel.add(addCustomerButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Таблица клиентов
        String[] columnNames = {"ID", "Имя", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable customerTable = new JTable(tableModel);
        panel.add(new JScrollPane(customerTable), BorderLayout.CENTER);

        // Событие для добавления клиентов
        addCustomerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            Customer customer = new Customer(nextCustomerId++, name, email);
            customers.add(customer);

            tableModel.addRow(new Object[]{customer.customerId, customer.name, customer.email});
            JOptionPane.showMessageDialog(this, "Клиент добавлен!");

            nameField.setText("");
            emailField.setText("");
        });

        return panel;
    }

    private JPanel createOrdersTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Форма добавления заказов
        JPanel formPanel = new JPanel(new GridLayout(1, 5));
        JTextField customerIdField = new JTextField();
        JTextField productIdField = new JTextField();
        JTextField quantityField = new JTextField();
        JComboBox<String> statusComboBox = new JComboBox<>(Order.STATUS_OPTIONS);
        JButton addOrderButton = new JButton("Добавить заказ");

        formPanel.add(new JLabel("ID клиента:"));
        formPanel.add(customerIdField);
        formPanel.add(new JLabel("ID товара:"));
        formPanel.add(productIdField);
        formPanel.add(new JLabel("Количество:"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel("Статус:"));
        formPanel.add(statusComboBox);
        formPanel.add(addOrderButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Таблица заказов
        String[] columnNames = {"ID заказа", "Клиент", "Товар", "Статус"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable orderTable = new JTable(tableModel);
        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);

        // Событие для добавления заказов
        addOrderButton.addActionListener(e -> {
            try {
                int customerId = Integer.parseInt(customerIdField.getText());
                int productId = Integer.parseInt(productIdField.getText());
                int quantity = Integer.parseInt(quantityField.getText());
                String status = (String) statusComboBox.getSelectedItem();

                Customer customer = customers.stream().filter(c -> c.customerId == customerId).findFirst().orElse(null);
                Product product = products.stream().filter(p -> p.productId == productId).findFirst().orElse(null);

                if (customer == null || product == null) {
                    JOptionPane.showMessageDialog(this, "Клиент или товар не найден.");
                    return;
                }

                OrderItem orderItem = new OrderItem(product, quantity);
                List<OrderItem> orderItems = new ArrayList<>();
                orderItems.add(orderItem);

                Order order = new Order(nextOrderId++, customer, orderItems);
                order.status = status;
                orders.add(order);

                tableModel.addRow(new Object[]{order.orderId, customer.name, product.name, order.status});
                JOptionPane.showMessageDialog(this, "Заказ добавлен!");

                customerIdField.setText("");
                productIdField.setText("");
                quantityField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректные значения для ID и количества.");
            }
        });

        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Выбор отчета
        JPanel formPanel = new JPanel();
        JComboBox<String> reportTypeComboBox = new JComboBox<>(new String[]{"Отчет по товарам", "Отчет по клиентам"});
        JButton generateReportButton = new JButton("Создать отчет");
        JTextArea reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);

        formPanel.add(reportTypeComboBox);
        formPanel.add(generateReportButton);
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(reportTextArea), BorderLayout.CENTER);

        // Генерация отчета
        generateReportButton.addActionListener(e -> {
            String reportType = (String) reportTypeComboBox.getSelectedItem();
            StringBuilder report = new StringBuilder();

            if ("Отчет по товарам".equals(reportType)) {
                report.append("Отчет по товарам:\n");
                for (Product product : products) {
                    report.append(String.format("ID: %d, Название: %s, Цена: %.2f, Количество: %d\n",
                            product.productId, product.name, product.price, product.quantity));
                }
            } else if ("Отчет по клиентам".equals(reportType)) {
                report.append("Отчет по клиентам:\n");
                for (Customer customer : customers) {
                    report.append(String.format("ID: %d, Имя: %s, Email: %s\n", customer.customerId, customer.name, customer.email));
                    for (Order order : customer.orders) {
                        report.append(String.format("  Заказ ID %d, Статус: %s\n", order.orderId, order.status));
                        for (OrderItem item : order.products) {
                            report.append(String.format("    - %s x%d\n", item.product.name, item.quantity));
                        }
                    }
                }
            }
            reportTextArea.setText(report.toString());
        });

        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            OnlineStoreApp app = new OnlineStoreApp();
            app.setVisible(true);
        });
    }
}
