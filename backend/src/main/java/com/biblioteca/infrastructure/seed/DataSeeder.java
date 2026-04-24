package com.biblioteca.infrastructure.seed;

import com.biblioteca.domain.model.book.Book;
import com.biblioteca.domain.model.client.Client;
import com.biblioteca.domain.model.rental.Rental;
import com.biblioteca.domain.repository.BookRepository;
import com.biblioteca.domain.repository.ClientRepository;
import com.biblioteca.domain.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Order(2)
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final ClientRepository clientRepository;
    private final RentalRepository rentalRepository;

    private static final Random RANDOM = new Random(42);

    @Override
    @Transactional
    public void run(String... args) {
        if (bookRepository.count() > 0) return;

        List<Client> clients = seedClients();
        List<Book> books = seedBooks();
        seedRentals(books, clients);
    }

    // -------------------------------------------------------------------------
    // Clients — 25 first names × 8 last names = 200 clients
    // -------------------------------------------------------------------------

    private List<Client> seedClients() {
        String[] firstNames = {
            "Ana", "Bruno", "Carla", "Daniel", "Eduardo",
            "Fernanda", "Gabriel", "Helena", "Igor", "Juliana",
            "Kevin", "Larissa", "Marcos", "Natália", "Otávio",
            "Patricia", "Rafael", "Samara", "Thiago", "Ursula",
            "Vinícius", "Wanda", "Xavier", "Yasmin", "Zara"
        };
        String[] lastNames = {
            "Oliveira", "Santos", "Mendes", "Costa",
            "Lima", "Souza", "Pereira", "Rodrigues"
        };
        int[] ddds = {11, 21, 31, 41, 51, 61, 71, 81, 91};

        List<Client> clients = new ArrayList<>();
        int idx = 0;
        for (String last : lastNames) {
            for (String first : firstNames) {
                String email = slug(first) + "." + slug(last) + "@email.com";
                String phone = ddds[idx % ddds.length] + "9" + String.format("%08d", idx + 1);
                clients.add(clientRepository.save(new Client(first + " " + last, email, phone)));
                idx++;
            }
        }
        return clients;
    }

    private static String slug(String s) {
        return Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");
    }

    // -------------------------------------------------------------------------
    // Books — 50 titles × 4 copies = 200 books
    // All ISBNs verified with ISBN-13 checksum
    // -------------------------------------------------------------------------

    private List<Book> seedBooks() {
        record Bd(String name, String author, String isbn) {}

        var catalog = List.of(
            // Programação
            new Bd("Clean Code",                           "Robert C. Martin",     "9780132350884"),
            new Bd("Design Patterns",                      "Erich Gamma et al.",   "9780201633610"),
            new Bd("JavaScript: The Good Parts",           "Douglas Crockford",    "9780596517748"),
            new Bd("Domain-Driven Design",                 "Eric Evans",           "9780321125217"),
            new Bd("Effective Java",                       "Joshua Bloch",         "9780134685991"),
            new Bd("The Pragmatic Programmer",             "David Thomas",         "9780136083252"),
            new Bd("The Mythical Man-Month",               "Frederick Brooks",     "9780201835953"),
            new Bd("Code Complete",                        "Steve McConnell",      "9780735619678"),
            new Bd("The Clean Coder",                      "Robert C. Martin",     "9780137081073"),
            new Bd("Refactoring",                          "Martin Fowler",        "9780134757599"),
            new Bd("Introduction to Algorithms",           "Cormen et al.",        "9780262033848"),
            new Bd("Structure and Interpretation",         "Abelson e Sussman",    "9780262510875"),
            // Literatura Internacional
            new Bd("Pride and Prejudice",                  "Jane Austen",          "9780141439518"),
            new Bd("1984",                                 "George Orwell",        "9780451524935"),
            new Bd("Brave New World",                      "Aldous Huxley",        "9780060850524"),
            new Bd("To Kill a Mockingbird",                "Harper Lee",           "9780061120084"),
            new Bd("The Great Gatsby",                     "F. Scott Fitzgerald",  "9780743273565"),
            new Bd("Crime and Punishment",                 "Fiódor Dostoiévski",   "9780140449136"),
            new Bd("Don Quixote",                          "Miguel de Cervantes",  "9780060934347"),
            new Bd("The Brothers Karamazov",               "Fiódor Dostoiévski",   "9780374528379"),
            new Bd("War and Peace",                        "Liev Tolstói",         "9781400079988"),
            new Bd("The Odyssey",                          "Homero",               "9780140268867"),
            new Bd("Hamlet",                               "William Shakespeare",  "9780743477123"),
            new Bd("Moby Dick",                            "Herman Melville",      "9780142437247"),
            new Bd("The Catcher in the Rye",               "J.D. Salinger",        "9780316769174"),
            new Bd("Lord of the Flies",                    "William Golding",      "9780571295715"),
            new Bd("The Stranger",                         "Albert Camus",         "9780679720201"),
            // Literatura Brasileira
            new Bd("Dom Casmurro",                         "Machado de Assis",     "9788535909555"),
            new Bd("Memórias Póstumas de Brás Cubas",     "Machado de Assis",     "9788535914207"),
            new Bd("Grande Sertão: Veredas",               "Guimarães Rosa",       "9788535910780"),
            new Bd("O Cortiço",                            "Aluísio Azevedo",      "9788572329057"),
            new Bd("Iracema",                              "José de Alencar",      "9788535900064"),
            new Bd("O Guarani",                            "José de Alencar",      "9788535908565"),
            // Ficção Científica e Fantasia
            new Bd("Harry Potter e a Pedra Filosofal",     "J.K. Rowling",         "9780439708180"),
            new Bd("The Hobbit",                           "J.R.R. Tolkien",       "9780547928227"),
            new Bd("A Game of Thrones",                    "George R.R. Martin",   "9780553573404"),
            new Bd("Dune",                                 "Frank Herbert",        "9780441013593"),
            new Bd("Foundation",                           "Isaac Asimov",         "9780553293357"),
            new Bd("Ender's Game",                         "Orson Scott Card",     "9780812550702"),
            new Bd("Fahrenheit 451",                       "Ray Bradbury",         "9781451673319"),
            new Bd("The Hitchhiker's Guide to the Galaxy", "Douglas Adams",        "9780345391803"),
            new Bd("Neuromancer",                          "William Gibson",       "9780441569595"),
            new Bd("The Lord of the Rings",                "J.R.R. Tolkien",       "9780544003415"),
            // Não-Ficção e Clássicos Adicionais
            new Bd("Sapiens",                              "Yuval Noah Harari",    "9780062316097"),
            new Bd("Thinking, Fast and Slow",              "Daniel Kahneman",      "9780374533557"),
            new Bd("The Art of War",                       "Sun Tzu",              "9781590302255"),
            new Bd("The Alchemist",                        "Paulo Coelho",         "9780062315007"),
            new Bd("Animal Farm",                          "George Orwell",        "9780451526342"),
            new Bd("The Da Vinci Code",                    "Dan Brown",            "9780307474278"),
            new Bd("Frankenstein",                         "Mary Shelley",         "9780141439471")
        );

        var books = new ArrayList<Book>();
        int counter = 0;
        for (int copy = 0; copy < 4; copy++) {
            for (var bd : catalog) {
                counter++;
                var code = String.format("LIV-%05d", counter);
                books.add(bookRepository.save(new Book(code, bd.name(), bd.author(), bd.isbn())));
            }
        }
        return books;
    }

    // -------------------------------------------------------------------------
    // Rentals — 100 returned (histórico, último ano) + 60 ativas
    // -------------------------------------------------------------------------

    private void seedRentals(List<Book> books, List<Client> clients) {
        LocalDate today = LocalDate.now();

        for (int i = 0; i < 100; i++) {
            Book book = books.get(i);
            Client client = clients.get(i % clients.size());

            LocalDate startDate = today.minusDays(365 - (long) i * 3);
            LocalDate endDate = startDate.plusDays(7 + RANDOM.nextInt(22));
            int maxReturnOffset = (int) (endDate.toEpochDay() - startDate.toEpochDay());
            LocalDate returnedAt = startDate.plusDays(5 + RANDOM.nextInt(maxReturnOffset));

            Rental rental = new Rental(book, client, startDate, endDate);
            rental.returnBook();
            overrideReturnedAt(rental, returnedAt);
            rentalRepository.save(rental);
        }

        for (int i = 0; i < 60; i++) {
            Book book = books.get(100 + i);
            Client client = clients.get((100 + i) % clients.size());

            LocalDate startDate = today.minusDays(30 - i % 30);
            LocalDate endDate = startDate.plusDays(7 + RANDOM.nextInt(22));

            rentalRepository.save(new Rental(book, client, startDate, endDate));
        }
    }

    private void overrideReturnedAt(Rental rental, LocalDate date) {
        try {
            Field field = Rental.class.getDeclaredField("returnedAt");
            field.setAccessible(true);
            field.set(rental, date);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("DataSeeder: failed to set returnedAt", e);
        }
    }
}
