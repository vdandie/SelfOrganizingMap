package com.wiseaux.setCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * This class will take in a records, create a queue of records shuffle an array
 * of records
 *
 * @author Daniel Swain
 */
public class RandRecordSet {

    /**
     * Holds all records from the input file.
     */
    private Record[] allRecords;

    /**
     * Holds all records with the first of two decision values.
     */
    private Queue<Record> decision_0;

    /**
     * Holds all records with the second of two decision values.
     */
    private Queue<Record> decision_1;

    RandRecordSet() {

    }

    RandRecordSet(Record[] records) {
        fillRecordArray(records);
    }

    /**
     * Copies the given array into the array kept in RandRecordSet.
     */
    private void fillRecordArray(Record[] records) {
        allRecords = new Record[records.length];
        System.arraycopy(records, 0, allRecords, 0, records.length);
    }

    /**
     * Returns a queue filled with given records that are shuffled in order.
     */
    public Queue<Record> createQueue(Record[] records) {
        Queue<Record> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(shuffleArray(records)));
        return queue;

    }

    /**
     * Returns a queue filled with allRecords.
     */
    public Queue<Record> createQueue() {
        Queue<Record> queue = new LinkedList<>();
        queue.addAll(Arrays.asList(shuffleArray(allRecords)));
        return queue;
    }

    /**
     * Returns a queue filled with records from a Queue[] at index.
     */
    public Queue<Record> createQueue(Queue[] groups, int index) {
        Queue<Record> queue = new LinkedList<>();
        queue.addAll(new LinkedList<>(groups[index]));
        queue.addAll(new LinkedList<>(groups[index + 10]));
        return shuffleQueue(queue);
    }

    /**
     * Returns an array of shuffled records.
     */
    public Record[] shuffleArray(Record[] records) {

        Random rand = new Random(Double.doubleToLongBits(Math.random()));
        Record[] shuffle = new Record[records.length];
        System.arraycopy(records, 0, shuffle, 0, records.length);

        for (int i = shuffle.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            Record a = shuffle[index];
            shuffle[index] = shuffle[i];
            shuffle[i] = a;
        }

        setNamesForRecords(shuffle);
        return shuffle;
    }

    /**
     * Shuffles a queue of records.
     */
    public Queue<Record> shuffleQueue(Queue<Record> records) {
        Random rand = new Random(Double.doubleToLongBits(Math.random()));

        //Create a list so that they may be shuffled
        List queue = new LinkedList<>();
        queue.addAll(records);
        Collections.shuffle(queue, rand);

        //Put elements into a Queue<Record>
        Queue<Record> shuffle = new LinkedList<>();
        shuffle.addAll(queue);

        setNamesForRecords(shuffle);

        return shuffle;
    }

    /**
     * Sets the names of the records in order.
     */
    private void setNamesForRecords(Record[] records) {
        for (int i = 0; i < records.length; i++) {
            records[i].setName(i + 1);
        }
    }

    private void setNamesForRecords(Queue<Record> queue) {
        int i = 1;
        for (Record record : queue) {
            record.setName(i);
            i++;
        }
    }

    /**
     * Returns a Queue<Record> with removed records at specified indexes from
     * the boolean array.
     */
    public Queue<Record> removeRecords(Queue<Record> records, boolean[] indexes) {
        Queue<Record> newList = new LinkedList<>();
        int length = records.size();
        for (int i = 0; i < length; i++) {
            if (!indexes[i]) {
                newList.add(records.remove());
            } else {
                records.remove();
            }
        }
        return newList;
    }

    /**
     * Returns a queue of 10 unique groups that contain 10% of the total number
     * of records that have the given decisionValue.
     */
    public Queue[] createTestSet(Record[] records, int decVal_0, int decVal_1) {
        splitRecords(records, decVal_0, decVal_1);
        Queue[] groups = new Queue[20];
        int count = 0;

        // Ten percent values of each queue
        int tenX = new Double(decision_0.size() * 0.1).intValue();
        int tenY = new Double(decision_1.size() * 0.1).intValue();

        Queue<Record> copyOfDecision_0 = new LinkedList<>(decision_0);
        Queue<Record> copyOfDecision_1 = new LinkedList<>(decision_1);

        while (count < (groups.length / 2)
                && (!copyOfDecision_0.isEmpty() || !copyOfDecision_1.isEmpty())) {

            groups[count] = getTenPercentGroup(copyOfDecision_0, tenX);
            groups[count + 10] = getTenPercentGroup(copyOfDecision_1, tenY);
            count++;
        }

        return groups;
    }

    /**
     * Grab random records from the queue equal to 10% of the length of the
     * original queue.
     */
    private Queue<Record> getTenPercentGroup(Queue<Record> records, int tenPercent) {
        Queue<Record> tenSet = new LinkedList<>();
        for (int i = 0; i < tenPercent; i++) {
            tenSet.add(new Record(records.remove()));
        }

        return tenSet;
    }

    /**
     * Split allRecords into records with decision 0 and decision 1.
     */
    private void splitRecords(Record[] records, int decVal_0, int decVal_1) {
        decision_0 = new LinkedList<>();
        decision_1 = new LinkedList<>();

        for (Record record : records) {
            if (record == null) {
                break;
            }
            if (record.getDecision() == decVal_0) {
                decision_0.add(new Record(record));
            }
            if (record.getDecision() == decVal_1) {
                decision_1.add(new Record(record));
            }
        }
    }

    /**
     * Takes values that aren't in the current testSet and returns an equal
     * amount of records from decision_0 and decision_1.
     */
    public Queue[] createTrainingSet(Queue[] groups) {
        Queue[] trainingGroups = new Queue[20];

        int size = getSizeDifference(decision_0, decision_1);
        int i = 0;
        for (Queue<Record> queue : groups) {
            if(i >= 10) {
                Queue<Record> newTrainingSet = getLeftovers(decision_1, queue, size, i/10);
                trainingGroups[i] = newTrainingSet;
                
            } else {
                Queue<Record> newTrainingSet = getLeftovers(decision_0, queue, size, i/10);
                trainingGroups[i] = newTrainingSet;
            }
            i++;
        }

        return trainingGroups;
    }

    /**
     * Gets the leftover records from the given group.
     */
    private Queue<Record> getLeftovers(Queue<Record> decisionSet, Queue<Record> testSet, int length, int dec) {
        Queue<Record> leftovers = new LinkedList<>();
        
        for(Record decRecord: decisionSet) {
            if(leftovers.size() == length) { // Stop if the length needed is acquired
                break;
            }
            
            boolean unique = true;
            
            for(Record testRecord: testSet) {
                if(decRecord.isEqualTo(testRecord)){ //Check if it's the same
                    unique = false;                  //or if it has the right decision
                }
            }
            
            if(unique) {
                Record record = new Record(decRecord);
                leftovers.add(record);
            }
        }
        return leftovers;
    }

    /**
     * Gets the sizes of the two queues and if one is larger than the other,
     * take the smaller of the two.
     */
    private int getSizeDifference(Queue<Record> dec_0, Queue<Record> dec_1) {
        Double value_1 = dec_0.size() - (dec_0.size() * 0.1);
        Double value_2 = dec_1.size() - (dec_1.size() * 0.1);
        if (value_1 > value_2) {
            return value_2.intValue();
        } else {
            return value_1.intValue();
        }
    }
    
    public void printQue(Queue<Record> que, String name){
        String print = "";
            
        for (Record record: que) {
            if (record.getName() != null && record.getIntName() < 10) {
                print += record.getName() + "\t"
                        + record.printRecord() + "\n";
            } else if (record.getName() != null) {
                print += record.getName() + "\t " + record.printRecord() + "\n";
            }
        }
        
        File output = new File(name);
        try {
            
            int i = 1;
            while(output.exists()) {
                output = new File(name + (i++) + ".txt");
            }
            output.createNewFile();

        } catch (IOException ex) {
            System.out.print(ex);
            System.exit(1);
        }

        try (PrintWriter write = new PrintWriter(output)) {

            write.flush();
            write.write(print);
            write.close();

        } catch (FileNotFoundException e) {
            System.out.println("Output file not found.");
            System.exit(1);
        }
    }
}
