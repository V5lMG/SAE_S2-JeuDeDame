package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour le moteur d'Intelligence Artificielle RobotIA.
  * @author MUNIER-GÉNIE Valentin
 */
public class RobotIATest {

    @Test
    @DisplayName("TDD : Le robot doit obligatoirement choisir un coup de capture lorsqu'il en a un")
    void doitForcerCaptureObligatoirePourLeRobot() {
        // Given
        Partie partie = new Partie();
        Plateau p = partie.getPlateau();

        for (int y = 0; y < Plateau.TAILLE; y++) {
            for (int x = 0; x < Plateau.TAILLE; x++) {
                p.getCase(x, y).reset();
            }
        }
        partie.getJoueurBlanc().reset();
        partie.getJoueurNoir().reset();

        Pion noir = new Pion(false, 4, 4, Pion.COULEUR_NOIR);
        p.placerPion(noir, 4, 4);
        partie.getJoueurNoir().ajouterPion(noir);

        Pion blanc = new Pion(false, 3, 3, Pion.COULEUR_BLANC);
        p.placerPion(blanc, 3, 3);
        partie.getJoueurBlanc().ajouterPion(blanc);

        partie.changerJoueur(); // Tour du robot (Noir)

        // When
        RobotIA robot = new RobotIA(Pion.COULEUR_NOIR);
        RobotIA.ActionRobot action = robot.choisirCoup(partie);

        // Then
        assertNotNull(action);
        assertSame(noir, action.getPion());
        assertTrue(action.getCoup().estCapture());
        assertEquals(2, action.getCoup().getArriveeX());
        assertEquals(2, action.getCoup().getArriveeY());
        assertSame(blanc, action.getCoup().getPionCapture());
    }

    @Test
    @DisplayName("TDD : En début de partie, le robot (Noir) doit sélectionner un déplacement simple vers le bas (y + 1)")
    void doitChoisirDeplacementSimpleValideEnOuverture() {
        // Given
        Partie partie = new Partie();
        partie.changerJoueur(); // Tour du robot (Noir)

        // When
        RobotIA robot = new RobotIA(Pion.COULEUR_NOIR);
        RobotIA.ActionRobot action = robot.choisirCoup(partie);

        // Then
        assertNotNull(action);
        assertTrue(action.getPion().estNoir());
        assertFalse(action.getCoup().estCapture());
        assertEquals(action.getPion().getPosY() + 1, action.getCoup().getArriveeY());
    }
}

