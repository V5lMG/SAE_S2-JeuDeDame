package fr.iutrodez.jeudedame.modele;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Spécifications TDD pour le moteur de damier Plateau.
  * @author MUNIER-GÉNIE Valentin
 */
public class PlateauTest {

    private Plateau plateau;
    private Joueur joueurBlanc;
    private Joueur joueurNoir;

    @BeforeEach
    void setUp() {
        plateau = new Plateau();
        joueurBlanc = new Joueur(Pion.COULEUR_BLANC);
        joueurNoir = new Joueur(Pion.COULEUR_NOIR);
        plateau.initialiser(joueurNoir, joueurBlanc);
    }

    @Test
    @DisplayName("TDD : L'initialisation doit placer 20 pions blancs et 20 pions noirs sur cases sombres")
    void doitInitialiserPlateauOfficiel() {
        // Then
        assertEquals(20, joueurBlanc.getNombreDePions());
        assertEquals(20, joueurNoir.getNombreDePions());

        // Noirs aux lignes 0 à 3 (en haut)
        for (Pion p : joueurNoir.getPions()) {
            assertTrue(plateau.getCase(p.getPosX(), p.getPosY()).estCaseJouable());
            assertTrue(p.getPosY() >= 0 && p.getPosY() <= 3);
            assertTrue(p.estNoir());
            assertFalse(p.isDame());
        }

        // Blancs aux lignes 6 à 9 (en bas, côté joueur)
        for (Pion p : joueurBlanc.getPions()) {
            assertTrue(plateau.getCase(p.getPosX(), p.getPosY()).estCaseJouable());
            assertTrue(p.getPosY() >= 6 && p.getPosY() <= 9);
            assertTrue(p.estBlanc());
            assertFalse(p.isDame());
        }

        // Lignes centrales 4 et 5 vides
        for (int y = 4; y <= 5; y++) {
            for (int x = 0; x < Plateau.TAILLE; x++) {
                assertNull(plateau.getPion(x, y));
            }
        }
    }

    @Test
    @DisplayName("TDD : Un pion blanc doit avancer d'une case en diagonale vers le haut (y - 1)")
    void doitDeplacerPionBlancVersLeHaut() {
        // Given : pion blanc sur la ligne 6 (face avant des Blancs)
        Pion pion = plateau.getPion(1, 6);
        assertNotNull(pion);

        // When
        List<Coup> coups = plateau.getCoupsSimples(pion);

        // Then : en (1, 6), il peut aller en (0, 5) ou (2, 5)
        assertEquals(2, coups.size());
        Coup coupChoisi = coups.get(0);
        assertEquals(5, coupChoisi.getArriveeY());

        // When
        plateau.executerCoup(coupChoisi, pion);

        // Then
        assertNull(plateau.getPion(1, 6));
        assertSame(pion, plateau.getPion(coupChoisi.getArriveeX(), coupChoisi.getArriveeY()));
        assertEquals(coupChoisi.getArriveeX(), pion.getPosX());
        assertEquals(coupChoisi.getArriveeY(), pion.getPosY());
    }

    @Test
    @DisplayName("TDD : Un pion noir doit avancer d'une case en diagonale vers le bas (y + 1)")
    void doitDeplacerPionNoirVersLeBas() {
        // Given : pion noir sur la ligne 3 (face avant des Noirs)
        Pion pion = plateau.getPion(2, 3);
        assertNotNull(pion);

        // When
        List<Coup> coups = plateau.getCoupsSimples(pion);

        // Then : en (2, 3), il peut aller en (1, 4) ou (3, 4)
        assertEquals(2, coups.size());
        Coup coup = coups.get(0);
        assertEquals(4, coup.getArriveeY());

        // When
        plateau.executerCoup(coup, pion);

        // Then
        assertNull(plateau.getPion(2, 3));
        assertSame(pion, plateau.getPion(coup.getArriveeX(), coup.getArriveeY()));
    }

    @Test
    @DisplayName("TDD : Un pion simple doit pouvoir capturer un pion adverse en arrière")
    void doitPermettreCaptureArrierePourPionSimple() {
        // Given
        Plateau p = new Plateau();
        Joueur jb = new Joueur(Pion.COULEUR_BLANC);
        Joueur jn = new Joueur(Pion.COULEUR_NOIR);

        Pion blanc = new Pion(false, 3, 3, Pion.COULEUR_BLANC);
        p.placerPion(blanc, 3, 3);
        jb.ajouterPion(blanc);

        // Pion noir situé en arrière (y=2 < y=3)
        Pion noir = new Pion(false, 2, 2, Pion.COULEUR_NOIR);
        p.placerPion(noir, 2, 2);
        jn.ajouterPion(noir);

        // When
        List<Coup> captures = p.getCoupsDeCapture(blanc);

        // Then : capture en (1, 1) en sautant (2, 2)
        assertEquals(1, captures.size());
        Coup capture = captures.get(0);
        assertEquals(1, capture.getArriveeX());
        assertEquals(1, capture.getArriveeY());
        assertSame(noir, capture.getPionCapture());

        // When
        p.executerCoup(capture, blanc);

        // Then
        assertSame(blanc, p.getPion(1, 1));
        assertNull(p.getPion(2, 2), "Le pion adverse capturé doit être retiré");
        assertNull(p.getPion(3, 3), "La case d'origine doit être libérée");
    }

