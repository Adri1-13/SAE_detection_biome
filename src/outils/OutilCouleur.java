package outils;

public class OutilCouleur {

    public static int[] getTabColor(int c) {
        int blue = c & 0xFF;
        int green = (c & 0xFF00) >> 8;
        int red = (c & 0xFF0000) >> 16;
        return new int[]{red, green, blue};
    }

}
