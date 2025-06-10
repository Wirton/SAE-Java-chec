public class Dame extends Pieces {
    private Echiquier ech;

    public Dame(int lig, int col, String couleur, Echiquier ech) {
        super(lig, col, couleur);
        this.ech = ech;
    }

    @Override
    public boolean coupOk(int lig, int col, String cible) {
        int dl = Math.abs(lig - getLigne());
        int dc = Math.abs(col - getColonne());
        boolean tour = (lig == getLigne() && col != getColonne()) || (col == getColonne() && lig != getLigne());
        boolean fou = (dl == dc && dl != 0);
        if (tour || fou) {
            return ech.estCheminLibre(getLigne(), getColonne(), lig, col);
        }
        return false;
    }
}
