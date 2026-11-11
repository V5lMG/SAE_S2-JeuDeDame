package fr.iutrodez.jeudedame.modele;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestionnaire d'état et d'arbitrage d'une partie de jeu de dames.
 * Coordonne les tours des joueurs, la sélection des pièces, l'enchaînement des rafles,
 * la prise obligatoire et les conditions de victoire par élimination ou blocage.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class Partie {

    /** Joueur contrôlant les pièces blanches. */
    private final Joueur joueurBlanc;

    /** Joueur contrôlant les pièces noires. */
    private final Joueur joueurNoir;

    /** Damier 10x10. */
    private final Plateau plateau;

    /** Joueur dont c'est le tour actuellement. */
    private Joueur joueurActuel;

    /** Pion actuellement sélectionné par le joueur actif. */
    private Pion pionSelectionne;

    /** Pion en cours de rafle (doit obligatoirement enchaîner ses captures). */
    private Pion pionEnRafle;

    /** Vainqueur de la partie, ou null si la partie est toujours en cours. */
    private Joueur joueurGagnant;

    /** Indique si la partie est terminée. */
    private boolean partieTerminee;

    /** Dernier coup exécuté sur le plateau. */
    private Coup dernierCoup;

    /**
     * Démarre une nouvelle partie réglementaire :
     * 20 pions blancs et 20 pions noirs positionnés, les Blancs ouvrent la partie.
     */
    public Partie() {
        this.joueurBlanc = new Joueur(Pion.COULEUR_BLANC);
        this.joueurNoir = new Joueur(Pion.COULEUR_NOIR);
        this.plateau = new Plateau();
        this.plateau.initialiser(joueurNoir, joueurBlanc);
        this.joueurActuel = joueurBlanc;
        this.partieTerminee = false;
        this.joueurGagnant = null;
        this.pionSelectionne = null;
        this.pionEnRafle = null;
    }

    /**
     * @return le plateau de jeu associé à cette partie
     */
    public Plateau getPlateau() {
        return plateau;
    }

    /**
     * @return le joueur Blanc
     */
    public Joueur getJoueurBlanc() {
        return joueurBlanc;
    }

    /**
     * @return le joueur Noir
     */
    public Joueur getJoueurNoir() {
        return joueurNoir;
    }

    /**
     * @return le joueur devant effectuer le prochain coup
     */
    public Joueur getJoueurActuel() {
        return joueurActuel;
    }

    /**
     * @return le pion actuellement sélectionné, ou null
     */
    public Pion getPionSelectionne() {
        return pionSelectionne;
    }

    /**
     * @return le pion enchaînant une rafle, ou null
     */
    public Pion getPionEnRafle() {
        return pionEnRafle;
    }

    /**
     * @return true si la partie est achevée, false sinon
     */
    public boolean isPartieTerminee() {
        return partieTerminee;
    }

    /**
     * @return le joueur victorieux, ou null si la partie continue
     */
    public Joueur getJoueurGagnant() {
        return joueurGagnant;
    }

    /**
     * @return le dernier coup joué
     */
    public Coup getDernierCoup() {
        return dernierCoup;
    }

    /**
     * Raccourci vers le pion situé sur la case (x, y).
     *
     * @param x colonne
     * @param y ligne
     * @return le pion présent, ou null
     */
    public Pion getPionAt(int x, int y) {
        return plateau.getPion(x, y);
    }

    /**
     * Retourne la liste de tous les pions du joueur actif ayant l'obligation de capturer.
     *
     * @return liste des pions devant obligatoirement prendre
     */
    public List<Pion> getPionsAyantCaptures() {
        List<Pion> pions = new ArrayList<>();
        if (pionEnRafle != null) {
            pions.add(pionEnRafle);
            return pions;
        }
        for (Pion p : joueurActuel.getPions()) {
            if (!plateau.getCoupsDeCapture(p).isEmpty()) {
                pions.add(p);
            }
        }
        return pions;
    }

    /**
     * Sélectionne un pion du joueur actif situé en (x, y).
     * Vérifie la règle de prise obligatoire et l'obligation de rafle.
     *
     * @param x colonne
     * @param y ligne
     * @return true si la sélection est acceptée, false sinon
     */
    public boolean selectionnerPion(int x, int y) {
        if (partieTerminee) {
            return false;
        }

        Pion pion = plateau.getPion(x, y);
        if (pion == null || !pion.getColor().equalsIgnoreCase(joueurActuel.getColor())) {
            return false;
        }

        // Pendant une rafle, seul le pion en rafle est jouable
        if (pionEnRafle != null) {
            if (pion == pionEnRafle) {
                pionSelectionne = pion;
                return true;
            }
            return false;
        }

        // Si la prise est obligatoire, seuls les pions preneurs peuvent être sélectionnés
        List<Pion> preneurs = getPionsAyantCaptures();
        if (!preneurs.isEmpty()) {
            if (preneurs.contains(pion)) {
                pionSelectionne = pion;
                return true;
            }
            return false;
        }

        // Sélection libre s'il dispose d'au moins un déplacement
        if (!plateau.getCoupsLegaux(pion, joueurActuel).isEmpty()) {
            pionSelectionne = pion;
            return true;
        }

        return false;
    }

    /**
     * Désélectionne le pion actuel, sauf si une rafle obligatoire est en cours.
     */
    public void deselectionner() {
        if (pionEnRafle == null) {
            pionSelectionne = null;
        }
    }

    /**
     * Déplace le pion sélectionné vers les coordonnées cibles (targetX, targetY).
     * Gère la capture, la poursuite de rafle, la promotion en dame et l'alternance de tour.
     *
     * @param targetX colonne d'arrivée
     * @param targetY ligne d'arrivée
     * @return true si le coup a été validé et exécuté, false sinon
     */
    public boolean deplacerPion(int targetX, int targetY) {
        if (partieTerminee || pionSelectionne == null) {
            return false;
        }

        List<Coup> coupsValides = (pionEnRafle != null)
                ? plateau.getCoupsDeCapture(pionSelectionne)
                : plateau.getCoupsLegaux(pionSelectionne, joueurActuel);

        Coup coupChoisi = null;
        for (Coup c : coupsValides) {
            if (c.getArriveeX() == targetX && c.getArriveeY() == targetY) {
                coupChoisi = c;
                break;
            }
        }

        if (coupChoisi == null) {
            return false;
        }

        // Exécution du coup
        plateau.executerCoup(coupChoisi, pionSelectionne);
        dernierCoup = coupChoisi;

        if (coupChoisi.estCapture()) {
            // Vérifier si le pion peut continuer à capturer (rafle)
            List<Coup> capturesSuivantes = plateau.getCoupsDeCapture(pionSelectionne);
            if (!capturesSuivantes.isEmpty()) {
                pionEnRafle = pionSelectionne;
                return true; // Tour non terminé : rafle en cours
            }
        }

        // Fin normale du coup
        pionEnRafle = null;
        plateau.verifierPromotion(pionSelectionne);
        terminerTour();
        return true;
    }

    /**
     * Conclut le tour du joueur actif, alterne le tour et vérifie la fin de partie.
     */
    private void terminerTour() {
        pionSelectionne = null;
        changerJoueur();
        verifierFinDePartie();
    }

    /**
     * Alterne le joueur actif entre Blanc et Noir.
     */
    public void changerJoueur() {
        joueurActuel = (joueurActuel == joueurBlanc) ? joueurNoir : joueurBlanc;
    }

    /**
     * Analyse les conditions de fin de partie :
     * 1. Élimination complète de toutes les pièces adverses.
     * 2. Blocage absolu : l'adversaire dont c'est le tour n'a plus aucun coup légal.
     *
     * @return true si la partie est terminée, false sinon
     */
    public boolean verifierFinDePartie() {
        if (joueurBlanc.getNombreDePions() == 0) {
            partieTerminee = true;
            joueurGagnant = joueurNoir;
            return true;
        }
        if (joueurNoir.getNombreDePions() == 0) {
            partieTerminee = true;
            joueurGagnant = joueurBlanc;
            return true;
        }

        // Vérification de blocage
        boolean coupDisponible = false;
        for (Pion p : joueurActuel.getPions()) {
            if (!plateau.getCoupsLegaux(p, joueurActuel).isEmpty()) {
                coupDisponible = true;
                break;
            }
        }

        if (!coupDisponible) {
            partieTerminee = true;
            joueurGagnant = (joueurActuel == joueurBlanc) ? joueurNoir : joueurBlanc;
            return true;
        }

        return false;
    }

    /**
     * Indique si un pion peut capturer.
     *
     * @param pion le pion candidat
     * @return true si au moins une capture est réalisable
     */
    public boolean canCapture(Pion pion) {
        return pion != null && !plateau.getCoupsDeCapture(pion).isEmpty();
    }

    /**
     * Retourne tous les pions actifs d'une couleur donnée.
     *
     * @param color la couleur recherchée ("BLANC" ou "NOIR")
     * @return tableau des pions de cette couleur
     */
    public Pion[] getAllPions(String color) {
        Joueur joueur = Pion.COULEUR_BLANC.equalsIgnoreCase(color) ? joueurBlanc : joueurNoir;
        return joueur.getPions().toArray(new Pion[0]);
    }

    /**
     * Capture manuellement un pion pour compatibilité historique.
     *
     * @param pion le pion à retirer
     */
    public void capturerPion(Pion pion) {
        plateau.retirerPion(pion);
    }
}
