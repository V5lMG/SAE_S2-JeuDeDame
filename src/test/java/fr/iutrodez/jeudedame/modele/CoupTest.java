package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour la classe immuable Coup.
  * @author MUNIER-GÉNIE Valentin
 */
public class CoupTest {

    @Test
    @DisplayName("TDD : Un coup de déplacement simple ne doit pas comporter de pion capturé")
    void doitCreerCoupSimpleCorrectement() {
        // Given & When
        Coup coup = new Coup(1, 3, 2, 4);

        // Then
        assertEquals(1, coup.getDepartX());
        assertEquals(3, coup.getDepartY());
        assertEquals(2, coup.getArriveeX());
        assertEquals(4, coup.getArriveeY());
        assertFalse(coup.estCapture());
        assertNull(coup.getPionCapture());
    }

    @Test
    @DisplayName("TDD : Un coup de capture doit référencer la pièce sautée")
    void doitCreerCoupDeCaptureCorrectement() {
        // Given
        Pion adverse = new Pion(false, 3, 3, Pion.COULEUR_NOIR);

        // When
        Coup coup = new Coup(2, 2, 4, 4, adverse);

        // Then
        assertTrue(coup.estCapture());
        assertSame(adverse, coup.getPionCapture());
        assertEquals(2, coup.getDepartX());
        assertEquals(4, coup.getArriveeX());
    }

    @Test
    @DisplayName("TDD : Deux coups avec les mêmes coordonnées et captures doivent être sémantiquement égaux")
    void doitRespecterContratEqualsEtHashCode() {
        // Given
        Pion p1 = new Pion(false, 3, 3, Pion.COULEUR_NOIR);
        Pion p2 = new Pion(false, 3, 3, Pion.COULEUR_NOIR);

        Coup c1 = new Coup(2, 2, 4, 4, p1);
        Coup c2 = new Coup(2, 2, 4, 4, p2);
        Coup cDiff = new Coup(2, 2, 0, 4, p1);

        // Then
        assertEquals(c1, c2);
        assertEquals(c1.hashCode(), c2.hashCode());
        assertNotEquals(c1, cDiff);
    }
}

