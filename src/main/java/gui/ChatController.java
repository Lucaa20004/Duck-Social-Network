package gui;

import domain.Message;
import domain.Person;
import domain.User;
import event.EntityChangeEvent;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import observer.Observer;
import service.DuckService;

import java.util.List;
import event.EntityChangeEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import paging.Page;
import paging.Pageable;
import service.DuckService;

import domain.Duck;
import domain.User;
import domain.SwimmingDuck;
import domain.FlyingDuck;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.cell.PropertyValueFactory;
import observer.Observer;
import service.DuckService;
import event.EntityChangeEvent;
import event.EntityChangeEventType;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import observer.*;



@SuppressWarnings("rawtypes")
public class ChatController implements Observer<EntityChangeEvent> {

    private DuckService service;
    private User currentUser;
    private User selectedFriend;

    private Message messageToReply = null;

    @FXML
    private Label lblLoggedUser;
    @FXML
    private ListView<User> listFriends;
    @FXML
    private TextField txtFriendEmail;
    @FXML
    private VBox chatBox;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField txtEventId;
    @FXML
    private Label lblReply;

    @FXML
    private TextField txtMessage;

    private final ObservableList<User> friendsModel = FXCollections.observableArrayList();

    public void setService(DuckService service, User user) {
        this.service = service;
        this.currentUser = user;

        service.addObserver(this);

        lblLoggedUser.setText("Logat ca: " + user.getUsername());
        listFriends.setItems(friendsModel);

        listFriends.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    if (item instanceof Person) {
                        setText(((Person) item).getPrenume() + " " + ((Person) item).getNume());
                    } else {
                        setText(item.getUsername());
                    }
                }
            }
        });

        loadFriends();

        listFriends.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedFriend = newVal;
                loadConversation();
                cancelReply();
            }
        });
    }

    private void loadFriends() {
        List<User> friends = service.getFriends(currentUser.getId());
        friendsModel.setAll(friends);
    }

    private void loadConversation() {
        if (selectedFriend == null) return;

        chatBox.getChildren().clear();

        List<Message> conversation = service.getConversation(currentUser.getId(), selectedFriend.getId());

        conversation.forEach(this::addMessageBubble);

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

    private void addMessageBubble(Message msg) {
        HBox row = new HBox();
        VBox bubbleContent = new VBox(2);

        String senderName = "Unknown User";
        if (msg.getFrom() != null) {
            senderName = msg.getFrom().getUsername();
            if (msg.getFrom() instanceof Person) {
                senderName = ((Person) msg.getFrom()).getPrenume();
            }
        }

        final String finalSenderName = senderName;

        if (msg.getReply() != null) {
            String replyText = "Răspuns la: " + msg.getReply().getMessage();

            if (replyText.length() > 30) {
                replyText = replyText.substring(0, 30) + "...";
            }

            Label replyLabel = new Label(replyText);
            replyLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray; -fx-padding: 0 0 2 0; -fx-border-color: transparent transparent #ccc transparent;");
            bubbleContent.getChildren().add(replyLabel);
        }

        Label textLabel = new Label(finalSenderName + ":\n" + msg.getMessage());
        textLabel.setWrapText(true);
        textLabel.setMaxWidth(200);

        bubbleContent.getChildren().add(textLabel);
        bubbleContent.setPadding(new javafx.geometry.Insets(10));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem replyItem = new MenuItem("Răspunde");

        replyItem.setOnAction(event -> {
            this.messageToReply = msg;

            String preview = msg.getMessage();
            if (preview.length() > 20) {
                preview = preview.substring(0, 20) + "...";
            }

            lblReply.setText("Răspunzi lui " + finalSenderName + ": " + preview);
            lblReply.setVisible(true);
            lblReply.setManaged(true);
        });

        contextMenu.getItems().add(replyItem);

        bubbleContent.setOnContextMenuRequested(e ->
                contextMenu.show(bubbleContent, e.getScreenX(), e.getScreenY())
        );

        boolean isMyMessage = false;
        if (msg.getFrom() != null) {
            if (msg.getFrom().getId().equals(currentUser.getId())) {
                isMyMessage = true;
            }
        }

        if (isMyMessage) {
            row.setAlignment(Pos.CENTER_RIGHT);
            bubbleContent.setStyle("-fx-background-color: #DCF8C6; -fx-background-radius: 10; -fx-border-radius: 10; -fx-font-size: 14px;");
        } else {
            row.setAlignment(Pos.CENTER_LEFT);
            bubbleContent.setStyle("-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ccc; -fx-font-size: 14px;");
        }

        row.getChildren().add(bubbleContent);
        chatBox.getChildren().add(row);
    }

    @FXML
    public void handleSendMessage() {
        String text = txtMessage.getText();
        if (selectedFriend == null || text.isEmpty()) return;

        service.sendMessage(currentUser.getId(), selectedFriend.getId(), text, messageToReply);

        txtMessage.clear();
        cancelReply();
    }

    private void cancelReply() {
        messageToReply = null;
        lblReply.setVisible(false);
        lblReply.setManaged(false);
    }

    @Override
    public void update(EntityChangeEvent event) {
        Platform.runLater(() -> {
            // 1. Reîncarcă lista de prieteni (Asta rezolvă problema ta!)
            // Indiferent ce s-a schimbat (mesaj nou, prieten nou), actualizăm lista.
            loadFriends();

            // 2. Dacă avem un prieten selectat, reîncarcă și conversația
            if (selectedFriend != null) {
                loadConversation();
            }
        });
    }

    @FXML
    public void handleSendRequest() {
        String text = txtFriendEmail.getText();
        if (text.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Introduceți ID-ul utilizatorului!");
            return;
        }

        try {
            Long toId = Long.parseLong(text);

            service.sendFriendRequest(currentUser.getId(), toId);

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Cerere de prietenie trimisa!");
            txtFriendEmail.clear();

        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "ID-ul trebuie sa fie un numar!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, e.getMessage());
        }
    }

    @FXML
    public void handleOpenNotifications() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/notifications_view.fxml"));
            VBox root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Notificări - " + currentUser.getUsername());
            stage.setScene(new Scene(root));

            NotificationsController controller = loader.getController();
            // Îi trimitem service-ul și userul CURENT (ca să știe ale cui notificări să le încarce)
            controller.setService(service, currentUser);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Nu s-a putut deschide fereastra de notificări.");
        }
    }

    @FXML
    public void handleSubscribe() {
        String idText = txtEventId.getText();
        if (idText.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Introduceți ID-ul evenimentului!");
            return;
        }

        try {
            Long eventId = Long.parseLong(idText);

            // Apelăm metoda din Service
            // (Asigură-te că metoda subscribeToRace este publică în DuckService)
            service.subscribeToEvent(currentUser.getId(), eventId);

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes",
                    "Te-ai abonat la evenimentul " + eventId + "!\nVei primi notificări când începe cursa.");

            txtEventId.clear();

        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "ID-ul trebuie să fie un număr valid!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare la abonare: " + e.getMessage());
        }

    }

    @FXML
    public void handleOpenProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_profile_view.fxml"));
            // Sau "/user_profile_view.fxml" depinde unde l-ai pus

            VBox root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Profil: " + currentUser.getUsername());
            stage.setScene(new Scene(root));

            UserProfileController controller = loader.getController();
            // Aici e magia: trimitem service-ul și ID-ul, iar el își ia obiectul Page
            controller.setService(service, currentUser.getId());

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}