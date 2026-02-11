package org.example;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox; // Sau AnchorPane, în funcție de ce ai pus rădăcină în FXML
import javafx.stage.Stage;
import service.DuckService;
import gui.DuckController;

import java.io.IOException;
public class DuckApplication extends Application {
    private static DuckService duckService;

    public static void setDuckService(DuckService duckService) {
        DuckApplication.duckService = duckService;
    }

    @Override
    public void start(Stage primaryStage) throws IOException {

        var location = getClass().getResource("/duck_view.fxml");
        if (location == null) {
            System.err.println("EROARE CRITICĂ: Nu am găsit fișierul FXML!");
            System.err.println("Verifică folderul resources!");
            return; // Oprim aici ca să nu luăm crash
        } else {
            System.out.println("Succes: Am găsit FXML la: " + location);
        }

        FXMLLoader loader = new FXMLLoader(location);
        VBox root = loader.load();

        DuckController controller = loader.getController();
        controller.setService(duckService);

        // 4. Configurăm scena (fereastra)
        Scene scene = new Scene(root);

        // Opțional: Putem seta dimensiuni minime
        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(400);

        primaryStage.setTitle("Duck Management System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
