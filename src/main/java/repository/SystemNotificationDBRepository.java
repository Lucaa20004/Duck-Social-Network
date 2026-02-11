package repository;

import domain.SystemNotification;
import domain.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SystemNotificationDBRepository implements Repository<Long, SystemNotification> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userRepo;

    public SystemNotificationDBRepository(String url, String username, String password, Repository<Long, User> userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepo = userRepo;
    }

    @Override
    public SystemNotification save(SystemNotification entity) {
        String sql = "INSERT INTO system_notifications (user_id, message, date) VALUES (?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getUser().getId());
            ps.setString(2, entity.getMessage());
            ps.setTimestamp(3, Timestamp.valueOf(entity.getDate()));

            ps.executeUpdate();

            try(ResultSet generatedKeys = ps.getGeneratedKeys()){
                if(generatedKeys.next()){
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
    }

    // Metoda specifică pentru GUI: Notificările unui singur user, cele mai noi primele
    public List<SystemNotification> findAllForUser(Long userId) {
        List<SystemNotification> list = new ArrayList<>();
        String sql = "SELECT * FROM system_notifications WHERE user_id = ? ORDER BY date DESC";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                String message = rs.getString("message");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();

                // Încărcăm userul (deși știm ID-ul, e bine să avem obiectul)
                User user = userRepo.findOne(userId);

                list.add(new SystemNotification(id, user, message, date));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public SystemNotification findOne(Long aLong) { return null; }
    @Override
    public Iterable<SystemNotification> findAll() { return new ArrayList<>(); }
    @Override
    public SystemNotification delete(Long aLong) { return null; }
    @Override
    public SystemNotification update(SystemNotification entity) { return null; }
}