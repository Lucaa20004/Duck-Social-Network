package gui;

import domain.Person;
import domain.SystemNotification;
import domain.User;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import domain.UserProfilePage; // Atenție la import!
import service.DuckService;

public class UserProfileController {

    @FXML private Label lblUsername;
    @FXML private Label lblEmail;
    @FXML private Label lblStats;

    @FXML private ListView<User> listFriends;
    @FXML private ListView<SystemNotification> listActivity;

    public void setService(DuckService service, Long userId) {
        // 1. Cerem PAGINA COMPLETĂ de la Service
        UserProfilePage page = service.getUserProfilePage(userId);

        // 2. Populăm datele
        initData(page);
    }

    private void initData(UserProfilePage page) {
        User user = page.getUser();

        // Header info
        String name = user.getUsername();
        if (user instanceof Person) {
            name = ((Person) user).getPrenume() + " " + ((Person) user).getNume();
        }
        lblUsername.setText(name);
        lblEmail.setText(user.getEmail());

        // Statistici simple
        int friendsCount = page.getFriendsList().size();
        int notifCount = page.getRecentNotifications().size();
        lblStats.setText(friendsCount + " Prieteni | " + notifCount + " Activități recente");

        // Listele
        listFriends.setItems(FXCollections.observableArrayList(page.getFriendsList()));
        listActivity.setItems(FXCollections.observableArrayList(page.getRecentNotifications()));
    }
}