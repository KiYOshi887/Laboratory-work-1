import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Класс Product
class Product {
    private String name;
    private double price;
    private int stock;

    public Product(String name, double price, int stock) {
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void updateStock(int quantity) {
        this.stock -= quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Класс Customer
class Customer {
    private String name;
    private String email;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Класс Region
class Region {
    private String name;

    public Region(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}

// Класс Sale
class Sale {
    private LocalDate date;
    private Product product;
    private int quantity;
    private double totalPrice;
    private Customer customer;
    private Region region;

    public Sale(LocalDate date, Product product, int quantity, Customer customer, Region region) {
        this.date = date;
        this.product = product;
        this.quantity = quantity;
        this.totalPrice = product.getPrice() * quantity;
        this.customer = customer;
        this.region = region;
    }

    public LocalDate getDate() {
        return date;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Region getRegion() {
        return region;
    }
}

// Основное приложение
public class SalesManagementApp extends JFrame {
    private List<Product> products;
    private List<Customer> customers;
    private List<Region> regions;
    private List<Sale> sales;

    private JComboBox<Product> productComboBox;
    private JComboBox<Customer> customerComboBox;
    private JComboBox<Region> regionComboBox;

    public SalesManagementApp() {
        products = new ArrayList<>();
        customers = new ArrayList<>();
        regions = new ArrayList<>();
        sales = new ArrayList<>();

        // Инициализация окна
        setTitle("Система учета продаж");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Создание вкладок
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Продажи", createSalesTab());
        tabbedPane.add("Товары", createProductsTab());
        tabbedPane.add("Клиенты", createCustomersTab());
        tabbedPane.add("Регионы", createRegionsTab());
        tabbedPane.add("Отчеты", createReportsTab());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createSalesTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Поля для добавления продажи
        productComboBox = new JComboBox<>();
        customerComboBox = new JComboBox<>();
        regionComboBox = new JComboBox<>();
        JTextField quantityField = new JTextField();

        // Кнопка добавления продажи
        JButton addSaleButton = new JButton("Добавить продажу");

        // Таблица продаж
        DefaultTableModel salesTableModel = new DefaultTableModel(new String[]{"Дата", "Товар", "Количество", "Сумма", "Клиент", "Регион"}, 0);
        JTable salesTable = new JTable(salesTableModel);

        addSaleButton.addActionListener(e -> {
            Product product = (Product) productComboBox.getSelectedItem();
            Customer customer = (Customer) customerComboBox.getSelectedItem();
            Region region = (Region) regionComboBox.getSelectedItem();
            int quantity;

            try {
                quantity = Integer.parseInt(quantityField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Количество должно быть числом", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (product != null && customer != null && region != null) {
                if (quantity > product.getStock()) {
                    JOptionPane.showMessageDialog(this, "Недостаточно товара на складе", "Ошибка", JOptionPane.ERROR_MESSAGE);
                } else {
                    product.updateStock(quantity);
                    Sale sale = new Sale(LocalDate.now(), product, quantity, customer, region);
                    sales.add(sale);
                    salesTableModel.addRow(new Object[]{sale.getDate(), product.getName(), quantity, sale.getTotalPrice(), customer.getName(), region.getName()});
                    JOptionPane.showMessageDialog(this, "Продажа добавлена", "Успех", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        JPanel formPanel = new JPanel(new GridLayout(5, 2));
        formPanel.add(new JLabel("Товар"));
        formPanel.add(productComboBox);
        formPanel.add(new JLabel("Клиент"));
        formPanel.add(customerComboBox);
        formPanel.add(new JLabel("Регион"));
        formPanel.add(regionComboBox);
        formPanel.add(new JLabel("Количество"));
        formPanel.add(quantityField);
        formPanel.add(new JLabel());
        formPanel.add(addSaleButton);

        panel.add(new JScrollPane(salesTable), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createProductsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица продуктов
        DefaultTableModel productsTableModel = new DefaultTableModel(new String[]{"Название", "Цена", "На складе"}, 0);
        JTable productsTable = new JTable(productsTableModel);

        // Поля для добавления продуктов
        JTextField productNameField = new JTextField();
        JTextField productPriceField = new JTextField();
        JTextField productStockField = new JTextField();
        JButton addProductButton = new JButton("Добавить товар");

        addProductButton.addActionListener(e -> {
            String name = productNameField.getText();
            double price;
            int stock;

            try {
                price = Double.parseDouble(productPriceField.getText());
                stock = Integer.parseInt(productStockField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Цена и количество должны быть числами", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Product product = new Product(name, price, stock);
            products.add(product);
            productsTableModel.addRow(new Object[]{name, price, stock});
            updateProductComboBox();
            JOptionPane.showMessageDialog(this, "Товар добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel formPanel = new JPanel(new GridLayout(4, 2));
        formPanel.add(new JLabel("Название"));
        formPanel.add(productNameField);
        formPanel.add(new JLabel("Цена"));
        formPanel.add(productPriceField);
        formPanel.add(new JLabel("На складе"));
        formPanel.add(productStockField);
        formPanel.add(new JLabel());
        formPanel.add(addProductButton);

        panel.add(new JScrollPane(productsTable), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCustomersTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица клиентов
        DefaultTableModel customersTableModel = new DefaultTableModel(new String[]{"Имя", "Email"}, 0);
        JTable customersTable = new JTable(customersTableModel);

        // Поля для добавления клиентов
        JTextField customerNameField = new JTextField();
        JTextField customerEmailField = new JTextField();
        JButton addCustomerButton = new JButton("Добавить клиента");

        addCustomerButton.addActionListener(e -> {
            String name = customerNameField.getText();
            String email = customerEmailField.getText();

            if (name.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Customer customer = new Customer(name, email);
            customers.add(customer);
            customersTableModel.addRow(new Object[]{name, email});
            updateCustomerComboBox();
            JOptionPane.showMessageDialog(this, "Клиент добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel formPanel = new JPanel(new GridLayout(3, 2));
        formPanel.add(new JLabel("Имя"));
        formPanel.add(customerNameField);
        formPanel.add(new JLabel("Email"));
        formPanel.add(customerEmailField);
        formPanel.add(new JLabel());
        formPanel.add(addCustomerButton);

        panel.add(new JScrollPane(customersTable), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createRegionsTab() {
        JPanel panel = new JPanel(new BorderLayout());

        // Таблица регионов
        DefaultTableModel regionsTableModel = new DefaultTableModel(new String[]{"Регион"}, 0);
        JTable regionsTable = new JTable(regionsTableModel);

        // Поля для добавления региона
        JTextField regionNameField = new JTextField();
        JButton addRegionButton = new JButton("Добавить регион");

        addRegionButton.addActionListener(e -> {
            String name = regionNameField.getText();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название региона", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Region region = new Region(name);
            regions.add(region);
            regionsTableModel.addRow(new Object[]{name});
            updateRegionComboBox();
            JOptionPane.showMessageDialog(this, "Регион добавлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.add(new JLabel("Название региона"));
        formPanel.add(regionNameField);
        formPanel.add(new JLabel());
        formPanel.add(addRegionButton);

        panel.add(new JScrollPane(regionsTable), BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);

        JButton generateReportButton = new JButton("Сгенерировать отчет");
        generateReportButton.addActionListener(e -> {
            StringBuilder report = new StringBuilder("Отчет по продажам:\n\n");
            for (Sale sale : sales) {
                report.append("Дата: ").append(sale.getDate())
                        .append(", Товар: ").append(sale.getProduct().getName())
                        .append(", Количество: ").append(sale.getQuantity())
                        .append(", Сумма: ").append(sale.getTotalPrice())
                        .append(", Клиент: ").append(sale.getCustomer().getName())
                        .append(", Регион: ").append(sale.getRegion().getName())
                        .append("\n");
            }
            reportArea.setText(report.toString());
        });

        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);
        panel.add(generateReportButton, BorderLayout.SOUTH);

        return panel;
    }

    private void updateProductComboBox() {
        productComboBox.removeAllItems();
        for (Product product : products) {
            productComboBox.addItem(product);
        }
    }

    private void updateCustomerComboBox() {
        customerComboBox.removeAllItems();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer);
        }
    }

    private void updateRegionComboBox() {
        regionComboBox.removeAllItems();
        for (Region region : regions) {
            regionComboBox.addItem(region);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SalesManagementApp app = new SalesManagementApp();
            app.setVisible(true);
        });
    }
}
