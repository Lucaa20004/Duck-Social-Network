package repository;

import com.sun.jdi.LongValue;
import domain.Culoar;
import domain.Event;
import domain.RaceEvent;
import validation.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class EventDBRepository implements Repository<Long, Event> {

    private String url;
    private String username;
    private String password;
    private Validator<Event> validator;

    private String getInseertSub(Long id1, Long id2) {
        return String.format("INSERT INTO event_subscribers (event_id, user_id) VALUES (%d, %d)",
                Math.min(id1, id2), Math.max(id1, id2));
    }

    public void addSubscriber(Long eventId, Long userId) {
        String sql = "INSERT INTO event_subscribers (event_id, user_id) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, eventId);
            ps.setLong(2, userId);

            ps.executeUpdate();
        } catch (SQLException e) {
            if (!e.getSQLState().startsWith("23")) { // Ignorăm duplicatele
                e.printStackTrace();
            }
        }
    }

    public EventDBRepository(String url, String username, String password, Validator<Event> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }


    private String convertLanesToString(List<Culoar> culoareList) {
        if (culoareList == null || culoareList.isEmpty()) {
            return "";
        }
        return culoareList.stream()
                .map(c -> String.valueOf(c.getDistanta()))
                .collect(Collectors.joining(","));
    }


    private List<Culoar> convertStringToLanes(String distances) {
        if (distances == null || distances.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(distances.split(","))
                .map(String::trim)
                .map(Double::parseDouble)
                .map(Culoar::new)
                .collect(Collectors.toList());
    }


    @Override
    public Event findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        String sql = "SELECT id, event_type, nume_eveniment, lane_distances FROM events WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                return extractEvent(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    private Event extractEvent(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String nume = rs.getString("nume_eveniment");
        String type = rs.getString("event_type");

        Event event;

        if ("RACEEVENT".equalsIgnoreCase(type)) {
            String laneDistances = rs.getString("lane_distances");
            List<Culoar> culoareList = convertStringToLanes(laneDistances);

            event = new RaceEvent(id, nume, culoareList);
        } else {
            event = new Event(id, nume);
        }



        return event;
    }

    @Override
    public Iterable<Event> findAll() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT id, event_type, nume_eveniment, lane_distances FROM events";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                events.add(extractEvent(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entitatea nu poate fi null!");
        }
        validator.validate(entity);

        if (entity.getId() != null && findOne(entity.getId()) != null) {

            return entity;
        }

        String sql = "INSERT INTO events (event_type, nume_eveniment, lane_distances) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String eventType = (entity instanceof RaceEvent) ? "RACEEVENT" : "EVENT";
            String laneDistances = "";

            if (entity instanceof RaceEvent raceEvent) {
                laneDistances = convertLanesToString(raceEvent.getCuloare());
            }

            statement.setString(1, eventType);
            statement.setString(2, entity.getNume());
            statement.setString(3, laneDistances); // Salvăm distanțele

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));
                    }
                }
                return null;
            }
        } catch (SQLException e) {
            System.err.println("Eroare SQL la salvare: " + e.getMessage());
        }
        return entity;
    }


    @Override
    public Event delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }
        Event entityToDelete = findOne(id);
        if (entityToDelete == null) {
            return null;
        }

        String sqlDeleteSubscribers = "DELETE FROM evente_subscribers WHERE event_id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statementSubscribers = connection.prepareStatement(sqlDeleteSubscribers)) {
            statementSubscribers.setLong(1, id);
            statementSubscribers.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Eroare SQL la stergerea abonatilor: " + e.getMessage());
        }

        String sqlDeleteEvent = "DELETE FROM events WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statementEvent = connection.prepareStatement(sqlDeleteEvent)) {
            statementEvent.setLong(1, id);
            int affectedRows = statementEvent.executeUpdate();
            if (affectedRows > 0) {
                return entityToDelete;
            }
        } catch (SQLException e) {
            System.err.println("Eroare SQL la stergerea evenimentului: " + e.getMessage());
        }
        return null;
    }

    public List<Long> getSubscribers(Long eventId) {
        List <Long> subcribersID =  new ArrayList<>();
        String sql = "SELECT user_id FROM event_subscribers WHERE event_id = ?";
        try( Connection connection = getConnection();
            PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1,eventId);
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                subcribersID.add(rs.getLong("user_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return subcribersID;
    }

    @Override
    public Event update(Event entity) {

        throw new UnsupportedOperationException("Metoda UPDATE nu este inca implementata.");
    }
}