    @Test
    @DisplayName("TDD : Si un joueur a une capture possible, la prise est obligatoire (coups simples bloqués)")
    void doitAppliquerPriseObligatoire() {
        // Given
        Plateau p = new Plateau();
        Joueur jb = new Joueur(Pion.COULEUR_BLANC);
        Joueur jn = new Joueur(Pion.COULEUR_NOIR);

        Pion blancPreneur = new Pion(false, 3, 3, Pion.COULEUR_BLANC);
        Pion blancBloque = new Pion(false, 7, 3, Pion.COULEUR_BLANC);
        p.placerPion(blancPreneur, 3, 3);
        p.placerPion(blancBloque, 7, 3);
        jb.ajouterPion(blancPreneur);
        jb.ajouterPion(blancBloque);

        Pion noirCible = new Pion(false, 4, 4, Pion.COULEUR_NOIR);
        p.placerPion(noirCible, 4, 4);
        jn.ajouterPion(noirCible);

        // Then
        assertTrue(p.joueurADesCaptures(jb));

        // Le pion preneur a un coup de capture obligatoire
        List<Coup> coupsPreneur = p.getCoupsLegaux(blancPreneur, jb);
        assertEquals(1, coupsPreneur.size());
        assertTrue(coupsPreneur.get(0).estCapture());

        // L'autre pion blanc n'a aucun coup légal autorisé (prise obligatoire globale)
        List<Coup> coupsBloque = p.getCoupsLegaux(blancBloque, jb);
        assertTrue(coupsBloque.isEmpty());
    }

    @Test
    @DisplayName("TDD : Un pion blanc arrivant en ligne 0 ou un noir en ligne 9 doit être promu en dame")
    void doitVerifierPromotionEnDame() {
        // Given
        Plateau p = new Plateau();
        Joueur jb = new Joueur(Pion.COULEUR_BLANC);
        Joueur jn = new Joueur(Pion.COULEUR_NOIR);

        Pion blanc = new Pion(false, 3, 1, Pion.COULEUR_BLANC);
        p.placerPion(blanc, 3, 1);
        jb.ajouterPion(blanc);

        // When : coup blanc menant à la ligne 0 (promotion Blancs)
        Coup coupBlanc = new Coup(3, 1, 2, 0);
        p.executerCoup(coupBlanc, blanc);
        boolean promuBlanc = p.verifierPromotion(blanc);

        // Then
        assertTrue(promuBlanc);
        assertTrue(blanc.isDame());

        // Test Noir vers ligne 9 (promotion Noirs)
        Pion noir = new Pion(false, 2, 8, Pion.COULEUR_NOIR);
        p.placerPion(noir, 2, 8);
        jn.ajouterPion(noir);

        Coup coupNoir = new Coup(2, 8, 1, 9);
        p.executerCoup(coupNoir, noir);
        boolean promuNoir = p.verifierPromotion(noir);

        assertTrue(promuNoir);
        assertTrue(noir.isDame());
    }

    @Test
    @DisplayName("TDD : Une dame doit pouvoir voler et capturer sur toute la diagonale libre")
    void doitGererDeplacementsEtCapturesDameVolante() {
        // Given
        Plateau p = new Plateau();
        Joueur jb = new Joueur(Pion.COULEUR_BLANC);
        Joueur jn = new Joueur(Pion.COULEUR_NOIR);

        Pion dame = new Pion(true, 4, 4, Pion.COULEUR_BLANC);
        p.placerPion(dame, 4, 4);
        jb.ajouterPion(dame);

        // Then : en (4, 4), la dame a accès à toutes les diagonales libres
        List<Coup> simples = p.getCoupsSimples(dame);
        assertTrue(simples.size() >= 10);

        // Given : un pion adverse en (6, 6)
        Pion noir = new Pion(false, 6, 6, Pion.COULEUR_NOIR);
        p.placerPion(noir, 6, 6);
        jn.ajouterPion(noir);

        // When
        List<Coup> captures = p.getCoupsDeCapture(dame);

        // Then : atterrissages possibles en (7, 7), (8, 8), (9, 9)
        assertEquals(3, captures.size());
        for (Coup c : captures) {
            assertSame(noir, c.getPionCapture());
            assertTrue(c.getArriveeX() >= 7 && c.getArriveeY() >= 7);
        }
    }
}

