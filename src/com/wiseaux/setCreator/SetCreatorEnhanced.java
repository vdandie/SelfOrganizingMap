package com.wiseaux.setCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * This class is responsible for sending records into the given matrix
 *
 * @author Daniel Swain
 * @version 2.0.01
 * @since 9/04/2014
 */
public class SetCreatorEnhanced extends SetCreator {

    /**
     * Debugging strings.
     */
    ArrayList<String> debug = new ArrayList<>();
    String table = "%-10s|%-10s|%-10s|%-10s\n";
    String line = "----------|----------|----------|----------\n";
    /**
     * Handle creation of the sets
     */
    private RandRecordSet rSet;

    public SetCreatorEnhanced() {
    }

    /**
     * Reads the configuration file, initializes the records array, reads the
     * input file, initializes rSet, creates the testSets, creates the
     * trainingSet and initializes seeds.
     */
    public void run() {
        readConfig();
        initializeAllRecords(numOfRecords, numOfAttributes);
        readRecords();

        initDebug();//Debug

        rSet = new RandRecordSet(allRecords);
        testSet = rSet.createTestSet(allRecords, 0, 1);
        trainingSet = rSet.createTrainingSet(testSet, 0.1); // using 90%
        matrices = new MatrixEnhanced[trainingSet.length];

        testSetsToFiles();
    }

    /**
     * Reads the configuration and trains with the pre-created sets
     */
    public void runForPreCreatedSets() {
        readConfig();
        rSet = new RandRecordSet();
    }

    /**
     * Reads the configuration and trains k-1 records
     */
    public void runForSpecialSet() {
        readConfig();
        initializeAllRecords(numOfRecords, numOfAttributes);
        readRecords();
        rSet = new RandRecordSet(allRecords);
        testSet = rSet.createSpecialTestSet(allRecords, 0, 1);
        trainingSet = rSet.createSpecialTrainingSet(testSet, 0.01); // using 99%
        matrices = new MatrixEnhanced[trainingSet.length];

        specialTestSetsToFiles();
    }

    /**
     * Puts the testSets into files
     */
    public void testSetsToFiles() {
        Queue[] temp = new Queue[testSet.length];
        for (int i = 0; i < temp.length / 2; i++) {
            temp[i] = rSet.createQueue(testSet, i, testSet.length);
        }
        testSet = temp;
        new File("testSets").mkdir();

        for (int i = 0; i < testSet.length / 2; i++) {
            rSet.printQue(testSet[i], "testSets\\testSet");
        }
    }

    /**
     * Puts the specialTestSets into files
     */
    public void specialTestSetsToFiles() {
        Queue<Record> tmp = new LinkedList<>();

        for (Queue<Record> test : testSet) {
            tmp.addAll(test);
        }
        rSet.setNamesForRecords(tmp);

        new File("testSets").mkdir();

        rSet.printQue(tmp, "testSets\\specialTestSet");

    }

    /**
     * Access shootRecords() function in order to train the trainingSet
     */
    public void train(int numOfSets) {
        new File("trainingSets").mkdir();
        shootRecords(trainingSet, numOfSets, matrices);

        debuggingFile();

    }

    /**
     * Access shootRecordsForPreCreatedSets() using the pre-created
     * trainingSets.
     */
    public void trainPreCreatedSets() {
        trainingSet = new Queue[10];
        matrices = new MatrixEnhanced[trainingSet.length];
        fillTrainingSet();
        shootRecords_2(trainingSet, trainingSet.length, matrices);
        this.outputToFile(matrices, "trainingSets\\trainingSetWithData");
    }

    /**
     * Access shootRecords() using a set of k-1 records
     */
    public void trainAllButOneRecord() {
        new File("trainingSets").mkdir();
        shootRecords_3(trainingSet, trainingSet.length / 2);
    }

