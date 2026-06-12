package flou_gaussien;

public class FiltreGaussien {

    double[][] filtre;

    public FiltreGaussien(int taille) {
        filtre = new double[taille][taille];   // allocation manquante
        int centre = taille / 2;
        for (int i = 0; i < taille; i++) {
            for (int j = 0; j < taille; j++) {
                filtre[i][j] = calcul_gauss(i - centre, j - centre, 3.0);  // centrage
            }
        }
    }

    public double calcul_gauss(int x, int y, double sigma) {
        return (1.0 / (2 * Math.PI * sigma * sigma)) * Math.exp(-(x * x + y * y) / (2 * sigma * sigma));
    }
}