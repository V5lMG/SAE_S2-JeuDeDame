package fr.iutrodez.jeudedame.vue;

import fr.iutrodez.jeudedame.modele.Pion;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

/**
 * Fabrique de composants graphiques vectoriels pour les pièces du jeu de dames.
 * Génère des pions et des dames en haute définition (pur vectoriel JavaFX) avec
 * effets de relief 3D, dégradés radiaux, chanfreins et couronnes impériales.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public final class PionGraphique {

    /** Diamètre extérieur de la base du pion en pixels. */
    private static final double RAYON_BASE = 22.0;

    /** Diamètre du plateau central du pion. */
    private static final double RAYON_CORPS = 18.0;

    /** Diamètre de l'anneau concentrique gravé. */
    private static final double RAYON_GRAVURE = 12.5;

    /** Diamètre du centre en relief. */
    private static final double RAYON_CENTRE = 7.5;

    /** Constructeur privé (classe utilitaire / Factory). */
    private PionGraphique() {}

    /**
     * Instancie un composant JavaFX représentant visuellement un pion ou une dame.
     *
     * @param pion la pièce du modèle à représenter
     * @return un Node JavaFX prêt à être injecté dans une case de plateau
     */
    public static Node creer(Pion pion) {
        if (pion == null) return null;

        boolean isBlanc = pion.estBlanc();
        boolean isDame = pion.isDame();

        StackPane container = new StackPane();
        container.setAlignment(Pos.CENTER);
        container.setPrefSize(50, 50);
        container.setMaxSize(50, 50);

        // 1. Ombre portée 3D réaliste
        DropShadow ombrePortee = new DropShadow();
        ombrePortee.setRadius(7);
        ombrePortee.setOffsetY(3.5);
        ombrePortee.setColor(Color.rgb(0, 0, 0, 0.45));
        container.setEffect(ombrePortee);

        // 2. Base extérieure (biseau et cerclage)
        Circle base = new Circle(RAYON_BASE);
        if (isBlanc) {
            base.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#f1f5f9")),
                    new Stop(1, Color.web("#cbd5e1"))));
            base.setStroke(Color.web("#94a3b8"));
            base.setStrokeWidth(1.2);
        } else {
            base.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                    new Stop(0, Color.web("#334155")),
                    new Stop(1, Color.web("#0f172a"))));
            base.setStroke(Color.web("#020617"));
            base.setStrokeWidth(1.2);
        }

        // 3. Corps intérieur avec dégradé radial (effet nacre ou ébène laqué)
        Circle corps = new Circle(RAYON_CORPS);
        if (isBlanc) {
            RadialGradient radialBlanc = new RadialGradient(
                    0, 0, 0.35, 0.35, 0.7, true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.web("#ffffff")),
                    new Stop(0.65, Color.web("#f8fafc")),
                    new Stop(1.0, Color.web("#e2e8f0"))
            );
            corps.setFill(radialBlanc);
            corps.setStroke(Color.web("#cbd5e1"));
            corps.setStrokeWidth(1.0);
        } else {
            RadialGradient radialNoir = new RadialGradient(
                    0, 0, 0.35, 0.35, 0.7, true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.web("#475569")),
                    new Stop(0.45, Color.web("#1e293b")),
                    new Stop(1.0, Color.web("#090d16"))
            );
            corps.setFill(radialNoir);
            corps.setStroke(Color.web("#1e293b"));
            corps.setStrokeWidth(1.0);
        }

        // 4. Anneau intérieur gravé
        Circle anneauGrave = new Circle(RAYON_GRAVURE);
        anneauGrave.setFill(Color.TRANSPARENT);
        anneauGrave.setStroke(isBlanc ? Color.web("#e2e8f0") : Color.web("#334155"));
        anneauGrave.setStrokeWidth(1.2);

        // 5. Pastille centrale
        Circle centre = new Circle(RAYON_CENTRE);
        centre.setFill(isBlanc ? Color.web("#f1f5f9") : Color.web("#1e293b"));
        centre.setStroke(isBlanc ? Color.web("#cbd5e1") : Color.web("#0f172a"));
        centre.setStrokeWidth(0.8);

        container.getChildren().addAll(base, corps, anneauGrave, centre);

        // 6. Couronne royale pour les Dames
        if (isDame) {
            SVGPath couronne = new SVGPath();
            couronne.setContent("M -10 4 L -11 -4 L -5 0 L 0 -8 L 5 0 L 11 -4 L 10 4 Z");

            RadialGradient goldGradient = new RadialGradient(
                    0, 0, 0.4, 0.4, 0.8, true, CycleMethod.NO_CYCLE,
                    new Stop(0.0, Color.web("#fef08a")),
                    new Stop(0.4, Color.web("#facc15")),
                    new Stop(1.0, Color.web("#ca8a04"))
            );
            couronne.setFill(goldGradient);
            couronne.setStroke(Color.web("#a16207"));
            couronne.setStrokeWidth(1.2);

            DropShadow ombreCouronne = new DropShadow();
            ombreCouronne.setRadius(4);
            ombreCouronne.setColor(Color.rgb(0, 0, 0, 0.5));
            couronne.setEffect(ombreCouronne);

            // Joyau rubis central
            Circle joyauCentral = new Circle(2.2);
            joyauCentral.setFill(Color.web("#ef4444"));
            joyauCentral.setStroke(Color.web("#991b1b"));
            joyauCentral.setStrokeWidth(0.5);

            container.getChildren().addAll(couronne, joyauCentral);
        }

        return container;
    }
}
