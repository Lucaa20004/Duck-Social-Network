package gui;

import domain.FriendRequest;
import domain.Person;
import domain.SystemNotification;
import domain.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import service.DuckService;

import java.util.List;

public class NotificationsController {

    private DuckService service;
    private User currentUser;

    // --- TAB 1: Cereri ---
    @FXML private ListView<FriendRequest> listViewRequests;
    private ObservableList<FriendRequest> requestsModel = FXCollections.observableArrayList();

    // --- TAB 2: Sistem ---
    @FXML private ListView<SystemNotification> listViewSystem;
    private ObservableList<SystemNotification> systemModel = FXCollections.observableArrayList();

    public void setService(DuckService service, User currentUser) {
        this.service = service;
        this.currentUser = currentUser;

        initRequestList();
        initSystemList();
    }

    // Inițializare Tab 1 (Cereri)
    private void initRequestList() {
        listViewRequests.setItems(requestsModel);

        listViewRequests.setCellFactory(param -> new ListCell<FriendRequest>() {
            @Override
            protected void updateItem(FriendRequest item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    String senderName = item.getFrom().getUsername();
                    if (item.getFrom() instanceof Person) {
                        senderName = ((Person) item.getFrom()).getPrenume() + " " + ((Person) item.getFrom()).getNume();
                    }
                    setText("De la: " + senderName + " | Data: " + item.getDate().toLocalDate());
                }
            }
        });

        loadRequests();
    }

    // Inițializare Tab 2 (Sistem)
    private void initSystemList() {
        listViewSystem.setItems(systemModel);

        // Folosim un CellFactory special ca să facem textul să treacă pe rândul următor (Wrap Text)
        // pentru că mesajele de la curse sunt lungi
        listViewSystem.setCellFactory(param -> new ListCell<SystemNotification>() {
            @Override
            protected void updateItem(SystemNotification item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Text text = new Text(item.toString());
                    text.wrappingWidthProperty().bind(listViewSystem.widthProperty().subtract(20)); // Wrap la lățimea listei
                    setGraphic(text);
                }
            }
        });

        loadSystemNotifications();
    }

    private void loadRequests() {
        List<FriendRequest> reqs = service.getPendingRequests(currentUser.getId());
        requestsModel.setAll(reqs);
    }

    private void loadSystemNotifications() {
        // Apelează metoda nouă din Service pe care am făcut-o la pasul anterior
        List<SystemNotification> notifs = service.getUserNotifications(currentUser.getId());
        systemModel.setAll(notifs);
    }

    @FXML
    private void handleRefreshSystem() {
        loadSystemNotifications();
    }

    @FXML
    private void handleAccept() {
        processRequest("APPROVE");
    }

    @FXML
    private void handleReject() {
        processRequest("REJECT");
    }

    private void processRequest(String response) {
        FriendRequest selected = listViewRequests.getSelectionModel().getSelectedItem();
        if (selected == null) {
            MessageAlert.showErrorMessage(null, "Selectează o cerere din listă!");
            return;
        }
        try {
            service.respondToRequest(selected.getId(), response);
            String message = response.equals("APPROVE") ? "Prietenie creată!" : "Cerere respinsă.";
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", message);
            loadRequests();
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }
}