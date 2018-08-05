package machinelearning.basicKMeans;

import java.util.List;

/**
 * Created by skynet on 05/08/18
 */
public class Cluster {
    double [] centriod;
    int clusterNo = -1;

    public Cluster(int id, List<double[]> features, int row) {
        this.clusterNo = id;
        centriod = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            centriod[i] = features.get(i)[row];
        }
    }

    public Cluster(int id, List<double[]> features) {
        this.clusterNo = id;
        centriod = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            centriod[i] = 0.0;
        }
    }
}