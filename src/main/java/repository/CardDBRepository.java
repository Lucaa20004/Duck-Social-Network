package repository;

import domain.Card;
import domain.FlyCard;
import domain.SwimCard;
import validation.Validator;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardDBRepository implements Repository<Long, Card> {
    private String url;
    private String username;
    private String password;
    private Validator<Card> validator;

    public CardDBRepository(String url, String username, String password, Validator<Card> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override
    public Card save(Card entity) {
        String sql = "INSERT INTO carduri (nume_card, tip_card) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, entity.getNumeCard());

            if (entity instanceof SwimCard) {
                ps.setString(2, "SWIM");
            } else if (entity instanceof FlyCard) {
                ps.setString(2, "FLY");
            } else {
                ps.setString(2, "GENERIC");
            }

            ps.executeUpdate();

            // Setăm ID-ul generat de baza de date
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return null; // Succes

        } catch (SQLException e) {
            e.printStackTrace();
            return entity;
        }
    }

    @Override
    public Card findOne(Long id) {
        String sql = "SELECT * FROM carduri WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractCard(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Iterable<Card> findAll() {
        List<Card> cards = new ArrayList<>();
        String sql = "SELECT * FROM carduri";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                cards.add(extractCard(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cards;
    }

    @Override
    public Card delete(Long id) {
        String sql = "DELETE FROM carduri WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addMemberToCard(Long cardId, Long userId) {
        String sql = "INSERT INTO membri_card (card_id, user_id) VALUES (?, ?)";

        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, cardId);
            ps.setLong(2, userId);

            ps.executeUpdate();

        } catch (SQLException e) {
            if (!e.getSQLState().equals("23505")) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Card update(Card entity) {
        return null; // Poți implementa UPDATE dacă vrei
    }

    private Card extractCard(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String nume = rs.getString("nume_card");
        String tip = rs.getString("tip_card");

        if ("SWIM".equals(tip)) {
            return new SwimCard(nume, id);
        } else if ("FLY".equals(tip)) {
            return new FlyCard(nume, id);
        }
        return null;
    }

    public Map<Long, List<Long>> loadAllMemberships() {
        Map<Long, List<Long>> cardMembers = new HashMap<>();
        String sql = "SELECT card_id, user_id FROM membri_card";

        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while(rs.next()) {
                Long cId = rs.getLong("card_id");
                Long uId = rs.getLong("user_id");

                cardMembers.putIfAbsent(cId, new ArrayList<>());
                cardMembers.get(cId).add(uId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cardMembers;
    }
}