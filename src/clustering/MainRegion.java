package clustering;

import flou_image.FlouGaussien;
import outils.AffichageBiomes;
import outils.Etiquetage;
import outils.OutilCouleur;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainRegion {

    public static void main(String[] args) {
        try {
            String chemin = args.length > 0 ? args[0] : "images/NouvelleImage.png";
            File f = new File(chemin);
            BufferedImage imageOriginale = ImageIO.read(f);

            int largeur = imageOriginale.getWidth();
            int hauteur = imageOriginale.getHeight();

            // clustering par couleur pour identifier les biomes
            BufferedImage imageFloutee = new FlouGaussien(5).flouterBufferedImage(imageOriginale);
            int[][] descriptionCouleurs = OutilCouleur.imageVersDescription(imageFloutee);
            AlgoClustering algoKMeans = new KMeansClustering(10, 100);
            int[] clustersBiomes = algoKMeans.attribuerCluster(descriptionCouleurs);

            Etiquetage etiquetage = new Etiquetage();
            int nbBiomes = 10;

            new File("resultats_regions").mkdirs();

            // on extrait les positions des pixels et on applique DBScan sur ces positions pour trouver les régions
            for (int biome = 0; biome < nbBiomes; biome++) {
                int[] couleurMoyenne = OutilCouleur.calculerCouleurMoyenne(descriptionCouleurs, clustersBiomes, biome);
                String nomBiome = etiquetage.etiqueter(couleurMoyenne);

                // Compter les pixels de ce biome
                int nbPixelsBiome = 0;
                for (int i = 0; i < clustersBiomes.length; i++) {
                    if (clustersBiomes[i] == biome) nbPixelsBiome++;
                }

                if (nbPixelsBiome == 0) continue;

                // Construire la description positionnelle (x, y) pour ce biome
                int[][] positions = new int[nbPixelsBiome][2];
                int[] indexOriginaux = new int[nbPixelsBiome]; // mapping vers l'index pixel original
                int idx = 0;

                for (int y = 0; y < hauteur; y++) {
                    for (int x = 0; x < largeur; x++) {
                        int pixelIndex = y * largeur + x;
                        if (clustersBiomes[pixelIndex] == biome) {
                            positions[idx][0] = x;
                            positions[idx][1] = y;
                            indexOriginaux[idx] = pixelIndex;
                            idx++;
                        }
                    }
                }

                // DBScan pour trouver les régions
                // eps=5 : deux pixels sont voisins s'ils sont à moins de 5 pixels de distance
                AlgoClustering algoDBScan = new DBScanClustering(5, 10);
                int[] clustersRegions = algoDBScan.attribuerCluster(positions);

                // Trouver le nombre de régions (clusters positifs)
                int nbRegions = 0;
                for (int c : clustersRegions) {
                    if (c > nbRegions) nbRegions = c;
                }
                nbRegions++;

                System.out.println("Biome " + biome + " (" + nomBiome + ") : "
                        + nbPixelsBiome + " pixels, " + nbRegions + " région(s)");

                // Générer une image colorée par région pour ce biome
                BufferedImage imageRegions = creerImageRegions(imageOriginale, indexOriginaux, clustersRegions, nbRegions);

                String nomFichier = "resultats_regions/biome_" + biome + "_" + nomBiome.replace(" ", "_") + ".png";
                ImageIO.write(imageRegions, "png", new File(nomFichier));
            }

            System.out.println("Images des régions générées dans resultats_regions/");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Crée une image où chaque région spatiale d'un biome est colorée différemment
    private static BufferedImage creerImageRegions(
            BufferedImage imageOriginale,
            int[] indexOriginaux,
            int[] clustersRegions,
            int nbRegions
    ) {
        int largeur = imageOriginale.getWidth();
        int hauteur = imageOriginale.getHeight();

        // Fond grisé
        AffichageBiomes affichage = new AffichageBiomes();
        BufferedImage resultat = affichage.creerFondClair(imageOriginale, 75);

        // Palette de couleurs distinctes pour les régions
        Color[] palette = genererPalette(nbRegions);

        for (int i = 0; i < indexOriginaux.length; i++) {
            int pixelIndex = indexOriginaux[i];
            int x = pixelIndex % largeur;
            int y = pixelIndex / largeur;

            int region = clustersRegions[i];
            if (region >= 0) { // ignorer le bruit (region == -1)
                resultat.setRGB(x, y, palette[region % palette.length].getRGB());
            }
        }

        return resultat;
    }

    private static Color[] genererPalette(int n) {
        if (n == 0) return new Color[]{Color.RED};
        Color[] palette = new Color[Math.max(n, 1)];
        for (int i = 0; i < palette.length; i++) {
            float teinte = (float) i / palette.length;
            palette[i] = Color.getHSBColor(teinte, 0.85f, 0.9f);
        }
        return palette;
    }
}
