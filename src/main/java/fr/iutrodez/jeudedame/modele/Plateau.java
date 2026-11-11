package fr.iutrodez.jeudedame.modele;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Moteur du damier international 10x10.
 * Responsable du placement initial des pièces, du contrôle de validité des coups,
 * de l'évaluation des captures (prises simples et rafles), et de la promotion des dames.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public class Plateau {

    /** Dimension standard du damier officiel international (10x10). */
    public static final int TAILLE = 10;

    /** Ligne de fond adverse pour la promotion des pions Blancs (en haut). */
    public static final int LIGNE_PROMOTION_BLANC = 0;

    /** Ligne de fond adverse pour la promotion des pions Noirs (en bas). */
    public static final int LIGNE_PROMOTION_NOIR = 9;

    /** Les 4 vecteurs de déplacement diagonal : haut-gauche, haut-droite, bas-gauche, bas-droite. */
    private static final int[][] DIRECTIONS_DIAGONALES = {{-1, -1}, {1, -1}, {-1, 1}, {1, 1}};

    /** Matrice bidimensionnelle des cases du plateau [y][x]. */
    private final Case[][] cases;

    /** Référence vers le joueur Noir. */
    private Joueur joueurNoir;

    /** Référence vers le joueur Blanc. */
    private Joueur joueurBlanc;

    /**
     * Initialise un damier 10x10 avec 100 cases vides.
     */
    public Plateau() {
        this.cases = new Case[TAILLE][TAILLE];
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                cases[y][x] = new Case(x, y);
            }
        }
    }

    /**
     * Positionne les 20 pions de chaque joueur sur les cases sombres initiales.
     * Noirs (camp robot / haut) : lignes 0 à 3.
     * Blancs (camp joueur / bas) : lignes 6 à 9.
     *
     * @param joueurNoir  le joueur contrôlant les pièces noires
     * @param joueurBlanc le joueur contrôlant les pièces blanches
     */
    public void initialiser(Joueur joueurNoir, Joueur joueurBlanc) {
        this.joueurNoir = Objects.requireNonNull(joueurNoir, "Le joueur Noir ne peut pas être null");
        this.joueurBlanc = Objects.requireNonNull(joueurBlanc, "Le joueur Blanc ne peut pas être null");

        // Vider toutes les cases
        for (int y = 0; y < TAILLE; y++) {
            for (int x = 0; x < TAILLE; x++) {
                cases[y][x].reset();
            }
        }
        joueurBlanc.reset();
        joueurNoir.reset();

        // Placer les 20 pions Noirs en haut (lignes 0 à 3)
        placerPionsDepart(joueurNoir, 0, 3, Pion.COULEUR_NOIR);

        // Placer les 20 pions Blancs en bas (lignes 6 à 9 - côté joueur)
        placerPionsDepart(joueurBlanc, 6, 9, Pion.COULEUR_BLANC);
    }

    /**
     * Remplit les rangées spécifiées avec des pions sur les cases sombres.
     */
    private void placerPionsDepart(Joueur joueur, int startY, int endY, String color) {
        for (int y = startY; y <= endY; y++) {
            for (int x = 0; x < TAILLE; x++) {
                if ((x + y) % 2 != 0) {
                    Pion pion = new Pion(false, x, y, color);
                    joueur.ajouterPion(pion);
                    cases[y][x].setPion(pion);
                }
            }
        }
    }

    /**
     * Vérifie si une coordonnée (x, y) est située à l'intérieur du damier.
     *
     * @param x abscisse
     * @param y ordonnée
     * @return true si dans les bornes [0, 9], false sinon
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < TAILLE && y >= 0 && y < TAILLE;
    }

    /**
     * Récupère la case aux coordonnées spécifiées.
     *
     * @param x colonne
     * @param y ligne
     * @return la case correspondante, ou null si hors bornes
     */
    public Case getCase(int x, int y) {
        if (!isWithinBounds(x, y)) {
            return null;
        }
        return cases[y][x];
    }

    /**
     * Récupère le pion situé sur la case (x, y).
     *
     * @param x colonne
     * @param y ligne
     * @return le pion présent, ou null si la case est vide ou hors bornes
     */
    public Pion getPion(int x, int y) {
        Case c = getCase(x, y);
        return c != null ? c.getPion() : null;
    }

    /**
     * Positionne manuellement un pion à des coordonnées données.
     *
     * @param pion le pion à placer
     * @param x    colonne
     * @param y    ligne
     */
    public void placerPion(Pion pion, int x, int y) {
        if (!isWithinBounds(x, y) || pion == null) {
            return;
        }
        pion.setPosX(x);
        pion.setPosY(y);
        cases[y][x].setPion(pion);
    }

    /**
     * Retire un pion du damier et de la collection de son propriétaire.
     *
     * @param pion le pion à retirer
     */
    public void retirerPion(Pion pion) {
        if (pion == null) return;
        int x = pion.getPosX();
        int y = pion.getPosY();
        if (isWithinBounds(x, y) && cases[y][x].getPion() == pion) {
            cases[y][x].reset();
        }
        if (joueurBlanc != null && pion.estBlanc()) {
            joueurBlanc.retirerPion(pion);
        } else if (joueurNoir != null && pion.estNoir()) {
            joueurNoir.retirerPion(pion);
        }
    }

    /**
     * Calcule l'ensemble des coups de capture possibles pour un pion donné.
     * Pour un pion : saut diagonal dans les 4 directions par-dessus un pion adverse.
     * Pour une dame : saut diagonal volant avec atterrissage sur les cases libres derrière.
     *
     * @param pion le pion candidat
     * @return liste des coups de capture disponibles
     */
    public List<Coup> getCoupsDeCapture(Pion pion) {
        if (pion == null) return Collections.emptyList();
        List<Coup> captures = new ArrayList<>();
        int x = pion.getPosX();
        int y = pion.getPosY();

        if (!pion.isDame()) {
            // Règle officielle : le pion capture dans les 4 directions diagonales
            for (int[] dir : DIRECTIONS_DIAGONALES) {
                int midX = x + dir[0];
                int midY = y + dir[1];
                int landX = x + 2 * dir[0];
                int landY = y + 2 * dir[1];

                if (isWithinBounds(landX, landY) && cases[landY][landX].getPion() == null) {
                    Pion saute = isWithinBounds(midX, midY) ? cases[midY][midX].getPion() : null;
                    if (saute != null && !saute.getColor().equalsIgnoreCase(pion.getColor())) {
                        captures.add(new Coup(x, y, landX, landY, saute));
                    }
                }
            }
        } else {
            // Dame volante : capture à distance sur les 4 diagonales
            for (int[] dir : DIRECTIONS_DIAGONALES) {
                int curX = x + dir[0];
                int curY = y + dir[1];
                Pion adversaireTrouve = null;

                while (isWithinBounds(curX, curY)) {
                    Pion pieceSurChemin = cases[curY][curX].getPion();
                    if (adversaireTrouve == null) {
                        if (pieceSurChemin != null) {
                            if (pieceSurChemin.getColor().equalsIgnoreCase(pion.getColor())) {
                                break; // Bloqué par une pièce amie
                            } else {
                                adversaireTrouve = pieceSurChemin; // Première pièce adverse
                            }
                        }
                    } else {
                        if (pieceSurChemin != null) {
                            break; // Deuxième pièce bloquant l'atterrissage
                        } else {
                            captures.add(new Coup(x, y, curX, curY, adversaireTrouve));
                        }
                    }
                    curX += dir[0];
                    curY += dir[1];
                }
            }
        }
        return captures;
    }

    /**
     * Calcule les déplacements simples (sans capture) possibles pour un pion.
     * Pour un pion : avance diagonale d'une case vers l'avant.
     * Pour une dame : vol diagonal sur toutes les cases libres adjacentes.
     *
     * @param pion le pion candidat
     * @return liste des déplacements simples possibles
     */
    public List<Coup> getCoupsSimples(Pion pion) {
        if (pion == null) return Collections.emptyList();
        List<Coup> simples = new ArrayList<>();
        int x = pion.getPosX();
        int y = pion.getPosY();

        if (!pion.isDame()) {
            int dy = pion.estBlanc() ? -1 : 1;
            int[] dxs = {-1, 1};
            for (int dx : dxs) {
                int nx = x + dx;
                int ny = y + dy;
                if (isWithinBounds(nx, ny) && cases[ny][nx].getPion() == null) {
                    simples.add(new Coup(x, y, nx, ny));
                }
            }
        } else {
            for (int[] dir : DIRECTIONS_DIAGONALES) {
                int curX = x + dir[0];
                int curY = y + dir[1];
                while (isWithinBounds(curX, curY) && cases[curY][curX].getPion() == null) {
                    simples.add(new Coup(x, y, curX, curY));
                    curX += dir[0];
                    curY += dir[1];
                }
            }
        }
        return simples;
    }

    /**
     * Vérifie si le joueur donné dispose d'au moins une capture sur le plateau.
     *
     * @param joueur le joueur à évaluer
     * @return true si au moins une prise est obligatoire, false sinon
     */
    public boolean joueurADesCaptures(Joueur joueur) {
        if (joueur == null) return false;
        for (Pion p : joueur.getPions()) {
            if (!getCoupsDeCapture(p).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calcule tous les coups légaux pour un pion, en appliquant la règle de la prise obligatoire.
     *
     * @param pion         le pion sélectionné
     * @param joueurActuel le joueur dont c'est le tour
     * @return liste des coups légaux autorisés
     */
    public List<Coup> getCoupsLegaux(Pion pion, Joueur joueurActuel) {
        if (pion == null) return Collections.emptyList();
        List<Coup> captures = getCoupsDeCapture(pion);

        if (joueurADesCaptures(joueurActuel)) {
            // Prise obligatoire : seules les captures sont permises
            return captures;
        } else {
            return getCoupsSimples(pion);
        }
    }

    /**
     * Méthode de compatibilité : retourne les coordonnées d'arrivée [x, y] des coups légaux.
     *
     * @param pion le pion candidat
     * @return liste des coordonnées d'arrivée sous forme de int[]targetX, targetY
     */
    public List<int[]> getLegalMoves(Pion pion) {
        if (pion == null) return Collections.emptyList();
        Joueur joueur = pion.estBlanc() ? joueurBlanc : joueurNoir;
        List<Coup> coups = getCoupsLegaux(pion, joueur);
        List<int[]> moves = new ArrayList<>(coups.size());
        for (Coup c : coups) {
            moves.add(new int[]{c.getArriveeX(), c.getArriveeY()});
        }
        return moves;
    }

    /**
     * Vérifie si un déplacement vers (newX, newY) est valide pour un pion.
     *
     * @param pion le pion
     * @param newX colonne cible
     * @param newY ligne cible
     * @return true si le coup est autorisé, false sinon
     */
    public boolean estDeplacementValide(Pion pion, int newX, int newY) {
        return getCoup(pion, newX, newY) != null;
    }

    /**
     * Recherche le coup légal correspondant à une destination donnée.
     *
     * @param pion le pion candidat
     * @param newX colonne cible
     * @param newY ligne cible
     * @return le coup validé, ou null s'il n'est pas autorisé
     */
    public Coup getCoup(Pion pion, int newX, int newY) {
        if (pion == null) return null;
        Joueur joueur = pion.estBlanc() ? joueurBlanc : joueurNoir;
        for (Coup c : getCoupsLegaux(pion, joueur)) {
            if (c.getArriveeX() == newX && c.getArriveeY() == newY) {
                return c;
            }
        }
        return null;
    }

    /**
     * Exécute un coup validé sur le plateau en mettant à jour les cases et les pièces.
     *
     * @param coup le coup à exécuter
     * @param pion le pion déplacé
     * @return le coup exécuté
     */
    public Coup executerCoup(Coup coup, Pion pion) {
        if (coup == null || pion == null) return null;

        // Libérer la case initiale
        cases[pion.getPosY()][pion.getPosX()].reset();

        // Retirer la pièce adverse capturée
        if (coup.estCapture() && coup.getPionCapture() != null) {
            retirerPion(coup.getPionCapture());
        }

        // Poser le pion sur la case d'arrivée
        pion.setPosX(coup.getArriveeX());
        pion.setPosY(coup.getArriveeY());
        cases[coup.getArriveeY()][coup.getArriveeX()].setPion(pion);

        return coup;
    }

    /**
     * Vérifie si un pion atteint la rangée de fond opposée et applique la promotion en dame.
     *
     * @param pion le pion à tester
     * @return true si le pion a été promu en dame lors de cet appel, false sinon
     */
    public boolean verifierPromotion(Pion pion) {
        if (pion == null || pion.isDame()) return false;
        if ((pion.estBlanc() && pion.getPosY() == LIGNE_PROMOTION_BLANC) ||
            (pion.estNoir() && pion.getPosY() == LIGNE_PROMOTION_NOIR)) {
            pion.promouvoir();
            return true;
        }
        return false;
    }

    /**
     * Déplacement direct pour compatibilité historique.
     *
     * @param pion le pion
     * @param newX colonne cible
     * @param newY ligne cible
     */
    public void deplacerPion(Pion pion, int newX, int newY) {
        Coup coup = getCoup(pion, newX, newY);
        if (coup != null) {
            executerCoup(coup, pion);
            verifierPromotion(pion);
        }
    }

    public Joueur getJoueurBlanc() {
        return joueurBlanc;
    }

    public Joueur getJoueurNoir() {
        return joueurNoir;
    }
}
