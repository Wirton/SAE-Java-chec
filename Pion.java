public class Pion extends Pieces {
    private Echiquier ech;

    public Pion(int lig, int col, String couleur, Echiquier ech) {
        super(lig, col, couleur);
        this.ech = ech;
    }

    @Override
    public boolean coupOk(int lig, int col, String cible) {
        int dir = getCouleur().equals("b") ? -1 : 1;
        int start = getCouleur().equals("b") ? 6 : 1;
        int dl = lig - getLigne();
        int dc = col - getColonne();

        // Avancer tout droit
        if (dc == 0) {
            // 1 case
            if (dl == dir && ech.getPiece(lig, col).trim().isEmpty()) {
                return true;
            }
            // 2 cases depuis la ligne de départ
            if (getLigne() == start
                && dl == 2 * dir
                && ech.getPiece(getLigne() + dir, col).trim().isEmpty()
                && ech.getPiece(lig, col).trim().isEmpty()) {
                return true;
            }
        }

        // Capture en diagonale
        if (Math.abs(dc) == 1 && dl == dir) {
            if (!cible.trim().isEmpty() && !cible.endsWith(getCouleur())) {
                return true;
            }
        }

        return false;
    }
}
