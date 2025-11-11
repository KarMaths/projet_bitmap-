
import java.awt.Color;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

/*
 * Programme princial 
 */
/*public class Main {
    private static Scanner scanner =  new Scanner()
}

/**
 * Programme principal avec menu interactif et mode batch
 */
public class Main {
    
    private static Scanner scanner = new Scanner(System.in);
    private static RQuadtree currentTree = null;
    //private static AVL currentAVL = null;
    
    public static void main(String[] args) {
        // Mode non-interactif (ligne de commande)
        if (args.length == 3) {
            batchMode(args);
        } else {
            // Mode interactif (menu)
            interactiveMode();
        }
    }
    
    /**
     * Mode batch: java Main <fichier.png> <Lambda|Phi> <parametre>
     */
    private static void batchMode(String[] args) {
        try {
            String inputFile = args[0];
            String method = args[1];
            int param = Integer.parseInt(args[2]);
            
            System.out.println("=== Mode Batch ===");
            System.out.println("Fichier: " + inputFile);
            System.out.println("Méthode: " + method);
            System.out.println("Paramètre: " + param);
            
            // Charger l'image
            ImagePNG img = new ImagePNG(inputFile);
            System.out.println("Image chargée: " + img.width() + "x" + img.height());
            
            // Construire le R-Quadtree
            RQuadtree tree = new RQuadtree(img);
            System.out.println("R-Quadtree construit avec " + tree.getLeafCount() + " feuilles");
            
            // Appliquer la compression
            if (method.equalsIgnoreCase("Lambda")) {
                tree.compressLambda(param);
            } else if (method.equalsIgnoreCase("Phi")) {
                tree.compressPhi(param);
            } else {
                System.err.println("Méthode inconnue. Utiliser Lambda ou Phi");
                return;
            }
            System.out.println("Compression appliquée. Nouvelles feuilles: " + tree.getLeafCount());
            
            // TODO: Générer les fichiers de sortie
            // 1. Image compressée: <inputFile>_compressed.png
            // 2. Représentation R-Quadtree: <inputFile>_tree.txt
            // 3. Représentation AVL: <inputFile>_avl.txt
            
            // TODO: Afficher les métriques de comparaison
            // - Ratio de poids des fichiers PNG
            // - EQM (qualité)
            
            System.out.println("\nFichiers générés:");
            System.out.println("  - Image compressée (TODO)");
            System.out.println("  - Arbre R-Quadtree (TODO)");
            System.out.println("  - Arbre AVL (TODO)");
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Mode interactif avec menu
     */
    private static void interactiveMode() {
        System.out.println("=== Compression d'Images par R-Quadtree ===");
        
        while (true) {
            displayMenu();
            int choice = getUserChoice();
            
            try {
                switch (choice) {
                    case 1:
                        buildRQuadtree();
                        break;
                    case 2:
                        applyCompressLambda();
                        break;
                    case 3:
                        applyCompressPhi();
                        break;
                    case 4:
                        saveTreeAsPNG();
                        break;
                    case 5:
                        saveTreeAsText();
                        break;
                    case 6:
                        comparePNGFiles();
                        break;
                    /*case 7:
                        buildAVLFromImage();
                        break;
                    case 8:
                        buildAVLFromTree();
                        break;
                    case 9:
                        saveAVLAsText();
                        break;
                    case 10:
                        searchColorInAVL();
                        break;
                    case 11:
                        addColorToAVL();
                        break;
                    case 12:
                        removeColorFromAVL();
                        break;*/
                    case 0:
                        System.out.println("Au revoir!");
                        return;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.err.println("Erreur: " + e.getMessage());
            }
            
            System.out.println();
        }
    }
    
    private static void displayMenu() {
        System.out.println("\n--- Menu Principal ---");
        System.out.println("1.  Construire R-Quadtree depuis une image");
        System.out.println("2.  Appliquer compression Lambda");
        System.out.println("3.  Appliquer compression Phi");
        System.out.println("4.  Sauvegarder R-Quadtree en PNG");
        System.out.println("5.  Sauvegarder R-Quadtree en texte");
        System.out.println("6.  Comparer deux fichiers PNG");
        System.out.println("7.  Construire AVL depuis une image");
        System.out.println("8.  Construire AVL depuis R-Quadtree");
        System.out.println("9.  Sauvegarder AVL en texte");
        System.out.println("10. Rechercher une couleur dans AVL");
        System.out.println("11. Ajouter une couleur à AVL");
        System.out.println("12. Supprimer une couleur de AVL");
        System.out.println("0.  Quitter");
        System.out.print("Votre choix: ");
    }
    
    private static int getUserChoice() {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Nettoyer le buffer
            return -1;
        }
    }
    
    // Implémentations des fonctions du menu
    
    private static void buildRQuadtree() throws Exception {
        scanner.nextLine(); // Nettoyer le buffer
        System.out.print("Nom du fichier PNG: ");
        String filename = scanner.nextLine();
        
        ImagePNG img = new ImagePNG(filename);
        currentTree = new RQuadtree(img);
        System.out.println("R-Quadtree construit avec " + currentTree.getLeafCount() + " feuilles");
    }
    
    private static void applyCompressLambda() {
        if (currentTree == null) {
            System.out.println("Veuillez d'abord construire un R-Quadtree (option 1)");
            return;
        }
        System.out.print("Valeur de Lambda: ");
        int lambda = scanner.nextInt();
        currentTree.compressLambda(lambda);
        System.out.println("Compression Lambda appliquée. Nouvelles feuilles: " + currentTree.getLeafCount());
    }
    
    private static void applyCompressPhi() {
        if (currentTree == null) {
            System.out.println("Veuillez d'abord construire un R-Quadtree (option 1)");
            return;
        }
        System.out.print("Valeur de Phi: ");
        int phi = scanner.nextInt();
        currentTree.compressPhi(phi);
        System.out.println("Compression Phi appliquée. Nouvelles feuilles: " + currentTree.getLeafCount());
    }
    
    private static void saveTreeAsPNG() throws Exception {
        if (currentTree == null) {
            System.out.println("Aucun R-Quadtree disponible");
            return;
        }
        scanner.nextLine();
        System.out.print("Nom du fichier de sortie: ");
        String filename = scanner.nextLine();
        ImagePNG img = currentTree.toPNG();
        img.save(filename);
        System.out.println("Image sauvegardée: " + filename);
    }
    
    private static void saveTreeAsText() throws Exception {
        if (currentTree == null) {
            System.out.println("Aucun R-Quadtree disponible");
            return;
        }
        scanner.nextLine();
        System.out.print("Nom du fichier texte: ");
        String filename = scanner.nextLine();
        PrintWriter writer = new PrintWriter(filename);
        writer.println(currentTree.toString());
        writer.close();
        System.out.println("Arbre sauvegardé: " + filename);
    }
    
    private static void comparePNGFiles() throws Exception {
        scanner.nextLine();
        System.out.print("Fichier PNG 1 (référence): ");
        String file1 = scanner.nextLine();
        System.out.print("Fichier PNG 2 (comparaison): ");
        String file2 = scanner.nextLine();
        
        ImagePNG img1 = new ImagePNG(file1);
        ImagePNG img2 = new ImagePNG(file2);
        
        double eqm = ImagePNG.computeEQM(img1, img2);
        
        File f1 = new File(file1);
        File f2 = new File(file2);
        double ratio = Math.ceil(10000.0 * f2.length() / f1.length()) / 100.0;
        
        System.out.println("\nRésultats de comparaison:");
        System.out.println("  Qualité (EQM): " + eqm + "%");
        System.out.println("  Ratio de poids: " + ratio + "%");
    }
    
    /*private static void buildAVLFromImage() throws Exception {
        scanner.nextLine();
        System.out.print("Nom du fichier PNG: ");
        String filename = scanner.nextLine();
        ImagePNG img = new ImagePNG(filename);
        currentAVL = new AVL(img);
        System.out.println("AVL construit depuis l'image");
    }
    
    private static void buildAVLFromTree() {
        if (currentTree == null) {
            System.out.println("Aucun R-Quadtree disponible");
            return;
        }
        currentAVL = new AVL(currentTree);
        System.out.println("AVL construit depuis le R-Quadtree");
    }
    
    private static void saveAVLAsText() throws Exception {
        if (currentAVL == null) {
            System.out.println("Aucun AVL disponible");
            return;
        }
        scanner.nextLine();
        System.out.print("Nom du fichier texte: ");
        String filename = scanner.nextLine();
        PrintWriter writer = new PrintWriter(filename);
        writer.println(currentAVL.toString());
        writer.close();
        System.out.println("AVL sauvegardé: " + filename);
    }
    
    private static void searchColorInAVL() {
        if (currentAVL == null) {
            System.out.println("Aucun AVL disponible");
            return;
        }
        scanner.nextLine();
        System.out.print("Couleur en hexadécimal (ex: ff0000 pour rouge): ");
        String hex = scanner.nextLine();
        Color color = ImagePNG.hexToColor(hex);
        boolean found = currentAVL.search(color);
        System.out.println(found ? "Couleur trouvée" : "Couleur non trouvée");
    }
    
    private static void addColorToAVL() {
        if (currentAVL == null) {
            System.out.println("Aucun AVL disponible");
            return;
        }
        scanner.nextLine();
        System.out.print("Couleur en hexadécimal (ex: 00ff00 pour vert): ");
        String hex = scanner.nextLine();
        Color color = ImagePNG.hexToColor(hex);
        currentAVL.add(color);
        System.out.println("Couleur ajoutée");
    }
    
    private static void removeColorFromAVL() {
        if (currentAVL == null) {
            System.out.println("Aucun AVL disponible");
            return;
        }
        scanner.nextLine();
        System.out.print("Couleur en hexadécimal (ex: 0000ff pour bleu): ");
        String hex = scanner.nextLine();
        Color color = ImagePNG.hexToColor(hex);
        currentAVL.remove(color);
        System.out.println("Couleur supprimée");
    }*/
}

