public class Case {
    private int ligne;
    private int col;
    private String contenu;

    public Case(int x, int y, String p) {
        if (!estValide(x, y)) {
            throw new IllegalArgumentException("Coordonnées invalides : (" + x + "," + y + ")");
        }
        this.ligne = x;
        this.col = y;
        this.contenu = p;
    }

    public Case(int x, int y) {
        this(x, y, "");
    }

    public static boolean estValide(int x, int y) {
        return x >= 0 && x <= 7 && y >= 0 && y <= 7;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void verifierPiece() {
        char colonneLettre = (char) ('A' + col);
        int ligneEchiquier = 8 - ligne;
        String notation = "" + colonneLettre + ligneEchiquier;
        if (contenu == null || contenu.trim().isEmpty()) {
            System.out.println("Il n'y a pas de pièce dans la case " + notation + ".");
        } else {
            System.out.println("La case " + notation + " contient une pièce : " + contenu);
        }
    }
}