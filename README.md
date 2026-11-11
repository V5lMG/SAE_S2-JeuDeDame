# EasyCheckers — Jeu de Dames en Java & JavaFX

Bienvenue sur **EasyCheckers**, une application moderne et interactive de jeu de dames développée en Java avec l'interface graphique JavaFX, suivant le patron d'architecture **Modèle-Vue-Contrôleur (MVC)**.

---

## Sommaire
- [Présentation](#présentation)
- [Nouveautés & Améliorations](#nouveautés--améliorations)
- [Fonctionnalités principales](#fonctionnalités-principales)
- [Règles du jeu implémentées](#règles-du-jeu-implémentées)
- [Architecture du projet](#architecture-du-projet)
- [Prérequis](#prérequis)
- [Compilation et exécution](#compilation-et-exécution)
- [Exécution des tests unitaires](#exécution-des-tests-unitaires)
- [Guide d'utilisation de l'interface](#guide-d'utilisation-de-l'interface)

---

## Présentation

**EasyCheckers** permet de s'affronter à deux joueurs sur le même ordinateur ou de défier une **Intelligence Artificielle (Robot)** sur un damier international officiel 10x10. L'application propose des pions vectoriels haute définition en relief 3D, une sauvegarde/reprise de partie instantanée, ainsi que le respect strict des règles officielles du jeu de dames (prise obligatoire, rafles, promotion en reine).

---

## Nouveautés & Améliorations

1. **🤖 Mode Joueur vs Robot (IA)** :
   - Affrontez une IA tactique évaluant la sécurité des cases, l'avancement, le contrôle du centre et les chaînes de prises.
   - Respect absolu de la prise obligatoire et enchaînement automatique des rafles.
   - Temporisation naturelle de réflexion (550 ms) avec message d'état en direct.

2. **💾 Sauvegarde & Reprise de partie** :
   - Bouton **💾 Sauvegarder** disponible à tout moment sur le damier.
   - Sauvegarde complète au format JSON (`sauvegarde_jeudedame.json`) : position exacte des pions, statut de reine, joueur actif, mode de jeu et chronomètre.
   - Bouton **📂 Reprendre la Partie** sur l'écran d'accueil pour continuer instantanément là où vous vous étiez arrêté.

3. **✨ Pions vectoriels procéduraux HD (remplacement des PNG)** :
   - Remplacement des images PNG par des composants vectoriels JavaFX purs (`PionGraphique`).
   - Dégradés radiaux 3D riches (effet nacre/ivoire pour les Blancs, ébène laqué pour les Noirs).
   - Anneaux concentriques gravés, biseau et ombre portée douce.
   - **Couronne royale dorée en relief** ornée d'un rubis pour les Dames.
   - Résolution infinie, aucune déformation et centrage géométrique parfait.

4. **🎨 Front-End moderne & réactif** :
   - Damier $10 \times 10$ ($600 \times 600$ px) aux couleurs officielles de tournoi (`#f0d9b5` et `#769656`).
   - Tableau de bord latéral unifié avec badge de tour, scores en direct, chronomètre et guidance contextuelle.

---

## Règles du jeu implémentées

- **Premier coup** : Les Blancs commencent la partie.
- **Déplacement d'un pion** : Avance diagonale d'une seule case vers l'avant (vers le bas pour Blancs, vers le haut pour Noirs).
- **Capture par un pion** : Saute par-dessus une pièce adverse adjacente pour atterrir sur la case vide située immédiatement derrière. **La capture est autorisée aussi bien vers l'avant que vers l'arrière.**
- **Prise obligatoire** : Dès qu'au moins une capture est réalisable, le joueur ne peut effectuer aucun coup simple (pions preneurs signalés en orange).
- **Rafle** : Si après une prise, la pièce peut encore capturer, elle doit obligatoirement poursuivre sa chaîne de prises.
- **Promotion en Dame** : Atteinte de la ligne de fond adverse (ligne 9 pour les Blancs, ligne 0 pour les Noirs).
- **Déplacement et prise de la Dame** : Déplacement sur un nombre quelconque de cases libres en diagonale ; capture d'une pièce adverse à distance avec atterrissage sur les cases libres situées derrière.

---

## Architecture du projet

```text
fr.iutrodez.jeudedame
├── Jeu.java                                # Point d'entrée JavaFX (Application)
├── modele/                                 # Couche métier / logique pure
│   ├── Case.java                           # Case (coordonnées x, y, occupation)
│   ├── Pion.java                           # Pièce (couleur, dame, coordonnées)
│   ├── Joueur.java                         # Joueur (couleur, pions)
│   ├── Coup.java                           # Déplacement ou prise (départ, arrivée, pièce sautée)
│   ├── Plateau.java                        # Damier 10x10, calcul des déplacements et captures
│   ├── Partie.java                         # Gestionnaire de partie, tours, rafles, victoire
│   ├── RobotIA.java                        # Moteur d'Intelligence Artificielle
│   └── GestionnaireSauvegarde.java         # Sérialisation et chargement JSON
├── controleur/                             # Couche contrôleur JavaFX
│   ├── AccueilControleur.java              # Contrôleur de l'écran d'accueil
│   └── Controleur.java                     # Contrôleur principal du plateau de jeu
└── vue/                                    # Vues FXML, styles CSS et composants vectoriels
    ├── PionGraphique.java                  # Générateur vectoriel procédural des pions et reines
    ├── accueil/
    │   ├── page-accueil.fxml
    │   └── pageAccueuil.css
    └── plateau/
        ├── jeu-dame.fxml
        └── jeuDame.css
```

---

## Prérequis

- **Java JDK** : Version 11 ou supérieure (testé et validé avec JDK 17, 21 et 23).
- **Maven** : Maven 3.8+ ou utilisation du wrapper Maven inclus (`mvnw` / `mvnw.cmd`).

---

## Compilation et exécution

### 1. Lancer l'application avec Maven

Sous Windows (PowerShell / CMD) :
```powershell
.\mvnw.cmd clean javafx:run
```

Sous Linux / macOS :
```bash
./mvnw clean javafx:run
```

### 2. Compiler le package JAR

```powershell
.\mvnw.cmd clean package
```

Le fichier exécutable est généré dans `target/JeuDeDame-1.0-SNAPSHOT.jar` et synchronisé dans `out/artifacts/JeuDeDame/JeuDeDame.jar`.

---

## Exécution des tests unitaires

Une suite complète de **29 tests unitaires JUnit 5** valide l'ensemble des règles métier, le Robot et le système de sauvegarde :
```powershell
.\mvnw.cmd test
```

Tous les tests affichent `BUILD SUCCESS` (29 tests réussis, 0 échec).

---

## Guide d'utilisation de l'interface

- **Mode 2 Joueurs** : Cliquez sur **👥 Joueur vs Joueur (Local)** sur l'accueil.
- **Mode Robot** : Cliquez sur **🤖 Joueur vs Robot** pour affronter l'ordinateur.
- **Sauvegarder** : Cliquez sur **💾 Sauvegarder** en cours de partie pour enregistrer votre progression.
- **Reprendre** : Cliquez sur **📂 Reprendre la Partie** sur l'accueil pour reprendre instantanément.
- **Sélectionner et déplacer** : Cliquez sur un pion, puis sur l'une des cases vertes (mouvement) ou rouges (capture).
- **Consulter les règles** : Bouton **📖 Règles** disponible à tout moment.

---

## Auteur

- **MUNIER-GÉNIE Valentin** — SAÉ Développement d'Applications, IUT de Rodez.
