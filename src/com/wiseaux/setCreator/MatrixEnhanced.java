package com.wiseaux.setCreator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This class will handle storing the records of each winner into a HashMap as
 * well as the decisions of the winners to calculate the certainty factor.
 *
 * @author Daniel Swain
 */
public class MatrixEnhanced extends Matrix {

    /**
     * Each record that updates a seed kept in the matrix is then stored in this
     * map so that they may be found again.
     */
    private final Map<Integer, HashSet<Record>> winners;

    /**
     * All decisions are kept here to be used to calculate the certainty factor
     * of the key value they correspond to then added to the records themselves.
     */
    private final Map<Integer, HashSet<Record>> certaintyFactors;

    /**
     * Strings of weights that have been combined with each other due to being
     * duplicates.
     */
    private final Map<String, HashSet<String>> combinedWeightVector;

    MatrixEnhanced() {
        this.combinedWeightVector = new HashMap<>();
        this.winners = new HashMap<>();
        this.certaintyFactors = new HashMap<>();
        for (int i = 0; i < 190; i++) {
            this.winners.put(i, new HashSet<Record>());
            this.certaintyFactors.put(i, new HashSet<Record>());
        }
    }

    /**
     * Adds a the record to the identified index.
     */
    public void addWinner(Record record, int index) {
        this.winners.get(index).add(record);
        this.certaintyFactors.get(index).add(record);
    }

    /**
     * Sets the Certainty Factor for each seed in the recordSet.
     */
    public void addCertainties() {

        for (int i = 0; i < getSize(); i++) {
            int index = recordSet[i].getIntName();
            String cf = printCertainty(index, i);
            recordSet[i].setCertaintyFactor(cf);
            recordSet[i].changeToDominantDecision();
        }
        cleanMatrix();
    }

    /**
     * Counts the dominant decision values of each winner in the list and
     * divides it by the total.
     */
    public double getCertainty(int name, int index) {
        return calcCertainty(name, index);
    }

    /**
     * Calculations for certainty factor.
     */
    private double calcCertainty(int name, int index) {
        double cf, decValue;
        double count_1, count_2, sum;
        double[] cover = new double[2];
        count_1 = count_2 = 0;
        decValue = recordSet[index].getDecision();

        for (Record decision : certaintyFactors.get(name)) {
            if (decision.getDecision() == 0) {
                count_1++;
            } else {
                count_2++;
            }
        }

        sum = count_1 + count_2;
        cover[1] = sum;
        if (count_1 == 0 && count_2 == 0) {     //If the seed had no winners, return 1
            return 1.0;
        }

        if (decValue == 0) {
            cf = (count_1 / sum);
            cover[0] = count_1;
        } else {
            cf = (count_2 / sum);
            cover[0] = count_2;
        }
        recordSet[index].setCover(cover);
        return cf;
    }

    /**
     * Returns the linked list of winners at the given index.
     */
    public HashSet<Record> getWinnerList(int index) {
        return this.winners.get(index);
    }

    /**
     * Updates the updateMatrix class to also add the winner to the index.
     */
    public void updateMatrix(Record record, int winnerIndex, double alpha, int name) {
        super.updateMatrix(record, winnerIndex, alpha);
        addWinner(record, name);

    }

    /**
     * Runs a check on the set of seeds to see if there are duplicates.
     */
    public void checkForDuplicates() {

        for (int i = 0; i < getSize(); i++) {
            Record firstRecord = recordSet[i];
            if (firstRecord == null) {
                break;
            }

            combinedWeightVector.put(firstRecord.getName(), new HashSet<String>());
            for (int j = i + 1; j < getSize(); j++) {     //Compare each record to find duplicates
                Record secondRecord = recordSet[j];
                if (secondRecord == null) {
                    break;
                }
                if (firstRecord.isDuplicate(secondRecord)) { //If duplicate, combine

                    combineRecords(recordSet[i], recordSet[j]);

                    if (!certaintyFactors.get(firstRecord.getIntName()).isEmpty()) {

                        combinedWeightVector
                                .get(firstRecord.getName())
                                .add(secondRecord.getName());
                    }
                }
            }
        }
    }

