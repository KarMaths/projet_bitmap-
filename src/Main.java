import java.io.File;
import java.io.PrintWriter;

/**
 * Programme principal avec menu interactif et mode batch
 */
public class Main {
    public static void main(String[] args) {
        batchMode(args);
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
            System.out.println("Fichier entré   : " + inputFile);
            System.out.println("Méthode         : " + method);
            System.out.println("Paramètre       : " + param);
            System.out.println();
            
            // 1. Charger l'image
            ImagePNG img = new ImagePNG(inputFile);
            System.out.println("Image chargée   : " + img.width() + "x" + img.height() + " pixels ");
            
            // 2. Construire le R-Quadtree
            RQuadtree tree = new RQuadtree(img);
            System.out.println("R-Quadtree construit avec " + tree.getLeafCount() + " feuilles");
            
            // 3. Appliquer la compression
            System.out.println();
            System.out.println("--- Application de la compression ---");
            if (method.equalsIgnoreCase("Lambda")) {
                tree.compressLambda(param);
                System.out.println ("Compression lambda appliquée avec λ=" + param + ") ");
            } else if (method.equalsIgnoreCase("Phi")) {
                tree.compressPhi(param);
                System.out.println ("Compression Phi appliquée avec Φ=" + param + ") ");

            } else {
                System.err.println("Méthode inconnue. Utiliser Lambda ou Phi");
                return;
            }
            System.out.println("Nombre de feuilles après compression :" + tree.getLeafCount());
            
            // 4. Générer les fichiers de sortie
            System.out.println("\n---Fichiers générés ---");

            // Extraire le nom de base du fichier 
            String nomDeBase = inputFile.replaceAll("\\.png$", "");
            String methodLower = method.toLowerCase();

            // 4.1. Image compressée: <inputFile>.png
            String FichierImgCompressee = nomDeBase + "_" + methodLower + param + ".png";
            ImagePNG imgCompressee = tree.toPNG();
            if( imgCompressee != null ){
                imgCompressee.save(FichierImgCompressee);
                System.out.println("- Image compressée :    " + FichierImgCompressee);
            } else {
                System.err.println(" Erreur ");
            }

            // 4.2. Représentation R-Quadtree: <inputFile>_R.txt
            String FichierTree = nomDeBase + "_" + methodLower + param + "R.txt";
            PrintWriter ecrireTree = new PrintWriter(FichierTree);
            ecrireTree.println(tree.toString());
            ecrireTree.close();
            System.out.println("- Arbre R-Quadtree :    " + FichierTree);

            // 4.3. Représentation AVL: <inputFile>_AVL.txt
            String FichierAVL = nomDeBase + "_" + methodLower + param + "AVL.txt";
            AVL avl = new AVL(tree);
            PrintWriter ecrireAVL = new PrintWriter(FichierAVL);
            ecrireAVL.println(avl.toString());
            ecrireAVL.close();
            System.out.println("- Arbre AVL        :    " + FichierAVL);

            // 5. Calculer et afficher les metriques de comparaison 
            System.out.println("\n---Metrique de qualité--- ");

                //5.1. Qualité (EQM - Ecart Quadratique Moyen ) Pourcentage de similarite
                double eqm = ImagePNG.computeEQM(img, imgCompressee);
                System.out.println("Qualité (EQM) " + eqm);

                // 5.2 - Ratio de poids des fichiers PNG
                File ficOriginel = new File(inputFile);
                File ficCompressee = new File(FichierImgCompressee);
                double ratioTaille = Math.ceil(10000.0 * ficCompressee.length()/ficOriginel.length())/100.0;
                System.out.println("Ratio de poids : " + ratioTaille + "%");

            
            //  Informations srpplementaires sur les fichiers
            System.out.println("\n---Tailles des fichiers---");
            System.out.println("Fichier Originel:   " + ficOriginel.length()+ " octets");
            System.out.println("Ficher Compressé:   " + ficCompressee.length()+ " octets");
            System.out.println("Economie:   " + (ficOriginel.length() - ficCompressee.length()) + " octets");
            
        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
}