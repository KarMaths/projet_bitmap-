import java.awt.Color;
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

   private Node root;

  
   /**
    * Constructeur d'un AVL a partir d'un image
    * Parcourir tous les pixels et ajoute les couleurs uniques 
    * @param img
    */
   public AVL(ImagePNG img){
        this.root = null;
        for(int i=0; i<img.height(); i++){
            for(int j=0; j<img.width(); j++){
                add(img.getPixel(i, j));
            }
        }
   }

   public void add(Color col){
        root = addRecursivement(root, col);
   }

   private Node addRecursivement(Node node, Color col){
    
        if(node == null) return new Node(col);
        
        int compare = compareColors(col, node.color);
        if(compare > 0){
            node.right = addRecursivement(node.right, col);
        }
        if(compare < 0){
            node.left = addRecursivement(node.left, col);
        } else{
            // Couleur deja presente, RAF
            return node;
        }

        // Update hauteur
        node.hauteur = 1 + Math.max(getHauteur(node.left), getHauteur(node.right));
        int balance = getBalance(node);

        // ------Rotations------- 
        // rotation vers la gauche 
        if( balance > 1 && compareColors(col, node.right.color) > 0){
            return rotationVersGauche(node);
        }

        // Double Rotation vers la gauche (droite-gauche)
        if( balance > 1 && compareColors(col, node.right.color) < 0){
            node.right = rotationVersDroite(node);
            return rotationVersGauche(node);
        }

        // Rotation vers la droite 
        if( balance < -1 && compareColors(col, node.left.color) < 0 ){
            return rotationVersDroite(node);
        }

        // Double rotation vers la droite (gauche-droite)
        if(balance < -1 && compareColors(col, node.left.color) > 0){
            node.left = rotationVersGauche(node.left);
            return rotationVersDroite(node);
        }

        return node;
   }

   public void remove(Color col){
        root = removeRecursive(root, col);
   }

   private Node removeRecursive(Node node, Color col){
        
        if(node == null) return null;
        int compare = compareColors(col, node.color);

        if(compare < 0){
            node.left = removeRecursive(node.left, col);
        } else if(compare > 0){
            node.right = removeRecursive(node.right, col);
        } else {
            // Noeud a supprimer trouve
            if(node.left != null || node.right != null){
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
        int balance = getBalance(node);

        // ------Rotations------- 
        // rotation vers la gauche 
        if( balance > 1 && compareColors(col, node.right.color) > 0){
            return rotationVersGauche(node);
        }

        // Double Rotation vers la gauche (droite-gauche)
        if( balance > 1 && compareColors(col, node.right.color) < 0){
            node.right = rotationVersDroite(node);
            return rotationVersGauche(node);
        }

        // Rotation vers la droite 
        if( balance < -1 && compareColors(col, node.left.color) < 0 ){
            return rotationVersDroite(node);
        }

        // Double rotation vers la droite (gauche-droite)
        if(balance < -1 && compareColors(col, node.left.color) > 0){
            node.left = rotationVersGauche(node.left);
            return rotationVersDroite(node);
        }

        return node; 
    }

    public String toString() {
        return toStringRecursive(root);
    }

    private String toStringRecursive(Node node) {
        if (node == null) return "";
        if (node.left == null && node.right == null) {
            return ImagePNG.colorToHex(node.color);
        }
        String left = toStringRecursive(node.left);
        String right = toStringRecursive(node.right);
        return "(" + (left.isEmpty() ? "" : left + " ") + 
            ImagePNG.colorToHex(node.color) + 
            (right.isEmpty() ? "" : " " + right) + ")";
    }
   

    private Node rotationVersDroite(Node y){
        Node x = y.left;
        Node z = x.right;

        x.right = y; 
        y.left = z;

        y.hauteur = 1 + Math.max(getHauteur(y.left), getHauteur(y.right));
        x.hauteur = 1 + Math.max(getHauteur(x.left), getHauteur(x.right));

        return x;
   }

   private Node rotationVersGauche(Node x){
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
    while(node.left != null){
        node =node.left;
    }
    return node;
   }

}
