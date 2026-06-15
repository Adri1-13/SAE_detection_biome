package flou_image;

public class MainFlouter {
    public static void main(String[] args) {
        FlouterImage flouterImage1 = new FlouterImage(new FlouMoyenne(9));

        flouterImage1.flouterImage("images/fleur.jpg");

        // Utiliser le flou gaussien par défaut (taille 5)
        FlouterImage flouterImage2 = new FlouterImage(new FlouGaussien());

        flouterImage2.flouterImage("images/fleur.jpg");

    }
}
