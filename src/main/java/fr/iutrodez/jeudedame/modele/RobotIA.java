package fr.iutrodez.jeudedame.modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Moteur d'Intelligence Artificielle (Robot) pour le jeu de dames.
 * Implémente une stratégie heuristique multicritère :
 * - Respect absolu de la prise obligatoire et priorité aux rafles longues.
 * - Ciblage préférentiel des Dames adverses.
 * - Promotion tactique en ligne de fond.
 * - Analyse de sécurité pour éviter de donner des pions à l'adversaire.
 * - Contrôle positionnel du centre et des bordures.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class RobotIA {

    /** Poids de base attribué à une capture simple. */
    private static final double POIDS_CAPTURE_BASE = 100.0;

    /** Bonus pour la capture d'une Dame adverse. */
    private static final double BONUS_CAPTURE_DAME = 150.0;

    /** Bonus pour une promotion immédiate en Dame. */
    private static final double BONUS_PROMOTION = 130.0;

    /** Pénalité majeure si une case expose immédiatement le pion à une prise adverse. */
    private static final double PENALITE_CASE_DANGEREUSE = 60.0;

    /** Couleur jouée par le robot ("NOIR" par défaut). */
    private final String couleur;

    /** Générateur aléatoire pour introduire une légère variété tactique. */
    private final Random random;

    /**
     * Crée un robot configuré pour jouer la couleur spécifiée.
     *
     * @param couleur la couleur du robot ("NOIR" ou "BLANC")
     */
    public RobotIA(String couleur) {
        this.couleur = Objects.requireNonNull(couleur, "La couleur ne peut pas être null").toUpperCase();
        this.random = new Random();
    }

    /**
     * @return la couleur du robot
     */
    public String getCouleur() {
        return couleur;
    }

    /**
     * Enveloppe associant un pion, un coup candidat et son score heuristique.
     */
    public static final class ActionRobot {
        private final Pion pion;
        private final Coup coup;
        private final double score;

        public ActionRobot(Pion pion, Coup coup, double score) {
            this.pion = pion;
            this.coup = coup;
            this.score = score;
        }

        public Pion getPion() {
            return pion;
        }

        public Coup getCoup() {
            return coup;
        }

        public double getScore() {
            return score;
        }
    }

    /**
     * Calcule et sélectionne la meilleure action à jouer selon l'état actuel de la partie.
     *
     * @param partie la partie en cours
     * @return la meilleure action (Pion et Coup), ou null si aucun coup n'est possible
     */
    public ActionRobot choisirCoup(Partie partie) {
        if (partie == null || partie.isPartieTerminee()) {
            return null;
        }

        Joueur joueurRobot = couleur.equalsIgnoreCase(Pion.COULEUR_BLANC)
                ? partie.getJoueurBlanc()
                : partie.getJoueurNoir();
        Joueur joueurAdverse = couleur.equalsIgnoreCase(Pion.COULEUR_BLANC)
                ? partie.getJoueurNoir()
                : partie.getJoueurBlanc();
        Plateau plateau = partie.getPlateau();

        // 1. Si une rafle est en cours, seul le pion en rafle est jouable
        if (partie.getPionEnRafle() != null) {
            Pion pionRafle = partie.getPionEnRafle();
            List<Coup> captures = plateau.getCoupsDeCapture(pionRafle);
            if (captures.isEmpty()) return null;

            Coup meilleurCoup = captures.get(0);
            double meilleurScore = -Double.MAX_VALUE;
            for (Coup c : captures) {
                double score = evaluerCapture(c, pionRafle, plateau, joueurAdverse);
                if (score > meilleurScore) {
                    meilleurScore = score;
                    meilleurCoup = c;
                }
            }
            return new ActionRobot(pionRafle, meilleurCoup, meilleurScore);
        }

        // 2. Prise obligatoire ou déplacements simples
        boolean priseObligatoire = plateau.joueurADesCaptures(joueurRobot);
        List<ActionRobot> actionsPossibles = new ArrayList<>();

        for (Pion pion : joueurRobot.getPions()) {
            List<Coup> coups = priseObligatoire
                    ? plateau.getCoupsDeCapture(pion)
                    : plateau.getCoupsLegaux(pion, joueurRobot);

            for (Coup coup : coups) {
                double score = coup.estCapture()
                        ? evaluerCapture(coup, pion, plateau, joueurAdverse)
                        : evaluerDeplacementSimple(coup, pion, plateau, joueurAdverse);

                score += (random.nextDouble() * 4.0 - 2.0);
                actionsPossibles.add(new ActionRobot(pion, coup, score));
            }
        }

        if (actionsPossibles.isEmpty()) {
            return null;
        }

        actionsPossibles.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        return actionsPossibles.get(0);
    }

    /**
     * Évalue l'intérêt tactique d'un coup de capture.
     */
    private double evaluerCapture(Coup coup, Pion pion, Plateau plateau, Joueur adverse) {
        double score = POIDS_CAPTURE_BASE;

        // Bonus pour la capture d'une Dame adverse
        if (coup.getPionCapture() != null && coup.getPionCapture().isDame()) {
            score += BONUS_CAPTURE_DAME;
        }

        // Bonus pour promotion consécutive
        if (!pion.isDame()) {
            if ((pion.estNoir() && coup.getArriveeY() == Plateau.LIGNE_PROMOTION_NOIR) ||
                (pion.estBlanc() && coup.getArriveeY() == Plateau.LIGNE_PROMOTION_BLANC)) {
                score += BONUS_PROMOTION;
            }
        }

        // Simulation temporaire pour vérifier les rafles enchaînables
        int oldX = pion.getPosX();
        int oldY = pion.getPosY();
        pion.setPosX(coup.getArriveeX());
        pion.setPosY(coup.getArriveeY());

        List<Coup> rafleSuivante = plateau.getCoupsDeCapture(pion);
        if (!rafleSuivante.isEmpty()) {
            score += 75.0 * rafleSuivante.size();
        }

        pion.setPosX(oldX);
        pion.setPosY(oldY);

        return score;
    }

    /**
     * Évalue l'intérêt positionnel d'un déplacement simple.
     */
    private double evaluerDeplacementSimple(Coup coup, Pion pion, Plateau plateau, Joueur adverse) {
        double score = 10.0;
        int destX = coup.getArriveeX();
        int destY = coup.getArriveeY();

        // Promotion immédiate
        if (!pion.isDame()) {
            if ((pion.estNoir() && destY == Plateau.LIGNE_PROMOTION_NOIR) ||
                (pion.estBlanc() && destY == Plateau.LIGNE_PROMOTION_BLANC)) {
                return 160.0;
            }
        }

        // Avancement vers le camp adverse (Noirs avancent vers y=9, Blancs vers y=0)
        if (!pion.isDame()) {
            int progression = pion.estNoir() ? destY : (9 - destY);
            score += progression * 3.5;
        }

        // Contrôle du centre (colonnes 3 à 6)
        if (destX >= 3 && destX <= 6 && destY >= 3 && destY <= 6) {
            score += 12.0;
        }

        // Sécurité des bords
        if (destX == 0 || destX == 9) {
            score += 6.0;
        }

        // Sécurité : éviter de donner le pion
        if (estCaseDangereuse(destX, destY, plateau, adverse)) {
            score -= PENALITE_CASE_DANGEREUSE;
        }

        return score;
    }

    /**
     * Vérifie si un pion posé en (x, y) est immédiatement prenable par l'adversaire.
     */
    private boolean estCaseDangereuse(int x, int y, Plateau plateau, Joueur adverse) {
        int[][] directions = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
        for (int[] dir : directions) {
            int advX = x - dir[0];
            int advY = y - dir[1];
            int landX = x + dir[0];
            int landY = y + dir[1];

            if (plateau.isWithinBounds(advX, advY) && plateau.isWithinBounds(landX, landY)) {
                Pion advPion = plateau.getPion(advX, advY);
                if (advPion != null && advPion.getColor().equalsIgnoreCase(adverse.getColor())) {
                    if (plateau.getPion(landX, landY) == null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
