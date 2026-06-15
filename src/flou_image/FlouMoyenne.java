package flou_image;

import outils.OutilCouleur;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FlouMoyenne implements ProcessFlou {

    private int longueurCarres;

    public FlouMoyenne() {
        this.longueurCarres = 3;
    }

    public FlouMoyenne(int longueurCarres) {
        this.longueurCarres = longueurCarres;
    }

    @Override
    public void flouterImage(String src) {
        try {
            File f = new File(src);
            BufferedImage bufferedImage = ImageIO.read(f);
            String newName = f.getPath().split(".jpg")[0] + "_FlouMoyenne.png" ;
            BufferedImage newImage = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            int ecartDepuisCentre = this.longueurCarres/2;
            for (int x = ecartDepuisCentre; x < bufferedImage.getWidth() - ecartDepuisCentre; x++) {
                for (int y = ecartDepuisCentre; y < bufferedImage.getHeight() - ecartDepuisCentre; y++) {

                    ArrayList<int[]> couleurs = new ArrayList<>();
                    for (int i = x - ecartDepuisCentre; i <= x + ecartDepuisCentre; i++) {
                        for (int j = y - ecartDepuisCentre; j <= y + ecartDepuisCentre; j++) {
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

                    newImage.setRGB(x, y, new Color(couleurFinale[0], couleurFinale[1], couleurFinale[2]).getRGB());
                }
            }
            File f2 = new File(newName);
            ImageIO.write(newImage, "png", f2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
