package numerical.basicKMeans;
/**
 * Created by skynet on 05/08/18.
 */

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

class Cluster {
    double [] centriod;
    int clusterNo = -1;

    public Cluster(int id, List<double[]> features, int row) {
        this.clusterNo = id;
        centriod = new double[features.size()];
        for (int i = 0; i < features.size(); i++) {
            centriod[i] = features.get(i)[row];
        }
    }
}

public class KMeans {


    //https://en.wikipedia.org/wiki/Euclidean_distance
    public double euclideanDistance(List<double[]> features, int row, Cluster [] clusters, int clusterIndex) {
        double diffs[] = new double[features.size()];

        for (int i = 0; i < features.size(); i++) {
            diffs[i] =  features.get(i)[row] - clusters[clusterIndex].centriod[i];
        }

        double SUM = 0.0;
        for ( double diff : diffs) {
            SUM = SUM + (diff*diff);
        }

        return  Math.sqrt(SUM);

    }

    public int[] kMeans(int K, List<double[]> features) {
        Cluster clusters [] = new Cluster[K];

        //STEP3: Create Initial Cluster Centroid
        for (int index = 0; index < K; index++) {
            clusters[index] = new Cluster(index, features, index);
        }

        final int numRows = features.get(0).length;
        int clusterAssignment [] = new int[numRows];
        int oldClusterAssignment [] = null;

        boolean continueFlag = true;
        while ( continueFlag ) {

            //STEP4: Calculate distance for each point from Centroid
            for (int i = 0; i < numRows; i++) {
                double minDistance =Double.MAX_VALUE;
                int minDistanceClusterIndex = -1;
                for (int index = 0; index < clusters.length; index++) {
                    double distance = euclideanDistance(features, i, clusters, index);
                    if ( distance < minDistance ) {
                        minDistanceClusterIndex = index;
                        minDistance = distance;
                    }
                }
                clusterAssignment[i] = minDistanceClusterIndex;
            }

            //STEP5: Check if cluster assignment has changed or not
            if ( oldClusterAssignment == null) {
                oldClusterAssignment = new int[clusterAssignment.length];

                for (int i = 0; i < clusterAssignment.length; i++) {
                    oldClusterAssignment[i] = clusterAssignment[i];
                }
            } else {
                int changedCount = 0;
                for (int i = 0; i < clusterAssignment.length; i++) {
                    if ( oldClusterAssignment[i] != clusterAssignment[i] ) {
                        changedCount++;
                    }
                    oldClusterAssignment[i] = clusterAssignment[i];
                }
                if ( changedCount == 0) {
                    continueFlag = false;
                }
            }

            //STEP6: Re-Calculate Cluster Centroid using mean of cluster values
            int clusterMemberCount [] = new int[K];
            for ( int i = 0; i < clusterMemberCount.length; i++) {
                clusterMemberCount[i] = 0;
                for (int featureIndex = 0; featureIndex < features.size(); featureIndex++) {
                    clusters[i].centriod[featureIndex] = 0.0;
                }
            }

            for ( int i = 0; i < numRows; i++) {
                int clusterIndex = clusterAssignment[i];
                clusterMemberCount[clusterIndex] = clusterMemberCount[clusterIndex] + 1;

                for (int featureIndex = 0; featureIndex < features.size(); featureIndex++) {
                    clusters[clusterIndex].centriod[featureIndex] += features.get(featureIndex)[i];
                }
            }
            for ( int i = 0; i < clusterMemberCount.length; i++) {
                    for (int j = 0; j < clusters[i].centriod.length; j++) {
                        clusters[i].centriod[j] = clusters[i].centriod[j] / (clusterMemberCount[i] * 1.0);
                    }

            }

            for(int i = 0; i < numRows; i++) {
                System.out.print(" " + clusterAssignment[i]);
            }
            System.out.println("");




        }
        return clusterAssignment;
    }

    public List<double[]> normalizeFeatures(Map<Integer, List<Integer>> featureMap) {
        List<double[]> result = new LinkedList<double[]>();
        //Now normalize each feature..min-max normalization
        for (Integer i : featureMap.keySet()) {
            List<Integer> list = featureMap.get(i);
            int max = Integer.MIN_VALUE;
            int min = Integer.MAX_VALUE;

            for (int val : list) {
                if ( val > max) {
                    max = val;
                } else  if ( val < min) {
                    min = val;
                }
            }

            double [] array = new double[list.size()];
            int index = 0;
            for (int val : list) {
                array[index++] = (( val - min )*1.0) / ((max - min)*1.0);
            }
            result.add(array);
        }

        return result;
    }

    public Map<Integer, List<Integer>> getFeatures(List<String> lines) throws Exception {

        Map<Integer, List<Integer>> featureMap = new HashMap<Integer, List<Integer>>();

        for ( String line : lines ) {
            String split [] = line.split(" ");
            for (int i = 0; i < split.length; i++) {
                int val = Integer.parseInt(split[i].trim());
                List<Integer> feature = featureMap.get(i);
                if ( feature == null) {
                    feature = new LinkedList<Integer>();
                    featureMap.put(i, feature);
                }
                feature.add(val);
            }
        }

        return featureMap;

    }



    public static void main(String args[]) throws Exception {
        KMeans client = new KMeans();
        int K = 3;


        File file = new File("src//main//java//numerical//input.txt");

        //Step1: Read Features
        List<String> lines = FileUtils.readLines(file,"UTF-8");
        Map<Integer, List<Integer>> featureMap = client.getFeatures(lines);

        //Step2: Normalize Features
        List<double[]> features = client.normalizeFeatures(featureMap);

        int clusterAssignment [] = client.kMeans(K, features);


        for (int i = 0; i < clusterAssignment.length; i++) {
            System.out.println(lines.get(i) + " ==> " + " Cluster-" + clusterAssignment[i]);
        }

    }

}
