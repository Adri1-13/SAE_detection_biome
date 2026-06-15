package outils;

public class Etiquetage {

    private int[][] referencesCouleursBiomes = {
            {71,70,61}, // Tundra
            {43,50,35}, // Taïga
            {59,66,43}, // Forêt tempérée
            {46,64,34}, // Forêt tropical
            {84,106,70}, // Savane
            {104,95,82}, // Prairie
            {152,140,120}, // Désert
            {200,200,200}, // Glacier
            {49,83,100}, // Eau peu profonde
            {12,31,47}  // Eau profonde
    };

    private String[] nomsBiomes = {
            "Tundra",
            "Taïga",
            "Forêt tempérée",
            "Forêt tropical",
            "Savane",
            "Prairie",
            "Désert",
            "Glacier",
            "Eau peu profonde",
            "Eau profonde"
    };

    // fonction qui calcule la distance entre deux couleurs en utilisant la distance euclidienne (à vérifier si c'est bien ce calcul de distance qu'il faut utiliser dans notre cas)
    // permet de comparer la couleur d'un pixel avec les couleurs de référence des biomes pour déterminer à quel biome appartient le pixel
    public String etiqueter(int c) {
        int[] couleurCluster = OutilCouleur.getTabColor(c);

        int meilleurIndex = 0;
        double meilleureDistance = distanceCouleur(couleurCluster, referencesCouleursBiomes[0]);

        for (int i = 1; i < referencesCouleursBiomes.length; i++) {
            double distance = distanceCouleur(couleurCluster, referencesCouleursBiomes[i]);

            if (distance < meilleureDistance) {
                meilleureDistance = distance;
                meilleurIndex = i;
            }

        }

        return nomsBiomes[meilleurIndex];

    }

    public double distanceCouleur(int[] couleur1, int[] couleur2) {
        int distanteRouge = couleur1[0] - couleur2[0];
        int distanteVert = couleur1[1] - couleur2[1];
        int distanteBleu = couleur1[2] - couleur2[2];

        return Math.sqrt((distanteRouge * distanteRouge) + (distanteVert * distanteVert) + (distanteBleu * distanteBleu));
    }
}
