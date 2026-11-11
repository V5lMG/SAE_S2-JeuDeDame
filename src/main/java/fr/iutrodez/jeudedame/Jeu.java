package fr.iutrodez.jeudedame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Point d'entrée principal de l'application EasyCheckers (JavaFX Application).
 * Initialise le stage primaire et charge la scène d'accueil.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class Jeu extends Application {

    /** Largeur minimale garantie de la fenêtre principale. */
    public static final int LARGEUR_MIN = 960;

    /** Hauteur minimale garantie de la fenêtre principale. */
    public static final int HAUTEUR_MIN = 680;

    /**
     * Point d'entrée de l'exécutable Java.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        URL accueilUrl = getClass().getResource("/fr/iutrodez/jeudedame/vue/accueil/page-accueil.fxml");
        if (accueilUrl == null) {
            throw new IllegalStateException("Ressource page-accueil.fxml introuvable dans le classpath");
        }

        FXMLLoader fxmlLoader = new FXMLLoader(accueilUrl);
        Scene scene = new Scene(fxmlLoader.load());

        stage.setScene(scene);
        stage.setTitle("Jeu de Dames");
        stage.setMinWidth(LARGEUR_MIN);
        stage.setMinHeight(HAUTEUR_MIN);
        stage.show();
    }
}
