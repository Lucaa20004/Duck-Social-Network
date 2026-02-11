package gui;

import domain.FlyingDuck;
import domain.Person;
import domain.SwimmingDuck;
import domain.User;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.DuckService;

public class AddUserController {

    private DuckService service;
    private Stage stage; // Referință la fereastra curentă (ca să o putem închide)

    // --- Elemente FXML ---
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtParola;
    @FXML private ComboBox<String> comboUserType;

    // Container Person
    @FXML private VBox containerPerson;
    @FXML private TextField txtNume;
    @FXML private TextField txtPrenume;
    @FXML private TextField txtOcupatie;
    @FXML private TextField txtEmpatie;

    // Container Duck
    @FXML private VBox containerDuck;
    @FXML private ComboBox<String> comboDuckType;
    @FXML private TextField txtViteza;
    @FXML private TextField txtRezistenta;

    public void setService(DuckService service, Stage stage) {
        this.service = service;
        this.stage = stage;
    }

    @FXML
    public void initialize() {
        comboUserType.getItems().addAll("Persoana", "Rata");
        comboDuckType.getItems().addAll("Swimming", "Flying"); // Identic cu clasele tale

        comboUserType.setOnAction(e -> updateFormVisibility());
    }

    private void updateFormVisibility() {
        String selection = comboUserType.getValue();

        if ("Persoana".equals(selection)) {
            containerPerson.setVisible(true);
            containerPerson.setManaged(true);

            containerDuck.setVisible(false);
            containerDuck.setManaged(false);
        } else if ("Rata".equals(selection)) {
            containerPerson.setVisible(false);
            containerPerson.setManaged(false);

            containerDuck.setVisible(true);
            containerDuck.setManaged(true);
        }
    }

    @FXML
    private void handleSave() {
        try {
            String username = txtUsername.getText();
            String email = txtEmail.getText();
            String password = txtParola.getText();
            String type = comboUserType.getValue();

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || type == null) {
                MessageAlert.showErrorMessage(null, "Completați toate câmpurile obligatorii!");
                return;
            }

            User userToAdd = null;

            if ("Persoana".equals(type)) {
                String nume = txtNume.getText();
                String prenume = txtPrenume.getText();
                String ocupatie = txtOcupatie.getText();
                int empatie = Integer.parseInt(txtEmpatie.getText());

                // Validare simplă (poți lăsa validatorul din service să facă restul)
                if (nume.isEmpty()) throw new RuntimeException("Numele este obligatoriu!");

                userToAdd = new Person(null, username, email, password, nume, prenume, null, ocupatie, empatie);

            } else if ("Rata".equals(type)) {
                String duckType = comboDuckType.getValue();
                double viteza = Double.parseDouble(txtViteza.getText());
                double rezistenta = Double.parseDouble(txtRezistenta.getText());

                if ("Swimming".equals(duckType)) {
                    userToAdd = new SwimmingDuck(null, username, email, password, viteza, rezistenta);
                } else if ("Flying".equals(duckType)) {
                    userToAdd = new FlyingDuck(null, username, email, password, viteza, rezistenta);
                } else {
                    throw new RuntimeException("Selectați tipul de rață!");
                }
            }

            service.addUser(userToAdd);

            MessageAlert.showMessage(null, Alert.AlertType.INFORMATION, "Succes", "Utilizator adăugat!");
            stage.close();

        } catch (NumberFormatException e) {
            MessageAlert.showErrorMessage(null, "Viteza, Rezistența și Empatia trebuie să fie numere!");
        } catch (Exception e) {
            MessageAlert.showErrorMessage(null, "Eroare la salvare: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }
}