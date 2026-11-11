package fr.iutrodez.jeudedame.modele;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Service de persistance pour sauvegarder et restaurer l'état d'une partie de jeu de dames.
 * Les données sont sérialisées au format JSON dans un fichier texte local.
 *
 * @author MUNIER-GÉNIE Valentin
 * @version 2.0
 */
public final class GestionnaireSauvegarde {

    /** Nom du fichier de sauvegarde par défaut. */
    public static final String FICHIER_SAUVEGARDE = "sauvegarde_jeudedame.json";

    /** Constructeur privé pour classe utilitaire. */
    private GestionnaireSauvegarde() {}

    /**
     * DTO contenant l'état d'une partie restaurée.
     */
    public static final class DonneesSauvegarde {
        private final Partie partie;
        private final long tempsSecondes;
        private final boolean vsRobot;

        public DonneesSauvegarde(Partie partie, long tempsSecondes, boolean vsRobot) {
            this.partie = partie;
            this.tempsSecondes = tempsSecondes;
            this.vsRobot = vsRobot;
        }

        public Partie getPartie() {
            return partie;
        }

        public long getTempsSecondes() {
            return tempsSecondes;
        }

        public boolean isVsRobot() {
            return vsRobot;
        }
    }

    /**
     * Détermine si un fichier de sauvegarde valide existe sur le disque.
     *
     * @return true si une sauvegarde exploitable existe, false sinon
     */
    public static boolean existeSauvegarde() {
        File file = new File(FICHIER_SAUVEGARDE);
        return file.exists() && file.length() > 10;
    }

    /**
     * Supprime le fichier de sauvegarde existant s'il est présent.
     *
     * @return true si la suppression a réussi
     */
    public static boolean supprimerSauvegarde() {
        File file = new File(FICHIER_SAUVEGARDE);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Sauvegarde l'état complet de la partie dans le fichier JSON.
     *
     * @param partie        la partie à enregistrer
     * @param tempsSecondes le temps écoulé au chronomètre
     * @param vsRobot       true si la partie oppose un humain à l'IA, false pour 2 joueurs
     * @return true en cas de succès, false en cas d'erreur I/O
     */
    public static boolean sauvegarder(Partie partie, long tempsSecondes, boolean vsRobot) {
        if (partie == null) return false;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"mode\": \"").append(vsRobot ? "VS_ROBOT" : "VS_HUMAIN").append("\",\n");
        sb.append("  \"joueurActuel\": \"").append(partie.getJoueurActuel().getColor()).append("\",\n");
        sb.append("  \"tempsSecondes\": ").append(tempsSecondes).append(",\n");
        sb.append("  \"pions\": [\n");

        Plateau plateau = partie.getPlateau();
        boolean premier = true;
        for (int y = 0; y < Plateau.TAILLE; y++) {
            for (int x = 0; x < Plateau.TAILLE; x++) {
                Pion p = plateau.getPion(x, y);
                if (p != null) {
                    if (!premier) sb.append(",\n");
                    sb.append(String.format("    {\"x\": %d, \"y\": %d, \"color\": \"%s\", \"isDame\": %b}",
                            p.getPosX(), p.getPosY(), p.getColor(), p.isDame()));
                    premier = false;
                }
            }
        }
        sb.append("\n  ]\n");
        sb.append("}\n");

        try {
            Files.writeString(new File(FICHIER_SAUVEGARDE).toPath(), sb.toString(), StandardCharsets.UTF_8);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Charge et reconstruit une partie complète depuis le fichier de sauvegarde.
     *
     * @return les données restaurées, ou null si le fichier est absent ou corrompu
     */
    public static DonneesSauvegarde charger() {
        File file = new File(FICHIER_SAUVEGARDE);
        if (!file.exists()) return null;

        try {
            String contenu = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            boolean vsRobot = contenu.contains("\"mode\": \"VS_ROBOT\"");
            String joueurActuelStr = extraireChaine(contenu, "joueurActuel", Pion.COULEUR_BLANC);
            long tempsSecondes = extraireNombre(contenu, "tempsSecondes", 0);

            Partie partie = new Partie();
            Plateau plateau = partie.getPlateau();

            // Vider le damier par défaut
            for (int y = 0; y < Plateau.TAILLE; y++) {
                for (int x = 0; x < Plateau.TAILLE; x++) {
                    plateau.getCase(x, y).reset();
                }
            }
            partie.getJoueurBlanc().reset();
            partie.getJoueurNoir().reset();

            // Parser les pièces sauvegardées
            int idxPions = contenu.indexOf("\"pions\":");
            if (idxPions != -1) {
                String blocPions = contenu.substring(idxPions);
                int debut = blocPions.indexOf("[");
                int fin = blocPions.lastIndexOf("]");
                if (debut != -1 && fin != -1) {
                    String liste = blocPions.substring(debut + 1, fin);
                    String[] elements = liste.split("(?<=\\}),?");
                    for (String el : elements) {
                        el = el.trim();
                        if (el.startsWith("{") && el.contains("}")) {
                            int x = (int) extraireNombre(el, "x", -1);
                            int y = (int) extraireNombre(el, "y", -1);
                            String color = extraireChaine(el, "color", Pion.COULEUR_BLANC);
                            boolean isDame = el.contains("\"isDame\": true");

                            if (x >= 0 && x < Plateau.TAILLE && y >= 0 && y < Plateau.TAILLE) {
                                Pion pion = new Pion(isDame, x, y, color);
                                plateau.placerPion(pion, x, y);
                                if (pion.estBlanc()) {
                                    partie.getJoueurBlanc().ajouterPion(pion);
                                } else {
                                    partie.getJoueurNoir().ajouterPion(pion);
                                }
                            }
                        }
                    }
                }
            }

            // Aligner le joueur actif
            if (!partie.getJoueurActuel().getColor().equalsIgnoreCase(joueurActuelStr)) {
                partie.changerJoueur();
            }

            return new DonneesSauvegarde(partie, tempsSecondes, vsRobot);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String extraireChaine(String json, String cle, String defaut) {
        String pattern = "\"" + cle + "\": \"";
        int idx = json.indexOf(pattern);
        if (idx == -1) return defaut;
        int debut = idx + pattern.length();
        int fin = json.indexOf("\"", debut);
        if (fin == -1) return defaut;
        return json.substring(debut, fin);
    }

    private static long extraireNombre(String json, String cle, long defaut) {
        String pattern = "\"" + cle + "\":";
        int idx = json.indexOf(pattern);
        if (idx == -1) return defaut;
        int debut = idx + pattern.length();
        while (debut < json.length() && Character.isWhitespace(json.charAt(debut))) {
            debut++;
        }
        StringBuilder num = new StringBuilder();
        while (debut < json.length() && (Character.isDigit(json.charAt(debut)) || json.charAt(debut) == '-')) {
            num.append(json.charAt(debut));
            debut++;
        }
        try {
            return Long.parseLong(num.toString());
        } catch (Exception e) {
            return defaut;
        }
    }
}
