package hw4;

/* I worked on this projecty by myself without the help of a tutor. -John baik
* arg0 = data, arg1 = k, arg2 = new file to write
* */
import java.util.*;
import java.io.*;


public class Kmeans{

    public static List<double[]> DATA;
    public static List<List<double[]>> clusters;

    public static void main(String[] args) throws Exception{

        if(args.length != 5){
            System.out.println("Please use 4 arguments when running the Kmeans.java program.");
            System.out.println("Example: ");
            System.out.println("java Kmeans [Data Filepath] [NewFile Filepath] [k] [Threshold] [# of tuples to show]");
            System.out.println("k = number of clusters to be created");

            System.out.println("Threshold = Minimum percentage of difference before new clustering stops.");
            System.out.println("Example: 5 -> Minimum of 5% difference between new and old cluster centroids before creating new cluster required.");
            System.out.println("# of tuples to show = How many tuples to be returned during file creation for ease of read.");
        }
        //handle all arguments
        File dataFile = new File(args[0]);
        File outputFile = new File(args[1]);
        int k = Integer.parseInt(args[2]);
        double threshhold = Double.parseDouble(args[3]);
        int numberOfVectorsToShow = Integer.parseInt(args[4]);

        DATA = transformToData(dataFile);
        Points pt = new Points(DATA, k, threshhold);
        pt.normalize();
        pt.setInitialPoints();
        int clusteredCount = 0;
        while(pt.getHasChanged()){
            pt.createClusters();
            pt.assignCluster();
            pt.updateClusterMean();
            pt.checkForChange();
            if(!pt.getHasChanged()){
                clusters = pt.getClusters();
            }
            else{
                pt.updateCenters();
            }
            clusteredCount++;
        }
        pt.reverseMinMax();

        //now write to new file!!

        FileWriter fw = new FileWriter(outputFile.getPath());
        BufferedWriter br = new BufferedWriter(fw);
        br.write("The data from " + dataFile.getName() + " has been clustered " + Integer.toString(clusteredCount) + " times.");
        br.newLine();
        br.write("You specified the threshold for noticing change between cluster means to be " + Double.toString(threshhold) + "%.");
        br.newLine();
        br.newLine();
        br.write("List of all centeroids."); br.newLine();
        for(double[] center : pt.getCenters()){
            br.write(Arrays.toString(center));
            br.newLine();
        }
        br.newLine();
        int clusterNum = 1;
        for(List<double[]> cluster : pt.getReversedCluster()){
            br.write("This is the centroid for the number " + Integer.toString(clusterNum) + " cluster from the data set:" );
            br.newLine();
            br.write(Arrays.toString(cluster.get(0)));
            br.newLine();
            br.newLine();
            String clusterSize = Integer.toString(cluster.size());
            br.write("The size for cluster number " + Integer.toString(clusterNum) + " is " + clusterSize + " , including the centroid." );
            br.newLine();
            br.newLine();
            br.write("The first " + Integer.toString(numberOfVectorsToShow) + " vectors nearest to mean centroid cluster number " + Integer.toString(clusterNum) + ":");
            br.newLine();
            br.newLine();
            for(int j = 1; j < numberOfVectorsToShow+1; j++){
                br.write(Arrays.toString(cluster.get(j)));
                br.newLine();
            }
            br.newLine();
            br.newLine();
            br.newLine();
            clusterNum++;
        }
        br.close();
        System.out.println("New file created in " + outputFile.getParent() + ".");
        System.out.println("Name of file is " + outputFile.getName());
}

    /*
    transform data files to list of string objects.
     */

    public static List<double[]> transformToData(File dataFile){
        List<double[]> data = new ArrayList<double[]>();
        try{
            Scanner sc = new Scanner(dataFile);
            System.out.println();
            sc.nextLine(); //skip header line
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                String[] attributes = line.split(",");
                double[] toDouble = transformToDouble(attributes);
                data.add(toDouble);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }

        return data;
    }


    public static void printTuples(List<double[]> list){
        for(double[] x : list){
            for(int i = 0; i < x.length; i++){
                System.out.print(x[i] + " , ");
            }
            System.out.println();
        }
    }
    /*
    cleans data of all numerical values. Maybe transform months to numbers
     */
    public static double[] transformToDouble(String[] attributes){
        double[] toDouble = new double[attributes.length];
        for(int i = 0; i < attributes.length; i++){
            String attribute = attributes[i];
            double value = 0;
            try{
                toDouble[i] = Double.parseDouble(attribute);
            }
            catch(NumberFormatException e) {
                toDouble[i] = dayMonthtoNumerical(attribute);
            }
        }
        return toDouble;
    }

    /*converts days or months to its numerical counterparts. i.e. mon = 1, jan = 1;*/
    public static double dayMonthtoNumerical(String s){
        double numVal = 0;
        switch (s){
            //take care of the days
            case "mon": numVal = 1;
                break;
            case "tue": numVal = 2;
                break;
            case "wed": numVal = 3;
                break;
            case "thu": numVal = 4;
                break;
            case "fri": numVal = 5;
                break;
            case "sat": numVal = 6;
                break;
            case "sun": numVal = 7;
                break;
            //from here we take care of the months
            case "jan": numVal = 1;
                break;
            case "feb": numVal = 2;
                break;
            case "mar": numVal = 3;
                break;
            case "apr": numVal = 4;
                break;
            case "may": numVal = 5;
                break;
            case "jun": numVal = 6;
                break;
            case "jul": numVal = 7;
                break;
            case "aug": numVal = 8;
                break;
            case "sep": numVal = 9;
                break;
            case "oct": numVal = 10;
                break;
            case "nov": numVal = 11;
                break;
            case "dec": numVal = 12;
                break;
        }
        return numVal;
    }


}