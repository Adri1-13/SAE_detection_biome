package flou_image;

import outils.OutilCouleur;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FlouMoyenne implements ProcessFlou {

    @Override
    public void flouterImage(String src) {
        try {
            File f = new File(src);
            BufferedImage bufferedImage = ImageIO.read(f);
            String newName = f.getPath().split(".jpg")[0] + "_FlouMoyenne.png" ;
            BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            for (int x = 0; x < bufferedImage.getWidth()/3; x++) {
                for (int y = 0; y < bufferedImage.getHeight()/3; y++) {

                    //x;y --> division en carré de 3x3

                    ArrayList<int[]> couleurs = new ArrayList<>();
                    // longueur et largeur divisés par 3 => pour récupérer des blocs de 3x3 pixels
                    for (int i = 3 * x; i < 3 * x + 3; i++) {
                        for (int j = 3 * y; j < 3 * y + 3; j++) {
                            //i;j --> un pixel de l'image
                            int[] rgb = OutilCouleur.getTabColor(bufferedImage.getRGB(i, j));
                            couleurs.add(rgb);
                        }
                    }

                    int[] couleurFinale = new int[3];
                    for (int[] couleur : couleurs) { // partie "addition" de la moyenne
                        couleurFinale[0] += couleur[0];
                        couleurFinale[1] += couleur[1];
                        couleurFinale[2] += couleur[2];
                    }

                    // partie division de la moyenne
                    couleurFinale[0] /= couleurs.size();
                    couleurFinale[1] /= couleurs.size();
                    couleurFinale[2] /= couleurs.size();

                    for (int i = 3 * x; i < 3 * x + 3; i++) { //parcours pour réécrire la couleur de chaque pixel du carré
                        for (int j = 3 * y; j < 3 * y + 3; j++) {
                            newImage.setRGB(i, j, new Color(couleurFinale[0], couleurFinale[1], couleurFinale[2]).getRGB());
                        }
                    }
                }
            }
            File f2 = new File(newName);
            ImageIO.write(newImage, "png", f2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
