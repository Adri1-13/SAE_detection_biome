package clustering;

import outils.OutilCouleur;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainClustering {
    public static void main(String[] args) {
        try {
            File f = new File("images/Planete 1.jpg");
            BufferedImage bufferedImage = ImageIO.read(f);
            int[][] couleurs = new int[bufferedImage.getWidth()*bufferedImage.getHeight()][3];
            int i = 0;
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    couleurs[i] = OutilCouleur.getTabColor(bufferedImage.getRGB(x, y));
                    i++;
                }
            }
            AlgoClustering algo = new KMeansClustering(5, 10);
            int[] clusters = algo.attribuerCluster(couleurs);

            for (i=0; i<clusters.length; i++) {
                System.out.println("pixel: "+i+" ; cluster: "+clusters[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
