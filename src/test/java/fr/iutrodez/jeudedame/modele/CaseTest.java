package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour la classe Case.
  * @author MUNIER-GÉNIE Valentin
 */
public class CaseTest {

    @Test
    @DisplayName("TDD : Une case doit stocker fidèlement ses coordonnées (x, y)")
    void doitStockerCoordonneesCorrectement() {
        // Given & When
        Case c = new Case(3, 7);

        // Then
        assertEquals(3, c.getX());
        assertEquals(7, c.getY());
        assertFalse(c.estOccupee());
        assertNull(c.getPion());
    }

    @Test
    @DisplayName("TDD : Une case avec des coordonnées négatives ou >= 10 doit lever une exception")
    void doitRejeterCoordonneesHorsLimites() {
        // Coordonnées négatives
        assertThrows(IllegalArgumentException.class, () -> new Case(-1, 4));
        assertThrows(IllegalArgumentException.class, () -> new Case(4, -1));

        // Coordonnées >= 10
        assertThrows(IllegalArgumentException.class, () -> new Case(10, 5));
        assertThrows(IllegalArgumentException.class, () -> new Case(5, 10));
    }

    @Test
    @DisplayName("TDD : Une case est jouable si et seulement si (x + y) est impair")
    void doitIdentifierCasesJouables() {
        // (0, 0) est claire -> non jouable
        assertFalse(new Case(0, 0).estCaseJouable());

        // (1, 0) et (0, 1) sont sombres -> jouables
        assertTrue(new Case(1, 0).estCaseJouable());
        assertTrue(new Case(0, 1).estCaseJouable());

        // (3, 5) a 3+5=8 (pair) -> non jouable
        assertFalse(new Case(3, 5).estCaseJouable());

        // (3, 4) a 3+4=7 (impair) -> jouable
        assertTrue(new Case(3, 4).estCaseJouable());
    }

    @Test
    @DisplayName("TDD : L'assignation et la réinitialisation de pion doivent fonctionner")
    void doitGererOccupationEtReset() {
        // Given
        Case c = new Case(2, 3);
        Pion p = new Pion(false, 2, 3, Pion.COULEUR_BLANC);

        // When
        c.setPion(p);

        // Then
        assertTrue(c.estOccupee());
        assertSame(p, c.getPion());

        // When
        c.reset();

        // Then
        assertFalse(c.estOccupee());
        assertNull(c.getPion());
    }
}

