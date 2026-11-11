package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour la classe Pion.
  * @author MUNIER-GÉNIE Valentin
 */
public class PionTest {

    @Test
    @DisplayName("TDD : Un pion créé doit conserver sa position, sa couleur et débuter en pion simple")
    void doitCreerPionSimpleCorrectement() {
        // Given & When
        Pion p = new Pion(false, 3, 5, Pion.COULEUR_BLANC);

        // Then
        assertEquals(3, p.getPosX());
        assertEquals(5, p.getPosY());
        assertEquals(Pion.COULEUR_BLANC, p.getColor());
        assertFalse(p.isDame());
        assertTrue(p.estBlanc());
        assertFalse(p.estNoir());
    }

    @Test
    @DisplayName("TDD : La méthode promouvoir() doit transformer le pion en dame")
    void doitPromouvoirPionEnDame() {
        // Given
        Pion p = new Pion(false, 1, 9, Pion.COULEUR_BLANC);

        // When
        p.promouvoir();

        // Then
        assertTrue(p.isDame());
    }

    @Test
    @DisplayName("TDD : Les mutateurs setPosX et setPosY doivent mettre à jour les coordonnées")
    void doitMettreAJourCoordonnees() {
        // Given
        Pion p = new Pion(false, 2, 6, Pion.COULEUR_NOIR);

        // When
        p.setPosX(3);
        p.setPosY(5);

        // Then
        assertEquals(3, p.getPosX());
        assertEquals(5, p.getPosY());
    }

    @Test
    @DisplayName("TDD : Deux pions aux mêmes coordonnées et même statut doivent être égaux")
    void doitRespecterContratEqualsEtHashCode() {
        Pion p1 = new Pion(false, 2, 4, Pion.COULEUR_NOIR);
        Pion p2 = new Pion(false, 2, 4, Pion.COULEUR_NOIR);
        Pion p3 = new Pion(true, 2, 4, Pion.COULEUR_NOIR);
        Pion p4 = new Pion(false, 2, 4, Pion.COULEUR_BLANC);

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
        assertNotEquals(p1, p4);
    }
}

