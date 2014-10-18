package com.wiseaux.intersector;

import com.wiseaux.setCreator.Record;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
     * Holds the testSets.
     */
    Queue[] testSets;

    /**
     * Holds the trainingSets.
     */
    Queue[] trainingSets;

    /**
     * Holds results for each of the runs.
     */
    ArrayList[] results;
    
    /**
     * Holds the average results
     */
    ArrayList<Double> averages;

    /**
     * Number of files to go through.
     */
    protected int times;

    public Intersector() {
        testSets = new Queue[11];
        trainingSets = new Queue[11];
        results = new ArrayList[11];
        averages = new ArrayList<>();
    }

    /**
     * Reads the test and training for the given file.
     */
    public void read(int fileNumber) {
        times = fileNumber;

        for (int i = 0; i < fileNumber; i++) {
            readTrainingSetFile(i + 1);
            readTestSetFile(i + 1);
        }
    }

    /**
     * Runs the tests.
     */
    public void run() {
        if (testSets.length < 1 || trainingSets.length < 1) {
            System.out.println("Error: Test or Training set is empty.");
            System.exit(0);
        }
        for (int i = 1; i <= times; i++) {
            if (testSets[i] == null) {
                System.out.println("Null testSet at " + i);
                continue;
            }
            if (trainingSets[i] == null) {
                System.out.println("Null trainingSet at " + i);
                continue;
            }
            runAlgorithm(i);
        }
    }

    /**
     * Sends the test set record at the given training set.
     */
    void runAlgorithm(int setNumber) {
        Queue<Record> testQueue = testSets[setNumber];
        Queue<Record> trainingQueue = trainingSets[setNumber];
        ArrayList<String> result = new ArrayList<>();
        int falsePos = 0, truePos = 0, falseNeg = 0, trueNeg = 0;
        result.add("TestSet : " + setNumber + "\n");
        //System.out.println("TestSet : " + setNumber);

        int count = 0;
        double temp;
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

            if (match) {
                count++;
                if (test.getDecision() == 0) {
                    truePos += 1;
                } else {
                    falsePos += 1;
                }
            } else if (!match) {
                if (test.getDecision() == 1) {
                    trueNeg += 1;
                } else {
                    falseNeg += 1;
                }

            }

            result.add(String.format("Test Record : %-6s | Min Value: %-3s "
                    + "| Rule : %-5s | Match: %-5s | %-6s",
                    String.format("%5s", test.getName()),
                    String.format("%.2f", min),
                    String.format("R:%2s", name),
                    String.format("%s", match),
                    String.format("D: %.0f", test.getDecision()))
                    + "\n"
            );
            //System.out.println();
        }

        //True/False Pos/Neg table
        String tableFormat = "%-10s | %-6s | %-6s |";
        String line = "-------------------------------";
        result.add(String.format(tableFormat, " ", "0", "1") + "\n" + line
                + "\n" + String.format(tableFormat, "0", truePos, trueNeg)
                + "\n" + String.format(tableFormat, "1", falseNeg, falsePos)
                + "\n" + line
        );

        result.add("\nOverall Match percentage:  "
                + String.format("%.2f", ((double) count / (double) testQueue.size())*100)
                + "|  # Matched " + count
                + "|  # Not Matched " + (testQueue.size() - count)
                + "|  Total : " + testQueue.size()
                + "\n"
        );
        
        averages.add(((double) count / (double) testQueue.size()));
        
        //System.out.println(count + " " + testQueue.size());
        results[setNumber] = result;
    }

    /**
     * Gets the sum of the absolute value differences in the records given.
     */
    double sumAbsDif(Record testRecord, Record rule) {
        double sum = 0;
        double num = 0;
        for (int i = 0; i < testRecord.getSize(); i++) {
            num = Math.abs(testRecord.getAttribute(i) - rule.getAttribute(i));
            if (num < 1) {
                sum += 0;
            } else {
                sum += 1;
            }
        }
        return sum;
    }
    
    double calcAvg(){
        double avg = 0;
        for(double dub: averages){
            avg += dub;
        }
        return avg/averages.size();
    }

    /**
     * Reads the trainingSet File.
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
            System.out.println("TrainingSet File " + index + " was not found.");
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
            System.out.println("TestSet File " + index + " was not found.");
            System.exit(1);
        } catch (NullPointerException e) {
            System.out.println("Record array is not initialized.");
            System.exit(1);
        }
    }

    /**
     * Prints the results into a directory.
     */
    public void resultsToFile(String name) {

        File output = new File(name + "s");
        output.mkdir();

        output = new File(name + "s\\" + name + 1 + ".txt");

        for (ArrayList<String> result : results) {
            if (result == null) {
                continue;
            }

            try {
                int i = 1;
                while (output.exists()) {
                    output = new File(name + "s\\" + name + i++ + ".txt");
                }
                output.createNewFile();

            } catch (IOException ex) {
                System.out.print("OutputToFile can't find directory");
                System.exit(1);
            }

            try (PrintWriter write = new PrintWriter(output)) {

                write.flush();
                for (String line : result) {
                    write.write(line);
                }
                write.close();

            } catch (FileNotFoundException e) {
                System.out.println("Output file not found.");
                System.exit(1);
            }
        }
    }

    /**
     * Prints an overall.txt that has overall results for all results
     */
    public void totalResultsFile() {
        File output = new File("results");
        output.mkdir();

        output = new File("results\\overall.txt");

        try {
            int i = 1;
            while (output.exists()) {
                output = new File("results\\overall" + i++ + ".txt");
            }
            output.createNewFile();

        } catch (IOException ex) {
            System.out.print("OutputToFile can't find directory");
            System.exit(1);
        }

        try (PrintWriter write = new PrintWriter(output)) {

            write.flush();
            for (ArrayList<String> line : results) {
                if (line == null) {
                    continue;
                }
                write.write("\n\n");
                write.write(line.get(0));
                write.write(line.get(line.size() - 2) + "\n");
                write.write(line.get(line.size() - 1));
            }
            write.write("\n\nTotal average match percentage: "+ 
                    String.format("%.2f",calcAvg() * 100 )+ "%");
            write.close();

        } catch (FileNotFoundException e) {
            System.out.println("Output file not found.");
            System.exit(1);
        }

    }
}
