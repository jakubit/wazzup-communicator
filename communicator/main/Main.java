package proz.communicator.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Główna klasa programu, inicjalizuje scenę i wyświetla ją.
 *
 * Komunikator P2P.
 *
 * @author Jakub Pawlak
 */

public class Main extends Application {

    /**
     * Inicjalizuje i wyświetla scenę.
     * @param primaryStage główne okno programu.
     */
    @Override
    public void start(Stage primaryStage) {

        Parent root = null;
        FXMLLoader fxmlLoader = null;
        try {
            fxmlLoader = new FXMLLoader(getClass().getResource("WindowView.fxml"));
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Wazzup");

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    /**
     * Główna funkcja programu. Rozpoczyna działanie aplikacji.
     * @param args argumenty wywołania
     */
    public static void main(String[] args) {
        launch(args);
    }
}
