package com.wiseaux.setCreator;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class is responsible for sending records into the given matrix
 *
 * @author Daniel Swain
 * @version 1.2
 * @since 9/04/2014
 */
public class SetCreatorEnhanced extends SetCreator {

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
        rSet = new RandRecordSet(allRecords);
        testSet = rSet.createTestSet(allRecords, 0, 1);
        trainingSet = rSet.createTrainingSet(testSet);
        matrices = new MatrixEnhanced[trainingSet.length];

        testSetsToFiles();
    }

    /**
     * Puts the testSets into files
     */
    public void testSetsToFiles() {
           Queue[] temp = new Queue[testSet.length];
        for (int i = 0; i < temp.length / 2; i++) {
            temp[i] = rSet.createQueue(testSet, i);
        }
        testSet = temp;
        new File("testSets").mkdir();

        for (int i = 0; i < testSet.length / 2; i++) {
            rSet.printQue(testSet[i], "testSets\\testSet");
        }
    }

    /**
     * Access shootRecords() function in order to train the trainingSet
     */
    public void train(int numOfSets) {
        new File("trainingSets").mkdir();
        shootRecords(trainingSet, numOfSets, matrices);
        this.outputToFile(matrices, "trainingSets\\trainingSetWithData");
    }

    /**
     * Creates a queue to send to findWinner using the given queue array
     */
    private void shootRecords(Queue[] records, int numOfSets, MatrixEnhanced[] matrices) {
        //Currently only returns a single trainingSet
        for (int i = 0; i < numOfSets; i++) {
            Queue<Record> que = rSet.createQueue(records, i);

            matrices[i] = new MatrixEnhanced();
            resetAlpha();

            if (que.isEmpty()) {
                System.out.println("There are no records to send!");
                System.exit(1);
            }
            rSet.printQue(que, "trainingSets\\origTrainingSet");
            findWinner(que, epochs, matrices[i]);
            matrices[i].checkForDuplicates();
            matrices[i].addCertainties();
            matrices[i].printForRead();
        }
    }

    /**
     * Takes a queue of records and shoots them at the seeds, checking if they
     * are neighbors with each other. If so, then add them to winners and update
     * the matrix.
     *
     * @see isNeighbor
     */
    private void findWinner(Queue<Record> que, int epochCount, MatrixEnhanced matrix) {
        int epoch = epochCount;
        if (que.isEmpty() && 1 == 0) {
            System.out.print("Que is empty");
            System.exit(1);
        }

        Queue<Record> d = new LinkedList<>(que);
        boolean[] removeFromQue = new boolean[que.size()];
        boolean recordRemoved = false;

        int recNum = 1;
        //for each record in the queue
        while (!d.isEmpty()) {
            Record record = d.remove();
            //Offset neighbors by 1 to include first epoch when record_1 becomes seed_1            
            boolean[] neighbors = new boolean[matrix.getSize() + 1];

            //Used to count the amount of neighbors between the record and seeds
            int count = 0;

            //Reason for <= : Check the 0th value as well
            for (int i = 0; i <= matrix.getSize(); i++) {
                if (record.isNeighbor(matrix.getRecord(i), delta)) {
                    neighbors[i] = true;
                    count++;
                }
            }

            if (count == 0) {   //No matches, add to matrix, add to winner, mark to remove
                matrix.addRecord(record);
                matrix.addWinner(record, record.getIntName());
                removeFromQue[recNum - 1] = true;
                recordRemoved = true;
            } else if (count == 1) { //Find which record it matched, update
                int index = findTrue(neighbors);
                int name = matrix.getRecord(index).getIntName();
                matrix.updateMatrix(record, index, alpha, name);
            } else if (count > 1) { //Find which record it matched and matched decision, update
                for (int index = 0; index < neighbors.length; index++) {
                    if (neighbors[index] && matrix.getRecord(index).isWinner(record)) {
                        int name = matrix.getRecord(index).getIntName();
                        matrix.updateMatrix(record, index, alpha, name);
                    }
                }
            }
            recNum++;
        }

        // If the epoch is at an interval of 10% of the original epoch
        // the update alpha
        if (epoch % (epoch * 0.1) == 0) {
            updateAlpha();
        }

        epoch -= 1;

        if (recordRemoved && epoch != 0) {
            findWinner(rSet.removeRecords(que, removeFromQue), epoch, matrix);
        } else if (epoch != 0) {
            findWinner(que, epoch, matrix);
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
}