    /**
     * Creates a queue to send to doAlgorithm using the given queue array
     */
    private void shootRecords(Queue[] records, int numOfSets, MatrixEnhanced[] matrices) {
        for (int i = 0; i < numOfSets; i++) {
            Queue<Record> que = rSet.createQueue(records, i, records.length);

            MatrixEnhanced matrix = new MatrixEnhanced();
            resetAlpha();

            if (que.isEmpty()) {
                System.out.println("There are no records to send!");
                System.exit(1);
            }
            rSet.printQue(que, "trainingSets\\origTrainingSet");
            doAlgorithm(que, epochs, matrix);
            matrix.combineRegions();
            matrix.checkForDuplicates();
            matrix.addCertainties();
            matrix.printForRead();
            this.outputToFile(matrix, "trainingSets\\trainingSetWithData");
        }
    }

    /**
     * Creates a queue to send to doAlgorithm using the given queue array; for
     * pre-created trainingSets
     */
    private void shootRecords_2(Queue[] records, int numOfSets, MatrixEnhanced[] matrices) {
        for (int i = 0; i < numOfSets; i++) {
            Queue<Record> que = records[i];

            matrices[i] = new MatrixEnhanced();
            resetAlpha();

            if (que.isEmpty()) {
                System.out.println("There are no records to send!");
                System.exit(1);
            }
            //rSet.printQue(que, "trainingSets\\origTrainingSet");
            doAlgorithm(que, epochs, matrices[i]);
            matrices[i].combineRegions();
            matrices[i].checkForDuplicates();
            matrices[i].addCertainties();
            matrices[i].printForRead();
        }
    }

    /**
     * Creates a queue to send to doAlgorithm using the given queue array
     */
    private void shootRecords_3(Queue[] records, int numOfSets) {
        for (int i = 0; i < numOfSets; i++) {
            Queue<Record> que = rSet.createQueue(records, i, records.length);

            MatrixEnhanced matrix = new MatrixEnhanced();
            resetAlpha();

            if (que.isEmpty()) {
                System.out.println("There are no records to send!");
                System.exit(1);
            }
            rSet.printQue(que, "trainingSets\\origTrainingSet");
            doAlgorithm(que, epochs, matrix);
            matrix.combineRegions();
            matrix.checkForDuplicates();
            matrix.addCertainties();
            matrix.printForRead();
            this.outputToFile(matrix, "trainingSets\\trainingSetWithData");
        }
    }

    /**
     * Takes a queue of records and shoots them at the seeds, checking if they
     * are neighbors with each other. If so, then add them to winners and update
     * the matrix.
     *
     * @see isNeighbor
     */
    private void doAlgorithm(Queue<Record> que, int epochCount, MatrixEnhanced matrix) {
        int epoch = epochCount;
        //Debug
        int nonMatchCase = 0;
        int matchCase = 0;

        if (que.isEmpty()) {
            System.out.print("Que is empty");
            System.exit(1);
        }

        //Initialize newRegion
        Map<Integer, HashSet<Record>> newRegion = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            newRegion.put(i, new HashSet<Record>());
        }

        Queue<Record> inputQueue = new LinkedList<>(que);
        boolean[] removeFromQue = new boolean[que.size()];
        boolean recordRemoved = false;

