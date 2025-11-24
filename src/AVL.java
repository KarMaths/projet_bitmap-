import java.awt.Color;
import java.util.List;
/*
 * Classe representant un arbre AVL pour gerer la palette des couleurs 
 * Arbre binaire de recherche auto equilibré 
 * Les couleurs sont comparés lexicographiquement (R, G, B)
 */
public class AVL {
   // Classe interne pour representer un noeud de l'arbre AVL
   private class Node{
        Color color;
        Node left, right;   // Enfant gauche et droit 
        int hauteur;        // Hauteur du sous-arbre (pour l'equilibrage)

        Node(Color c){
            this.color=c;
            this.left = this.right = null;
            this.hauteur = 1; 
        }
   }

   private Node root; // Racine de l'AVL

  
   /**
    * Constructeur d'un AVL a partir d'un image
    * Parcourir tous les pixels et ajoute les couleurs uniques 
    * @param img
    */
   public AVL(ImagePNG img){
        this.root = null;
        for(int i=0; i<img.height(); i++){
            for(int j=0; j<img.width(); j++){
                // ajout de chaque pixel 
                add(img.getPixel(i, j));
            }
        }
   }

   /**
     * Constructeur: construit l'AVL à partir d'un RQuadtree
     * Parcourir l'arbre et ajoute les couleurs des feuilles uniquement
     * Complexite: O(n log k) avec  n est le nombre de noeuds et k le nombre de couleurs
     * @param tree Le RQuadtree source 
     */
    public AVL(RQuadtree tree) {
        this.root = null;
        // Recuperer toutes les couleurs des feuilles 
        List<Color> colors = tree.nbCouleur();

        // Ajouter chaque couleur a L'AVL
        for(Color col: colors){
            add(col);
        }
    }


    /**
     * 
     * @param col
     */
    public void remove(Color col){
        root = removeRecursive(this.root, col);
    }

    /**
     * Procedure d'ajout dans AVL
     * @param col
     */
    public void add(Color col){
        root = addRecursivement(this.root, col);
    }

    /**
     * 
     * @param col
     * @return true si Couleur est retrouve dans l'arbre false sinon
     */
    public boolean searchAVL(Color col){
        return searchRecAVL(this.root, col);
    }

    
    public String toString() {
        return toStringRecursive(root);
    }

    // *** Fonctions recursives associees aux fonctionnalités des AVLs ie Search, Add et Remove ***

    /**
     * Recherche recursive sur le noeud 
     * @param node
     * @param color
     * @return  true si Couleur est retrouve dans noeud false sinon
     */
    private boolean searchRecAVL(Node node, Color color){
        
        if(node == null) return false ;
        
        int compare =  compareColors(node.color, color);
        if(compare == 0) return true ;
        else if(compare < 0){
            return searchRecAVL(node.left, color);
        } else {
            return searchRecAVL(node.right, color);
        }
    }

    /**
     * A
     * @param node
     * @param col
     * @return
     */
    private Node addRecursivement(Node node, Color col){
    
        if(node == null) return new Node(col);
        
        int compare = compareColors(col, node.color);
        if(compare > 0){
            node.right = addRecursivement(node.right, col);
        }
        else if(compare < 0){
            node.left = addRecursivement(node.left, col);
        } else{
            // Couleur deja presente, RAF
            return node;
        }

        // Update hauteur
        node.hauteur = 1 + Math.max(getHauteur(node.left), getHauteur(node.right));

        // Reequilibrage 
        return RebalancerAVL(node, col);
    }

    /**
     * 
     * @param node
     * @param col
     * @return
     */
    private Node removeRecursive(Node node, Color col){
        
        if(node == null) return null;
        int compare = compareColors(col, node.color);

        if(compare < 0){
            node.left = removeRecursive(node.left, col);
        } else if(compare > 0){
            node.right = removeRecursive(node.right, col);
        } else {
            // Noeud a supprimer trouve
            if(node.left == null || node.right == null){
                node = (node.left != null) ? node.left : node.right;
            } else{
                //Noeud avec 2 enfants, trouver le successeur 
                Node succ = findMin(node.right);
                node.color = succ.color;
                node.right = removeRecursive(node.right, succ.color);
            }
        }

        if(node == null ) return null;

        // mise a jour hauteur et reequilibrage
        node.hauteur = 1 + Math.max(getHauteur(node.left), getHauteur(node.right));
        return RebalancerAVL(node, col);
    }

    /**
     * 
     * @param node
     * @return
     */
    private String toStringRecursive(Node node) {
        if (node == null) return "()";
        if (node.left == null && node.right == null) {
            return ImagePNG.colorToHex(node.color);
        }
        String left = toStringRecursive(node.left);
        String right = toStringRecursive(node.right);
        return "(" + (isEmptyString(left) ? "" : left + " ") + 
            ImagePNG.colorToHex(node.color) + 
            (isEmptyString(right) ? "" : " " + right) + ")";
    }
    
    // -------------- Fonctions utlitaires------------------------------------

    private Node RebalancerAVL(Node node, Color col){
    if(node == null) return null;
    
    int balance = getBalance(node);
    
    // Cas Gauche-Gauche: rotation droite simple
    if(balance < -1 && node.left != null && getBalance(node.left) <= 0){
        return rotationVersDroite(node);
    }
    
    // Cas Gauche-Droite: double rotation
    if(balance < -1 && node.left != null && getBalance(node.left) > 0){
        node.left = rotationVersGauche(node.left);
        return rotationVersDroite(node);
    }
    
    // Cas Droite-Droite: rotation gauche simple
    if(balance > 1 && node.right != null && getBalance(node.right) >= 0){
        return rotationVersGauche(node);
    }
    
    // Cas Droite-Gauche: double rotation
    if(balance > 1 && node.right != null && getBalance(node.right) < 0){
        node.right = rotationVersDroite(node.right);
        return rotationVersGauche(node);
    }
    
    return node;
    } 

    private Node rotationVersDroite(Node y){
        if(y ==null || y.left == null) return y; 

        Node x = y.left;
        Node z = x.right;

        x.right = y; 
        y.left = z;

        y.hauteur = 1 + Math.max(getHauteur(y.left), getHauteur(y.right));
        x.hauteur = 1 + Math.max(getHauteur(x.left), getHauteur(x.right));

        return x;
    }

    private Node rotationVersGauche(Node x){
        if(x == null || x.right == null) return x; 
        Node y = x.right;
        Node z = y.left;
        
        y.left = x;
        x.right = z;
        
        x.hauteur = 1 + Math.max(getHauteur(x.left), getHauteur(x.right));
        y.hauteur = 1 + Math.max(getHauteur(y.left), getHauteur(y.right));
        
        return y;
   }

   /**
    * Compare deux couleur lexicographiquement 
    * @param col1
    * @param col2
    * @return < 0 si col1 < col2, 0 si égales, > 0 si col1 > col2
    */
   public int compareColors(Color col1, Color col2){
        if(col1.getRed() != col2.getRed()) return col1.getRed() - col2.getRed() ;
        if(col1.getGreen() != col2.getGreen()) return col1.getGreen() - col2.getGreen();
        return col1.getBlue() - col2.getBlue();
    }

    private int getHauteur(Node node){
        return (node == null) ? 0 : node.hauteur;
    }

    private int getBalance(Node node){
        return (node == null) ? 0 : getHauteur(node.right) - getHauteur(node.left);
    }

    private Node findMin(Node node){
        while(node.left != null) {
            node =node.left;
        }
    return node;
    }

    private boolean isEmptyString(String s){
        return (s == null || s.length() ==0 );
    }   
}
