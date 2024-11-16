import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

//Задание №1 из Лабараторной работы №1

// Класс BankAccount
abstract class BankAccount {
    protected double balance;
    protected List<Transaction> transactions;

    public BankAccount() {
        this.balance = 0.0;
        this.transactions = new ArrayList<>();
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactions.add(new Transaction(new Date(), amount, "Deposit"));
        } else {
            System.out.println("Сумма депозита должна быть положительной.");
        }
    }

    public void withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            transactions.add(new Transaction(new Date(), -amount, "Withdraw"));
        } else {
            System.out.println("Недостаточно средств для снятия или некорректная сумма.");
        }
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getLastTransactions(int n) {
        return transactions.subList(Math.max(transactions.size() - n, 0), transactions.size());
    }

    public abstract void accountInfo();
}

// Класс SavingsAccount с начислением процентов
class SavingsAccount extends BankAccount {
    private double interestRate;

    public SavingsAccount(double interestRate) {
        super();
        this.interestRate = interestRate;
    }

    public void applyInterest() {
        double interest = balance * interestRate / 100;
        balance += interest;
        transactions.add(new Transaction(new Date(), interest, "Interest Applied"));
    }

    @Override
    public void accountInfo() {
        System.out.println("Сберегательный счет. Баланс: " + balance + ", Процентная ставка: " + interestRate + "%");
    }
}

// Класс CheckingAccount с проверкой переплаты
class CheckingAccount extends BankAccount {
    private double overdraftLimit;

    public CheckingAccount(double overdraftLimit) {
        super();
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void withdraw(double amount) {
        if (amount > 0 && balance + overdraftLimit >= amount) {
            balance -= amount;
            transactions.add(new Transaction(new Date(), -amount, "Withdraw"));
        } else {
            System.out.println("Превышен лимит переплаты или некорректная сумма.");
        }
    }

    @Override
    public void accountInfo() {
        System.out.println("Расчетный счет. Баланс: " + balance + ", Лимит переплаты: " + overdraftLimit);
    }
}

// Класс Transaction для хранения информации о транзакциях
class Transaction {
    private Date date;
    private double amount;
    private String description;

    public Transaction(Date date, double amount, String description) {
        this.date = date;
        this.amount = amount;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Дата: " + date + ", Сумма: " + amount + ", Описание: " + description;
    }
}

// Класс Customer для связи пользователя с аккаунтами
class Customer {
    private String name;
    private List<BankAccount> accounts;

    public Customer(String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
    }

    public void addAccount(BankAccount account) {
        accounts.add(account);
    }

    public List<BankAccount> getAccounts() {
        return accounts;
    }

    public String getName() {
        return name;
    }
}

// Класс Bank для управления аккаунтами и транзакциями
class Bank {
    private HashMap<String, Customer> customers;

    public Bank() {
        customers = new HashMap<>();
    }

    public Customer createCustomer(String name) {
        Customer customer = new Customer(name);
        customers.put(name, customer);
        return customer;
    }

    public void transferFunds(BankAccount from, BankAccount to, double amount) {
        if (from.getBalance() >= amount) {
            from.withdraw(amount);
            to.deposit(amount);
            System.out.println("Перевод выполнен успешно.");
        } else {
            System.out.println("Недостаточно средств для перевода.");
        }
    }

    public Customer getCustomer(String name) {
        return customers.get(name);
    }
}

// Интерфейс для управления через командную строку
public class exercise1 {
    private static Bank bank = new Bank();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в систему управления банковскими счетами!");

        while (true) {
            System.out.println("1. Создать нового клиента");
            System.out.println("2. Добавить счет клиенту");
            System.out.println("3. Внести депозит");
            System.out.println("4. Снять средства");
            System.out.println("5. Перевести средства");
            System.out.println("6. Показать транзакции");
            System.out.println("7. Выйти");
            System.out.print("Выберите действие: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createCustomer();
                case 2 -> addAccount();
                case 3 -> deposit();
                case 4 -> withdraw();
                case 5 -> transfer();
                case 6 -> showTransactions();
                case 7 -> {
                    System.out.println("Выход из системы...");
                    return;
                }
                default -> System.out.println("Некорректный выбор.");
            }
        }
    }

    private static void createCustomer() {
        System.out.print("Введите имя клиента: ");
        String name = scanner.nextLine();
        Customer customer = bank.createCustomer(name);
        System.out.println("Клиент " + customer.getName() + " создан.");
    }

    private static void addAccount() {
        System.out.print("Введите имя клиента: ");
        String name = scanner.nextLine();
        Customer customer = bank.getCustomer(name);
        if (customer == null) {
            System.out.println("Клиент не найден.");
            return;
        }

        System.out.println("Выберите тип счета: 1. Сберегательный 2. Расчетный");
        int type = scanner.nextInt();
        scanner.nextLine();

        if (type == 1) {
            System.out.print("Введите процентную ставку: ");
            double rate = scanner.nextDouble();
            customer.addAccount(new SavingsAccount(rate));
            System.out.println("Сберегательный счет добавлен клиенту " + customer.getName());
        } else if (type == 2) {
            System.out.print("Введите лимит переплаты: ");
            double limit = scanner.nextDouble();
            customer.addAccount(new CheckingAccount(limit));
            System.out.println("Расчетный счет добавлен клиенту " + customer.getName());
        } else {
            System.out.println("Некорректный тип счета.");
        }
    }

    private static void deposit() {
        // Реализация функции депозита
    }

    private static void withdraw() {
        // Реализация функции снятия средств
    }

    private static void transfer() {
        // Реализация функции перевода средств
    }

    private static void showTransactions() {
        // Реализация показа транзакций
    }
}
