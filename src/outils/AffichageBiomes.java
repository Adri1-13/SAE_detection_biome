package outils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class AffichageBiomes {

    public BufferedImage creerFondClair(BufferedImage image, int pourcentage) {
        int largeur = image.getWidth();
        int hauteur = image.getHeight();

        BufferedImage fond = new BufferedImage(
                largeur,
                hauteur,
                BufferedImage.TYPE_INT_RGB
        );

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                int rgb = image.getRGB(x, y);

                int[] tabColor = OutilCouleur.getTabColor(rgb);

                int r = tabColor[0];
                int g = tabColor[1];
                int b = tabColor[2];

                r = eclaircir(r, pourcentage);
                g = eclaircir(g, pourcentage);
                b = eclaircir(b, pourcentage);

                int nouveauRGB = new Color(r, g, b).getRGB();
                fond.setRGB(x, y, nouveauRGB);
            }
        }

        return fond;
    }

    private int eclaircir(int valeur, int pourcentage) {
        return Math.round(valeur + (pourcentage / 100.0f) * (255 - valeur));
    }

    public BufferedImage afficherBiome(
            BufferedImage imageOriginale,
            int[] clusters,
            int clusterAafficher,
            int pourcentageFond
    ) {
        int largeur = imageOriginale.getWidth();
        int hauteur = imageOriginale.getHeight();

        BufferedImage resultat = creerFondClair(imageOriginale, pourcentageFond);

        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                int index = y * largeur + x;

                if (clusters[index] == clusterAafficher) {
                    resultat.setRGB(x, y, imageOriginale.getRGB(x, y));
                }
            }
        }

        return resultat;
    }
}