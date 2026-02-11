package gui;

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

public class DuckController implements Observer<EntityChangeEvent> {
    private DuckService duckService;
    private ObservableList<Duck> model = FXCollections.observableArrayList();

    @FXML
    private TableView<Duck> table;
    @FXML private TextField id1user;
    @FXML private TextField id2user;

    // --- ELEMENTE NOI ---
    @FXML private TextField txtRaceEventId;
    // --------------------

    @FXML private ComboBox<String> comboType;


    @FXML
    private TableColumn<Duck , Long>  idColumn;
    @FXML
    private TableColumn<Duck , String> nameColumn;
    @FXML
    private TableColumn<Duck , String>  emailColumn;
    @FXML
    private TableColumn<Duck , Double>  vitezaColumn;
    @FXML
    private TableColumn<Duck , Double>  rezistentaColumn;
    @FXML
    private Button btnPrevious;
    @FXML
    private Button btnNext;
    @FXML
    private Label labelPage;
    @FXML
    private Button btnAdd;
    @FXML
    private Button btnDelete;
    @FXML
    private TextField id1user1;
    @FXML
    private TextField id2user1;


    @FXML
    private ComboBox<String> comboType1;



    private int pageSize = 2;
    private int currentPage = 0;
    private int totalNumberOfElements = 0;
    public void setService(DuckService duckService) {
        this.duckService = duckService;

        duckService.addObserver(this);

        // aici trebuiie sa revii cu initTypeCombo si initModel
        initTypeCombo();
        initModel();
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        vitezaColumn.setCellValueFactory(new PropertyValueFactory<>("viteza"));
        rezistentaColumn.setCellValueFactory(new PropertyValueFactory<>("rezistenta"));

        table.setItems(model);
        //daca se schimba ceva in combobox mergem la prima pagina
        comboType.setOnAction(event -> {
            currentPage = 0;
            initModel();
        });
    }

    private void initTypeCombo() {
        comboType.getItems().setAll("Toate", "Inotator" , "Zburator");
        comboType.getSelectionModel().select("Toate");
    }


    private void initModel() {
        String selectedType = comboType.getSelectionModel().getSelectedItem();
        if (selectedType == null) selectedType = "Toate";

        Pageable pageable = new Pageable(currentPage, pageSize);

        Page<User> page = duckService.findAllOnPage(pageable, selectedType);

        this.totalNumberOfElements = page.getTotalNumberOfElements();

        List<Duck> duckList = StreamSupport.stream(page.getElementsOnPage().spliterator(), false)
                .map(u -> (Duck) u)
                .collect(Collectors.toList());

        model.setAll(duckList);

        labelPage.setText("Page " + (currentPage + 1));
        buttonStateUpdate();
    }
    private void buttonStateUpdate() {
        btnPrevious.setDisable(currentPage == 0);

        btnNext.setDisable((currentPage + 1) * pageSize >= totalNumberOfElements);
    }


    @FXML
    public void onNextPage(ActionEvent actionEvent) {
        currentPage++;
        initModel();
    }

    @FXML
    public void onPreviousPage(ActionEvent actionEvent) {
        currentPage--;
        initModel();
    }

    @Override
    public void update(EntityChangeEvent entityChangeEvent) {
        if (entityChangeEvent.getType() == EntityChangeEventType.DELETE || entityChangeEvent.getType() == EntityChangeEventType.ADD) {
            initModel();
            currentPage = 0;
        }
    }

    @FXML
    private void handleAdduser(ActionEvent actionEvent) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/add_user_view.fxml"));
            VBox root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Adăugare Utilizator");
            stage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(root);
            stage.setScene(scene);

            AddUserController controller = loader.getController();
            controller.setService(duckService, stage);

            stage.showAndWait();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteFriendship() {
        try {
            // 1. Citim ID-urile
            Long id1 = Long.parseLong(id1user.getText());
            Long id2 = Long.parseLong(id2user.getText());

            // 2. Apelăm service-ul pentru ștergere
            duckService.removeFriendship(id1, id2);

            // 3. Afișăm succes
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Prietenia a fost ștearsă!");
            id1user.clear();
            id2user.clear();

        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "ID-urile trebuie să fie numere valide!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }

    @FXML
    public void handleOpenLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login_view.fxml"));
            VBox root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Autentificare Duck Social");
            loginStage.setScene(new Scene(root));

            LoginController loginController = loader.getController();

            loginController.setService(duckService);

            loginStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Nu s-a putut deschide fereastra de login: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddFriendship() {
        try {
            Long id1 = Long.parseLong(id1user.getText());
            Long id2 = Long.parseLong(id2user.getText());

            duckService.addFriendship(id1, id2);

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Prietenia a fost creată!");
            id1user.clear();
            id2user.clear();

        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "ID-urile trebuie să fie numere valide!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteDuck() {
        Duck selectedDuck = table.getSelectionModel().getSelectedItem();

        if (selectedDuck == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Nicio selecție");
            alert.setHeaderText("Nu ați selectat nicio rață!");
            alert.setContentText("Vă rugăm să selectați un utilizator din tabel pentru a-l șterge.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmare Ștergere");
        confirm.setHeaderText("Sunteți sigur că vreți să ștergeți utilizatorul?");
        confirm.setContentText("Utilizator: " + selectedDuck.getUsername());

        var result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                duckService.removeUser(selectedDuck.getId());

                Alert success = new Alert(Alert.AlertType.INFORMATION);
                success.setTitle("Succes");
                success.setHeaderText(null);
                success.setContentText("Utilizatorul a fost șters cu succes!");
                success.showAndWait();


            } catch (Exception e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Eroare");
                error.setContentText("Nu s-a putut șterge: " + e.getMessage());
                error.showAndWait();
            }
        }
    }

    @FXML
    public void handleShowStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/stats_view.fxml"));
            VBox root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Statistici Duck Social Network");
            // stage.initModality(Modality.WINDOW_MODAL); // Opțional: blochează fereastra principală

            Scene scene = new Scene(root);
            stage.setScene(scene);

            StatsController controller = loader.getController();
            controller.setService(duckService, stage);

            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Nu s-a putut deschide fereastra de statistici: " + e.getMessage());
        }
    }

    @FXML
    private void handleRunRace() {
        String idText = txtRaceEventId.getText();
        if (idText.isEmpty()) {
            MessageAlert.showErrorMessage(null, "Introduceți ID-ul evenimentului de tip cursă!");
            return;
        }

        try {
            Long eventId = Long.parseLong(idText);

            // Apelează metoda din service care rulează pe Thread separat
            duckService.runRaceEventConcurren(eventId);

            // Afișăm mesaj că s-a pornit (nu așteptăm rezultatul aici, că e pe alt thread)
            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Info",
                    "Cursa a fost pornită!\nRezultatele vor fi trimise ca notificări abonaților.");

            txtRaceEventId.clear();

        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "ID-ul trebuie să fie un număr valid!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare la pornirea cursei: " + e.getMessage());
        }
    }
}
