import java.util.*;

public class Joueurs {
    private Echiquier ech;
    private boolean blancJoue = true;
    private boolean partieTerminee = false;
    private String nomBlanc;
    private String nomNoir;

    public Joueurs(Echiquier e, String nomBlanc, String nomNoir) {
        this.ech = e;
        this.nomBlanc = nomBlanc;
        this.nomNoir = nomNoir;
    }

    public String getNomJoueurActuel() {
        return blancJoue ? nomBlanc : nomNoir;
    }

    public String getNomAdversaire() {
        return blancJoue ? nomNoir : nomBlanc;
    }

    public String getSuffixeJoueurActuel() {
        return blancJoue ? "b" : "n";
    }
    
    public void changerTour() {
        blancJoue = !blancJoue;
    }

    public boolean isPartieTerminee() {
        return partieTerminee;
    }

    public void setPartieTerminee(boolean t) {
        partieTerminee = t;
    }

    public int[] demanderCoordonnees(Scanner sc, String message) {
        while (true) {
            System.out.print(message);
            String entree = sc.nextLine().trim().toUpperCase();
            if (entree.equals("STOP") || entree.equals("ANNULER")) return null;
            if (entree.length() == 2) {
                char col = entree.charAt(0);
                char row = entree.charAt(1);
                if (col >= 'A' && col <= 'H' && row >= '1' && row <= '8') {
                    int c = col - 'A';
                    int l = 8 - (row - '0');
                    return new int[]{l, c};
                }
            }
            System.out.println("Entrée invalide. Format attendu : A2, B5... ou STOP.");
        }
    }

    public List<int[]> getCoupsPossibles(String piece, int[] src) {
        List<int[]> coups = new ArrayList<>();
        int x = src[0], y = src[1];
        char type = piece.charAt(0);
        String coul = piece.substring(1);
        Pieces p = null;

        switch (type) {
            case 'P':
                p = new Pion(x, y, coul, ech);
                break;
            case 'C':
                p = new Cavalier(x, y, coul);
                break;
            case 'F':
                p = new Fou(x, y, coul, ech);
                break;
            case 'T':
                p = new Tour(x, y, coul, ech);
                break;
            case 'D':
                p = new Dame(x, y, coul, ech);
                break;
            case 'R':
                p = new Roi(x, y, coul);
                break;
            default:
                return coups;
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i == x && j == y) continue;
                String cible = ech.getPiece(i, j);
                // Ne pas prendre ses propres pièces
                if (!cible.trim().isEmpty() && cible.endsWith(coul)) continue;
                // Mouvement logique
                if (p.coupOk(i, j, cible)) {
                    // Vérifier obstacle pour Fou/Tour/Dame
                    if ((p instanceof Fou || p instanceof Tour || p instanceof Dame)
                        && !ech.estCheminLibre(x, y, i, j)) {
                        continue;
                    }
                    // Vérifier que le coup n'expose pas son roi (ni rois adjacents)
                    if (coupAutorise(x, y, i, j, piece)) {
                        coups.add(new int[]{i, j});
                    }
                }
            }
        }
        return coups;
    }

    // Vérifie si un coup est légal (ne met pas son roi en échec et pas rois adjacents)
    public boolean coupAutorise(int ligSource, int colSource, int ligDest, int colDest, String piece) {
        // Sauvegarder l’état actuel
        String pieceSource = ech.getPiece(ligSource, colSource);
        String pieceDest = ech.getPiece(ligDest, colDest);

        // Effectuer coup temporaire
        ech.placerPiece(ligDest, colDest, pieceSource);
        ech.placerPiece(ligSource, colSource, "  ");

        boolean roiEnEchec = ech.estEnEchec(getSuffixeJoueurActuel());
        boolean roisCollés = ech.roisAdjacents();

        // Revenir en arrière
        ech.placerPiece(ligSource, colSource, pieceSource);
        ech.placerPiece(ligDest, colDest, pieceDest);

        // Si roi en échec ou rois adjacents, interdit
        return !roiEnEchec && !roisCollés;
    }

    // Vérifie si la partie est en échec et mat pour la couleur donnée
    public boolean estEchecEtMat(String couleur) {
        if (!ech.estEnEchec(couleur)) return false;

        // Pour chaque pièce alliée, vérifier si elle a un coup qui sauve
        for (int i=0; i<8; i++) {
            for (int j=0; j<8; j++) {
                String piece = ech.getPiece(i,j);
                if (!piece.trim().isEmpty() && piece.trim().endsWith(couleur)) {
                    char type = piece.charAt(0);
                    String coul = piece.substring(1);
                    Pieces p = null;

                    switch (type) {
                        case 'P':
                            p = new Pion(i, j, coul, ech);
                            break;
                        case 'C':
                            p = new Cavalier(i, j, coul);
                            break;
                        case 'F':
                            p = new Fou(i, j, coul, ech);
                            break;
                        case 'T':
                            p = new Tour(i, j, coul, ech);
                            break;
                        case 'D':
                            p = new Dame(i, j, coul, ech);
                            break;
                        case 'R':
                            p = new Roi(i, j, coul);
                            break;
                        default:
                            // Pièce inconnue, retourner null ou ignorer
                            break;
                    }

                    List<int[]> coups = getCoupsPossibles(piece.trim(), new int[]{i,j});
                    if (!coups.isEmpty()) {
                        return false; // au moins un coup possible pour sortir
                    }
                }
            }
        }
        return true; // aucun coup possible pour sortir de l'échec = échec et mat
    }

    // Méthode à appeler après chaque coup pour vérifier état de la partie
        // Méthode à appeler après chaque coup pour vérifier état de la partie
    public void verifierEtatPartie() {
        // Couleur de l'adversaire (celui qui va jouer ensuite)
        String adversaire = blancJoue ? "n" : "b";

        // Vérifier que roi de l’adversaire est encore présent
        if (ech.chercherRoi(adversaire) == null) {
            System.out.println("Le roi " + (adversaire.equals("b") ? "blanc" : "noir") + " a été capturé.");
            System.out.println("Partie terminée, " + getNomJoueurActuel() + " gagne !");
            setPartieTerminee(true);
            return;
        }

        // Vérifier si l'adversaire est en échec
        if (ech.estEnEchec(adversaire)) {
            System.out.println("Échec au roi " + (adversaire.equals("b") ? "blanc" : "noir") + " !");
            // Vérifier si échec et mat
            if (estEchecEtMat(adversaire)) {
                System.out.println("Échec et Mat ! " + getNomJoueurActuel() + " gagne la partie.");
                setPartieTerminee(true);
            }
        } else {
            // Pas d'échec, vérifier si échec et mat impossible (pat)
            if (estEchecEtMat(adversaire)) {
                System.out.println("Pat ! Partie nulle.");
                setPartieTerminee(true);
            }
        }
    }
}