        int recNum = 1;
        //for each inputRecord in the queue
        while (!inputQueue.isEmpty()) {
            Record inputRecord = inputQueue.remove();
            //Offset neighbors by 1 to include first epoch when record_1 becomes seed_1            
            boolean[] neighbors = new boolean[matrix.getSize() + 1];

            //Used to count the amount of neighbors between the inputRecord and seeds
            int count = 0;

            //Reason for <= : Check the 0th value as well
            for (int i = 0; i <= matrix.getSize(); i++) {
                if (inputRecord.isNeighbor(matrix.getRecord(i), delta)) {
                    neighbors[i] = true;
                    count++;
                }
            }

            if (count == 0) {   //No matches, add to matrix, add to winner, mark to remove
                matrix.addRecord(inputRecord);
                matrix.addWinner(inputRecord, inputRecord.getIntName()); //Update newRegion
                newRegion.get(inputRecord.getIntName()).add(inputRecord);

                removeFromQue[recNum - 1] = true;
                recordRemoved = true;
            } else if (count == 1) { //Find which inputRecord it matched, update
                int index = findTrue(neighbors);
                int name = matrix.getRecord(index).getIntName();
                matrix.updateMatrix(inputRecord, index, alpha, name); // Update newRegion
                newRegion.get(name).add(inputRecord);
            } else if (count > 1) { //Find which inputRecord it matched and matched decision, update

                //debug.add("\n\nCase 3\nCount : " + count);
                int nonMatches = 0;//Debug
                int matches = 0;//Debug

                int matchMax = Integer.MIN_VALUE;
                int nonMatchMax = Integer.MIN_VALUE;
                Map<Integer, Record> competitors = new HashMap<>();
                for (int index = 0; index < neighbors.length; index++) {
                    if (neighbors[index]
                            && matrix.getRecord(index).hasSameDecision(inputRecord)) {

                        matches++;//Debug

                        int temp = newRegion.get(index).size();
                        if (matchMax < temp) {
                            competitors.clear();
                            competitors.put(index, matrix.getRecord(index));
                        } else if (matchMax == temp) {
                            competitors.put(index, matrix.getRecord(index));
                        }
                    } else if (neighbors[index]
                            && !(matrix.getRecord(index).hasSameDecision(inputRecord))
                            && (matchMax == Integer.MIN_VALUE)) {

                        nonMatches++;//Debug

                        int temp = newRegion.get(index).size();
                        if (nonMatchMax < temp) {
                            competitors.clear();
                            competitors.put(index, matrix.getRecord(index));
                        } else if (nonMatchMax == temp) {
                            competitors.put(index, matrix.getRecord(index));
                        }
                    }
                }

                //Debug
                if (matches > 0) {
                    matchCase++;
                    //debug.add("\nMatching decision found : " + matches);
                } else {
                    nonMatchCase++;
                    //debug.add("\nNo matching decision found : " + nonMatches);
                }

                if (!competitors.isEmpty()) {
                    Random random = new Random();
                    List<Integer> keys = new ArrayList<>(competitors.keySet());
                    int randomKey = keys.get(random.nextInt(keys.size()));
                    Record winner = competitors.get(randomKey);
                    int name = winner.getIntName();
                    // Update
                    matrix.updateMatrix(inputRecord, randomKey, alpha, name);
                    newRegion.get(name).add(inputRecord);
                }
            }
            recNum++;
        }
        
        debug.add(String.format(table, epoch,
                                       matchCase,
                                       nonMatchCase,
                                       (matchCase + nonMatchCase)
        ));

        matrix.addToRegions(newRegion);
        // If the epoch is at an interval of 10% of the original epoch
        // the update alpha
        if (epoch % (epoch * 0.1) == 0) {
            updateAlpha();
        }

        epoch -= 1;

        if (recordRemoved && epoch != 0) {
            doAlgorithm(rSet.removeRecords(que, removeFromQue), epoch, matrix);
        } else if (epoch != 0) {
            doAlgorithm(que, epoch, matrix);
        }
        
        if(epoch == 0) {
            debug.add("\n");
            initDebug();
        }
    }

    /**
     * Finds the true value in a boolean array
     */
    private static int findTrue(boolean[] boolArray) {
        int index = -1;
        for (int i = 0; i < boolArray.length; i++) {
            if (boolArray[i]) {
                index = i;
            }
        }

        if (index < 0) {
            return index;
        }
        return index;
    }

    private void initDebug() {
        debug.add(String.format(table, "Epoch","Match","NonMatch","Total"));
        debug.add(line);
    }

    private void debuggingFile() {
        String name = "debug";
        File output = new File(name + 1 + ".txt");
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
            for (String str : debug) {
                write.write(str);
            }
            write.close();

        } catch (FileNotFoundException e) {
            System.out.println("Output file not found.");
            System.exit(1);
        }
    }

}
