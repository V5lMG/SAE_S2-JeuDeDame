# Documentation d'Architecture et Conception Technique — EasyCheckers

Ce document présente l'architecture logicielle, les patrons de conception (Design Patterns) et les principes de **Clean Code** appliqués dans le projet **EasyCheckers**.

---

## 1. Principes d'Ingénierie Logicielle et Clean Code

Le projet est conçu selon les principes fondamentaux du **Clean Code** et des normes **SOLID** :

1. **Responsabilité Unique (SRP - Single Responsibility Principle)** :
   - `Plateau` ne gère que la géométrie du damier et la validation physique des coups.
   - `Partie` gère l'arbitrage (tours, rafles, victoire).
   - `RobotIA` gère uniquement la prise de décision automatisée.
   - `GestionnaireSauvegarde` gère exclusivement la sérialisation / désérialisation.
   - `PionGraphique` isole la création vectorielle des composants d'affichage.
2. **Immutabilité des Objets Valeurs** :
   - La classe `Coup` est entièrement immuable (`final class`, attributs `final`), garantissant l'absence d'effets de bord lors des simulations de coups par l'IA.
3. **Absence de "Magic Numbers"** :
   - Remplacement de toutes les constantes brutes par des identifiants expressifs (`Plateau.TAILLE`, `Plateau.LIGNE_PROMOTION_BLANC`, `Plateau.LIGNE_PROMOTION_NOIR`, etc.).
4. **Encapsulation stricte et DTOs défensifs** :
   - `Joueur.getPions()` renvoie une collection non modifiable (`Collections.unmodifiableList`).
   - `GestionnaireSauvegarde.DonneesSauvegarde` isole les données de persistance sans polluer les contrôleurs.
5. **Documentation Javadoc intégrale** :
   - Chaque classe, méthode publique, constructeur et constante dispose de son bloc Javadoc explicatif avec tags `@param`, `@return`, `@throws`, `@author` et `@version`.

---

## 2. Diagramme d'Architecture Globale (MVC)

```mermaid
flowchart TD
    subgraph Vue [Couche Vue JavaFX]
        AccueilFXML[page-accueil.fxml]
        JeuFXML[jeu-dame.fxml]
        CSS[jeuDame.css / pageAccueuil.css]
        PionGraph[PionGraphique.java (Factory vectorielle)]
    end

    subgraph Controleur [Couche Contrôleur]
        AccueilCtrl[AccueilControleur.java]
        JeuCtrl[Controleur.java]
    end

    subgraph Modele [Couche Modèle Métier Pur]
        Partie[Partie.java]
        Plateau[Plateau.java]
        Coup[Coup.java (Value Object)]
        Pion[Pion.java]
        Joueur[Joueur.java]
        Case[Case.java]
        Robot[RobotIA.java (Moteur IA)]
        SaveMgr[GestionnaireSauvegarde.java]
    end

    AccueilFXML --> AccueilCtrl
    JeuFXML --> JeuCtrl
    AccueilCtrl --> JeuCtrl
    AccueilCtrl --> SaveMgr
    AccueilCtrl --> PionGraph
    JeuCtrl --> Partie
    JeuCtrl --> Robot
    JeuCtrl --> SaveMgr
    JeuCtrl --> PionGraph
    Partie --> Plateau
    Partie --> Joueur
    Plateau --> Case
    Plateau --> Pion
    Plateau --> Coup
    Robot --> Plateau
```

---

## 3. Patrons de Conception (Design Patterns) mis en œuvre

1. **Modèle-Vue-Contrôleur (MVC)** :
   - **Modèle** : Totalement découplé de JavaFX, exécutable et testable en environnement headless sans toolkit graphique (aucun `javafx.scene` dans le modèle).
   - **Vue** : Déclarée en FXML moderne et stylée en CSS avec respect des grilles vectorielles ($600 \times 600$ px).
   - **Contrôleur** : Écoute les événements IHM, orchestre les transitions et rafraîchit la vue de façon déclarative.

2. **Fabrique Abstraite / Factory Method (`PionGraphique`)** :
   - Centralise la création procédurale des pièces vectorielles haute définition (dégradés radiaux, chanfreins 3D, couronnes impériales) pour le plateau et l'écran d'accueil.

3. **Stratégie Heuristique (`RobotIA`)** :
   - Isole la logique décisionnelle du robot. L'algorithme attribue des poids numériques (sécurité, promotion, capture de dames, rafles) pour sélectionner le coup optimal.

4. **Objet Valeur / Immutabilité (`Coup`)** :
   - Encapsule de façon immuable un déplacement ou une prise avec contrat `equals()` et `hashCode()`.

5. **Data Transfer Object / DTO (`DonneesSauvegarde`)** :
   - Permet le passage propre de données entre le service de stockage JSON et les contrôleurs JavaFX.

---

## 4. Tests et Démarche TDD

Pour le détail des 29 tests unitaires et des cycles Red-Green-Refactor, veuillez consulter le document dédié :
👉 [Méthodologie TDD](file:///e:/1 - JAVA/JeuDeDame/docs/TDD_METHODOLOGIE.md).
