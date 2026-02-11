package gui;

import domain.Duck;
import domain.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import service.DuckService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StatsController {

    private DuckService service;
    private Stage stage;

    @FXML private Label lblCommunityCount;
    @FXML private ListView<String> listSociable;
    @FXML private TextArea txtNetwork;

    public void setService(DuckService service, Stage stage) {
        this.service = service;
        this.stage = stage;
        loadStatistics();
    }

    private void loadStatistics() {
        int count = service.getCommunityCount();
        lblCommunityCount.setText(String.valueOf(count));

        Set<User> mostSociable = service.getMostSociableCommunity();
        List<String> sociableNames = new ArrayList<>();
        if (mostSociable != null) {
            for (User u : mostSociable) {

                sociableNames.add(u.getUsername() + " (ID: " + u.getId() + ")");
            }
        }
        listSociable.setItems(FXCollections.observableArrayList(sociableNames));

        String networkStructure = buildNetworkString();
        txtNetwork.setText(networkStructure);
    }

    private String buildNetworkString() {
        StringBuilder sb = new StringBuilder();

        Iterable<User> users = service.getAllUsers(); // Sau userRepo.findAll() prin service
        Map<Long, Set<Long>> friendships = service.getFriendships();

        for (User u : users) {
            sb.append(u.getUsername()).append(" (ID ").append(u.getId()).append(") e prieten cu: ");

            Set<Long> friends = friendships.get(u.getId());
            if (friends == null || friends.isEmpty()) {
                sb.append("nimeni");
            } else {
                List<String> friendNames = new ArrayList<>();
                for (Long friendId : friends) {

                    friendNames.add("ID " + friendId);
                }
                sb.append(String.join(", ", friendNames));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @FXML
    private void handleClose() {
        stage.close();
    }
}