package gui;

import domain.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import service.DuckService;

public class LoginController {

    private DuckService service;

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;

    public void setService(DuckService service) {
        this.service = service;
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText();
        String password = txtPassword.getText();


        User loggedUser = service.login(username, password);

        if (loggedUser != null) {
            openChatWindow(loggedUser);

            closeLoginWindow();
        } else {
            MessageAlert.showErrorMessage(null, "Username sau parolă greșită!");
        }
    }

    private void openChatWindow(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user_chat_view.fxml"));
            VBox root = loader.load();

            Stage chatStage = new Stage();
            chatStage.setTitle("Chat - " + user.getUsername());
            chatStage.setScene(new Scene(root));


            ChatController chatController = loader.getController();
            chatController.setService(service, user);

            chatStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            MessageAlert.showErrorMessage(null, "Nu s-a putut deschide fereastra de chat: " + e.getMessage());
        }
    }

    private void closeLoginWindow() {
        Stage currentStage = (Stage) txtUsername.getScene().getWindow();
        currentStage.close();
    }
}