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
     * Holds the true/false pos/neg values throughout the tests.
     */
    Collective col;
    
    /**
     * Holds the average results
     */
    ArrayList<Double> averages;

    /**
     * Number of files to go through.
     */
    protected int times;
    
    /**
     * String formats.
     */
    String tableFormat = "%-10s | %-7s | %-7s |";
    String tableLine = "-------------------------------";
    
    /**
     * Overall
     */
    String overall;

    public Intersector() {
        testSets = new Queue[11];
        trainingSets = new Queue[11];
        results = new ArrayList[11];
        averages = new ArrayList<>();
        col = new Collective();
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
    
    public void readSpecialCase() {
        times = 427;
        testSets = new Queue[times + 1];
        trainingSets = new Queue[times + 1];
        results = new ArrayList[times + 1];
        readSpecialTestSetFile();
        for(int i = 1; i < times; i++) {
            readTrainingSetFile(i);
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
        
        overall = "\n" + String.format(tableFormat, " ", "1", "0") + "\n" + tableLine
                + "\n" + String.format(tableFormat, "1", col.truePos, col.falsePos)
                + "\n" + String.format(tableFormat, "0",  col.falseNeg, col.trueNeg)
                + "\n" + tableLine
                + "\n" + String.format(tableFormat, " ",
                        col.truePos+ "/" + (col.falseNeg+col.truePos),
                        col.trueNeg+"/"+(col.trueNeg+col.falsePos))
                + "\n" + String.format(tableFormat, " ",
                        String.format("%.2f",
                                ((double)col.truePos / ((double)col.falseNeg + col.truePos))),
                        String.format("%.2f", 
                                ((double)col.trueNeg) / ((double)col.trueNeg + col.falsePos)))
                + "\n" + tableLine
                + "\n" + "Total Not Predictable : " + col.notPredictable
        ;
    }

    
    /**
     * Runs the tests for the k-1 case
     */
    public void run_2() {
        if (testSets.length < 1 || trainingSets.length < 1) {
            System.out.println("Error: Test or Training set is empty.");
            System.exit(0);
        }
        for (int i = 0; i < times; i++) {
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
        
        overall = "\n" + String.format(tableFormat, " ", "1", "0") + "\n" + tableLine
                + "\n" + String.format(tableFormat, "1", col.truePos, col.falsePos)
                + "\n" + String.format(tableFormat, "0",  col.falseNeg, col.trueNeg)
                + "\n" + tableLine
                + "\n" + String.format(tableFormat, " ",
                        col.truePos+ "/" + (col.falseNeg+col.truePos),
                        col.trueNeg+"/"+(col.trueNeg+col.falsePos))
                + "\n" + String.format(tableFormat, " ",
                        String.format("%.2f",
                                ((double)col.truePos / ((double)col.falseNeg + col.truePos))),
                        String.format("%.2f", 
                                ((double)col.trueNeg) / ((double)col.trueNeg + col.falsePos)))
                + "\n" + tableLine
                + "\n" + "Total Not Predictable : " + col.notPredictable
        ;
    }
    
    /**
     * Sends the test set record at the given training set.
     */
    void runAlgorithm(int setNumber) {
        Queue<Record> testQueue = testSets[setNumber];
        Queue<Record> trainingQueue = trainingSets[setNumber];
        ArrayList<String> result = new ArrayList<>();
        int falsePos = 0, truePos = 0, falseNeg = 0, trueNeg = 0, notPred = 0;
        result.add("TestSet : " + setNumber + "\n");
        //System.out.println("TestSet : " + setNumber);

        int count = 0, outerCount = 0;
        double temp;
        
        for (Record test : testQueue) {
            
            double min = Double.MAX_VALUE;
            boolean match;
            
            ArrayList<Competitor> comp = new ArrayList<>();
            for (Record rule : trainingQueue) { //For each test, send it against every rule
                //and get the sumAbsDif. 
                temp = sumAbsDif(test, rule);
                if (temp < min) {
                    min = temp;
                    comp.clear(); //New minimum found, remove all
                    
                    match = test.hasSameDecision(rule);
                    comp.add(new Competitor(min,rule, match));
                } else if(temp == min) {
                    match = test.hasSameDecision(rule);
                    comp.add(new Competitor(min,rule, match));
                }
            }
            
            boolean predictable = true;
            if (comp.size() > 1) {
                int dec0 = 0, dec1 = 0;
                for(Competitor dec : comp) {
                    if(dec.dec == 0) {
                        dec0++;
                    } else {
                        dec1++;
                    }
                }
                
                if (dec0 > dec1) { //Take the dominant decision
                    comp.get(0).dec = 0;
                    comp.get(0).match = comp.get(0).dec == test.getDecision();
                } else if (dec0 < dec1) {
                    comp.get(0).dec = 1;
                    comp.get(0).match = comp.get(0).dec == test.getDecision();
                } else if (dec0 == dec1) { //If no dominant decision, not predictable
                    predictable = false;
                }
            } 
            
            if (comp.get(0).match && predictable) { // Matched and predictable
                count++;
                outerCount++;
                if (test.getDecision() == 1) {
                    truePos += 1;
                } else {
                    trueNeg += 1;
                }
            } else if (predictable) { // Not matched, yet predictable
                outerCount++;
                if (test.getDecision() == 0) {
                    falsePos += 1;
                } else {
                    falseNeg += 1;
                }
            }

            if (predictable) {
                result.add(String.format("Test Record : %-6s | Min Value: %-3s "
                        + "| Rule : %-5s | Match: %-5s | Test %-6s",
                        String.format("%-5s", test.getName()),
                        String.format("%.2f", comp.get(0).min),
                        String.format("R:%2s", comp.get(0).rec.getIntName()),
                        String.format("%s", comp.get(0).match),
                        String.format("TD: %.0f", test.getDecision()))
                        + "RD : " + comp.get(0).dec
                        + "\n"
                );
            } else {
                result.add("Test Record " + test.getName() + " is not predictable "
                        + "\n"
                );
                notPred++;
            }
            //System.out.println();
        }

        //True/False Pos/Neg table
        result.add(String.format(tableFormat, " ", "1", "0") + "\n" + tableLine
                + "\n" + String.format(tableFormat, "1", truePos, falsePos)
                + "\n" + String.format(tableFormat, "0", falseNeg, trueNeg)
                + "\n" + tableLine
                + "\n" + String.format(tableFormat, " ",
                        truePos +  "/" + (truePos+falseNeg),
                        trueNeg +  "/" + (trueNeg+falsePos))
                + "\n" + tableLine
        );
        
        // Collect all of true/false pos/neg information
        col.falsePos += falsePos;
        col.falseNeg += falseNeg;
        col.truePos += truePos;
        col.trueNeg += trueNeg;
        col.notPredictable += notPred;

        result.add("\nOverall Match percentage:  "
                + String.format("%.2f", ((double) count / (double) outerCount) * 100)
                + "|  # Matched " + count
                + "|  # Not Matched " + (outerCount-count)
                + "|  # Not Predicted " + notPred
                + "|  Total : " + testQueue.size()
                + "\n"
        );

        averages.add(((double) count / ((double) outerCount 
                //+ (double) notPred
                )));

        //System.out.println(count + " " + testQueue.size());
        results[setNumber] = result;
    }

    /**
     * Gets the sum of the absolute value differences in the records given.
     */
    double sumAbsDif(Record testRecord, Record rule) {
        double num, sum = 0;
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

    double calcAvg() {
        double avg = 0;
        for (double dub : averages) {
            avg += dub;
        }
        return avg / averages.size();
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
     * Reads the testSet file
     */
    private void readSpecialTestSetFile() {
        try (Scanner input = new Scanner(new File("testSets\\specialTestSet1.txt"))) {
            Queue<Record> newQueue;
            int index = 0;
            
            String name = "", decision = "";
            ArrayList<Double> attributes = new ArrayList<>();

            while (input.hasNext()) {
                newQueue = new LinkedList<>();
                
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
                testSets[index++] = newQueue;
            }

            input.close();
        } catch (FileNotFoundException e) {
            System.out.println("SpecialTestSetFile was not found.");
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
            write.write("\n\nTotal average match percentage: "
                    + String.format("%.2f", calcAvg() * 100) + "%");
            write.write(overall);
            write.close();

        } catch (FileNotFoundException e) {
            System.out.println("Output file not found.");
            System.exit(1);
        }

    }
    
    
    /**
     * Prints an overall.txt that has overall results for all results
     */
    public void specialTotalResultsFile() {
        File output = new File("results");
        output.mkdir();

        output = new File("results\\xoverall.txt");

        try {
            int i = 1;
            while (output.exists()) {
                output = new File("results\\xoverall" + i++ + ".txt");
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
                write.write(line.get(1));
                write.write(line.get(line.size() - 2) + "\n");
                write.write(line.get(line.size() - 1));
            }
            write.write("\n\nTotal average match percentage: "
                    + String.format("%.2f", calcAvg() * 100) + "%");
            write.write(overall);
            write.close();

        } catch (FileNotFoundException e) {
            System.out.println("Output file not found.");
            System.exit(1);
        }

    }
}

/**
 * Small class to handle finding the min values.
 */
class Competitor {

    double min;
    Record rec;
    double dec;
    boolean match;

    Competitor() {

    }

    Competitor(double min, Record rec, boolean match) {
        this.min = min;
        this.rec = rec;
        this.dec = rec.getDecision();
        this.match = match;
    }
}

class Collective {
    
    int falsePos, falseNeg, truePos, trueNeg, notPredictable;
    
    Collective() {
        falsePos = falseNeg = truePos = trueNeg = notPredictable = 0;
    }
}
