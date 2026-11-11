package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour le gestionnaire d'arbitrage Partie.
  * @author MUNIER-GÉNIE Valentin
 */
public class PartieTest {

    @Test
    @DisplayName("TDD : Les Blancs doivent commencer la partie, puis les tours alternent")
    void doitAlternerToursReglementairement() {
        // Given
        Partie partie = new Partie();
        assertEquals(Pion.COULEUR_BLANC, partie.getJoueurActuel().getColor());

        // When : coup des Blancs (1, 6) -> (0, 5) vers le haut
        assertTrue(partie.selectionnerPion(1, 6));
        assertTrue(partie.deplacerPion(0, 5));

        // Then : au tour des Noirs
        assertEquals(Pion.COULEUR_NOIR, partie.getJoueurActuel().getColor());

        // When : coup des Noirs (2, 3) -> (1, 4) vers le bas
        assertTrue(partie.selectionnerPion(2, 3));
        assertTrue(partie.deplacerPion(1, 4));

        // Then : retour aux Blancs
        assertEquals(Pion.COULEUR_BLANC, partie.getJoueurActuel().getColor());
    }

    @Test
    @DisplayName("TDD : Un joueur ne peut pas sélectionner un pion appartenant à l'adversaire")
    void doitInterdireSelectionPionAdverse() {
        // Given : au tour des Blancs
        Partie partie = new Partie();

        // When : tentative de sélectionner un pion noir en (2, 3)
        boolean selection = partie.selectionnerPion(2, 3);

        // Then
        assertFalse(selection);
        assertNull(partie.getPionSelectionne());
    }

    @Test
    @DisplayName("TDD : En cas de rafle (prises multiples), le joueur conserve son tour avec le même pion")
    void doitGererChaineDeRafleConsecutive() {
        // Given
        Partie partie = new Partie();
        Plateau p = partie.getPlateau();

        // Vider le damier pour préparer le cas d'école
        for (int y = 0; y < Plateau.TAILLE; y++) {
            for (int x = 0; x < Plateau.TAILLE; x++) {
                p.getCase(x, y).reset();
            }
        }
        partie.getJoueurBlanc().reset();
        partie.getJoueurNoir().reset();

        // Pion blanc en (2, 2)
        Pion blanc = new Pion(false, 2, 2, Pion.COULEUR_BLANC);
        p.placerPion(blanc, 2, 2);
        partie.getJoueurBlanc().ajouterPion(blanc);

        // Deux cibles noires successives : (3, 3) puis (5, 5)
        Pion noir1 = new Pion(false, 3, 3, Pion.COULEUR_NOIR);
        Pion noir2 = new Pion(false, 5, 5, Pion.COULEUR_NOIR);
        p.placerPion(noir1, 3, 3);
        p.placerPion(noir2, 5, 5);
        partie.getJoueurNoir().ajouterPion(noir1);
        partie.getJoueurNoir().ajouterPion(noir2);

        // When : 1ère capture vers (4, 4)
        assertTrue(partie.selectionnerPion(2, 2));
        assertTrue(partie.deplacerPion(4, 4));

        // Then : rafle en cours, le tour reste aux Blancs !
        assertEquals(1, partie.getJoueurNoir().getNombreDePions());
        assertNotNull(partie.getPionEnRafle());
        assertSame(blanc, partie.getPionEnRafle());
        assertEquals(Pion.COULEUR_BLANC, partie.getJoueurActuel().getColor());

        // When : 2ème capture vers (6, 6)
        assertTrue(partie.deplacerPion(6, 6));

        // Then : fin de rafle et victoire car tous les Noirs sont éliminés
        assertEquals(0, partie.getJoueurNoir().getNombreDePions());
        assertNull(partie.getPionEnRafle());
        assertTrue(partie.isPartieTerminee());
        assertSame(partie.getJoueurBlanc(), partie.getJoueurGagnant());
    }

    @Test
    @DisplayName("TDD : La partie doit déclarer vainqueur le joueur adverse lorsque le joueur actif est totalement bloqué")
    void doitDetecterVictoireParBlocage() {
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

        // Pion noir bloqué en coin (0, 0)
        Pion noir = new Pion(false, 0, 0, Pion.COULEUR_NOIR);
        p.placerPion(noir, 0, 0);
        partie.getJoueurNoir().ajouterPion(noir);

        // Pions blancs bouchant la sortie et interdisant le saut
        Pion b1 = new Pion(false, 1, 1, Pion.COULEUR_BLANC);
        Pion b2 = new Pion(false, 2, 2, Pion.COULEUR_BLANC);
        p.placerPion(b1, 1, 1);
        p.placerPion(b2, 2, 2);
        partie.getJoueurBlanc().ajouterPion(b1);
        partie.getJoueurBlanc().ajouterPion(b2);

        // When : tour de Noir
        partie.changerJoueur();
        assertEquals(Pion.COULEUR_NOIR, partie.getJoueurActuel().getColor());

        // Then : fin de partie déclarée car Noir ne peut jouer aucun coup
        assertTrue(partie.verifierFinDePartie());
        assertSame(partie.getJoueurBlanc(), partie.getJoueurGagnant());
    }
}

