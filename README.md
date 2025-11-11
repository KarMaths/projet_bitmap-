# projet_bitmap-
compression d'images bitmap

##  Description

Ce projet implémente un système de compression d'images bitmap utilisant des structures de données avancées:
- **R-Quadtree**: Pour la représentation hiérarchique d'images
- **Arbre AVL**: Pour la gestion de palette de couleurs

## Structure du Projet

/app/
|-- src/
|   |-- ImagePNG.java      # Classe utilitaire (fournie)
│   |-- RQuadtree.java     # Arbre quaternaire régulier
│   |-- AVL.java           # Arbre AVL pour couleurs
│   |__ Main.java          # Programme principal
|-- bin/                    # Fichiers compilés (.class)
|-- test_images/           # Images de test PNG
|__ README.md              # Ce fichier

##  Compilation

**bash
javac -source 1.8 -target 1.8 -d /app/bin /app/src/*.java


##  Exécution

### Mode Interactif (Menu)
bash
java -classpath /app/bin Main

### Mode Batch (Ligne de commande)
bash
java -classpath /app/bin Main <fichier.png> <Lambda|Phi> <parametre>

## 📊 Complexités

### R-Quadtree
- Construction: O(n²) où n = taille de l'image
- compressLambda: O(m) où m = nombre de noeuds
- compressPhi: O(m log m) (avec tri des sur-feuilles)
- toPNG: O(n²)

### AVL
- Insertion: O(log k) où k = nombre de couleurs
- Recherche: O(log k)
- Suppression: O(log k)


### Pour toPNG()
1. Créer une nouvelle ImagePNG de la bonne taille
2. Pour chaque pixel (x,y):
   - Parcourir l'arbre pour trouver la feuille correspondante
   - Utiliser la couleur de cette feuille
3. Retourner l'image

##  Ressources

- Luminance: L = 0.2126*R + 0.7152*G + 0.0722*B
- Couleur moyenne: (R1+R2+R3+R4)/4, (G1+G2+G3+G4)/4, (B1+B2+B3+B4)/4
- Dégradation: X = max(|Lm - Li|) pour i=1,2,3,4