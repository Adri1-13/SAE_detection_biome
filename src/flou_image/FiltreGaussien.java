package flou_image;

/**
 * Construit un noyau (kernel) de convolution gaussien carré et normalisé.
 *
 * Le noyau est calculé à partir de la formule de Gauss 2D, centré sur sa case
 * du milieu (le pixel courant lors de la convolution), puis normalisé pour que
 * la somme de tous ses coefficients vaille 1 — ce qui préserve la luminosité
 * de l'image floutée.
 */
public class FiltreGaussien {

    /** Écart-type utilisé par défaut si aucun n'est précisé. */
    private static final double SIGMA_PAR_DEFAUT = 1.0;

    private final double[][] coefficients;

    /**
     * @param taille dimension du noyau (doit être impaire, ex. 3, 5, 7)
     */
    public FiltreGaussien(int taille) {
        this(taille, SIGMA_PAR_DEFAUT);
    }

    /**
     * @param taille dimension du noyau (doit être impaire, ex. 3, 5, 7)
     * @param sigma  écart-type de la gaussienne : plus il est grand, plus le flou est diffus
     */
    public FiltreGaussien(int taille, double sigma) {
        this.coefficients = new double[taille][taille];
        int centre = taille / 2;
        double somme = 0.0;

        // 1) Calcul des coefficients bruts à partir de la distance au centre.
        for (int ligne = 0; ligne < taille; ligne++) {
            for (int colonne = 0; colonne < taille; colonne++) {
                int distanceX = colonne - centre;
                int distanceY = ligne - centre;
                double coefficient = calculGauss(distanceX, distanceY, sigma);
                coefficients[ligne][colonne] = coefficient;
                somme += coefficient;
            }
        }

        // 2) Normalisation : la somme des coefficients doit valoir 1.
        for (int ligne = 0; ligne < taille; ligne++) {
            for (int colonne = 0; colonne < taille; colonne++) {
                coefficients[ligne][colonne] /= somme;
            }
        }
    }

    /**
     * Renvoie le noyau gaussien normalisé sous forme de matrice [ligne][colonne].
     */
    public double[][] getFiltre() {
        return coefficients;
    }

    /**
     * Évalue la formule de Gauss 2D pour un point situé à (x, y) du centre.
     *
     * @param x     distance horizontale au pixel central
     * @param y     distance verticale au pixel central
     * @param sigma écart-type de la distribution
     */
    public double calculGauss(int x, int y, double sigma) {
        double variance = sigma * sigma;
        return (1.0 / (2 * Math.PI * variance)) * Math.exp(-(x * x + y * y) / (2 * variance));
    }
}