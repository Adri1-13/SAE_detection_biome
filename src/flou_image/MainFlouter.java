package flou_image;

public class MainFlouter {
    public static void main(String[] args) {

        FlouterImage flouterImage = new FlouterImage(new FlouMoyenne());

        flouterImage.flouterImage("images/Planete 1.jpg");

    }
}
