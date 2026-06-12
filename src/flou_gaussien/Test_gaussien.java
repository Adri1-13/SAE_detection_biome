package flou_gaussien;

public class Test_gaussien {

    public static void main(String[] args) {
        FiltreGaussien filtre = new FiltreGaussien(3);
        for (int i = 0; i < filtre.filtre.length; i++) {
            for (int j = 0; j < filtre.filtre[i].length; j++) {
                System.out.print(filtre.filtre[i][j] + " ");
            }
            System.out.println();
        }
    }
}