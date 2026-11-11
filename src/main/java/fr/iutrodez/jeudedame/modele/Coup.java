package fr.iutrodez.jeudedame.modele;

import java.util.Objects;

/**
 * Modélise un coup sur le damier, correspondant à un déplacement simple ou à une capture.
 * Cette classe est immuable afin de garantir la cohérence des calculs prévisionnels
 * et de l'historique des coups.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public final class Coup {

    /** Abscisse de départ. */
    private final int departX;

    /** Ordonnée de départ. */
    private final int departY;

    /** Abscisse d'arrivée. */
    private final int arriveeX;

    /** Ordonnée d'arrivée. */
    private final int arriveeY;

    /** Pion adverse capturé lors de ce coup, ou null si déplacement simple. */
    private final Pion pionCapture;

    /**
     * Crée un coup complet avec ou sans capture.
     *
     * @param departX      colonne de départ
     * @param departY      ligne de départ
     * @param arriveeX     colonne d'arrivée
     * @param arriveeY     ligne d'arrivée
     * @param pionCapture  pion capturé, ou null
     */
    public Coup(int departX, int departY, int arriveeX, int arriveeY, Pion pionCapture) {
        this.departX = departX;
        this.departY = departY;
        this.arriveeX = arriveeX;
        this.arriveeY = arriveeY;
        this.pionCapture = pionCapture;
    }

    /**
     * Crée un coup de déplacement simple (sans capture).
     *
     * @param departX   colonne de départ
     * @param departY   ligne de départ
     * @param arriveeX  colonne d'arrivée
     * @param arriveeY  ligne d'arrivée
     */
    public Coup(int departX, int departY, int arriveeX, int arriveeY) {
        this(departX, departY, arriveeX, arriveeY, null);
    }

    /**
     * @return la colonne de départ
     */
    public int getDepartX() {
        return departX;
    }

    /**
     * @return la ligne de départ
     */
    public int getDepartY() {
        return departY;
    }

    /**
     * @return la colonne d'arrivée
     */
    public int getArriveeX() {
        return arriveeX;
    }

    /**
     * @return la ligne d'arrivée
     */
    public int getArriveeY() {
        return arriveeY;
    }

    /**
     * @return le pion capturé par ce coup, ou null s'il s'agit d'un déplacement simple
     */
    public Pion getPionCapture() {
        return pionCapture;
    }

    /**
     * Indique si le coup constitue une capture de pièce adverse.
     *
     * @return true si une pièce est sautée et capturée, false sinon
     */
    public boolean estCapture() {
        return pionCapture != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coup coup = (Coup) o;
        return departX == coup.departX && departY == coup.departY &&
                arriveeX == coup.arriveeX && arriveeY == coup.arriveeY &&
                Objects.equals(pionCapture, coup.pionCapture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(departX, departY, arriveeX, arriveeY, pionCapture);
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) -> (%d, %d)%s",
                departX, departY, arriveeX, arriveeY,
                estCapture() ? " [Capture: " + pionCapture + "]" : "");
    }
}
