package fr.iutrodez.jeudedame.controleur;

import fr.iutrodez.jeudedame.modele.Coup;
import fr.iutrodez.jeudedame.modele.GestionnaireSauvegarde;
import fr.iutrodez.jeudedame.modele.Partie;
import fr.iutrodez.jeudedame.modele.Pion;
import fr.iutrodez.jeudedame.modele.Plateau;
import fr.iutrodez.jeudedame.modele.RobotIA;
import fr.iutrodez.jeudedame.vue.PionGraphique;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur principal de l'écran de jeu (damier 10x10 et tableau de bord).
 * Fait le lien entre les interactions utilisateur sur la vue JavaFX,
 * le modèle Partie, le moteur RobotIA et le service GestionnaireSauvegarde.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class Controleur {

    /** Grille JavaFX représentant le damier. */
    @FXML public GridPane gameGrid;

    /** Label affichant le nombre de pions noirs restants. */
    @FXML private Label nombreDePionNoir;

    /** Label affichant le nombre de pions blancs restants. */
    @FXML private Label nombreDePionBlanc;

    /** Label indiquant le joueur actif. */
    @FXML private Label playerTurnLabel;

    /** Label du chronomètre numérique. */
    @FXML private Label chronometre;

    /** Label du bandeau de guidage contextuel. */
    @FXML private Label statutLabel;

    /** Label du mode de jeu actif. */
    @FXML private Label modeLabel;

    /** Label du nom du joueur Blanc. */
    @FXML private Label nomJoueurBlancLabel;

    /** Label du nom du joueur Noir. */
    @FXML private Label nomJoueurNoirLabel;

    /** Conteneur stylisé du badge de tour. */
    @FXML private HBox turnBadge;

    /** Carte de score du joueur Blanc. */
    @FXML private HBox cardBlanc;

    /** Carte de score du joueur Noir. */
    @FXML private HBox cardNoir;

    /** Emplacement de l'avatar vectoriel Blanc. */
    @FXML private StackPane avatarBlancContainer;

    /** Emplacement de l'avatar vectoriel Noir. */
    @FXML private StackPane avatarNoirContainer;

    /** Bouton de sauvegarde de la partie en cours. */
    @FXML private Button btnSauvegarder;

    /** Bouton de réinitialisation de la partie. */
    @FXML private Button btnRecommencer;

    /** Bouton d'affichage des règles officielles. */
    @FXML private Button boutonAmpoule;

    /** Bouton de retour au menu principal. */
    @FXML private Button btnAccueil;

    /** Instance de la partie en cours. */
    private Partie partie;

    /** Tableau bidimensionnel des cellules graphiques [y][x]. */
    private final StackPane[][] cells = new StackPane[Plateau.TAILLE][Plateau.TAILLE];

    /** Chronomètre JavaFX cadencé à la seconde. */
    private Timeline timeline;

    /** Temps écoulé en secondes depuis le début de la partie. */
    private long secondesEcoulees = 0;

    /** Indique si le mode Robot (IA) est activé. */
    private boolean vsRobot = false;

    /** Instance du moteur d'IA pour les pièces noires. */
    private final RobotIA robot = new RobotIA(Pion.COULEUR_NOIR);

    /** Verrou empêchant l'interaction humaine pendant la réflexion du robot. */
    private boolean robotEnTrainDeJouer = false;

    /**
     * Initialisation automatique après chargement du FXML.
     */
    @FXML
    public void initialize() {
        if (partie == null) {
            partie = new Partie();
        }
        initialiserBoutons();
        initialiserAvatars();
        initialiserPlateauVisuel();
        demarrerChronometre();
        rafraichirAffichage();
    }

    /**
     * Active ou désactive le mode de jeu contre le Robot.
     *
     * @param vsRobot true pour jouer contre l'IA, false pour 2 joueurs
     */
    public void setVsRobot(boolean vsRobot) {
        this.vsRobot = vsRobot;
        if (modeLabel != null) {
            modeLabel.setText(vsRobot ? "Mode : Joueur vs Robot" : "Mode : 2 Joueurs (Local)");
        }
        if (nomJoueurNoirLabel != null) {
            nomJoueurNoirLabel.setText(vsRobot ? "Robot" : "Joueur Noir");
        }
    }

    /**
     * Restaure le temps de départ pour les parties chargées.
     *
     * @param temps secondes écoulées
     */
    public void setTempsSecondes(long temps) {
        this.secondesEcoulees = Math.max(0, temps);
        mettreAJourChronometre();
    }

    /**
     * Définit une nouvelle partie et rafraîchit l'ensemble des composants.
     *
     * @param nouvellePartie la partie à associer
     */
    public void setPartie(Partie nouvellePartie) {
        this.partie = nouvellePartie;
        if (gameGrid != null) {
            initialiserPlateauVisuel();
            demarrerChronometre();
            rafraichirAffichage();
            verifierTourRobot();
        }
    }

    private void initialiserBoutons() {
        if (btnSauvegarder != null) {
            btnSauvegarder.setOnAction(event -> sauvegarderPartie());
        }
        if (boutonAmpoule != null) {
            boutonAmpoule.setOnAction(event -> afficherAideRegles());
        }
        if (btnRecommencer != null) {
            btnRecommencer.setOnAction(event -> recommencerPartie());
        }
        if (btnAccueil != null) {
            btnAccueil.setOnAction(event -> retournerAccueil());
        }
    }

    private void initialiserAvatars() {
        if (avatarBlancContainer != null) {
            avatarBlancContainer.getChildren().clear();
            avatarBlancContainer.getChildren().add(PionGraphique.creer(new Pion(false, 0, 0, Pion.COULEUR_BLANC)));
        }
        if (avatarNoirContainer != null) {
            avatarNoirContainer.getChildren().clear();
            avatarNoirContainer.getChildren().add(PionGraphique.creer(new Pion(false, 0, 0, Pion.COULEUR_NOIR)));
        }
    }

    private void sauvegarderPartie() {
        boolean succes = GestionnaireSauvegarde.sauvegarder(partie, secondesEcoulees, vsRobot);
        if (statutLabel != null) {
            statutLabel.setText(succes ? "✔ Partie sauvegardée avec succès !" : "❌ Erreur de sauvegarde.");
        }
    }

    private void demarrerChronometre() {
        if (timeline != null) {
            timeline.stop();
        }
        mettreAJourChronometre();

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            secondesEcoulees++;
            mettreAJourChronometre();
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void mettreAJourChronometre() {
        if (chronometre == null) return;
        long h = secondesEcoulees / 3600;
        long m = (secondesEcoulees % 3600) / 60;
        long s = secondesEcoulees % 60;
        chronometre.setText(String.format("%02d : %02d : %02d", h, m, s));
    }

    /**
     * Stoppe le chronomètre pour libérer les ressources.
     */
    public void arreterChronometre() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    private void initialiserPlateauVisuel() {
        gameGrid.getChildren().clear();

        for (int y = 0; y < Plateau.TAILLE; y++) {
            for (int x = 0; x < Plateau.TAILLE; x++) {
                StackPane cell = new StackPane();
                cell.setAlignment(Pos.CENTER);
                cell.setPrefSize(60, 60);
                cell.setMinSize(60, 60);
                cell.setMaxSize(60, 60);

                if ((x + y) % 2 == 0) {
                    cell.getStyleClass().add("board-cell-light");
                } else {
                    cell.getStyleClass().add("board-cell-dark");
                }

                final int col = x;
                final int row = y;
                cell.setOnMouseClicked(event -> gererClicCase(col, row));

                cells[y][x] = cell;
                gameGrid.add(cell, col, row);
            }
        }
    }

    private void gererClicCase(int x, int y) {
        if (partie.isPartieTerminee() || robotEnTrainDeJouer) {
            return;
        }

        // Si c'est au tour du robot, l'humain est bloqué
        if (vsRobot && partie.getJoueurActuel().getColor().equalsIgnoreCase(Pion.COULEUR_NOIR)) {
            return;
        }

        Pion pionSelectionne = partie.getPionSelectionne();

        // 1. Déplacement vers la case cliquée
        if (pionSelectionne != null) {
            List<Coup> coupsPossibles = (partie.getPionEnRafle() != null)
                    ? partie.getPlateau().getCoupsDeCapture(pionSelectionne)
                    : partie.getPlateau().getCoupsLegaux(pionSelectionne, partie.getJoueurActuel());

            boolean valide = false;
            for (Coup coup : coupsPossibles) {
                if (coup.getArriveeX() == x && coup.getArriveeY() == y) {
                    valide = true;
                    break;
                }
            }

            if (valide) {
                partie.deplacerPion(x, y);
                rafraichirAffichage();

                if (partie.isPartieTerminee()) {
                    arreterChronometre();
                    afficherVictoire();
                } else {
                    verifierTourRobot();
                }
                return;
            }
        }

        // 2. Sélection d'une pièce
        if (partie.getPionEnRafle() == null) {
            Pion pionClique = partie.getPionAt(x, y);
            if (pionClique != null && pionClique.getColor().equalsIgnoreCase(partie.getJoueurActuel().getColor())) {
                boolean succes = partie.selectionnerPion(x, y);
                if (!succes && !partie.getPionsAyantCaptures().isEmpty()) {
                    if (statutLabel != null) {
                        statutLabel.setText("⚠ Prise obligatoire ! Vous devez sélectionner un pion preneur");
                    }
                }
                rafraichirAffichage();
            } else {
                partie.deselectionner();
                rafraichirAffichage();
            }
        }
    }

    private void verifierTourRobot() {
        if (!vsRobot || partie.isPartieTerminee()) return;

        if (partie.getJoueurActuel().getColor().equalsIgnoreCase(Pion.COULEUR_NOIR)) {
            robotEnTrainDeJouer = true;
            if (statutLabel != null) {
                statutLabel.setText("🤖 Le robot réfléchit...");
            }

            PauseTransition pause = new PauseTransition(Duration.millis(550));
            pause.setOnFinished(event -> jouerCoupRobot());
            pause.play();
        }
    }

    private void jouerCoupRobot() {
        if (partie.isPartieTerminee()) {
            robotEnTrainDeJouer = false;
            return;
        }

        RobotIA.ActionRobot action = robot.choisirCoup(partie);
        if (action != null) {
            partie.selectionnerPion(action.getPion().getPosX(), action.getPion().getPosY());
            partie.deplacerPion(action.getCoup().getArriveeX(), action.getCoup().getArriveeY());
            rafraichirAffichage();

            if (partie.getPionEnRafle() != null) {
                // Rafle enchaînée
                if (statutLabel != null) {
                    statutLabel.setText("⚡ Le robot enchaîne une prise !");
                }
                PauseTransition raflePause = new PauseTransition(Duration.millis(450));
                raflePause.setOnFinished(e -> jouerCoupRobot());
                raflePause.play();
                return;
            }

            robotEnTrainDeJouer = false;
            rafraichirAffichage();

            if (partie.isPartieTerminee()) {
                arreterChronometre();
                afficherVictoire();
            }
        } else {
            robotEnTrainDeJouer = false;
            partie.verifierFinDePartie();
            if (partie.isPartieTerminee()) {
                arreterChronometre();
                afficherVictoire();
            }
        }
    }

    /**
     * Synchronise l'ensemble de la vue avec le modèle métier.
     */
    public void rafraichirAffichage() {
        if (gameGrid == null || partie == null) return;

        boolean isBlanc = partie.getJoueurActuel().getColor().equalsIgnoreCase(Pion.COULEUR_BLANC);

        // 1. Mise à jour des scores et badges
        if (playerTurnLabel != null) {
            if (vsRobot && !isBlanc) {
                playerTurnLabel.setText("Tour du Robot");
            } else {
                playerTurnLabel.setText(isBlanc ? "Tour des Blancs" : "Tour des Noirs");
            }
        }
        if (turnBadge != null) {
            turnBadge.setStyle(isBlanc
                    ? "-fx-background-color: linear-gradient(to right, #059669, #10b981);"
                    : "-fx-background-color: linear-gradient(to right, #334155, #1e293b); -fx-border-color: #64748b; -fx-border-width: 1.5; -fx-border-radius: 10;");
        }
        if (cardBlanc != null) {
            cardBlanc.getStyleClass().removeAll("player-score-card-active");
            if (isBlanc) cardBlanc.getStyleClass().add("player-score-card-active");
        }
        if (cardNoir != null) {
            cardNoir.getStyleClass().removeAll("player-score-card-active");
            if (!isBlanc) cardNoir.getStyleClass().add("player-score-card-active");
        }
        if (nombreDePionBlanc != null) {
            nombreDePionBlanc.setText(partie.getJoueurBlanc().getNombreDePions() + " pions restants");
        }
        if (nombreDePionNoir != null) {
            nombreDePionNoir.setText(partie.getJoueurNoir().getNombreDePions() + " pions restants");
        }

        // 2. Message de guidage
        if (statutLabel != null && !robotEnTrainDeJouer) {
            if (partie.getPionEnRafle() != null) {
                statutLabel.setText("⚡ Rafle en cours ! Continuez vos prises");
            } else if (!partie.getPionsAyantCaptures().isEmpty()) {
                statutLabel.setText("⚠ Prise obligatoire ! Jouez un des pions en orange");
            } else if (partie.getPionSelectionne() != null) {
                statutLabel.setText("Pion sélectionné — Cliquez sur une case verte");
            } else {
                statutLabel.setText("Au tour des " + (isBlanc ? "Blancs" : (vsRobot ? "Noirs (Robot)" : "Noirs")) + " — Choisissez un pion");
            }
        }

        List<Pion> pionsPreneurs = partie.getPionsAyantCaptures();
        Pion pionSelectionne = partie.getPionSelectionne();

        // 3. Dessiner les pièces vectorielles
        for (int y = 0; y < Plateau.TAILLE; y++) {
            for (int x = 0; x < Plateau.TAILLE; x++) {
                StackPane cell = cells[y][x];
                cell.getChildren().clear();
                cell.getStyleClass().remove("board-cell-selected");

                Pion pion = partie.getPionAt(x, y);
                if (pion != null) {
                    Node pionNode = PionGraphique.creer(pion);

                    if (pion == pionSelectionne) {
                        pionNode.getStyleClass().add("highlighted-pion");
                        cell.getStyleClass().add("board-cell-selected");
                    } else if (pionsPreneurs.contains(pion)) {
                        pionNode.getStyleClass().add("pion-must-capture");
                    }

                    cell.getChildren().add(pionNode);
                }
            }
        }

        // 4. Pastilles indicatrices pour le pion sélectionné
        if (pionSelectionne != null) {
            List<Coup> coups = (partie.getPionEnRafle() != null)
                    ? partie.getPlateau().getCoupsDeCapture(pionSelectionne)
                    : partie.getPlateau().getCoupsLegaux(pionSelectionne, partie.getJoueurActuel());

            for (Coup coup : coups) {
                int ax = coup.getArriveeX();
                int ay = coup.getArriveeY();

                Circle indicator = new Circle(13);
                if (coup.estCapture()) {
                    indicator.getStyleClass().add("target-indicator-capture");

                    // Surbrillance rouge de la pièce menacée
                    Pion capturee = coup.getPionCapture();
                    if (capturee != null) {
                        StackPane caseCapturee = cells[capturee.getPosY()][capturee.getPosX()];
                        for (Node node : caseCapturee.getChildren()) {
                            if (!node.getStyleClass().contains("highlight-capture")) {
                                node.getStyleClass().add("highlight-capture");
                            }
                        }
                    }
                } else {
                    indicator.getStyleClass().add("target-indicator-move");
                }

                cells[ay][ax].getChildren().add(indicator);
            }
        }
    }

    private void afficherVictoire() {
        String gagnant = partie.getJoueurGagnant().getColor();
        boolean robotGagne = vsRobot && Pion.COULEUR_NOIR.equalsIgnoreCase(gagnant);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partie !");
        alert.setHeaderText(robotGagne ? "🤖 Le Robot remporte la partie !" : "🏆 Félicitations au Joueur " + gagnant + " !");
        alert.setContentText((robotGagne ? "Le Robot a remporté la victoire !\n\n" : "Le Joueur " + gagnant + " remporte la partie !\n\n") +
                "⏱ Durée de la partie : " + chronometre.getText() + "\n" +
                "⚪ Pions Blancs restants : " + partie.getJoueurBlanc().getNombreDePions() + "\n" +
                "⚫ Pions Noirs restants : " + partie.getJoueurNoir().getNombreDePions());

        ButtonType btnRejouer = new ButtonType("Rejouer");
        ButtonType btnAccueilMenu = new ButtonType("Menu Principal");
        alert.getButtonTypes().setAll(btnRejouer, btnAccueilMenu);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == btnRejouer) {
                recommencerPartie();
            } else if (result.get() == btnAccueilMenu) {
                retournerAccueil();
            }
        }
    }

    private void recommencerPartie() {
        this.partie = new Partie();
        this.secondesEcoulees = 0;
        demarrerChronometre();
        rafraichirAffichage();
        verifierTourRobot();
    }

    private void afficherAideRegles() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Règles du Jeu de Dames");
        alert.setHeaderText("Jeu de Dames — Règles Officielles");
        alert.setContentText(
                "• Plateau : Damier officiel 10×10 avec 20 pions par joueur.\n\n" +
                "• Premier coup : Les Blancs commencent la partie.\n\n" +
                "• Déplacement simple : En diagonale d'une case vers l'avant.\n\n" +
                "• Capture : Saute par-dessus une pièce adverse en diagonale vers l'avant OU vers l'arrière.\n\n" +
                "• Prise obligatoire : Si une prise est possible, elle doit obligatoirement être jouée (pions en orange).\n\n" +
                "• Rafle : Le joueur doit enchaîner toutes les captures consécutives possibles avec la même pièce.\n\n" +
                "• Dame : Atteindre la ligne de fond adverse transforme le pion en Dame, qui peut voler sur toute la diagonale libre.\n\n" +
                "• Sauvegarde : Cliquez sur 💾 Sauvegarder à tout moment pour reprendre plus tard depuis le menu principal."
        );
        alert.showAndWait();
    }

    private void retournerAccueil() {
        arreterChronometre();
        try {
            Stage stage = (Stage) gameGrid.getScene().getWindow();
            URL accueilUrl = getClass().getResource("/fr/iutrodez/jeudedame/vue/accueil/page-accueil.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader(accueilUrl);
            Scene scene = new Scene(fxmlLoader.load());
            stage.setScene(scene);
            stage.setTitle("Jeu de Dames");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
