import java.awt.Color;

//import java.io.PrintWriter;
//import java.io.IOException;

/**
 * Classe representant un R-Quadtree pour la compression d'images
 * Structure: Arbre quaternaire ou chaque noeud interne a exactement 4 enfants 
 * Ordre des enfants: NO(1), NE(2), SE(3), SO(4)
 */
public class RQuadtree {
    // Classe interne pour representer un noeud de l'arbre 
    private class Node{
        Color color;
        Node no, ne, so, se; 
        Boolean isLeaf;

        // Constructueur d'une feuille 
        Node (Color c){
            this.color = c; 
            this.isLeaf = true;
            this.no = this.ne = this.so = this.se = null;
        }

        // Constructeur pour un noeud interne
        Node(Node no, Node ne, Node se, Node so){
            this.no = no; 
            this.ne = ne;
            this.se = se;
            this.so = so; 
             
            this.isLeaf = false; 
        }
    }
    
    private Node root; // racine de l'arbre 
    private int size;  // taille du RQuadtree 

    /**
     * Constructeur: construit le R-Quadtree a partir d'une image
     * @param image
     */
    public RQuadtree(ImagePNG image) { 
        this.size = image.width();
        // Verifier que la taille est une puissance de 2 
        if (!isPowerOfTwo(size) || (size != image.height())) {}
        
        // construction de l'arbre recursivement 
        this.root = buildTree(image,0,0, size);
    }

    /**
     * construire l'arbre recursivement a partir de l'image 
     * @param image
     * @param x
     * @param y
     * @param taille
     * @return Le noeud racine du sous-arbre
     */
    private Node buildTree(ImagePNG image, int x, int y,int taille ){
        if(taille == 1){
            //pixel unique
            return new Node(image.getPixel(x,y));
        }
        // Cas Recursif
        // 1. Diviser la region en 4
        // 2. Construire recursivement sur chaque sous-arbre
        // 3. verifier si tout les pixels ont la meme couleur
        //      si oui, creer une feuille sinon creer un noeud interne 
        
        int halfSize = taille / 2; 
        Node no = buildTree(image, x, y, halfSize);
        Node ne = buildTree(image, x + halfSize, y, halfSize);
        Node se = buildTree(image, x + halfSize, y + halfSize, halfSize);
        Node so = buildTree(image, x , y + halfSize, taille);
        

        // verifier si tous les enfants sont des feuilles
        if(no.isLeaf && ne.isLeaf && so.isLeaf && se.isLeaf && 
            sameColor(no.color, ne.color) && sameColor(ne.color, so.color) && sameColor(so.color, se.color)){
                return new Node(no.color);
        }
        return new Node(no, ne, so, se);
    }

    /**
     * Calcule la luminance selon la formule 
     * L = 0.2126*R + 0.7152*G + 0.0722*B
     * @param color
     * @return
     */
    private double luminance(Color color){
        return 0.2126*color.getRed() + 0.7152*color.getGreen() + 0.0722*color.getBlue();
    }

    /**
     * Compression a qualite controlee avce 0 < lambda < 255
     * Complexite a indiquer.
     * */ 
    public void compressLambda(int Lambda) {
        root = compressLambdaRec(root, Lambda);
    }

