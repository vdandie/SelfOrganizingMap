package com.wiseaux.intersector;

import com.wiseaux.setCreator.Record;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

/**
 * This class takes a test set and a training set, then sends the test set's
 * records at the training set's seeds.
 *
 * @author Daniel Swain Jr
 */
public class Intersector {

    /**
     * Holds the testSets
     */
    Queue[] testSets;

    /**
     * Holds the trainingSets
     */
    Queue[] trainingSets;
    
    /**
     * Holds the minimum values
     */
    Map[] minValues;
    
    /**
     * Number of files to go through
     */
    protected int times;
    
    public Intersector() {
        testSets = new Queue[11];
        trainingSets = new Queue[11];
        minValues = new HashMap[11];
    }

    /**
     * Reads the test and training for the given file
     */
    public void read(int fileNumber) {
        times = fileNumber;
        
        for(int i = 0; i < fileNumber; i++) {
            readTrainingSetFile(i + 1);
            readTestSetFile(i + 1);
        }
    }

    /**
     * Runs the tests
     */
    public void run() {
        if (testSets.length < 1 || trainingSets.length < 1) {
            System.out.println("Error: Test or Training set is empty.");
            System.exit(0);
        }
        for(int i = 1; i <= times; i++) {
            if(testSets[i] == null) {
                System.out.println("Null testSet at " + i);
                continue;
            }
            if(trainingSets[i] == null) {
                System.out.println("Null trainingSet at " + i);
                continue;
            }
            runAlgorithm(i);
        }
    }

    /**
     * Sends the test set record at the given training set
     */
    void runAlgorithm(int setNumber) {
        Queue<Record> testQueue = testSets[setNumber];
        Queue<Record> trainingQueue = trainingSets[setNumber];
        
        System.out.println("TestSet : " + setNumber);

        int count = 0;
        double temp = 0;
        for (Record test : testQueue) {
            double min = Double.MAX_VALUE;
            int name = 0;
            boolean match = false;
            
            for (Record rule : trainingQueue) { //For each test, send it against every rule
                                                //and get the sumAbsDif. 
                temp = sumAbsDif(test, rule);
                if (temp < min) {
                    min = temp;
                    name = rule.getIntName();
                    match = test.hasSameDecision(rule);
                }
            }
            
            if(match) {
                count++;
            }
            System.out.println(test.getName() +
                    " | min value : " + min + 
                    " | with Rule : "+ name +
                    " | match : "+ match);
        }
        System.out.println(count + " " + testQueue.size());
    }
    
    /**
     * Gets the sum of the absolute value differences in the records given
     */
    double sumAbsDif(Record testRecord, Record rule) {
        double sum = 0;
        for(int i = 0; i < testRecord.getSize(); i++) {
            sum += Math.abs(testRecord.getAttribute(i) - rule.getAttribute(i));
        }
        return sum;
    }

    /**
     * Reads the trainingSet File
     */
    private void readTrainingSetFile(int index) {
        try (Scanner input = new Scanner(new File("trainingSets\\trainingSet_" + index + ".txt"))) {
            Queue<Record> newQueue = new LinkedList<>();
            
            int name = 1;
            while (input.hasNext()) {
                String line = input.nextLine();

                String[] values = line.split("\t");
                double[] registry = new double[values.length];

                for (int i = 0; i < values.length; i++) {
                    registry[i] = Double.parseDouble(values[i]);
                }

                newQueue.add(new Record(new Record(registry), name++));
            }

            trainingSets[index] = newQueue;
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("TrainingSet File "+index+" was not found.");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Record array is not initialized.");
            System.exit(1);
        }
    }

    /**
     * Reads the testSet file
     */
    private void readTestSetFile(int index) {
        try (Scanner input = new Scanner(new File("testSets\\testSet" + index + ".txt"))) {
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

            testSets[index] = newQueue;

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("TestSet File "+index+" was not found.");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Record array is not initialized.");
            System.exit(1);
        }
    }
}
