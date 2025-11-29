# projet_bitmap-
# JeanAbakar

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

##  Compilation et Execution mode Batch
      # COMPILATION
      javac -source 1.8 -target 1.8 -d /JeanAbakar/bin /JeanAbakar/src/*.java
            ou (si on est dans le dossier JeanAbakar ie cd JeanAbakar)
      javac -source 1.8 -target 1.8 -d bin src/*.java 
            ou
      javac -d bin src/*.java

##  Exécution Batch
      java -classpath bin/ Main 64-tuxette.png lambda 20 [ Conseillé ]
         ou
      cd test_image && java -cp ../bin Main 64-tuxette.png lambda 20
         ou
      java -cp bin Main test_image/64-tuxette.png Lambda 20


### Exemple de sorti 
      pour l'image '64-tuxette.png' avec la methode 'lambda' et parametre '20':

         ---Fichiers générés ---
            - Image compressée : 64-tuxette_lambda20.png
            - Arbre R-Quadtree : 64-tuxette_lambda20R.txt
            - Arbre AVL :        64-tuxette_lambda20AVL.txt
         
         ---Console---- (Mode Batch)
            Fichier entré  :  64-tuxette.png
            Méthode        :  lambda
            Paramètre      : 20

            Image chargée  : 64x64 pixels 
            R-Quadtree construit avec 3040 feuilles

            --- Application de la compression ---
            Compression lambda appliquée avec λ=20 
            Nombre de feuilles après compression :1135

         ---Metriques de comparaison---
            Qualité (EQM) 75.78
            Ratio de poids :57.87%


##  Compilation et Execution mode iteratif
# COMPILATION

      javac -source 1.8 -target 1.8 -d /JeanAbakar/bin /JeanAbakar/src/*.java
            ou (si on est dans le dossier JeanAbakar ie cd JeanAbakar)
      javac -source 1.8 -target 1.8 -d bin src/*.java 
            ou
      javac -d bin src/*.java

# Exection
      java -classpath bin/ Main 
      java -cp bin Main 

# puis dans le menu 
      votre choix : 1
      Nom du fichier PNG: test_image/2.png <- Chemin complet necessaire 

### Exemple de sorti

      <--- Menu Principal --->
      1.  Construire R-Quadtree depuis une image
      2.  Appliquer compression Lambda
      3.  Appliquer compression Phi
      4.  Sauvegarder R-Quadtree en PNG
      5.  Sauvegarder R-Quadtree en texte
      6.  Comparer deux fichiers PNG
      7.  Construire AVL depuis une image
      8.  Construire AVL depuis R-Quadtree
      9.  Sauvegarder AVL en texte
      10. Rechercher une couleur dans AVL
      11. Ajouter une couleur à AVL
      12. Supprimer une couleur de AVL
      0.  Quitter
      Votre choix: 1
      Nom du fichier PNG: test_image/2.png
      R-Quadtree construit avec 4 feuilles
      ... 




##  Complexités

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


## Tests fait sur 2 image differents 
1) i.png 
2) 64-tuxette_lambda20.png