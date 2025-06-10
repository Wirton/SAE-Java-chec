import java.util.*; // Importe les classes nécessaires (Scanner, List, Map...)

public class Partie {
    // Constantes pour la mise en forme du texte (couleur, gras, fond rouge)
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String BG_RED = "\u001B[41m";
    private static final String BOLD = "\u001B[1m";

    // Affiche un message important en rouge et encadré
    private static void afficherMessageImportant(String message) {
        String msg = message.toUpperCase();
        String ligneEncadree = BG_RED + " ".repeat(msg.length() + 4) + RESET;
        System.out.println(ligneEncadree);
        System.out.println(BG_RED + "  " + RED + BOLD + msg + RESET + BG_RED + "  " + RESET);
        System.out.println(ligneEncadree);
    }

    // Convertit une coordonnée (ex: A2) en indices [ligne, colonne]
    private static int[] parseCoord(String coord) {
        if (coord.length() != 2) return null;
        char colChar = Character.toUpperCase(coord.charAt(0));
        char rowChar = coord.charAt(1);
        if (colChar < 'A' || colChar > 'H' || rowChar < '1' || rowChar > '8') return null;
        int col = colChar - 'A';
        int row = 8 - (rowChar - '0');
        return new int[]{row, col};
    }

    public static void main(String[] args) {
        Echiquier e = new Echiquier(); // Crée un échiquier
        Scanner sc = new Scanner(System.in); // Pour lire les entrées clavier

        // Demande le nom des joueurs
        System.out.print("Nom du joueur des Blancs : ");
        String nomBlanc = sc.nextLine().trim();
        if (nomBlanc.isEmpty()) nomBlanc = "Blancs";

        System.out.print("Nom du joueur des Noirs : ");
        String nomNoir = sc.nextLine().trim();
        if (nomNoir.isEmpty()) nomNoir = "Noirs";

        // Initialise la gestion des joueurs
        Joueurs joueurs = new Joueurs(e, nomBlanc, nomNoir);

        // Crée un historique des coups joués pour chaque joueur
        Map<String, List<String>> historiqueCoups = new HashMap<>();
        historiqueCoups.put(nomBlanc, new ArrayList<>());
        historiqueCoups.put(nomNoir, new ArrayList<>());

        // Place les pièces noires
        e.placerPiece(0, 0, "Tn"); e.placerPiece(0, 1, "Cn"); e.placerPiece(0, 2, "Fn"); e.placerPiece(0, 3, "Dn");
        e.placerPiece(0, 4, "Rn"); e.placerPiece(0, 5, "Fn"); e.placerPiece(0, 6, "Cn"); e.placerPiece(0, 7, "Tn");
        for (int j = 0; j < 8; j++) e.placerPiece(1, j, "Pn");

        // Place les pièces blanches
        e.placerPiece(7, 0, "Tb"); e.placerPiece(7, 1, "Cb"); e.placerPiece(7, 2, "Fb"); e.placerPiece(7, 3, "Db");
        e.placerPiece(7, 4, "Rb"); e.placerPiece(7, 5, "Fb"); e.placerPiece(7, 6, "Cb"); e.placerPiece(7, 7, "Tb");
        for (int j = 0; j < 8; j++) e.placerPiece(6, j, "Pb");

        // Boucle principale du jeu
        while (true) {
            e.afficher(); // Affiche l’échiquier
            System.out.println("Tour des " + joueurs.getNomJoueurActuel());

            // Demande la pièce à jouer
            System.out.print("Sélectionnez une pièce à jouer (ex: A2 ou STOP ou COUPS) : ");
            String entree = sc.nextLine().trim().toUpperCase();

            // Arrêter le jeu
            if (entree.equals("STOP")) break;

            // Afficher l’historique des coups
            if (entree.equals("COUPS")) {
                System.out.print("Historique de quel joueur ? (" + nomBlanc + "/" + nomNoir + ") : ");
                String nom = sc.nextLine().trim();
                List<String> coups = historiqueCoups.get(nom);
                if (coups == null || coups.isEmpty()) {
                    System.out.println("Aucun coup enregistré pour " + nom + ".");
                } else {
                    System.out.println("Coups joués par " + nom + " :");
                    System.out.println(String.join(", ", coups));
                }
                continue;
            }

            // Convertit l'entrée en coordonnées
            int[] source = parseCoord(entree);
            if (source == null) {
                System.out.println(RED + "Coordonnées invalides." + RESET);
                continue;
            }

            // Vérifie la présence et la validité de la pièce
            String piece = e.getPiece(source[0], source[1]);
            if (piece.equals("")) {
                System.out.println(RED + "Aucune pièce sur cette case." + RESET);
                continue;
            }
            if (!piece.endsWith(joueurs.getSuffixeJoueurActuel())) {
                System.out.println(RED + "Ce n'est pas une pièce de votre couleur." + RESET);
                continue;
            }

            // Récupère les coups possibles
            List<int[]> coupsPossibles = joueurs.getCoupsPossibles(piece, source);
            if (coupsPossibles.isEmpty()) {
                System.out.println(RED + "Cette pièce ne peut pas bouger. Choisissez une autre pièce." + RESET);
                continue;
            }

            // Affiche les destinations possibles
            System.out.println("Cases disponibles pour déplacement :");
            for (int[] dest : coupsPossibles) {
                char col = (char) ('A' + dest[1]);
                int row = 8 - dest[0];
                System.out.print(col + "" + row + " ");
            }
            System.out.println();

            // Demande où déplacer la pièce
            System.out.print("Où souhaitez-vous déplacer cette pièce ? (ex: A4 ou ANNULER) : ");
            String destEntree = sc.nextLine().trim().toUpperCase();
            if (destEntree.equals("ANNULER")) continue;

            // Convertit la destination en coordonnées
            int[] destination = parseCoord(destEntree);
            if (destination == null) {
                System.out.println(RED + "Coordonnées invalides." + RESET);
                continue;
            }

            // Vérifie si la destination est dans les coups possibles
            boolean valide = false;
            for (int[] cp : coupsPossibles) {
                if (cp[0] == destination[0] && cp[1] == destination[1]) {
                    valide = true;
                    break;
                }
            }

            if (!valide) {
                System.out.println(RED + "Déplacement non autorisé." + RESET);
                continue;
            }

            // Enregistre le coup joué
            String coup = String.format("%c%d -> %c%d",
                    (char) ('A' + source[1]), 8 - source[0],
                    (char) ('A' + destination[1]), 8 - destination[0]);
            historiqueCoups.get(joueurs.getNomJoueurActuel()).add(coup);

            // Déplace la pièce sur l’échiquier
            e.placerPiece(destination[0], destination[1], piece);
            e.placerPiece(source[0], source[1], "");

            // Vérifie échec ou échec et mat
            String adversaire = joueurs.getSuffixeJoueurActuel().equals("b") ? "n" : "b";
            if (e.estEnEchecEtMat(adversaire, joueurs)) {
                afficherMessageImportant("ÉCHEC ET MAT AU ROI " + (adversaire.equals("b") ? "BLANC" : "NOIR"));
                break;
            } else if (e.estEnEchec(adversaire)) {
                afficherMessageImportant("ÉCHEC AU ROI " + (adversaire.equals("b") ? "BLANC" : "NOIR"));
            }

            // Change le tour du joueur
            joueurs.changerTour();
        }

        sc.close(); // Ferme le scanner
        System.out.println("Fin du programme.");
    }
}
