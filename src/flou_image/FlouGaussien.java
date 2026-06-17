package flou_image;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Applique un flou à une image avec un filtre gaussien.
 *
 * Pour chaque pixel, on fait la moyenne pondérée de ses voisins selon les
 * coefficients du noyau : le pixel central pèse le plus, et le poids décroît
 * avec l'éloignement. Les bords sont gérés par « restriction » (les coordonnées hors
 * image sont ramenées sur le bord le plus proche).
 */
public class FlouGaussien implements ProcessFlou {

    /** Taille de noyau utilisée par le constructeur sans argument. */
    private static final int TAILLE_PAR_DEFAUT = 9;

    private final int tailleFiltre;

    public FlouGaussien() {
        this(TAILLE_PAR_DEFAUT);
    }

    /**
     * @param tailleFiltre dimension du noyau, impaire et >= 3 (ex. 3, 5, 7)
     */
    public FlouGaussien(int tailleFiltre) {
        if (tailleFiltre % 2 == 0 || tailleFiltre < 3) {
            throw new IllegalArgumentException("La taille du filtre doit être impaire et >= 3");
        }
        this.tailleFiltre = tailleFiltre;
    }

    @Override
    public void flouterImage(String src) {
        try {
            File fichierSource = new File(src);
            BufferedImage imageSource = ImageIO.read(fichierSource);
            if (imageSource == null) {
                throw new IOException("Impossible de lire l'image: " + src);
            }

            int largeur = imageSource.getWidth();
            int hauteur = imageSource.getHeight();
            BufferedImage imageFloutee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_3BYTE_BGR);

            // Noyau gaussien normalisé et demi-taille (portée du filtre autour du pixel).
            double[][] noyau = new FiltreGaussien(tailleFiltre).getFiltre();
            int rayon = tailleFiltre / 2;

            // Convolution : un passage du noyau sur chaque pixel de l'image.
            for (int y = 0; y < hauteur; y++) {
                for (int x = 0; x < largeur; x++) {
                    double sommeRouge = 0.0;
                    double sommeVert = 0.0;
                    double sommeBleu = 0.0;

                    // Parcours du voisinage couvert par le noyau.
                    for (int dy = -rayon; dy <= rayon; dy++) {
                        int yVoisin = restriction(y + dy, 0, hauteur - 1);
                        for (int dx = -rayon; dx <= rayon; dx++) {
                            int xVoisin = restriction(x + dx, 0, largeur - 1);

                            Color couleurVoisin = new Color(imageSource.getRGB(xVoisin, yVoisin));
                            double poids = noyau[dy + rayon][dx + rayon];

                            sommeRouge += couleurVoisin.getRed() * poids;
                            sommeVert  += couleurVoisin.getGreen() * poids;
                            sommeBleu  += couleurVoisin.getBlue() * poids;
                        }
                    }

                    int rouge = restriction((int) Math.round(sommeRouge), 0, 255);
                    int vert  = restriction((int) Math.round(sommeVert), 0, 255);
                    int bleu  = restriction((int) Math.round(sommeBleu), 0, 255);

                    imageFloutee.setRGB(x, y, new Color(rouge, vert, bleu).getRGB());
                }
            }

            ImageIO.write(imageFloutee, "png", new File(construireNomSortie(fichierSource)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Construit le chemin de sortie en remplaçant l'extension par le suffixe du flou.
     */
    private String construireNomSortie(File fichierSource) {
        String chemin = fichierSource.getPath();
        int pointExtension = chemin.lastIndexOf('.');
        String racine = (pointExtension > 0) ? chemin.substring(0, pointExtension) : chemin;
        return racine + "_FlouGaussien.png";
    }

    public BufferedImage flouterBufferedImage(BufferedImage imageSource) {
        int largeur = imageSource.getWidth();
        int hauteur = imageSource.getHeight();
        BufferedImage imageFloutee = new BufferedImage(largeur, hauteur, BufferedImage.TYPE_3BYTE_BGR);

        double[][] noyau = new FiltreGaussien(tailleFiltre).getFiltre();
        int rayon = tailleFiltre / 2;

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                double sommeRouge = 0.0;
                double sommeVert = 0.0;
                double sommeBleu = 0.0;

                for (int dy = -rayon; dy <= rayon; dy++) {
                    int yVoisin = restriction(y + dy, 0, hauteur - 1);
                    for (int dx = -rayon; dx <= rayon; dx++) {
                        int xVoisin = restriction(x + dx, 0, largeur - 1);

                        Color couleurVoisin = new Color(imageSource.getRGB(xVoisin, yVoisin));
                        double poids = noyau[dy + rayon][dx + rayon];

                        sommeRouge += couleurVoisin.getRed() * poids;
                        sommeVert  += couleurVoisin.getGreen() * poids;
                        sommeBleu  += couleurVoisin.getBlue() * poids;
                    }
                }

                int rouge = restriction((int) Math.round(sommeRouge), 0, 255);
                int vert  = restriction((int) Math.round(sommeVert), 0, 255);
                int bleu  = restriction((int) Math.round(sommeBleu), 0, 255);

                imageFloutee.setRGB(x, y, new Color(rouge, vert, bleu).getRGB());
            }
        }

        return imageFloutee;
    }

    /**
     * Restreint une valeur dans l'intervalle [min, max].
     */
    private static int restriction(int valeur, int min, int max) {
        if (valeur < min) return min;
        if (valeur > max) return max;
        return valeur;
    }
}