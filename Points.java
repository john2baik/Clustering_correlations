package hw4;

import java.util.*;

public class Points{
    private List<double[]> data;
    private List<double[]> normalizedData;
    private double[] min; //used to find the minimum of the attribute
    private double[] max; //used to find max of attribute
    private static int dataSize;
    private static int numOfAttributes;
    private static int k;//the number of clusters specified by user
    private List<double[]> centers;
    private List<double[]> newCenters;
    private List<List<double[]>> clusters;
    private List<List<double[]>> reversedCluster;
    private boolean hasChanged;//
    private double threshold;

    public Points(List<double[]> data, int k, double threshold){
        this.data = data;
        this.k = k;
        this.threshold = threshold;
        hasChanged = true;
        numOfAttributes = data.get(0).length;
        dataSize = data.size();
        normalizedData = new ArrayList<double[]>();
        initializeNormalizedData();
        min = new double[numOfAttributes];
        max = new double[numOfAttributes];
        findMinAndMax();
    }

    //update cluster mean - the first tuple of each list is the current mean
    public void updateClusterMean(){
        newCenters = new ArrayList<double[]>();
        for(List<double[]> cluster : clusters){
            int clusterSize = cluster.size();
            double[] sum = new double[numOfAttributes];
            for(double[] tuple : cluster){
                for(int i = 0; i < numOfAttributes; i++){
                    sum[i] += tuple[i];
                }
            }
            double[] average = new double[numOfAttributes];
            for(int i = 0; i < numOfAttributes; i++){
                average[i] = sum[i] / clusterSize;
            }
            newCenters.add(average);
        }
    }

    //set heuristics to see if the new mean centers has changed. Continue until for all clusters, the mean centeroid hasnt changed.
    //also set newCenters as old center now, if changed
    public void checkForChange(){
        hasChanged = true;//only change when ALL centers have not changed by a 5% margin
        for(int i = 0; i < k; i++){
            double[] oldCenterTuple = centers.get(i);
            double[] newCenterTuple = newCenters.get(i);
            double difference = Math.abs((oldCenterTuple[i] - newCenterTuple[i]));
            double percentChange = (difference / oldCenterTuple[i]) * 100.0;
            //consider all centers together. none or all rule.
            if(percentChange <= threshold){
                hasChanged = false;
            }
            else if(percentChange > threshold){
                hasChanged = true;
            }
        }
    }

    //make newCenters = oldCenters
    public void updateCenters(){
        centers = new ArrayList<double[]>(newCenters);
    }

    //for the normalized data, assign to the closest cluster
    public void assignCluster(){
        for(double[] tuple : normalizedData){
            double minDist = Double.MAX_VALUE;
            int pos = 0;
            //System.out.println("Current cluster size : " + centers.size());
            for(int curCluster = 0; curCluster < k; curCluster++){
                List<double[]> cluster = clusters.get(curCluster);
                double[] centeroid = cluster.get(0);//this is the centeroid, always at the beginning of the list;
                double distance = euclideanDistance(centeroid, tuple);
                if(distance < minDist){
                    minDist = distance;
                    pos = curCluster;
                }
            }
            clusters.get(pos).add(tuple);
        }
    }


    public double euclideanDistance(double[] centroid, double[] tuple){
        double total = 0, difference;
        for(int i = 0; i < numOfAttributes; i++){
            difference = centroid[i] - tuple[i];
            total += difference * difference;
        }
        return (double) Math.sqrt(total);
    }


    public void createClusters(){
        //initialize clusters from the new centers
        clusters = new ArrayList<List<double[]>>();
        for(double[] centeroid : centers){
            List<double[]> tempList = new ArrayList<double[]>();
            tempList.add(centeroid);
            clusters.add(tempList);
        }
    }

    //-----------------------Initial Programs Outside Loop-------------------

    public void reverseMinMax(){
        reversedCluster = new ArrayList<List<double[]>>(clusters);
        for(int i = 0; i < k; i++){
            List<double[]> cluster = clusters.get(i);
            for(int k = 0; k < cluster.size(); k++){
                double[] vector = clusters.get(i).get(k);
                for(int j = 0; j<numOfAttributes; j++){
                    double attribute = vector[j];
                    attribute = (attribute * (max[j] - min[j])) + min[j];
                    reversedCluster.get(i).get(k)[j] = attribute;
                }
            }
        }
    }

    //need to initialized w/ empty tuples of the same size to match w/ the unnormalized Data
    public void initializeNormalizedData(){
        for(int i = 0; i < dataSize; i++){
            double[] filler = new double[numOfAttributes];
            Arrays.fill(filler, 0.0);
            normalizedData.add(filler);
        }
    }

    //normalize all data on a [0,1] scale
    public void normalize(){
        //after finding the max and min for each data
        for(int i = 0; i < dataSize; i++){
            for(int j = 0; j < numOfAttributes; j++){
                double vPrime = 0;
                double v = data.get(i)[j];
                double numerator = v - min[j];
                double denominator = max[j] - min[j];
                normalizedData.get(i)[j] = numerator/denominator;
            }
        }
    }

    //from normalized data, set k random initial centeroids
    public void setInitialPoints(){
        centers = new ArrayList<double[]>();
        for(int i = 0; i < k; i++){
            int random = (int) (Math.random() * dataSize);
            centers.add(normalizedData.get(random));
        }
    }

    //find the min and max of the data for each attribute, used later to normalize
    public void findMinAndMax(){
        Arrays.fill(min, Double.MAX_VALUE);
        Arrays.fill(max, Double.MIN_VALUE);
        for(double[] tuple : data){
            for(int i = 0; i < numOfAttributes; i++){
                double curMin = min[i];
                double curMax = max[i];
                if(tuple[i] < min[i]){
                    min[i] = tuple[i];
                }
                else if(tuple[i] > max[i]){
                    max[i] = tuple[i];
                }
            }
        }
    }

    //-------------------Printing Functions-----------------------

    public void printList(List<double[]> y){
        for(double[] x : y){
            System.out.println(Arrays.toString(x));
        }
    }

    public void printClusters(){
        System.out.println("Printing cluster!");
        int i = 0;
        int cnt = 0;
        for(List<double[]> oneCluster : clusters){
            System.out.println("Printing cluster number " + i + ".");
            System.out.println();
            i++;
            for(double[] x : oneCluster){
                System.out.println(Arrays.toString(x));
                System.out.println();
                cnt++;
                if(cnt % 3 == 0) break;
            }
        }
    }

    //------------------Returning Private Instance/Class Variable Functions----------

    public List<double[]> getNormalizedData(){
        return normalizedData;
    }

    public List<double[]> getCenters(){
        return centers;
    }

    public List<double[]> getNewCenters(){
        return newCenters;
    }

    public List<List<double[]>> getReversedCluster(){
        return reversedCluster;
    }
    public List<double[]> getData(){
        return data;
    }

    public List<List<double[]>> getClusters(){
        return clusters;
    }

    public boolean getHasChanged(){
        return hasChanged;
    }

}