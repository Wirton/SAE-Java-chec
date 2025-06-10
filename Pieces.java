public abstract class Pieces {
    private int ligne;
    private int colonne;
    private String couleur;

    public Pieces(int lig, int col, String couleur) {
        this.ligne = lig;
        this.colonne = col;
        this.couleur = couleur;
    }

    public int getLigne() {
        return ligne;
    }
    public int getColonne() {
        return colonne;
    }
    public String getCouleur() {
        return couleur;
    }
    public void setPosition(int lig, int col) {
        this.ligne = lig;
        this.colonne = col;
    }

    // Vérifie la validité du déplacement hors obstacle
    public abstract boolean coupOk(int lig, int col, String pieceCible);
}