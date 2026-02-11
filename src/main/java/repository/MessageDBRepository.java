package repository;

import domain.Message;
import domain.User;
import domain.Entity;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageDBRepository implements Repository<Long, Message> {
    private String url;
    private String username;
    private String password;
    private Repository<Long, User> userRepo; // Avem nevoie ca să refacem obiectele User

    public MessageDBRepository(String url, String username, String password, Repository<Long, User> userRepo) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.userRepo = userRepo;
    }

    @Override
    public Message save(Message entity) {
        String sql = "INSERT INTO messages (from_user_id, to_user_id, message, data, reply_to_id) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, entity.getFrom().getId());
            ps.setLong(2, entity.getTo().getId());
            ps.setString(3, entity.getMessage());
            ps.setTimestamp(4, Timestamp.valueOf(entity.getDate()));

            if (entity.getReply() != null) {
                ps.setLong(5, entity.getReply().getId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ps.executeUpdate();

            // Setăm ID-ul generat
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return null; // null înseamnă succes la tine
        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
    }

    public List<Message> getConversation(Long user1Id, Long user2Id) {
        List<Message> messages = new ArrayList<>();
        String sql = "SELECT * FROM messages WHERE (from_user_id = ? AND to_user_id = ?) OR (from_user_id = ? AND to_user_id = ?) ORDER BY data ASC";

        System.out.println("DEBUG REPO: Caut mesaje intre User " + user1Id + " si User " + user2Id);

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, user1Id);
            ps.setLong(2, user2Id);
            ps.setLong(3, user2Id);
            ps.setLong(4, user1Id);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                Long fromId = rs.getLong("from_user_id");
                Long toId = rs.getLong("to_user_id");
                String text = rs.getString("message");

                Timestamp timestamp = rs.getTimestamp("data");
                LocalDateTime date = (timestamp != null) ? timestamp.toLocalDateTime() : null;

                Long replyId = rs.getLong("reply_to_id");
                if (rs.wasNull()) {
                    replyId = null;
                }

                System.out.print(" -> Mesaj ID " + id + ": fromId=" + fromId + ", toId=" + toId + " | ");

                User from = userRepo.findOne(fromId);
                User to = userRepo.findOne(toId);

                Message replyMessage = null;
                if (replyId != null) {
                    replyMessage = findOneMessage(replyId);
                }

                if (from == null) {
                    System.out.println("EROARE: userRepo.findOne(" + fromId + ") a returnat NULL!");
                } else {
                    System.out.println("OK: Gasit user " + from.getUsername());
                }

                Message msg = new Message(id, from, to, text, date, replyMessage);
                messages.add(msg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }

    private Message findOneMessage(Long messageId) {
        String sql = "SELECT * FROM messages WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, messageId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Long fromId = rs.getLong("from_user_id");
                Long toId = rs.getLong("to_user_id");
                String text = rs.getString("message");
                Timestamp timestamp = rs.getTimestamp("data");
                LocalDateTime date = (timestamp != null) ? timestamp.toLocalDateTime() : null;

                User from = userRepo.findOne(fromId);
                User to = userRepo.findOne(toId);

                return new Message(messageId, from, to, text, date, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Message findOne(Long aLong) { return null; }

    @Override
    public Iterable<Message> findAll() { return new ArrayList<>(); }

    @Override
    public Message delete(Long aLong) { return null; }

    @Override
    public Message update(Message entity) { return null; }
}