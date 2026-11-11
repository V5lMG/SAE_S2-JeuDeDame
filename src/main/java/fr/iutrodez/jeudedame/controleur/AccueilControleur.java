package fr.iutrodez.jeudedame.controleur;

import fr.iutrodez.jeudedame.modele.GestionnaireSauvegarde;
import fr.iutrodez.jeudedame.modele.Partie;
import fr.iutrodez.jeudedame.modele.Pion;
import fr.iutrodez.jeudedame.vue.PionGraphique;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Contrôleur de l'écran d'accueil d'EasyCheckers.
 * Propose le choix des modes de jeu (Joueur vs Joueur ou Joueur vs Robot),
 * la reprise de partie sauvegardée, l'accès aux règles et la fermeture de l'application.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class AccueilControleur {

    /** Bouton pour lancer une partie à deux joueurs en local. */
    @FXML private Button startVsHumain;

    /** Bouton pour lancer une partie contre l'ordinateur (IA). */
    @FXML private Button startVsRobot;

    /** Bouton pour reprendre une partie précédemment enregistrée. */
    @FXML private Button load;

    /** Bouton d'affichage des règles officielles. */
    @FXML private Button help;

    /** Bouton de fermeture de l'application. */
    @FXML private Button quit;

    /** Conteneur de présentation visuelle des pièces vectorielles. */
    @FXML private HBox pawnPreviewBox;

    /**
     * Initialisation automatique des gestionnaires d'événements et de la vitrine vectorielle.
     */
    @FXML
    public void initialize() {
        // Vitrine haute définition présentant les 4 types de pièces
        if (pawnPreviewBox != null) {
            pawnPreviewBox.getChildren().clear();
            pawnPreviewBox.getChildren().addAll(
                    PionGraphique.creer(new Pion(false, 0, 0, Pion.COULEUR_BLANC)),
                    PionGraphique.creer(new Pion(true, 0, 0, Pion.COULEUR_BLANC)),
                    PionGraphique.creer(new Pion(true, 0, 0, Pion.COULEUR_NOIR)),
                    PionGraphique.creer(new Pion(false, 0, 0, Pion.COULEUR_NOIR))
            );
        }

        if (startVsHumain != null) {
            startVsHumain.setOnAction(event -> {
                try {
                    lancerJeu((Stage) startVsHumain.getScene().getWindow(), false, null, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if (startVsRobot != null) {
            startVsRobot.setOnAction(event -> {
                try {
                    lancerJeu((Stage) startVsRobot.getScene().getWindow(), true, null, 0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        if (load != null) {
            if (GestionnaireSauvegarde.existeSauvegarde()) {
                load.setText("📂  Reprendre la Partie (Sauvegarde disponible)");
            }
            load.setOnAction(event -> gererChargementPartie());
        }

        if (help != null) {
            help.setOnAction(event -> afficherAide());
        }

        if (quit != null) {
            quit.setOnAction(event -> {
                Stage stage = (Stage) quit.getScene().getWindow();
                stage.close();
            });
        }
    }

    private void gererChargementPartie() {
        if (GestionnaireSauvegarde.existeSauvegarde()) {
            GestionnaireSauvegarde.DonneesSauvegarde donnees = GestionnaireSauvegarde.charger();
            if (donnees != null) {
                try {
                    lancerJeu((Stage) load.getScene().getWindow(), donnees.isVsRobot(), donnees.getPartie(), donnees.getTempsSecondes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Reprendre la partie");
            alert.setHeaderText("Aucune sauvegarde trouvée");
            alert.setContentText("Vous n'avez pas encore de partie sauvegardée. Démarrez une nouvelle partie !");
            alert.showAndWait();
        }
    }

    private void afficherAide() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Règles du Jeu de Dames");
        alert.setHeaderText("Jeu de Dames — Règles Officielles");
        alert.setContentText(
                "• Plateau : Damier officiel 10×10 avec 20 pions par joueur.\n\n" +
                "• Premier coup : Les Blancs commencent la partie.\n\n" +
                "• Déplacement d'un pion : En diagonale d'une case vers l'avant sur les cases sombres.\n\n" +
                "• Capture par un pion : Saute par-dessus une pièce adverse adjacente vers la case vide immédiatement derrière, " +
                "en diagonale vers l'avant OU vers l'arrière.\n\n" +
                "• Prise obligatoire : Dès qu'une capture est réalisable, le joueur DOIT obligatoirement capturer.\n\n" +
                "• Rafle : Si une pièce qui vient de capturer peut encore capturer, elle doit enchaîner ses prises jusqu'à épuisement.\n\n" +
                "• Promotion en Dame : Un pion atteignant la ligne de fond adverse devient une Dame. La Dame peut se déplacer " +
                "sur toute la diagonale libre et capturer à distance.\n\n" +
                "• Modes de jeu : Jouez à deux sur le même ordinateur ou affrontez le Robot !"
        );
        alert.showAndWait();
    }

    /**
     * Bascule la fenêtre active vers l'écran du plateau de jeu.
     *
     * @param stage           la fenêtre principale
     * @param vsRobot         true pour jouer contre l'IA, false pour 2 joueurs
     * @param partieExistante instance à restaurer, ou null pour une nouvelle partie
     * @param tempsSecondes   secondes de départ pour le chronomètre
     * @throws IOException si le fichier FXML ne peut pas être chargé
     */
    public void lancerJeu(Stage stage, boolean vsRobot, Partie partieExistante, long tempsSecondes) throws IOException {
        URL gameUrl = getClass().getResource("/fr/iutrodez/jeudedame/vue/plateau/jeu-dame.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(gameUrl);

        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.setTitle(vsRobot ? "Jeu de Dames — Joueur vs Robot" : "Jeu de Dames — 2 Joueurs");

        Controleur controller = fxmlLoader.getController();
        controller.setVsRobot(vsRobot);
        if (partieExistante != null) {
            controller.setTempsSecondes(tempsSecondes);
            controller.setPartie(partieExistante);
        } else {
            controller.setTempsSecondes(0);
            controller.setPartie(new Partie());
        }

        stage.setOnCloseRequest(event -> controller.arreterChronometre());
        stage.show();
    }
}
