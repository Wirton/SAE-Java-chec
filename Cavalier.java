public class Cavalier extends Pieces {
    public Cavalier(int lig, int col, String couleur) {
        super(lig, col, couleur);
    }

    @Override
    public boolean coupOk(int lig, int col, String cible) {
        int dl = Math.abs(lig - getLigne());
        int dc = Math.abs(col - getColonne());
        return (dl == 2 && dc == 1) || (dl == 1 && dc == 2);
    }
}