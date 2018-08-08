package machinelearning.bisectingKmeans;

import machinelearning.basicKMeans.Cluster;
import machinelearning.basicKMeans.KMeans;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.*;

/**
 * Created by skynet on 05/08/18
 */
public class Bisecting {

    public int[] bisectingKMeans(List<double[]> features, int maxClusters, KMeans base) {
        int numRows = features.get(0).length;
        int [] rowtoClusterAssignment = new int [numRows];

        int index4Cluster2Split  = 0;

        for (int i =0 ; i < maxClusters; i++) {
            System.out.println("\n------------------- Cycle " + i + "---------------------------------\n");
            for (int i_ = 0;  i_ < rowtoClusterAssignment.length; i_++) {
                System.out.print(" " + rowtoClusterAssignment[i_]);
            }
            System.out.println("\n----------------------------------------------------\n");

            System.out.println("Selected Cluster to split: " + index4Cluster2Split  );

            Map<Integer, Integer> new2OldMapping = new HashMap<Integer, Integer>();

            //Get all rows with cluster-id == randomClusterNo and in featureRow
            int totalNewRowCount = 0;
            for(int rowId = 0; rowId < rowtoClusterAssignment.length; rowId++) {
                if (rowtoClusterAssignment[rowId] == index4Cluster2Split ) {
                    totalNewRowCount++;
                }
            }
            List<double[]> newFeatures = new LinkedList<double[]>();
            double [] fet_1 = new double[totalNewRowCount];
            double [] fet_2 = new double[totalNewRowCount];
            newFeatures.add(fet_1);
            newFeatures.add(fet_2);


            int newRowId = 0;
            for(int rowId = 0; rowId < rowtoClusterAssignment.length; rowId++) {
                if ( rowtoClusterAssignment[rowId] == index4Cluster2Split ) {
                    new2OldMapping.put(newRowId, rowId);
                    newFeatures.get(0)[newRowId] = features.get(0)[rowId];
                    newFeatures.get(1)[newRowId] = features.get(1)[rowId];
                    newRowId++;
                }
            }

            int newClusterAssignment [] = executeKMeans(base, newFeatures);
            //Every thing which is 1 is old cluster no...everything which is new is new cluster to be added
            //Assign back the cluster assignments
            for ( int rowNo : new2OldMapping.keySet()) {
                int oldRowNo = new2OldMapping.get(rowNo);
                if ( newClusterAssignment[rowNo] == 0) {
                    rowtoClusterAssignment[oldRowNo] = index4Cluster2Split ;
                } else {
                    rowtoClusterAssignment[oldRowNo] = i+1;
                }
            }

            index4Cluster2Split = selectClusterToSplit(rowtoClusterAssignment, i+1, features, base);

        }

        return rowtoClusterAssignment;
    }


    public int selectClusterToSplit(int []newClusterAssignment, int maxCluster, List<double[]>subFeature, KMeans base) {
        //Calculate error distance for clusters
        Cluster [] clusters = new Cluster[maxCluster+1];
        for (int i = 0; i <= maxCluster; i++) {
            Cluster cluster = new Cluster(i,subFeature.size());
            cluster.errorDistance = 0.0;
            clusters[i] = cluster;
        }

        int [] clusterEntityCount = new int[clusters.length];
        //First calculate centroid
        for (int rowNo = 0; rowNo < newClusterAssignment.length; rowNo++) {
            Cluster cluster = clusters[newClusterAssignment[rowNo]];
            for (int centriodIndex = 0; centriodIndex < subFeature.size(); centriodIndex++) {
                cluster.centriod[centriodIndex] += subFeature.get(centriodIndex)[rowNo];
            }
            clusterEntityCount[newClusterAssignment[rowNo]]++;
        }
        for (int i = 0; i < clusters.length; i++) {
            Cluster cluster = clusters[i];
            for (int centriodIndex = 0; centriodIndex < subFeature.size(); centriodIndex++) {
                cluster.centriod[centriodIndex] = (cluster.centriod[centriodIndex]*1.0) / (clusterEntityCount[i] * 1.0);
            }
        }


        int clusterIndexWithMaxDistance = 0;

        for (int rowNo = 0; rowNo < newClusterAssignment.length; rowNo++) {
            int clusterNo = newClusterAssignment[rowNo];
            Cluster cluster = clusters[clusterNo];
            cluster.errorDistance += base.euclideanDistance(subFeature, rowNo, clusters, clusterNo);
        }

        double maxDistanceValue = Integer.MIN_VALUE;
        for (int rowNo = 0; rowNo < newClusterAssignment.length; rowNo++) {
            int clusterNo = newClusterAssignment[rowNo];
            Cluster cluster = clusters[clusterNo];
            if (cluster.errorDistance > maxDistanceValue) {
                maxDistanceValue = cluster.errorDistance;
                clusterIndexWithMaxDistance = clusterNo;
            }
        }

        System.out.println("\nMax Distance: " + maxDistanceValue + " found with cluster : " + clusterIndexWithMaxDistance);
        return clusterIndexWithMaxDistance;
    }


    public int [] executeKMeans(KMeans base, List<double[]> subFeature) {

        //Lets do kmean with 2 on cluster
        //Cluster which we want to split..only take points from that cluster

        Cluster clusters2 [] = new Cluster[2];

        //STEP3: Create Initial Cluster Centroid
        for (int index = 0; index < 2; index++) {
            clusters2[index] = new Cluster(index, subFeature, index);
        }


        return base.kMeans(2, subFeature, clusters2);


    }

    public static void main(String args[]) throws Exception {
        Bisecting client = new Bisecting();
        KMeans kmeans = new KMeans();
        int K = 4; //gets K+1 clusters


        File file = new File("src//main//java//machinelearning//basicKMeans//input.txt");

        //Step1: Read Features
        List<String> lines = FileUtils.readLines(file,"UTF-8");
        Map<Integer, List<Integer>> featureMap = kmeans.getFeatures(lines);

        //Step2: Normalize Features
        List<double[]> features = kmeans.normalizeFeatures(featureMap);

        int clusterAssignment []  = client.bisectingKMeans(features, K, kmeans);

        for (int i = 0; i < clusterAssignment.length; i++) {
            System.out.println(lines.get(i) + " ==> " + " Cluster-" + clusterAssignment[i]);
        }

    }

}
