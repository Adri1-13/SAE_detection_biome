package clustering;

import java.util.Random;

public class KMeansClustering implements AlgoClustering {

    private int nbBarycentre;
    private int maxIterations;
    private Random random;

    public KMeansClustering(int nb, int maxIterations) {
        this.nbBarycentre = nb;
        this.maxIterations = maxIterations;
        this.random = new Random();
    }

    @Override
    public int[] attribuerCluster(int[][] couleurs) {
        if (couleurs == null || couleurs.length == 0) { //si pas de couleur dans le tableau
            return new int[0];
        }

        int n = couleurs.length;
        int dimensions = 3; // --> R, G et B
        int[] clusters = new int[n];

        // Centres initiaux : on prend nbBaryton couleurs au hasard
        double[][] centres = new double[nbBarycentre][dimensions];
        for (int i = 0; i < nbBarycentre; i++) {
            int index = random.nextInt(n); // index compris dans la taille du tableau de couleurs

            // copie de la couleur d'une des couleurs de la liste dans un des centres (barycentre)
            centres[i][0] = couleurs[index][0];
            centres[i][1] = couleurs[index][1];
            centres[i][2] = couleurs[index][2];
        }

        boolean changed = true; // flag pour tester si on arrive à un équilibre
        int iteration = 0;

        while (changed && iteration < maxIterations) {
            changed = false;

            // ##################################################
            // Attribution des couleurs au cluster le plus proche
            // ##################################################
            for (int i = 0; i < n; i++) { //parcours du tableau des couleurs
                int meilleurCluster = 0;
                double meilleureDistance = distance(couleurs[i], centres[0]);

                for (int c = 1; c < nbBarycentre; c++) { // c=1 car première comparaison effectuée pour initialiser la meilleureDistance
                    double dist = distance(couleurs[i], centres[c]);
                    if (dist < meilleureDistance) {
                        meilleureDistance = dist;
                        meilleurCluster = c;
                    }
                }

                if (clusters[i] != meilleurCluster) { // changement depuis la dernière boucle ?
                    clusters[i] = meilleurCluster;
                    changed = true;
                }
            }

            //#####################
            // Recalcul des centres
            //#####################
            double[][] nouveauxCentres = new double[nbBarycentre][dimensions];
            int[] counts = new int[nbBarycentre];

            for (int i = 0; i < n; i++) { // parcours du tableau de couleur
                int cluster = clusters[i];
                counts[cluster]++; // incrémentation du compteur de cluster

                //ajout de la valeur de la couleur au nouveau barycentre (première partie de la moyenne)
                nouveauxCentres[cluster][0] += couleurs[i][0];
                nouveauxCentres[cluster][1] += couleurs[i][1];
                nouveauxCentres[cluster][2] += couleurs[i][2];
            }

            for (int i = 0; i < nbBarycentre; i++) {
                if (counts[i] == 0) { // cluster vide : on le réinitialise avec une couleur aléatoire
                    int index = random.nextInt(n);

                    centres[i][0] = couleurs[index][0];
                    centres[i][1] = couleurs[index][1];
                    centres[i][2] = couleurs[index][2];

                } else { // cluster non vide : on finalise la moyenne
                    //division (deuxième partie de la moyenne)
                    centres[i][0] = nouveauxCentres[i][0] / counts[i];
                    centres[i][1] = nouveauxCentres[i][1] / counts[i];
                    centres[i][2] = nouveauxCentres[i][2] / counts[i];

                }
            }

            iteration++;
        }

        return clusters;
    }

    private double distance(int[] couleurA, double[] couleurB) {
        return Math.sqrt(Math.pow(couleurA[0] - couleurB[0], 2) + Math.pow(couleurA[1] - couleurB[1], 2) + Math.pow(couleurA[2] - couleurB[2], 2));
    }
}