package fr.iutrodez.jeudedame.modele;

import java.util.Objects;

/**
 * Représente une case unitaire du damier 10x10.
 * Chaque case est identifiée par ses coordonnées cartésiennes (x, y)
 * et peut contenir au plus un pion.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class Case {

    /** Coordonnée en colonne (abscisse, de 0 à 9). */
    private final int x;

    /** Coordonnée en ligne (ordonnée, de 0 à 9). */
    private final int y;

    /** Pion présent sur la case, ou null si la case est vide. */
    private Pion pion;

    /**
     * Initialise une nouvelle case vide aux coordonnées spécifiées.
     *
     * @param x la coordonnée horizontale (colonne)
     * @param y la coordonnée verticale (ligne)
     * @throws IllegalArgumentException si les coordonnées sont hors du damier (0 à 9)
     */
    public Case(int x, int y) {
        if (x < 0 || x >= Plateau.TAILLE || y < 0 || y >= Plateau.TAILLE) {
            throw new IllegalArgumentException(
                    String.format("Coordonnées de case invalides : (%d, %d)", x, y));
        }
        this.x = x;
        this.y = y;
        this.pion = null;
    }

    /**
     * @return la coordonnée horizontale de la case
     */
    public int getX() {
        return x;
    }

    /**
     * @return la coordonnée verticale de la case
     */
    public int getY() {
        return y;
    }

    /**
     * @return le pion posé sur cette case, ou null si elle est libre
     */
    public Pion getPion() {
        return pion;
    }

    /**
     * Assigne un pion à cette case.
     *
     * @param pion le pion à positionner, ou null pour libérer la case
     */
    public void setPion(Pion pion) {
        this.pion = pion;
    }

    /**
     * Indique si la case est actuellement occupée par une pièce.
     *
     * @return true si un pion est présent, false sinon
     */
    public boolean estOccupee() {
        return pion != null;
    }

    /**
     * Détermine si la case fait partie des cases jouables (cases sombres du damier).
     * Sur un damier standard dont l'origine (0, 0) est claire,
     * les cases sombres vérifient la condition (x + y) % 2 != 0.
     *
     * @return true si la case est jouable, false si elle est claire
     */
    public boolean estCaseJouable() {
        return (x + y) % 2 != 0;
    }

    /**
     * Réinitialise la case en retirant le pion présent.
     */
    public void reset() {
        this.pion = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Case aCase = (Case) o;
        return x == aCase.x && y == aCase.y && Objects.equals(pion, aCase.pion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, pion);
    }

    @Override
    public String toString() {
        return String.format("Case(%d, %d)[%s]", x, y, estOccupee() ? pion : "vide");
    }
}
