import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//Задание №2 из Лабараторной работы №1

// Класс Book
class Book {
    private String title;
    private String author;
    private String genre;
    private boolean isAvailable;

    public Book(String title, String author, String genre) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isAvailable = true;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    @Override
    public String toString() {
        return title + " by " + author + " (" + genre + ")";
    }
}

// Класс Reader
class Reader {
    private String name;
    private List<Book> borrowedBooks;

    public Reader(String name) {
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Book> getBorrowedBooks() {
        return borrowedBooks;
    }

    public void borrowBook(Book book) {
        borrowedBooks.add(book);
    }

    public void returnBook(Book book) {
        borrowedBooks.remove(book);
    }
}

// Класс Library
class Library {
    private List<Book> books;
    private List<Reader> readers;

    public Library() {
        this.books = new ArrayList<>();
        this.readers = new ArrayList<>();
    }

    // Геттеры для списков книг и читателей
    public List<Book> getBooks() {
        return books;
    }

    public List<Reader> getReaders() {
        return readers;
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Книга добавлена: " + book);
    }

    public void registerReader(Reader reader) {
        readers.add(reader);
        System.out.println("Читатель зарегистрирован: " + reader.getName());
    }

    public void checkoutBook(Reader reader, Book book) {
        if (book.isAvailable()) {
            reader.borrowBook(book);
            book.setAvailable(false);
            System.out.println(reader.getName() + " взял книгу: " + book.getTitle());
        } else {
            System.out.println("Книга уже занята.");
        }
    }

    public void returnBook(Reader reader, Book book) {
        if (reader.getBorrowedBooks().contains(book)) {
            reader.returnBook(book);
            book.setAvailable(true);
            System.out.println(reader.getName() + " вернул книгу: " + book.getTitle());
        } else {
            System.out.println("Читатель не брал эту книгу.");
        }
    }

    public List<Book> searchBooks(String author, String genre) {
        List<Book> results = new ArrayList<>();
        for (Book book : books) {
            if ((author == null || book.getAuthor().equalsIgnoreCase(author)) &&
                (genre == null || book.getGenre().equalsIgnoreCase(genre))) {
                results.add(book);
            }
        }
        return results;
    }

    public void generateReport() {
        System.out.println("Отчет о текущих взятых книгах:");
        for (Reader reader : readers) {
            for (Book book : reader.getBorrowedBooks()) {
                System.out.println(reader.getName() + " взял " + book.getTitle());
            }
        }
    }
}

// Интерфейс управления библиотекой через командную строку
public class LibraryManagementSystem {
    private static Library library = new Library();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Добро пожаловать в библиотеку!");

        while (true) {
            System.out.println("\n1. Добавить книгу");
            System.out.println("2. Зарегистрировать читателя");
            System.out.println("3. Взять книгу");
            System.out.println("4. Вернуть книгу");
            System.out.println("5. Найти книги");
            System.out.println("6. Показать отчет о взятых книгах");
            System.out.println("7. Выйти");
            System.out.print("Выберите действие: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addBook();
                case 2 -> registerReader();
                case 3 -> checkoutBook();
                case 4 -> returnBook();
                case 5 -> findBooks();
                case 6 -> library.generateReport();
                case 7 -> {
                    System.out.println("Выход из системы...");
                    return;
                }
                default -> System.out.println("Некорректный выбор.");
            }
        }
    }

    private static void addBook() {
        System.out.print("Введите название книги: ");
        String title = scanner.nextLine();
        System.out.print("Введите автора книги: ");
        String author = scanner.nextLine();
        System.out.print("Введите жанр книги: ");
        String genre = scanner.nextLine();

        library.addBook(new Book(title, author, genre));
    }

    private static void registerReader() {
        System.out.print("Введите имя читателя: ");
        String name = scanner.nextLine();
        library.registerReader(new Reader(name));
    }

    private static void checkoutBook() {
        System.out.print("Введите имя читателя: ");
        String readerName = scanner.nextLine();
        Reader reader = findReader(readerName);

        if (reader == null) {
            System.out.println("Читатель не найден.");
            return;
        }

        System.out.print("Введите название книги для взятия: ");
        String title = scanner.nextLine();
        Book book = findBook(title);

        if (book != null && book.isAvailable()) {
            library.checkoutBook(reader, book);
        } else {
            System.out.println("Книга не найдена или недоступна.");
        }
    }

    private static void returnBook() {
        System.out.print("Введите имя читателя: ");
        String readerName = scanner.nextLine();
        Reader reader = findReader(readerName);

        if (reader == null) {
            System.out.println("Читатель не найден.");
            return;
        }

        System.out.print("Введите название книги для возврата: ");
        String title = scanner.nextLine();
        Book book = findBorrowedBook(reader, title);

        if (book != null) {
            library.returnBook(reader, book);
        } else {
            System.out.println("Эта книга не числится у читателя.");
        }
    }

    private static void findBooks() {
        System.out.print("Введите автора (или оставьте пустым): ");
        String author = scanner.nextLine();
        System.out.print("Введите жанр (или оставьте пустым): ");
        String genre = scanner.nextLine();

        List<Book> foundBooks = library.searchBooks(
                author.isEmpty() ? null : author,
                genre.isEmpty() ? null : genre
        );

        if (foundBooks.isEmpty()) {
            System.out.println("Книги не найдены.");
        } else {
            System.out.println("Найденные книги:");
            foundBooks.forEach(System.out::println);
        }
    }

    private static Reader findReader(String name) {
        return library.getReaders().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    private static Book findBook(String title) {
        return library.getBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst().orElse(null);
    }

    private static Book findBorrowedBook(Reader reader, String title) {
        return reader.getBorrowedBooks().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .findFirst().orElse(null);
    }
}
