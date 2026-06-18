package outils;

import clustering.AlgoClustering;
import clustering.DBScanClustering;
import clustering.KMeansClustering;
import flou_image.FlouGaussien;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainBiomes {

    public static void main(String[] args) {
        try {
            String chemin = args.length > 0 ? args[0] : "images/NouvelleImage.png";
            File f = new File(chemin);
            BufferedImage imageOriginale = ImageIO.read(f);

            int nbClusters = 10;
            int maxIterations = 100;

            BufferedImage imageFloutee = new FlouGaussien(5).flouterBufferedImage(imageOriginale);
            int[][] description = OutilCouleur.imageVersDescription(imageFloutee);

            AlgoClustering algo = new KMeansClustering(nbClusters, maxIterations);
//            AlgoClustering algo = new DBScanClustering(15, 10);
            int[] clusters = algo.attribuerCluster(description);

            AffichageBiomes affichage = new AffichageBiomes();
            Etiquetage etiquetage = new Etiquetage();

            new File("resultats").mkdirs();

            BufferedImage fond = affichage.creerFondClair(
                    imageOriginale,
                    75
            );

            ImageIO.write(
                    fond,
                    "png",
                    new File("resultats/fond.png")
            );

            for (int cluster = 0; cluster < nbClusters; cluster++) {
                int[] couleurMoyenne = OutilCouleur.calculerCouleurMoyenne(
                        description,
                        clusters,
                        cluster
                );

                String nomBiome = etiquetage.etiqueter(couleurMoyenne);

                BufferedImage imageBiome = affichage.afficherBiome(
                        imageOriginale,
                        clusters,
                        cluster,
                        75
                );

                String nomFichier = "resultats/biome_" + cluster + "_" + nomBiome + ".png";
                ImageIO.write(imageBiome, "png", new File(nomFichier));

                System.out.println("Cluster " + cluster + " -> " + nomBiome
                        + " | couleur moyenne : RGB("
                        + couleurMoyenne[0] + ", "
                        + couleurMoyenne[1] + ", "
                        + couleurMoyenne[2] + ")");
            }

            System.out.println("Images générées dans le dossier resultats/");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}