    /**
     * Combines the winners & certainties of any two records given and clears
     * the key of the second.
     */
    public void combineRecords(Record first, Record second) {

        HashSet<Record> combineWinners = this.winners.get(second.getIntName());
        this.winners.get(first.getIntName()).addAll(combineWinners);
        this.winners.get(second.getIntName()).clear();

        HashSet<Record> combineCertainties
                = this.certaintyFactors.get(second.getIntName());
        this.certaintyFactors.get(first.getIntName())
                .addAll(combineCertainties);
        this.certaintyFactors.get(second.getIntName()).clear();
        
        
    }

    /**
     * To be called only after the certainty factors are set. Cleans the matrix of
     * any records with 0/0 cover. Cleans the winners of any winners that aren't
     * in the matrix.
     */
    private void cleanMatrix() {
        for (int i = 0; i < getSize(); i++) {
            if (this.certaintyFactors.get(recordSet[i].getIntName()).isEmpty() 
                    && this.winners.get(recordSet[i].getIntName()).isEmpty()) {
                removeRecord(i);
                i--;
            }
        }
        
        for (int i = 0; i < this.winners.size(); i++) {
            HashSet<Record> list = this.winners.get(i);

            if (!list.isEmpty()) {
                if(this.findRecord(i) < 0) {
                    this.winners.get(i).clear();
                }
            }
         }
        
    }

    /**
     * Returns a string of records whose decisions have been changed.
     */
    public String printDecChange() {
        String print = "";
        for (int i = 0; i < getSize(); i++) {
            if (recordSet[i].decisionChanged) {
                print += recordSet[i].getName() + " has had its decision changed.\n";
            }
        }

        if (print.isEmpty()) {
            print = "No decisions changed.\n";
        }

        String newPrint = "\n" + print + "\n";

        return newPrint;
    }

    /**
     * Returns a string containing the certainty factor for readability.
     */
    public String printCertainty(int name, int index) {

        double cf = calcCertainty(name, index);
        String print;

        // If there are no matches for the given index
        if (cf == -1) {
            print = " N/A";
        } else {
            print = String.format("%, .2f", cf * 100);
        }

        return print;
    }

    /**
     * Returns a string of the combined records.
     */
    public String printCombined() {
        String print = "\n";
        String line = "------------------------------";
        
        for (Map.Entry<String, HashSet<String>> entry : combinedWeightVector.entrySet()) {
            if (entry.getValue().isEmpty()) {

            } else {
                print += entry.getKey() + " has been combined with:\n"+line+"\n";
                
                int count = 0;
                for (String name : entry.getValue()) {
                    
                    if(count % 5 == 0) {
                        print += "\n";
                    }
                    
                    print += String.format("%-6s  ", name);
                    count++;
                }

                print += "\n\nTotal combined records: "+count+"\n\n\n";
            }
        }
        
        if(print.length() < 5){
            print += "No records combined.";
        }

        return print + "\n";
    }

    /**
     * Returns a string of the winners made for readability.
     */
    public String printWinners() {
        String print = "\n";
        String format = "%-14s";
        String format_2 = "%-4s %-7s";

        for (int i = 0; i < this.winners.size(); i++) {
            HashSet<Record> list = this.winners.get(i);

            if (!list.isEmpty()) {

                print += "Rule " + (i) + " : ";

                int count = 0;
                for (Record record : list) {
                    if (count++ % 5 == 0) {
                        print += "\n";
                    }
                    print += String.format(format,
                            String.format(format_2,
                                    record.getName(),
                                    "[D:" + record.getDecision() + "]" + " "));
                }
                print += "\n\n";
            }
        }
        return print;
    }

    /**
     * Returns a string containing a printable version of the weight vector
     * matrix made for readability.
     */
    @Override
    public String printMatrix() {

        String leftAlignFormat = "%-5s | %-62s | %-12s | %7s |";
        String print = "\n";
        int i = 0;
        while (i < getSize()) {
            if (recordSet[i] != null) {
                if (recordSet[i].getName() != null) {
                    print += String.format(leftAlignFormat,
                            recordSet[i].getName(),
                            recordSet[i].printRecord(),
                            "C:" + recordSet[i].getCertaintyFactor() + "%",
                            recordSet[i].printCover())
                            + "\n";
                }
            }
            i++;
        }
        return print + "\n";
    }
}
