public class Tour extends Pieces {
    private Echiquier ech;

    public Tour(int lig, int col, String couleur, Echiquier ech) {
        super(lig, col, couleur);
        this.ech = ech;
    }

    @Override
    public boolean coupOk(int lig, int col, String cible) {
        if ((lig == getLigne() && col != getColonne()) || (col == getColonne() && lig != getLigne())) {
            return ech.estCheminLibre(getLigne(), getColonne(), lig, col);
        }
        return false;
    }
}