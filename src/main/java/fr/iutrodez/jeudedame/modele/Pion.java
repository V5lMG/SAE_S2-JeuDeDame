package fr.iutrodez.jeudedame.modele;

import java.util.Objects;

/**
 * Représente une pièce de jeu de dames (pion simple ou dame).
 * Encapsule ses coordonnées sur le damier, sa couleur et son statut de dame.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class Pion {

    /** Constante de couleur pour les Blancs. */
    public static final String COULEUR_BLANC = "BLANC";

    /** Constante de couleur pour les Noirs. */
    public static final String COULEUR_NOIR = "NOIR";

    /** Indique si le pion a été promu en dame. */
    private boolean isDame;

    /** Abscisse du pion sur le plateau (colonne, 0 à 9). */
    private int posX;

    /** Ordonnée du pion sur le plateau (ligne, 0 à 9). */
    private int posY;

    /** Couleur du pion ("BLANC" ou "NOIR"). */
    private final String color;

    /**
     * Crée une nouvelle pièce avec sa couleur et sa position initiale.
     *
     * @param isDame true si la pièce est initialement une dame, false pour un pion simple
     * @param posX   abscisse initiale sur le damier
     * @param posY   ordonnée initiale sur le damier
     * @param color  couleur de la pièce ("BLANC" ou "NOIR")
     */
    public Pion(boolean isDame, int posX, int posY, String color) {
        this.isDame = isDame;
        this.posX = posX;
        this.posY = posY;
        this.color = Objects.requireNonNull(color, "La couleur ne peut pas être null").toUpperCase();
    }

    /**
     * Constructeur de compatibilité historique.
     *
     * @param isDame    statut de dame
     * @param posX      abscisse
     * @param posY      ordonnée
     * @param color     couleur
     * @param imagePath chemin d'image hérité (ignoré par le moteur vectoriel)
     */
    public Pion(boolean isDame, int posX, int posY, String color, String imagePath) {
        this(isDame, posX, posY, color);
    }

    /**
     * Indique si la pièce est une dame.
     *
     * @return true si la pièce est une dame, false pour un pion simple
     */
    public boolean isDame() {
        return isDame;
    }

    /**
     * Définit le statut de dame de la pièce.
     *
     * @param dame true pour transformer en dame, false sinon
     */
    public void setDame(boolean dame) {
        this.isDame = dame;
    }

    /**
     * @return la position en colonne (X) de la pièce
     */
    public int getPosX() {
        return posX;
    }

    /**
     * Met à jour la position en colonne (X) de la pièce.
     *
     * @param posX la nouvelle abscisse
     */
    public void setPosX(int posX) {
        this.posX = posX;
    }

    /**
     * @return la position en ligne (Y) de la pièce
     */
    public int getPosY() {
        return posY;
    }

    /**
     * Met à jour la position en ligne (Y) de la pièce.
     *
     * @param posY la nouvelle ordonnée
     */
    public void setPosY(int posY) {
        this.posY = posY;
    }

    /**
     * @return la couleur du pion ("BLANC" ou "NOIR")
     */
    public String getColor() {
        return color;
    }

    /**
     * Indique si la pièce est de couleur blanche.
     *
     * @return true si le pion est blanc, false sinon
     */
    public boolean estBlanc() {
        return COULEUR_BLANC.equalsIgnoreCase(color);
    }

    /**
     * Indique si la pièce est de couleur noire.
     *
     * @return true si le pion est noir, false sinon
     */
    public boolean estNoir() {
        return COULEUR_NOIR.equalsIgnoreCase(color);
    }

    /**
     * Promeut le pion en dame si ce n'est pas déjà le cas.
     */
    public void promouvoir() {
        this.isDame = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pion pion = (Pion) o;
        return isDame == pion.isDame &&
                posX == pion.posX &&
                posY == pion.posY &&
                Objects.equals(color, pion.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isDame, posX, posY, color);
    }

    @Override
    public String toString() {
        return String.format("%s[%s en (%d, %d)]", isDame ? "Dame" : "Pion", color, posX, posY);
    }
}
