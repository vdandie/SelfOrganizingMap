package com.wiseaux.setCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Creates a 2D array of attributes this can be updated with given records
 *
 * @author Daniel Swain
 */
public class Matrix extends AbstractSetCreator {

    /**
     * Record array that holds the records that have become seeds
     */
    protected Record[] recordSet;
    
    /**
     * A nullRecord with the value -1 to signify the end length of records
     */
    private final Record nullRecord;
    
    /**
     * The index at which the nullRecord is found.
     */
    private int nullIndex;

    Matrix() {
        this.recordSet = new Record[1000];
        this.nullIndex = 0;
        nullRecord = new Record(new double[]{-1});
        nullRecord.setName(-1);
        this.recordSet[nullIndex] = nullRecord;
    }

    /**
     * Returns the record at the given index.
     */
    public Record getRecord(int index) {
        return recordSet[index];
    }

    /**
     * Adds a new record to the matrix and pushes down the nullRecord &
     * increasing the nullIndex.
     */
    public void addRecord(Record newRecord) {
        recordSet[nullIndex] = new Record(newRecord, newRecord.getIntName());
        recordSet[nullIndex + 1] = new Record(nullRecord, nullIndex);
        nullIndex += 1;
    }

    /**
     * Returns the nullIndex-1 i.e. the actual number of entries in the matrix
     */
    public int getSize() {
        return nullIndex - 1;
    }

    /**
     * Updates the matrix with the given record.
     */
    public void updateMatrix(Record record, int winnerIndex, double alpha) {
        Record seed = recordSet[winnerIndex];           // What matched with record
        Record update = new Record(record.getSize());   // What seed will be replaced by
        update.setName(seed.getIntName());              // Assume the seed's name
        for (int i = 0; i < record.getSize() - 1; i++) {
            double newValue = calcNewRecord(record.getAttribute(i),
                    seed.getAttribute(i), alpha);       //Calculate new values
            update.setAttribute(i, newValue);           //Set the value
        }
        update.setAttribute(update.getSize()-1, seed.getDecision());
        updateRecord(update, winnerIndex);              //Put record into matrix at index
    }

    /**
     * Calculates the value of the new record by adding value1 to alpha
     * multiplied by the difference of value2 by value1.
     */
    private double calcNewRecord(double value1, double value2, double alpha) {
        return round(value1 + alpha * (value2 - value1), 2);
    }
    
    /**
     * Rounds the value to two decimal places
     * 
     * stackoverflow.com/questions/2808535/round-a-double-to-2-decimal-places
     */
    private double round(double value, int places) {
    if (places < 0) throw new IllegalArgumentException();

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
}
    
    /**
     * Finds out if the record is in the recordSet.
     */
    public int findRecord(Record find) {
        int i = 0;
        for(Record record: recordSet) {
            if(record.hasSameName(find)){
                return i;
            }
            i++;
        }
        return -1;
    }
    /**
     * Finds out if the record is in the recordSet.
     */
    public int findRecord(int name){
        int i = 0;
        for(Record record: recordSet) {
            if(record == null) {
                
            } else if(record.getIntName() == name){
                return i;
            }
            i++;
        }
        return -1;
    }
    
    /**
     * Remove record at index from the recordSet.
     */
    public void removeRecord(int index){
        ArrayList<Record> copy = new ArrayList<>();
        for(int i = 0; i < getSize(); i++) {
            if(i == index) {
                //Do nothing
            } else {
                copy.add(recordSet[i]);
            }
        }
        recordSet =  copy.toArray(new Record[copy.size()]);
        nullIndex--;
    }

    /**
     * Updates the record at index to the new update.
     */
    private void updateRecord(Record update, int index) {
        this.recordSet[index].setRegistry(update);
        this.recordSet[index].setName(update.getIntName());
    }
    
    /**
     * Replaces the current matrix with an hash set.
     */
    public void replaceWithHashSet(HashSet<Record> newMatrix) {
        this.recordSet = new Record[500];
        this.nullIndex = 0;
        this.recordSet[nullIndex] = nullRecord;
        
        for(Record newRecord: newMatrix) {
            this.addRecord(newRecord);
        }
    }
    
    /**
     * Returns a string containing the matrix tailored for readability.
     */
    public String printMatrix() {
        String print = "";
        for (int i = 0; i < getSize(); i++) {
            if (recordSet[i].getName() != null && recordSet[i].getIntName() < 10) {
                print += recordSet[i].getName() + "\t"
                        + recordSet[i].printRecord() + "\n";
            } else if (recordSet[i].getName() != null) {
                print += recordSet[i].getName() + "\t " + recordSet[i].printRecord() + "\n";
            }
        }
        return print;
    }

    /**
     * Returns a string containing the matrix tailored for writing to a file to
     * be read in again.
     */
    public void printForRead() {
        String print = "";
        for(int i = 0; i < getSize(); i++) {
            print += recordSet[i].print() + "\n";
        }
        String name = "trainingSet_";
        File output = new File("trainingSets\\"+name+"1.txt");
        try {
            int i = 2;
            while(output.exists()) {
                output = new File("trainingSets\\"+name + (i++) + ".txt");
            }
            output.createNewFile();

        } catch (IOException ex) {
            System.out.print("Could not print trainingSet");
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
    
    public void printOriginal() {
        String print = this.printMatrix();
        File output = new File("trainingSet_1_Original.txt");
        try {
            int i = 1;
            while(output.exists()) {
                output = new File("trainingSet_" + i++ + "_Original.txt");
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
