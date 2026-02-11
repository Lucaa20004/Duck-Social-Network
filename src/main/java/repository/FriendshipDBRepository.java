package repository;

import exeption.UserNotFoundExeption;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// Înlocuiește FriendshipRepository
public class FriendshipDBRepository {

    private String url;
    private String username;
    private String password;

    public FriendshipDBRepository(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void save(Long userId) {
    }

    public void delete(Long userId) {
        // DB gestionează cascade delete, dar ștergem și manual
        String sql = "DELETE FROM friendships WHERE user1_id = ? OR user2_id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getInsertSql(Long id1, Long id2) {
        return String.format("INSERT INTO friendships (user1_id, user2_id) VALUES (%d, %d)",
                Math.min(id1, id2), Math.max(id1, id2));
    }

    private String getDeleteSql(Long id1, Long id2) {
        return String.format("DELETE FROM friendships WHERE user1_id = %d AND user2_id = %d",
                Math.min(id1, id2), Math.max(id1, id2));
    }

    public void addFriendship(Long userId1, Long userId2) {
        String sql = getInsertSql(userId1, userId2);
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();

        } catch (SQLException e) {
            if (!e.getSQLState().startsWith("23")) {
                e.printStackTrace();
            }
        }
    }

    public void removeFriendship(Long userId1, Long userId2) {
        String sql = getDeleteSql(userId1, userId2);
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Map<Long, Set<Long>> getFriendships() {
        Map<Long, Set<Long>> friendships = new HashMap<>();
        String sql = "SELECT user1_id, user2_id FROM friendships";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Long u1 = rs.getLong("user1_id");
                Long u2 = rs.getLong("user2_id");

                friendships.putIfAbsent(u1, new HashSet<>());
                friendships.putIfAbsent(u2, new HashSet<>());

                friendships.get(u1).add(u2);
                friendships.get(u2).add(u1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friendships;
    }

    public boolean friendshipExists(Long userId1, Long userId2) {
        String sql = "SELECT 1 FROM friendships WHERE user1_id = ? AND user2_id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, Math.min(userId1, userId2));
            ps.setLong(2, Math.max(userId1, userId2));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}