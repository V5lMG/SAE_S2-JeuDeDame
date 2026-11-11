package fr.iutrodez.jeudedame.modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Représente un joueur (Blanc ou Noir) avec sa collection de pions.
 * Gère l'ajout, la suppression et le décompte de ses pièces en jeu.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class Joueur {

    /** Couleur du joueur (ex: "BLANC" ou "NOIR"). */
    private final String color;

    /** Liste interne des pions actifs appartenant à ce joueur. */
    private final List<Pion> pions;

    /**
     * Crée un joueur avec la couleur spécifiée.
     *
     * @param color la couleur du joueur ("BLANC" ou "NOIR")
     */
    public Joueur(String color) {
        this.color = Objects.requireNonNull(color, "La couleur ne peut pas être null").toUpperCase();
        this.pions = new ArrayList<>();
    }

    /**
     * Ajoute un pion à la collection du joueur s'il n'y figure pas déjà.
     *
     * @param pion le pion à ajouter
     */
    public void ajouterPion(Pion pion) {
        if (pion != null && !pions.contains(pion)) {
            pions.add(pion);
        }
    }

    /**
     * Retire un pion capturé de la collection du joueur.
     *
     * @param pion le pion à retirer
     */
    public void retirerPion(Pion pion) {
        pions.remove(pion);
    }

    /**
     * Fournit une vue non modifiable de la liste des pions du joueur.
     *
     * @return une liste non modifiable des pions actifs
     */
    public List<Pion> getPions() {
        return Collections.unmodifiableList(pions);
    }

    /**
     * @return la couleur du joueur ("BLANC" ou "NOIR")
     */
    public String getColor() {
        return color;
    }

    /**
     * @return le nombre de pièces encore en jeu pour ce joueur
     */
    public int getNombreDePions() {
        return pions.size();
    }

    /**
     * Indique si le joueur dispose encore d'au moins une pièce en vie.
     *
     * @return true si le joueur a des pions, false sinon
     */
    public boolean aDesPions() {
        return !pions.isEmpty();
    }

    /**
     * Réinitialise la collection des pièces du joueur (vidage de la liste).
     */
    public void reset() {
        pions.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Joueur joueur = (Joueur) o;
        return Objects.equals(color, joueur.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(color);
    }

    @Override
    public String toString() {
        return String.format("Joueur{%s, pions=%d}", color, pions.size());
    }
}
