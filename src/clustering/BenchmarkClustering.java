package clustering;

import static clustering.DBScanClustering.NOISE_POINT;

import flou_image.FlouGaussien;
import outils.OutilCouleur;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BenchmarkClustering {

    public static void main(String[] args) throws IOException {
        String chemin = args.length > 0 ? args[0] : "images/NouvelleImage.png";

        BufferedImage image = ImageIO.read(new File(chemin));
        BufferedImage imageFloutee = new FlouGaussien(5).flouterBufferedImage(image);

        int[][] descriptionComplete = OutilCouleur.imageVersDescription(imageFloutee);
        int[][] descriptionEchantillon = echantillonnerImage(imageFloutee);

        System.out.println("algo;parametres;nbPoints;tempsMs;nbClusters;nbBruit");

        for (int k : new int[]{5, 8, 10, 12}) {
            System.out.println("KMeans; k=" + k + ",maxIter=100;" + descriptionComplete.length);
            tester("KMeans",new KMeansClustering(k, 100),descriptionComplete);
        }

        for (int eps : new int[]{8, 15, 25, 40}) {
            for (int minPts : new int[]{5, 10, 30}) {
                System.out.println("DBSCAN couleur;eps=" + eps + ",minPts=" + minPts + ",echantillon=1/4;" + descriptionEchantillon.length);
                tester("DBSCAN couleur",new DBScanClustering(eps, minPts),descriptionEchantillon);
            }
        }
    }

    private static void tester(String nomAlgo, AlgoClustering algo, int[][] description) {
        long debut = System.nanoTime();
        int[] clusters = algo.attribuerCluster(description);
        long fin = System.nanoTime();

        double tempsMs = (fin - debut) / 1_000_000.0;

        int nbClusters = compterClusters(clusters);
        int nbBruit = compterBruit(clusters);

        System.out.println(nomAlgo + " ;" + description.length + " ;" + tempsMs + " ms ;" + nbClusters + "clusters ;" + nbBruit + " bruits");
    }

    private static int compterClusters(int[] clusters) {
        int nbClusters = 0;

        for (int cluster : clusters) {
            if (cluster + 1 > nbClusters) {
                nbClusters = cluster + 1;
            }
        }

        return nbClusters;
    }

    private static int compterBruit(int[] clusters) {
        int nbBruit = 0;

        for (int cluster : clusters) {
            if (cluster == NOISE_POINT) {
                nbBruit++;
            }
        }

        return nbBruit;
    }

    private static int[][] echantillonnerImage(BufferedImage image) {
        ArrayList<int[]> points = new ArrayList<>();

        for (int x = 0; x < image.getWidth(); x += 4) {
            for (int y = 0; y < image.getHeight(); y += 4) {
                points.add(OutilCouleur.getTabColor(image.getRGB(x, y)));
            }
        }

         int[][] result = new int[points.size()][3];
         for (int i = 0; i < points.size(); i++) {
             result[i] = points.get(i);
         }
         return result;
    }
}