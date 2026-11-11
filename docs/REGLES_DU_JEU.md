# Règles Officielles du Jeu de Dames International (Damier 10×10)

Ce document résume les règles officielles de la **Fédération Mondiale du Jeu de Dames (FMJD)** implémentées dans l'application **EasyCheckers**.

---

## 1. Le Matériel

- **Le Damier** : Grille carrée de 10 cases sur 10 (100 cases au total).
- **Cases jouables** : Le jeu se déroule exclusivement sur les **50 cases sombres**. La case située en bas à gauche de chaque joueur est toujours une case sombre.
- **Les Pièces** :
  - 20 pions **Blancs** pour le joueur principal (positionnés en bas, lignes 6 à 9).
  - 20 pions **Noirs** pour l'adversaire ou le Robot (positionnés en haut, lignes 0 à 3).
  - Les deux rangées centrales (lignes 4 et 5) sont vides en début de partie.

---

## 2. Déplacement des Pions

- Un pion simple se déplace en diagonale d'**une seule case vers l'avant**, vers une case sombre vide.
  - Les Blancs avancent vers le haut (ligne 6 vers ligne 0).
  - Les Noirs avancent vers le bas (ligne 3 vers ligne 9).
- Un pion ne peut jamais reculer lors d'un déplacement simple sans capture.

---

## 3. La Capture (Prise)

- **Principe de capture** : Lorsqu'un pion se trouve en diagonale face à une pièce adverse (pion ou dame), et que la case située immédiatement derrière cette pièce adverse sur la même diagonale est vide, le joueur doit sauter par-dessus la pièce adverse pour atterrir sur la case vide. La pièce sautée est retirée du jeu.
- **Capture en avant et en arrière** : Contrairement aux règles des échecs ou de certaines variantes anglo-saxonnes (Checkers 8x8), en dames internationales, **un pion simple a le droit de capturer aussi bien vers l'avant que vers l'arrière**.
- **Prise obligatoire** : Dès qu'au moins une capture est possible pour un joueur, **il est obligatoire de prendre**. Il est strictement interdit d'effectuer un coup de déplacement simple si une capture existe.
- **La Rafle (Prises consécutives)** : Si la pièce qui vient de capturer se retrouve à nouveau en position de capture depuis sa nouvelle case, elle **doit continuer immédiatement sa chaîne de prises**. Le tour du joueur ne prend fin que lorsqu'aucune nouvelle capture n'est possible avec cette même pièce.

---

## 4. Promotion en Dame

- Lorsqu'un pion atteint la ligne de fond opposée (ligne 0 pour les Blancs, ligne 9 pour les Noirs) et s'y arrête à la fin de son coup, il est immédiatement **promu en Dame**.
- Visuellement, la pièce se transforme en Reine ornée d'une couronne royale dorée.
- **Pouvoirs de la Dame (Dame volante)** :
  - Elle peut se déplacer en diagonale sur **n'importe quel nombre de cases libres**, vers l'avant comme vers l'arrière.
  - Elle peut capturer une pièce adverse située à distance sur sa diagonale, à condition qu'il n'y ait aucune autre pièce entre elles et qu'au moins une case soit libre derrière la pièce adverse. La dame peut atterrir sur n'importe laquelle des cases libres situées derrière.

---

## 5. Conditions de Victoire

Une partie se termine par la victoire d'un joueur dans deux situations :
1. **Éradication totale** : L'adversaire n'a plus aucune pièce sur le damier (toutes ont été capturées).
2. **Blocage absolu** : C'est au tour de l'adversaire de jouer, mais **toutes ses pièces restantes sont totalement bloquées** (aucun déplacement simple ni aucune capture n'est réalisable).
