package flou_image;

public class MainFlouter {
    public static void main(String[] args) {

        // Utiliser le flou gaussien par défaut (taille 5)
        FlouterImage flouterImage = new FlouterImage(new FlouGaussien());

        flouterImage.flouterImage("images/Planete 1.jpg");

    }
}
