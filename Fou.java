public class Fou extends Pieces {
    private Echiquier ech;

    public Fou(int lig, int col, String couleur, Echiquier ech) {
        super(lig, col, couleur);
        this.ech = ech;
    }

    @Override
    public boolean coupOk(int lig, int col, String cible) {
        int dl = Math.abs(lig - getLigne());
        int dc = Math.abs(col - getColonne());
        if (dl == dc && dl != 0) {
            return ech.estCheminLibre(getLigne(), getColonne(), lig, col);
        }
        return false;
    }
}
