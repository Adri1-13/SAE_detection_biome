package outils;

import java.awt.image.BufferedImage;

public class OutilCouleur {

    public static int[] getTabColor(int c) {
        int blue = c & 0xFF;
        int green = (c & 0xFF00) >> 8;
        int red = (c & 0xFF0000) >> 16;
        return new int[]{red, green, blue};
    }

    public static int[][] imageVersDescription(BufferedImage image) {
        int[][] description = new int[image.getWidth() * image.getHeight()][3];

        int i = 0;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                description[i] = getTabColor(image.getRGB(x, y));
                i++;
            }
        }

        return description;
    }

    public static int[] calculerCouleurMoyenne(
            int[][] description,
            int[] clusters,
            int clusterRecherche
    ) {
        int[] somme = new int[description[0].length];
        int compteur = 0;

        for (int i = 0; i < description.length; i++) {
            if (clusters[i] == clusterRecherche) {
                for (int d = 0; d < description[i].length; d++) {
                    somme[d] += description[i][d];
                }
                compteur++;
            }
        }

        if (compteur == 0) {
            return new int[description[0].length];
        }

        int[] moyenne = new int[description[0].length];

        for (int d = 0; d < moyenne.length; d++) {
            moyenne[d] = somme[d] / compteur;
        }

        return moyenne;
    }
}