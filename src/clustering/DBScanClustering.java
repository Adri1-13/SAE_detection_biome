package clustering;

import java.util.ArrayList;
import java.util.Arrays;

public class DBScanClustering implements AlgoClustering {
    // Algorithme de clustering DBSCAN --> fonctionne de ce que j'ai compris comme une propagation de clusters à partir core points (qui contiennent plus de points que  minPts dans leur rayon) et de leurs voisins, si les voisins sont eux-mêmes des core points, on continue la propagation, par contre, les points qui ne sont pas atteints par cette propagation sont considérés comme du bruit car ils sont trop extérieur donc trop distant dans l'esplace


    /*
    Fonctionnement de l'algorithme DBSCAN de ce que j'ai compris de l'algo du cours :
    1. Ici comme on est sur une image, que ce soit des couleurs de pixels donc 3 dimensions ou des postions de pixels donc 2, on parcourt tous les points de l'image en commençant par le le premier pixel
    Donc à partir de ce premier pixel on regarde tous ses voisins, ce qui veut donc dire qu'on regarde qui est proche de ce premierr pixel
    2. Si ce premier pixel n'a pas assez de points (dans l'espace à 2 ou 3 dimensions) autour de lui alors c'est du bruit mais peut-être que ça va changer après
    Mais si ce premier pixel a assez de points autour de lui, alors on sait qu'on peut directement déclencher un nouveau cluster (premier au début) dans lequel on va venir ajouter tous les points qui sont assez proches
    3. On regarde donc ensuite tous les points voisins de ce premier pixel, si ces voisins sont assez proches mais qu'ils n'ont pas de points assez proches d'eux, alors ce sont des border points et on les ajoute au cluster mais on ne va pas propager à partir d'eux
    4. Par contre, si ces voisins sont eux-mêmes des core points alors la on peut propager depuis eux et donc ajouter tous leurs voisins à la liste des points à traiter pour le cluster courant

     */

    private int eps; // rayon de voisinage, correspond à la distance maximale entre deux points pour qu'ils soient considérés comme voisins
    private int minPts; // nombre minimum de points dans le voisinage pour qu'un point soit considéré comme un core point
    private static final int NON_CLASSE = -2;
    private static final int NOISE_POINT = -1;

    public DBScanClustering(int eps, int minPts) {
        this.eps = eps;
        this.minPts = minPts;
    }

    public int[] attribuerCluster(int[][] description) {
        int [] clusters = new int[description.length]; // clusters[i] contient le numéro du cluster du point description[i]
        boolean [] pixelsTraites = new boolean[description.length]; // indique si DBSCAN a déjà analysé les voisins de ce point

        // initialisation des clusters et des pixels traités
        for (int i = 0; i < description.length; i++) {
            clusters[i] = NON_CLASSE;
            pixelsTraites[i] = false;
        }

        // on commence à parcourir par le tout premier point, ici ce n'est pas comme dans KMeans où on choisit des points aléatoires pour initialiser les clusters parce que on parcourt tous les points et on va créer un cluster à partir de chaque point qui n'a pas encore été traité
        int clusterCourant = 0;

        // parcours de tous les points
        for (int i = 0; i < description.length; i++) {
            // si le pixel sur lequel on est maintenant n'a pas encore été pris dans les clusters on regarde ses voisins pour voir si on peut créer un cluster à partir de lui (donc si c'est un core point)
            if (!pixelsTraites[i]) {
                pixelsTraites[i] = true;
                ArrayList<Integer> voisins = getVoisins(description, i); // getVoisins renvoie la liste des points voisins du point en cours (donc les points qui sont couverts par le rayon eps)

                if (voisins.size() >= minPts) {
                    // si on sait que le point en cours est un core point et on va créer un cluster à partir de lui et de ses voisins
                    etendreCluster(description, clusters, pixelsTraites, i, voisins, clusterCourant);
                    // à partir de ce point on est sur que tous les points qui sont dans le cluster ont été traités donc on peut passer au point suivant et créer un nouveau cluster
                    clusterCourant++;
                } else {
                    clusters[i] = NOISE_POINT;

                }
            }
        }

        return clusters;
    }

    // fonction qui va étendre le cluster à partir du point de départ et de ses voisins, en ajoutant tous les core points et leurs voisins à la liste des voisins à traiter jusqu'à ce qu'on ait parcouru tous les points du cluster
    private void etendreCluster(int[][] description, int[] clusters, boolean[] pixelsTraites, int indexDepart, ArrayList<Integer> voisins, int clusterCourant) {
        clusters[indexDepart] = clusterCourant; // on ajoute d'office le point duquel on est parti dans le cluster courant, on va ensuite ajouter tous ses voisins qui sont eux-mêmes des core points et leurs voisins à leur tour etc jusqu'à ce qu'on ait parcouru tous les points du cluster

        // on parcourt tous les voisins du point de départ pour voir si ce sont eux-mêmes des core points et si oui on va ajouter leurs voisins à la liste des voisins à traiter
        for (int i = 0; i < voisins.size(); i++) {
            // on gère les border points dans la condition suivante, si le point voisin n'a pas encore été traité, on va le traiter et voir s'il est un core point ou pas, si il est un core point on va ajouter ses voisins à la liste des voisins à traiter puisqu'il faut donc propager depuis ce nouveau core point, sinon on ne fait rien parce que c'est juste un border point
            if (!pixelsTraites[voisins.get(i)]) {
                pixelsTraites[voisins.get(i)] = true;
                ArrayList<Integer> voisinsDuVoisin = getVoisins(description, voisins.get(i));

                if (voisinsDuVoisin.size() >= minPts) { // si le point voisin est lui-même un core point on ajoute  ses voisins à la liste des voisins à traiter pour le cluster courant
                    ajouterNouveauxVoisins(voisins, voisinsDuVoisin);
                }
            }

            if (clusters[voisins.get(i)] == NON_CLASSE || clusters[voisins.get(i)] == NOISE_POINT) { // le fait de ne pas être traité n'est pas la même chose que le fait de ne pas être classé dans un cluster parce que un point peut être traite mais ne pas être classé dans un cluster s'il a été mis comme bruit, donc on ajoute le point à notre cluster courant si il n'est pas encore classé dans un cluster ou s'il est considéré comme du bruit
                clusters[voisins.get(i)] = clusterCourant;
            }
        }
    }

    // fonction correspondant à l'ajout de nouveaux voisins à la liste des voisins à traiter, soit l'instruction suivante dans l'algo du cours "Vn = Vn + Vi"
    private void ajouterNouveauxVoisins(ArrayList<Integer> voisins, ArrayList<Integer> voisinsAAjouter) {
        for (int voisin : voisinsAAjouter) {
            if (!voisins.contains(voisin)) {
                voisins.add(voisin);
            }
        }
    }

    private ArrayList<Integer> getVoisins(int[][] description, int index) {
        ArrayList<Integer> voisins = new ArrayList<>();
        for (int i = 0; i < description.length; i++) {
            if (i != index && distance(description[index], description[i]) <= eps) { // description[index] est le point de référence, le Xn comme dans l'algo du cours, description[i] est le point à comparer puisqu'on se déplace dans l'espace autour du point de référence, donc si la distance entre les deux points est inférieure ou égale à eps, alors on considère que le point i est un voisin du point index
                voisins.add(i);
            }
        }
        return voisins;
    }

    private double distance(int[] point1, int[] point2) {
        double somme = 0;

        for (int i = 0; i < point1.length; i++) {
            int difference = point1[i] - point2[i];
            somme += difference * difference;
        }

        return Math.sqrt(somme);
    }

}
