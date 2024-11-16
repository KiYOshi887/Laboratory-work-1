import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Задание №3 из Лабараторной работы №1

// Класс Product
class Product {
    private String name;
    private double price;
    private int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void addQuantity(int amount) {
        this.quantity += amount;
    }

    public void removeQuantity(int amount) {
        if (amount <= this.quantity) {
            this.quantity -= amount;
        } else {
            System.out.println("Недостаточно товара на складе для списания.");
        }
    }

    @Override
    public String toString() {
        return name + " (Цена: " + price + ", Количество: " + quantity + ")";
    }
}

// Класс Category
class Category {
    private String name;
    private List<Product> products;

    public Category(String name) {
        this.name = name;
        this.products = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public Product findProduct(String productName) {
        return products.stream()
                .filter(product -> product.getName().equalsIgnoreCase(productName))
                .findFirst().orElse(null);
    }
}

// Класс Inventory
class Inventory {
    private Map<String, Category> categories;

    public Inventory() {
        categories = new HashMap<>();
    }

    public void addCategory(Category category) {
        categories.put(category.getName(), category);
    }

    public void addProductToCategory(String categoryName, Product product) {
        Category category = categories.get(categoryName);
        if (category == null) {
            category = new Category(categoryName);
            categories.put(categoryName, category);
        }
        category.addProduct(product);
    }

    public Product findProduct(String categoryName, String productName) {
        Category category = categories.get(categoryName);
        if (category != null) {
            return category.findProduct(productName);
        }
        return null;
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder("Отчет о текущем состоянии инвентаря:\n");
        categories.values().forEach(category -> {
            report.append("Категория: ").append(category.getName()).append("\n");
            category.getProducts().forEach(product -> report.append(" - ").append(product.toString()).append("\n"));
        });
        return report.toString();
    }
}

// Класс GUI для управления инвентарем
public class InventoryManagementGUI extends JFrame implements ActionListener {
    private Inventory inventory = new Inventory();

    private JTextField categoryField, productField, priceField, quantityField, stockProductField, stockAmountField;
    private JTextArea reportArea;

    public InventoryManagementGUI() {
        setTitle("Система управления инвентарем");
        setSize(1000, 600); // Установлен фиксированный размер окна
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false); // Запрет изменения размера окна

        // Панель для категорий
        JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        categoryPanel.add(new JLabel("Категория:"));
        categoryField = new JTextField(15);
        categoryPanel.add(categoryField);
        JButton addCategoryButton = new JButton("Добавить категорию");
        addCategoryButton.addActionListener(this);
        categoryPanel.add(addCategoryButton);

        // Панель для продуктов
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productPanel.add(new JLabel("Продукт:"));
        productField = new JTextField(10);
        productPanel.add(productField);
        productPanel.add(new JLabel("Цена:"));
        priceField = new JTextField(5);
        productPanel.add(priceField);
        productPanel.add(new JLabel("Количество:"));
        quantityField = new JTextField(5);
        productPanel.add(quantityField);
        JButton addProductButton = new JButton("Добавить продукт");
        addProductButton.addActionListener(this);
        productPanel.add(addProductButton);

        // Панель для управления количеством на складе
        JPanel stockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stockPanel.add(new JLabel("Продукт для добавления/удаления:"));
        stockProductField = new JTextField(10);
        stockPanel.add(stockProductField);
        stockPanel.add(new JLabel("Количество:"));
        stockAmountField = new JTextField(5);
        stockPanel.add(stockAmountField);
        JButton addStockButton = new JButton("Добавить на склад");
        addStockButton.addActionListener(this);
        stockPanel.add(addStockButton);
        JButton removeStockButton = new JButton("Удалить со склада");
        removeStockButton.addActionListener(this);
        stockPanel.add(removeStockButton);

        // Панель для отчета
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportArea = new JTextArea(12, 50); // Уменьшено количество строк для отображения отчета
        reportArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(reportArea);
        reportPanel.add(scrollPane, BorderLayout.CENTER);

        // Добавляем панели в главное окно
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5)); // Увеличен промежуток для кнопок и текстовых полей
        topPanel.add(categoryPanel);
        topPanel.add(productPanel);
        topPanel.add(stockPanel);
        add(topPanel, BorderLayout.NORTH);
        add(reportPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "Добавить категорию":
                String categoryName = categoryField.getText();
                if (!categoryName.isEmpty()) {
                    inventory.addCategory(new Category(categoryName));
                    categoryField.setText("");
                    JOptionPane.showMessageDialog(this, "Категория добавлена.");
                    updateReport();
                }
                break;

            case "Добавить продукт":
                categoryName = categoryField.getText();
                String productName = productField.getText();
                try {
                    double price = Double.parseDouble(priceField.getText());
                    int quantity = Integer.parseInt(quantityField.getText());
                    if (!categoryName.isEmpty() && !productName.isEmpty()) {
                        inventory.addProductToCategory(categoryName, new Product(productName, price, quantity));
                        productField.setText("");
                        priceField.setText("");
                        quantityField.setText("");
                        JOptionPane.showMessageDialog(this, "Продукт добавлен.");
                        updateReport();
                    } else {
                        JOptionPane.showMessageDialog(this, "Введите название категории и продукта.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректный формат цены или количества.");
                }
                break;

            case "Добавить на склад":
                categoryName = categoryField.getText();
                productName = stockProductField.getText();
                try {
                    int amount = Integer.parseInt(stockAmountField.getText());
                    Product product = inventory.findProduct(categoryName, productName);
                    if (product != null) {
                        product.addQuantity(amount);
                        stockAmountField.setText("");
                        JOptionPane.showMessageDialog(this, "Товар добавлен на склад.");
                        updateReport();
                    } else {
                        JOptionPane.showMessageDialog(this, "Продукт не найден.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректный формат количества.");
                }
                break;

            case "Удалить со склада":
                categoryName = categoryField.getText();
                productName = stockProductField.getText();
                try {
                    int amount = Integer.parseInt(stockAmountField.getText());
                    Product product = inventory.findProduct(categoryName, productName);
                    if (product != null) {
                        product.removeQuantity(amount);
                        stockAmountField.setText("");
                        JOptionPane.showMessageDialog(this, "Товар удален со склада.");
                        updateReport();
                    } else {
                        JOptionPane.showMessageDialog(this, "Продукт не найден.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Некорректный формат количества.");
                }
                break;
        }
    }

    private void updateReport() {
        reportArea.setText(inventory.generateReport());
    }

    public static void main(String[] args) {
        new InventoryManagementGUI();
    }
}
