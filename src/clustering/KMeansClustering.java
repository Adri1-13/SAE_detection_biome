package clustering;

import java.util.Random;

public class KMeansClustering implements AlgoClustering {

    private int nbBarycentre;
    private int maxIterations;
    private Random random = new Random();

    public KMeansClustering(int nb, int maxIterations) {
        this.nbBarycentre = nb;
        this.maxIterations = maxIterations;
    }

    @Override
    public int[] attribuerCluster(int[][] description) {
        if (description == null || description.length == 0) {
            return new int[0];
        }

        int tailleListe = description.length;
        int dimensions = description[0].length; // mieux que 3 en dur (pour les couleurs) --> plus de cas d'usage
        int[] clusters = new int[tailleListe];

        // Initialisation des centres
        double[][] centres = new double[nbBarycentre][dimensions];
        initialiserCentres(description, centres);

        boolean changed = true;
        int i = 0;

        while (changed && i < maxIterations) {
            changed = false;

            changed = agglomererValeursDansClusters(description, centres, clusters);

            recalculerCentres(description, centres, clusters);

            i++;
        }

        return clusters;
    }


    private boolean agglomererValeursDansClusters(int[][] description, double[][] centres, int[] clusters) {
        boolean changed = false;

        for (int i = 0; i < description.length; i++) { //parcours de la liste des valeurs (couleur des pixels ici)
            int meilleurCluster = 0;
            double meilleureDistance = distance(description[i], centres[0]);

            for (int c = 1; c < centres.length; c++) { // c=1 --> première comparaison déjà effectuée à l'initialisation de la meilleure distance
                double dist = distance(description[i], centres[c]);
                if (dist < meilleureDistance) {
                    meilleureDistance = dist;
                    meilleurCluster = c;
                }
            }

            if (clusters[i] != meilleurCluster) { //si aucun point ne change de cluster --> changed sera false
                clusters[i] = meilleurCluster;
                changed = true;
            }
        }

        return changed;
    }


    private void recalculerCentres(int[][] description, double[][] centres, int[] clusters) {
        int dimensions = description[0].length;
        double[][] nouveauxCentres = new double[nbBarycentre][dimensions];
        int[] counts = new int[nbBarycentre];

        // Somme des coordonnées de chaque cluster
        for (int i = 0; i < description.length; i++) { //parcours de tous les points
            int cluster = clusters[i];
            counts[cluster]++; // si le cluster n'a pas de points --> redéfini aléatoirement parmi les valeurs
            for (int d = 0; d < dimensions; d++) { //parcours des valeurs de la liste (3 --> RGB)
                nouveauxCentres[cluster][d] += description[i][d]; // ajout de toutes les valeurs (dans chaque dimension) --> preparation pour la moyenne (première étape : addition ; deuxième étape : division)
            }
        }

        // Moyenne pour obtenir le nouveau centre
        for (int c = 0; c < nbBarycentre; c++) {
            if (counts[c] == 0) {
                // Cluster vide : on réinitialise avec une couleur aléatoire
                int index = random.nextInt(description.length);
                for (int d = 0; d < dimensions; d++) {
                    centres[c][d] = description[index][d];
                }
            } else {
                for (int d = 0; d < dimensions; d++) {
                    centres[c][d] = nouveauxCentres[c][d] / counts[c]; //partie deux de la moyenne --> division
                }
            }
        }
    }


    private void initialiserCentres(int[][] description, double[][] centres) {
        for (int i = 0; i < nbBarycentre; i++) {
            int index = random.nextInt(description.length);
            for (int d = 0; d < description[index].length; d++) {
                centres[i][d] = description[index][d];
            }
        }
    }

    private double distance(int[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        } // --> somme du carré de la différence entre chaque dimension --> (x1 - x2)² + (y1 - y2)² + (z1 + z2)² + ...
        return Math.sqrt(sum);
    }
}