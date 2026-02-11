package repository;

import domain.*;
import validation.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.DriverManager;
import java.util.List;
import paging.*;

public class UserDBRepository implements DuckRepository {
    private String url;
    private String username;
    private String password;
    private Validator<User> validator;

    public UserDBRepository(String url, String username, String password, Validator<User> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public User save(User entity) {
        // 1. SQL-ul de inserare (folosim ? ca placeholder)
        String sql = "INSERT INTO users (tip_utilizator, username, email, password, nume, prenume, ocupatie, nivel_empatie, viteza, rezistenta) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // 2. Setăm valorile în funcție de tipul obiectului
            if (entity instanceof Person) {
                ps.setString(1, "PERSON");
                Person p = (Person) entity;
                ps.setString(5, p.getNume());
                ps.setString(6, p.getPrenume());
                ps.setString(7, p.getOcupatie());
                ps.setInt(8, p.getNivelEmpatie());

                // Setăm NULL pentru câmpurile de rață
                ps.setObject(9, null);
                ps.setObject(10, null);
            } else if (entity instanceof Duck) {
                Duck d = (Duck) entity;
                // Setăm tipul specific de rață
                if (d instanceof SwimmingDuck) ps.setString(1, "SWIMMING_DUCK");
                else if (d instanceof FlyingDuck) ps.setString(1, "FLYING_DUCK");
                else ps.setString(1, "DUCK");

                // Setăm NULL pentru câmpurile de persoană
                ps.setString(5, null);
                ps.setString(6, null);
                ps.setString(7, null);
                ps.setObject(8, null);

                // Setăm valorile de rață
                ps.setDouble(9, d.getViteza());
                ps.setDouble(10, d.getRezistenta());
            }

            // Datele comune
            ps.setString(2, entity.getUsername());
            ps.setString(3, entity.getEmail());
            ps.setString(4, entity.getPassword());

            // 3. Executăm
            ps.executeUpdate();

            // 4. Luăm ID-ul generat de Postgres și îl punem în obiect
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }

            // Returnăm null (succes, conform convenției tale)
            return null;

        } catch (SQLException e) {
            e.printStackTrace();
            return entity; // Returnăm entitatea în caz de eroare
        }
    }

    @Override
    public User findOne(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        System.out.println("--- DEBUG findOne(" + id + ") ---");
        System.out.println("    1. URL Conectare: " + this.url);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);

            // Verificăm dacă găsim ceva
            ResultSet rs = ps.executeQuery();
            boolean hasRow = rs.next();
            System.out.println("    2. Rezultat Query (rs.next()): " + hasRow);

            if (hasRow) {
                // Debugging pentru datele citite
                String tip = rs.getString("tip_utilizator");
                String user = rs.getString("username");
                System.out.println("    3. Date brute găsite -> Tip: " + tip + ", Username: " + user);

                // Încercăm conversia
                User u = extractUserFromResultSet(rs);
                System.out.println("    4. Conversie reușită: " + (u != null));
                return u;
            } else {
                System.err.println("    !!! EROARE: Rândul cu ID " + id + " NU a fost găsit în DB!");
            }
        } catch (SQLException e) {
            System.err.println("    !!! EROARE SQL: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("    !!! EROARE JAVA (Constructor/Logică): " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("--- END DEBUG ---");
        return null;
    }

    @Override
    public Iterable<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User delete(Long id) {
        // Opțional: Poți face findOne(id) înainte dacă vrei să returnezi obiectul șters
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public User update(User entity) {
        // Aici ar veni logica de UPDATE users SET ... WHERE id = ...
        return null;
    }

    // --- Helper: Transformă un rând din DB în obiect Java ---
    // IN UserDBRepository.java (Metoda extractUserFromResultSet)

    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        String tip = rs.getString("tip_utilizator");
        Long id = rs.getLong("id");
        String username = rs.getString("username");
        String email = rs.getString("email");
        String password = rs.getString("password");

        // --- Citirea Vitezei și Rezistenței în mod sigur ---

        // 1. Încercăm să citim Viteza și Rezistența
        double viteza = rs.getDouble("viteza");
        double rezistenta = rs.getDouble("rezistenta");

        // 2. Verificăm imediat dacă ULTIMA valoare citită (rezistenta) a fost NULL
        boolean isDuckDataNull = rs.wasNull();

        // --- Logică de Reconstrucție ---

        if ("PERSON".equals(tip)) {
            // ... (Logica ta de reconstrucție Person) ...
            String nume = rs.getString("nume");
            String prenume = rs.getString("prenume");
            String ocupatie = rs.getString("ocupatie");
            int empatie = rs.getInt("nivel_empatie");

            // Importă java.time.LocalDate sus
            return new Person(id, username, email, password, nume, prenume, LocalDate.now(), ocupatie, empatie);
        } else {
            // DUCK sau tip specific DUCK

            // 3. Dacă viteza sau rezistența sunt <= 0, înseamnă date corupte sau nevalide.
            // Pentru a preveni crash-ul, poți folosi o valoare default minimă (ex: 1.0)
            // sau cea mai mică valoare nenulă din DB (dacă ai fi implementat un loader mai inteligent).

            if (viteza <= 0.0 || rezistenta <= 0.0 || isDuckDataNull) {
                System.err.println("Avertisment DB: Rața " + username + " are viteza zero. Poate eșua în cursă.");
                // Înlocuim cu o valoare sigură, deși invalidă (pentru a nu crăpa)
                if (viteza <= 0.0) viteza = 0.1;
                if (rezistenta <= 0.0) rezistenta = 0.1;
            }

            // ... (Reconstrucția obiectului specific Duck) ...
            if ("SWIMMING_DUCK".equals(tip)) {
                return new SwimmingDuck(id, username, email, password, viteza, rezistenta);
            } else if ("FLYING_DUCK".equals(tip)) {
                return new FlyingDuck(id, username, email, password, viteza, rezistenta);
            } else {
                return new SwimmingDuck(id, username, email, password, viteza, rezistenta);
            }
        }
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable, String filterType) {
        // 1. Construim clauza WHERE dinamic
        String whereClause = " WHERE tip_utilizator != 'PERSON' "; // Default (Toate rațele)

        if ("Inotator".equals(filterType)) {
            whereClause += " AND tip_utilizator = 'SWIMMING_DUCK' ";
        } else if ("Zburator".equals(filterType)) {
            whereClause += " AND tip_utilizator = 'FLYING_DUCK' ";
        }
        // Dacă e "Toate", rămâne doar filtrul != PERSON

        // 2. Numărăm elementele FILTRATE (pentru butoanele de paginare)
        int totalElements = 0;
        String countSql = "SELECT count(*) FROM users " + whereClause;

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(countSql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                totalElements = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 3. Luăm pagina curentă cu elementele FILTRATE
        List<User> usersOnPage = new ArrayList<>();
        String querySql = "SELECT * FROM users " + whereClause + " LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(querySql)) {

            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                usersOnPage.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Page<>(usersOnPage, totalElements);
    }

    @Override
    public Page<User> findAllOnPage(Pageable pageable) {
        int totalElements = 0;
        String countSql = "SELECT count(*) FROM users WHERE tip_utilizator != 'PERSON'";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(countSql);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                totalElements = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<User> usersOnPage = new ArrayList<>();

        String querySql = "SELECT * FROM users WHERE tip_utilizator != 'PERSON' LIMIT ? OFFSET ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(querySql)) {

            statement.setInt(1, pageable.getPageSize());
            statement.setInt(2, pageable.getPageSize() * pageable.getPageNumber());

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                usersOnPage.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Page<>(usersOnPage, totalElements);
    }
}