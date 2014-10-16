package com.wiseaux.setCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * Creates a matrix of seeds that are updated by shooting neighboring records.
 * Reads the records from an input
 *
 * @author Daniel Swain
 */
public class SetCreator extends AbstractSetCreator {

    private final String configLoc = "config.txt";
    private final String inputLoc = "input.txt";

    /**
     * Holds the original alpha value
     */
    private double ogAlpha;

    /**
     * Read the configuration file for the project
     */
    protected void readConfig() {

        try (BufferedReader input = new BufferedReader(new FileReader(configLoc))) {
            String line;

            while ((line = input.readLine()) != null) {
                String[] split = line.split("\\s+");

                switch (split[0]) {
                    case "neighborhoods":
                        neighborhoods = Integer.parseInt(split[1]);
                        break;
                    case "alpha":
                        alpha = Double.parseDouble(split[1]);
                        break;
                    case "beta":
                        beta = Double.parseDouble(split[1]);
                        break;
                    case "delta":
                        delta = Double.parseDouble(split[1]);
                        break;
                    case "radius":
                        radius = Double.parseDouble(split[1]);
                        break;
                    case "epochs":
                        epochs = Integer.parseInt(split[1]);
                        break;
                    case "records":
                        numOfRecords = Integer.parseInt(split[1]);
                        break;
                    case "attributes":
                        numOfAttributes = Integer.parseInt(split[1]);
                }
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    /**
     * Read the record file for the project, add records to a set of records.
     */
    protected void readRecords() {
        try (Scanner input = new Scanner(new File(inputLoc))) {
            for (int i = 0; i < numOfRecords; i++) {
                for (int j = 0; j < numOfAttributes; j++) {
                    allRecords[i].setAttribute(j, input.nextInt());
                }
                //allRecords[i].setName(i);
            }
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found.");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Record array is not initialized.");
            System.exit(1);
        }
    }
    
    

    /**
     * Initializes the AllRecords[] array with the given parameters.
     */
    public void initializeAllRecords(int numOfRecords, int numOfAttributes) {
        this.allRecords = new Record[numOfRecords];
        for (int i = 0; i < allRecords.length; i++) {
            allRecords[i] = new Record(numOfAttributes);
        }
        //this.testSet = new RandRecordSet().getDecisionGroupsForTestSet(allRecords, 0, 1);
    }
    
    /**
     * Fills the trainingSet with the pre-created trainingSets.
     */
    public void fillTrainingSet(){
        for(int i = 0; i < 10; i++) {
            readOrigTrainingSetFile(i + 1);
        }
    }
    
     /**
     * Reads the testSet file
     */
    private void readOrigTrainingSetFile(int index) {
        try (Scanner input = new Scanner(new File("trainingSets\\origTrainingSet" + index + ".txt"))) {
            Queue<Record> newQueue = new LinkedList<>();

            String name = "", decision = "";
            ArrayList<Double> attributes = new ArrayList<>();

            while (input.hasNext()) {
                String line = input.nextLine();

                String[] values = line.split(" ");

                for (String thing : values) {
                    if (thing.contains("R:")) {  //Get name
                        name = thing;
                    }
                    if (thing.contains(",")) { // Get attributes
                        attributes.add(Double.valueOf(thing.substring(0, thing.length() - 1)));
                    }
                    if (thing.contains("]")) {   // Get decision
                        decision = thing.substring(0, thing.length() - 1);
                    }
                }

                attributes.add(Double.valueOf(decision));

                double[] registry = new double[attributes.size()];
                int i = 0;
                for (Double num : attributes) {
                    registry[i++] = num;
                }
                attributes.clear();
                newQueue.add(new Record(new Record(registry), name));
            }

            trainingSet[index - 1] = newQueue;

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("origTrainingSet File " + index + " was not found.");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Record array is not initialized.");
            System.exit(1);
        }
    }

    /**
     * Prints a matrix into an output file
     */
    public void outputToFile(MatrixEnhanced[] matrices, String name) {

        File output = new File(name + 1 + ".txt");

        for (MatrixEnhanced matrix : matrices) {
            if(matrix == null) {
                break;
            }
            try {
                int i = 1;
                while (output.exists()) {
                    output = new File(name + i++ + ".txt");
                }
                output.createNewFile();

            } catch (IOException ex) {
                System.out.print("Can't find directory");
                System.exit(1);
            }

            try (PrintWriter write = new PrintWriter(output)) {

                write.flush();
                write.write("WeightVectorMatrix:\n");
                write.write(matrix.printMatrix());
                write.write("Combined Records: \n");
                write.write(matrix.printCombined());
                write.write("Changed Records: \n");
                write.write(matrix.printDecChange());
                write.write("Clusters: \n");
                write.write(matrix.printWinners());
                write.close();

            } catch (FileNotFoundException e) {
                System.out.println("Output file not found.");
                System.exit(1);
            }
        }
    }
    
    /**
     * Updates the alpha by multiplying it with the beta
     */
    public void updateAlpha() {
        this.alpha = alpha - beta;
    }

    /**
     * On each new trainingSet, reset the alpha to its original value
     */
    public void resetAlpha() {
        this.alpha = ogAlpha;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    public double getBeta() {
        return this.beta;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
        this.ogAlpha = alpha;
    }

    public double getAlpha() {
        return this.alpha;
    }

    public int getNeighborhoods() {
        return neighborhoods;
    }

    public int getEpochs() {
        return epochs;
    }

    public int getNumOfRecords() {
        return numOfRecords;
    }

    public int getNumOfAttributes() {
        return numOfAttributes;
    }

    public double getDelta() {
        return delta;
    }

    public double getRadius() {
        return radius;
    }

    public Record[] getAllRecords() {
        return allRecords;
    }

    public Queue[] getTrainingSet() {
        return trainingSet;
    }

    public Queue[] getTestSet() {
        return testSet;
    }
}
