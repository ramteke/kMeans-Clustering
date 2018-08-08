package machinelearning.basicKMeans;

import java.util.List;

/**
 * Created by skynet on 05/08/18
 */
public class Cluster {
    public double [] centriod;
    int clusterNo = -1;
    public double errorDistance = 0.0;

    public Cluster(int id, List<double[]> features, int row) {
        this.clusterNo = id;
        centriod = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            centriod[i] = features.get(i)[row];
        }
    }

    public Cluster(int id, int numCentriods) {
        this.clusterNo = id;
        centriod = new double[numCentriods];
        for (int i = 0; i < numCentriods; i++) {
            centriod[i] = 0.0;
        }
    }
}