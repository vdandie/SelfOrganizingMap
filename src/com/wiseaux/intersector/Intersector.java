package com.wiseaux.intersector;

import com.wiseaux.setCreator.Record;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
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
    
    public Intersector() {
        testSets = new Queue[10];
        trainingSets = new Queue[10];
    }

    /**
     * Reads the test and training for the given file
     */
    public void read(int fileNumber) {
        readTrainingSetFile(fileNumber);
        readTestSetFile(fileNumber);
    }
    
    /**
     * Runs the tests
     */
    public void run() {
        if(testSets.length < 1 || trainingSets.length < 1) {
            System.out.println("Error: Test or Training set is empty.");
            System.exit(0);
        }
    }

    /**
     * Reads the trainingSet File
     */
    private void readTrainingSetFile(int index) {
        try (Scanner input = new Scanner(new File("trainingSets\\trainingSet" + index + ".txt"))) {
            Queue<Record> newQueue = new LinkedList<>();
            
            while (input.hasNext()) {
                String line = input.nextLine();

                String[] values = line.split(" ");
                double[] registry = new double[values.length];
                
                for (int i = 0; i < values.length; i++) {
                    registry[i] = Double.parseDouble(values[i]);
                }
                
                newQueue.add(new Record(registry));
            }
            
            trainingSets[index] = newQueue;
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
     * Reads the testSet file
     */
    private void readTestSetFile(int index) {
         try (Scanner input = new Scanner(new File("testSets\\testSet" + 1 + ".txt"))) {
            Queue<Record> newQueue = new LinkedList<>();
             
            String name = "", decision = "";
            ArrayList<Double> attributes = new ArrayList<>();
            
            while (input.hasNext()) {
                String line = input.nextLine();
                
                String[] values = line.split(" ");
                
                for (String thing: values) {
                    if(thing.contains("R:")) {  //Get name
                        name = thing;
                    }
                    if(thing.contains(",")) { // Get attributes
                        attributes.add(Double.valueOf(thing.substring(0, thing.length()-1)));
                    }
                    if(thing.contains("]")) {   // Get decision
                        decision = thing.substring(0, thing.length()-1);
                    }
                }
                
                attributes.add(Double.valueOf(decision));
                
                double[] registry = new double[attributes.size()];
                int i = 0;
                for(Double num: attributes) {
                    registry[i++] = num;
                }
                attributes.clear();
                newQueue.add(new Record(new Record(registry), name));
            }
            
            testSets[index] = newQueue;
            
            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found.");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Record array is not initialized.");
            System.exit(1);
        }
    }
}