    /**
     * 
     * @param node
     * @param lambda
     * @return
     */
    private Node compressLambdaRec(Node node, int lambda){
        if (node == null || node.isLeaf){
            return node;
        }

        //Recursivité sur chaque noeud
        node.no = compressLambdaRec(node.no, lambda);
        node.ne = compressLambdaRec(node.ne, lambda);
        node.se = compressLambdaRec(node.se, lambda);
        node.so = compressLambdaRec(node.so, lambda);

        //verifie si ce noeud est une sur-feuille 
        if(node.no.isLeaf && node.ne.isLeaf && node.se.isLeaf && node.so.isLeaf){
            // calcul la degradation en luminance 
            // si X < lambda, on retourne une feuille avec la couleur moyenne 
            Color avgColor = CouleurMoyenne(node.no.color, node.ne.color,node.se.color, node.so.color);
            double avglum = luminance(avgColor); 

            double maxDeg = 0; 
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.no.color)));
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.ne.color)));
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.se.color)));
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.so.color)));

            if(maxDeg < lambda){
                return new Node(avgColor);
            }
        }
        
        return node;    
    }

    
    private class SurFeuille {
        Node parent;
        double degradation;
        Color avgColor;

        SurFeuille(Node parent, double deg, Color col){
            this.parent = parent; 
            this.degradation = deg; 
            this.avgColor = col; 
        }
    
    }

    public void compressPhi(int Phi){
        // Compter le nombre actuel de feuilles
        int currentLeaves =  countLeaf(root);
        
        // si il y a deja moins de feuilles que phi, on fait rien 
        if(currentLeaves <= Phi){
            return;
        }
        
        //Trouver la "sur-feuille" avec la plus petite dégradation X
        //compression iterative jusqu'a atteindre phi feuille
        while (currentLeaves > Phi) {
            SurFeuille best = BestSurfeuille(root);

            if(best ==null){
                break;
            }
            //Élaguer cette sur-feuille 
            root = noeud(root, best);
            // chaque elarguage reduit de 3 feuilles (4 feuilles -> 1 feuille)
            currentLeaves -= 3; //Mettre à jour le nombre de feuilles (diminue de 3)
        }  
    }


    private SurFeuille BestSurfeuille(Node node){
        if(node == null || node.isLeaf){
            return null;
        }

        // Verifier si ce noeud est une sur-feuille 
        if(node.no.isLeaf && node.ne.isLeaf && node.se.isLeaf && node.so.isLeaf){
            // calcul la degradation 
            Color avgColor = CouleurMoyenne(node.no.color, node.ne.color,node.se.color, node.so.color);
            double avglum = luminance(avgColor); 

            double maxDeg = 0; 
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.no.color)));
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.ne.color)));
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.se.color)));
            maxDeg = Math.max(maxDeg, Math.abs(avglum - luminance(node.so.color)));

            return new SurFeuille(node, maxDeg, avgColor);
        }

        //cherche recursivement dans les sous-arbres 
        SurFeuille best = null;

        SurFeuille noResultat = BestSurfeuille(node.no);
        if(noResultat != null && (best == null || noResultat.degradation < best.degradation)){
            best = noResultat;
        }

        SurFeuille neResultat = BestSurfeuille(node.ne);
        if(neResultat != null && (best == null || neResultat.degradation < best.degradation)){
            best = neResultat;
        }

        SurFeuille seResultat = BestSurfeuille(node.se);
        if(seResultat != null && (best == null || seResultat.degradation < best.degradation)){
            best = seResultat; 
        }

        SurFeuille soResultat = BestSurfeuille(node.so);
        if(soResultat != null && (best == null || soResultat.degradation < best.degradation)){
            best = soResultat;
        }

        return best; 
    }

    // Elarguer un noeud specifique (le transformer en une feuille)
    private Node noeud(Node node, SurFeuille surFe){
        if(node == null) return null;

        // si on  trouve  le noeud a elarguer 
        if(node == surFe.parent){
            return new Node (surFe.avgColor);
        }

        //sinon, continuer la rechercher dans les sous-arbres
        if(!node.isLeaf){
            node.no = noeud(node.no, surFe);
            node.ne = noeud(node.ne, surFe);
            node.se = noeud(node.se, surFe);
            node.so = noeud(node.so, surFe);
        }
        return node;
    }
    
    /**
     *  Conversion du R-quadtree en objet ImagePNG 
     *  Complexite : O(L*H) pour remplir l'image.
     *  */
    public ImagePNG toPNG() {
        ImagePNG img = null; 
        return img;
    }

    // Représentation textuelle parenthésée 
    @Override
    public String toString() {
        return toStringRec(root);
    }

    private String toStringRec(Node node){
        if(node == null) return " ";
        if(node.isLeaf){
            return ImagePNG.colorToHex(node.color);
        }
        return "("+ toStringRec(node.no) + " " +
                    toStringRec(node.ne) + " " +
                    toStringRec(node.se) + " " +
                    toStringRec(node.so) + ")"; 

    }

    // FONCTIONS UTILITAIRES
    private boolean sameColor(Color c1, Color c2){
        return c1.getRed() == c2.getRed() &&
               c1.getGreen() == c2.getGreen() &&
               c1.getBlue() == c2.getBlue();
    }

    private boolean isPowerOfTwo(int n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    /**
     * Calcule la couleur moyenne des 4 couleurs
     * Chaque composante est la partie entière de la somme divisée par 4
     * @param c1
     * @param c2
     * @param c3
     * @param c4
     * @return la couleur moyenne ie (Rm, Vm, Bm)
     */
    private Color CouleurMoyenne(Color c1, Color c2, Color c3, Color c4){
        int rouge = (c1.getRed() + c2.getRed() + c3.getRed() + c4.getRed())/4;
        int vert = (c1.getGreen() + c2.getGreen() + c3.getGreen() + c4.getGreen())/4;
        int bleu = (c1.getBlue() + c2.getBlue() + c3.getBlue() + c4.getBlue())/4;

        return new Color(rouge, vert, bleu);
    }

    private int countLeaf(Node node){
        if (node == null) return 0;
        if(node.isLeaf) return 1; 
        return countLeaf(node.no)+countLeaf(node.ne)+countLeaf(node.se)+countLeaf(node.so);   
    }

    public int getLeafCount(){
        return countLeaf(root);
    }

}
