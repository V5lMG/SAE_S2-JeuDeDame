package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour la classe Joueur.
  * @author MUNIER-GÉNIE Valentin
 */
public class JoueurTest {

    @Test
    @DisplayName("TDD : Un joueur doit être créé avec sa couleur et une liste vide de pions")
    void doitCreerJoueurCorrectement() {
        // Given & When
        Joueur j = new Joueur(Pion.COULEUR_BLANC);

        // Then
        assertEquals(Pion.COULEUR_BLANC, j.getColor());
        assertEquals(0, j.getNombreDePions());
        assertFalse(j.aDesPions());
    }

    @Test
    @DisplayName("TDD : L'ajout et le retrait de pions doivent mettre à jour le compteur")
    void doitGererAjoutEtRetraitDePions() {
        // Given
        Joueur j = new Joueur(Pion.COULEUR_NOIR);
        Pion p1 = new Pion(false, 1, 6, Pion.COULEUR_NOIR);
        Pion p2 = new Pion(false, 3, 6, Pion.COULEUR_NOIR);

        // When
        j.ajouterPion(p1);
        j.ajouterPion(p2);

        // Then
        assertEquals(2, j.getNombreDePions());
        assertTrue(j.aDesPions());
        assertTrue(j.getPions().contains(p1));
        assertTrue(j.getPions().contains(p2));

        // When : retrait
        j.retirerPion(p1);

        // Then
        assertEquals(1, j.getNombreDePions());
        assertFalse(j.getPions().contains(p1));
        assertTrue(j.getPions().contains(p2));

        // When : reset
        j.reset();

        // Then
        assertEquals(0, j.getNombreDePions());
        assertFalse(j.aDesPions());
    }

    @Test
    @DisplayName("TDD : La liste des pions fournie par getPions() doit être non modifiable directement")
    void doitProtegerEncapsulationListePions() {
        // Given
        Joueur j = new Joueur(Pion.COULEUR_BLANC);
        Pion p = new Pion(false, 0, 0, Pion.COULEUR_BLANC);
        j.ajouterPion(p);

        // Then
        assertThrows(UnsupportedOperationException.class, () -> j.getPions().add(new Pion(false, 1, 1, Pion.COULEUR_BLANC)));
    }
}

