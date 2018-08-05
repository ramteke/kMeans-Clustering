package machinelearning.bisectingKmeans;

import machinelearning.basicKMeans.Cluster;
import machinelearning.basicKMeans.KMeans;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Created by skynet on 05/08/18
 */
public class Bisecting {
    //We will use k-means internally

    public int[] BisectingKMeans(KMeans kmeans, int finalClusterCount, List<double[]> features) {

        //We start with 1 cluster
        Cluster clusters[] = new Cluster[1];
        clusters[0] = new Cluster(0, features);
        int [] clusterAssignment = new int[features.get(0).length]; //Assign all to 1st cluster to start with
        for (int i = 0; i < clusterAssignment.length; i++) {
            clusterAssignment[i] = 0;
        }
        kmeans.reCalculateClusterCentriods(clusters, features, 1, clusterAssignment);


        return null;
    }


    public static void main(String args[]) throws Exception {
        Bisecting client = new Bisecting();
        KMeans kmeans = new KMeans();
        int K = 3;


        File file = new File("src//main//java//machinelearning//basicKMeans//input.txt");

        //Step1: Read Features
        List<String> lines = FileUtils.readLines(file,"UTF-8");
        Map<Integer, List<Integer>> featureMap = kmeans.getFeatures(lines);

        //Step2: Normalize Features
        List<double[]> features = kmeans.normalizeFeatures(featureMap);

        int clusterAssignment [] = client.BisectingKMeans(kmeans, K, features);


        for (int i = 0; i < clusterAssignment.length; i++) {
            System.out.println(lines.get(i) + " ==> " + " Cluster-" + clusterAssignment[i]);
        }

    }

}
