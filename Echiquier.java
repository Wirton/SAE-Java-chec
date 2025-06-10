import java.util.List;

public class Echiquier {

    private final String[][] tab;

    // Codes ANSI pour couleurs 
    private static final String RESET = "\u001B[0m";
    private static final String BG_LIGHT = "\u001B[47m";  // fond blanc 
    private static final String BG_DARK = "\u001B[42m";   // fond vert 
    private static final String FG_BLACK = "\u001B[30m";  // texte noir
    private static final String FG_WHITE = "\u001B[37m";  // texte blanc

    public Echiquier() {
        tab = new String[8][8];
        vider();
    }

    public void vider() {
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                tab[i][j] = "  ";
    }

    public void placerPiece(int ligne, int colonne, String piece) {
        if (piece.length() == 1) piece += " ";
        if (ligne >= 0 && ligne < 8 && colonne >= 0 && colonne < 8)
            tab[ligne][colonne] = piece;
    }

    public String getPiece(int ligne, int colonne) {
        if (ligne >= 0 && ligne < 8 && colonne >= 0 && colonne < 8)
            return tab[ligne][colonne];
        return "";
    }

    public void afficher() {
        String lettres = "    A   B   C   D   E   F   G   H";
        System.out.println("\n" + lettres);
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + "  ");
            for (int j = 0; j < 8; j++) {
                boolean clair = (i + j) % 2 == 0;
                String fond = clair ? BG_LIGHT : BG_DARK;
                String texte = clair ? FG_BLACK : FG_WHITE;

                String contenu = tab[i][j];
                // Normalise à 2 caractères pour toutes les cases
                if (contenu.equals("")) contenu = "  ";
                else if (contenu.length() == 1) contenu = contenu + " ";
                else if (contenu.length() > 2) contenu = contenu.substring(0, 2);

                System.out.print(fond + texte + " " + contenu + " " + RESET);
            }
            System.out.println("  " + (8 - i));
        }
        System.out.println(lettres + "\n");
    }

    // Méthode ajoutée pour vérifier que le chemin est libre
    public boolean estCheminLibre(int x1, int y1, int x2, int y2) {
        int dx = Integer.compare(x2, x1);
        int dy = Integer.compare(y2, y1);
        int cx = x1 + dx, cy = y1 + dy;
        while (cx != x2 || cy != y2) {
            if (!getPiece(cx, cy).trim().isEmpty()) return false;
            cx += dx; cy += dy;
        }
        return true;
    }

    // Trouve la position du roi d'une couleur ("b" ou "n")
    public int[] chercherRoi(String couleur) {
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                String p = getPiece(i,j);
                if (p.startsWith("R") && p.trim().endsWith(couleur)) return new int[]{i,j};
            }
        }
        return null; // roi absent (capturé)
    }

    // Vérifie si le roi de la couleur est en échec
    public boolean estEnEchec(String couleur) {
        int[] posRoi = chercherRoi(couleur);
        if (posRoi == null) return false; // roi capturé ou absent

        int roiL = posRoi[0];
        int roiC = posRoi[1];
        String adversaire = couleur.equals("b") ? "n" : "b";

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String piece = getPiece(i, j).trim();
                if (!piece.isEmpty() && piece.endsWith(adversaire)) {
                    Pieces p = null;
                    char type = piece.charAt(0);
                    switch (type) {
                        case 'P':
                            p = new Pion(i, j, adversaire, this);
                            break;
                        case 'C':
                            p = new Cavalier(i, j, adversaire);
                            break;
                        case 'F':
                            p = new Fou(i, j, adversaire, this);
                            break;
                        case 'T':
                            p = new Tour(i, j, adversaire, this);
                            break;
                        case 'D':
                            p = new Dame(i, j, adversaire, this);
                            break;
                        case 'R':
                            p = new Roi(i, j, adversaire);
                            break;
                    }
                    if (p != null && p.coupOk(roiL, roiC, getPiece(roiL, roiC))) {
                        // Vérifier chemin libre pour Fou, Tour, Dame
                        if ((p instanceof Fou || p instanceof Tour || p instanceof Dame)
                            && !estCheminLibre(i, j, roiL, roiC)) {
                            continue;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }


    // Vérifie si les rois sont adjacents (illégal)
    public boolean roisAdjacents() {
        int[] roiB = chercherRoi("b");
        int[] roiN = chercherRoi("n");
        if (roiB == null || roiN == null) return false;

        int dl = Math.abs(roiB[0] - roiN[0]);
        int dc = Math.abs(roiB[1] - roiN[1]);

        return (dl <= 1 && dc <= 1);
    }

    public boolean estEnEchecEtMat(String couleur, Joueurs joueurs) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String piece = getPiece(i, j);
                if (piece.endsWith(couleur)) {
                    List<int[]> coups = joueurs.getCoupsPossibles(piece, new int[]{i, j});
                    for (int[] dest : coups) {
                        String tmp = getPiece(dest[0], dest[1]);
                        placerPiece(dest[0], dest[1], piece);
                        placerPiece(i, j, "");
                        boolean enEchec = estEnEchec(couleur);
                        placerPiece(i, j, piece);
                        placerPiece(dest[0], dest[1], tmp);
                        if (!enEchec) return false;
                    }
                }
            }
        }
        return true;
    }


}
