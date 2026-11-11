package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour le service de persistance GestionnaireSauvegarde.
  * @author MUNIER-GÉNIE Valentin
 */
public class GestionnaireSauvegardeTest {

    @AfterEach
    void tearDown() {
        GestionnaireSauvegarde.supprimerSauvegarde();
    }

    @Test
    @DisplayName("TDD : La sérialisation et la désérialisation doivent restaurer fidèlement la partie")
    void doitSauvegarderEtRestaurerPartieFidelement() {
        // Given
        Partie partie = new Partie();
        long chrono = 215;
        boolean vsRobot = true;

        // Jouer un coup pour créer un état non initial (Blanc avance vers le haut)
        partie.selectionnerPion(1, 6);
        partie.deplacerPion(0, 5);

        // When
        boolean succes = GestionnaireSauvegarde.sauvegarder(partie, chrono, vsRobot);

        // Then
        assertTrue(succes);
        assertTrue(GestionnaireSauvegarde.existeSauvegarde());

        // When : rechargement
        GestionnaireSauvegarde.DonneesSauvegarde donnees = GestionnaireSauvegarde.charger();

        // Then
        assertNotNull(donnees);
        assertEquals(chrono, donnees.getTempsSecondes());
        assertTrue(donnees.isVsRobot());

        Partie restauree = donnees.getPartie();
        assertNotNull(restauree);
        assertEquals(Pion.COULEUR_NOIR, restauree.getJoueurActuel().getColor());

        // Vérifier l'emplacement déplacé
        Pion p = restauree.getPionAt(0, 5);
        assertNotNull(p);
        assertTrue(p.estBlanc());
        assertNull(restauree.getPionAt(1, 6));
    }

    @Test
    @DisplayName("TDD : En l'absence de fichier, existeSauvegarde() doit renvoyer false et charger() doit renvoyer null")
    void doitGererAbsenceDeFichierProprement() {
        // Given
        GestionnaireSauvegarde.supprimerSauvegarde();

        // Then
        assertFalse(GestionnaireSauvegarde.existeSauvegarde());
        assertNull(GestionnaireSauvegarde.charger());
    }
}

