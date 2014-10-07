package com.wiseaux.setCreator;

/**
 * Creates a record holding n attributes and one, d, decision.
 *
 * @author Daniel Swain
 */
public final class Record implements Registry {

    protected double[] registry;
    protected String name;
    protected String certaintyFactor;
    protected double[] cover = new double[2];
    protected boolean decisionChanged;

    public Record(int length) {
        this.registry = new double[length];
    }

    public Record(double[] registry) {
        this.registry = registry;
    }

    public Record(Record record) {
        this.setRegistry(record);
        //this.setName(record.getIntName());
        
    }

    public Record(Record record, int name) {
        this.setName(name);
        this.setRegistry(record);
    }
    
    
    public Record(Record record, String name) {
        this.name = name;
        this.setRegistry(record);
    }

    /**
     * Returns the record as a double array
     */
    @Override
    public double[] getRegistry() {
        return registry;
    }

    /**
     * Sets the registry equal to the newRegistry
     */
    @Override
    public void setRegistry(Record newRegistry) {
        this.registry = newRegistry.getRegistry().clone();
        //System.arraycopy(newRegistry.getRegistry(), 0, this.registry, 0, newRegistry.getSize());
        //setName(newRegistry.getIntName());
    }

    /**
     * Return the certainty factor of the record
     *
     * @see StoredWinners
     */
    @Override
    public String getCertaintyFactor() {
        return certaintyFactor;
    }
    
    /**
     * Return the certainty factor in double
     */
    @Override
    public double getDoubleCertaintyFactor() {
        String[] cf = certaintyFactor.split("%");
        return Double.parseDouble(cf[0].trim());
    }

    /**
     * Set the certainty factor of the record
     */
    @Override
    public void setCertaintyFactor(String certaintyFactor) {
        this.certaintyFactor = certaintyFactor;
    }

    /**
     * Returns the string version of the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name string
     */
    @Override
    public void setName(int newName) {
        if(newName < 10) {
            this.name = "R:" + String.valueOf(newName) + " ";
        } else {
            this.name = "R:" + String.valueOf(newName);
        }
    }
    
    
    /**
     * Returns the integer value of the name
     */
    @Override
    public int getIntName() {
        String[] nameSplit = this.name.split(":");
        int i = Integer.parseInt(nameSplit[1].trim());

        return i;
    }

    /**
     * Return the size of the record
     */
    @Override
    public int getSize() {
        return registry.length;
    }

    /**
     * Returns the decision of the record
     */
    @Override
    public double getDecision() {
        return registry[registry.length - 1];
    }
    
    /**
     * Sets the decision value of the record
     */
    @Override
    public void setDecision(double newValue) {
        registry[registry.length - 1] = newValue;
    }

    /**
     * Returns the attribute at the given index
     */
    @Override
    public double getAttribute(int index) {
        return registry[index];
    }

    /**
     * Sets a new value to the attribute at the given index
     */
    @Override
    public void setAttribute(int index, double newValue) {
        this.registry[index] = newValue;
    }

    /**
     * Returns the cover's first value
     */
    public double getCoverFirst() {
        return this.cover[0];
    }
    
    /**
     * Returns the cover's second value
     */
    public double getCoverSecond() {
        return this.cover[1];
    }

    /**
     * Sets the cover
     */
    public void setCover(double[] cover) {
        this.cover = cover;
    }

    /**
     * true if the sum of the attributes divided by the number of non-zero
     * absolute value differences between the records is greater than or equal
     * to 1 + delta
     */
    public boolean isNeighbor(Record check, double delta) {
        double sumOfAtt = 0;
        double counter = 0;
        for (int i = 0; i < check.getSize() - 1; i++) {
            double att = Math.abs(this.getAttribute(i) - check.getAttribute(i));
            sumOfAtt += att;
            if (att != 0) {
                counter++;
            }
        }
        return (sumOfAtt / counter) <= (1 + delta);
    }
    
    /**
     * Find out if this record is a duplicate of check
     */
    public boolean isDuplicate(Record check){
        return (this.isEqualTo(check) && !this.hasSameName(check));
    }
    
    /**
     * Find out if the records have identical attributes
     */
    public boolean isEqualTo(Record check) {
        if(check == null) { //If it's an empty spot
            return false;
        }
        if(check.getAttribute(0) == -1 || this.getAttribute(0) == -1) { //If check is the null record
            return false;
        }
        
        boolean bool = true;
        for (int i = 0; i < check.getSize() - 1; i++) { // Does not check decisions
            if (check.getAttribute(i) != this.getAttribute(i)) {
                bool = false;
            }
        }
        return bool;
    }
    
    /**
     * Find if the records have identical names
     */
    public boolean hasSameName(Record check) {
        return this.getIntName() == check.getIntName();
    }
    
    /**
     * Find if the records have identical names
     */
    public boolean hasSameName(String check) {
        return this.getName().contentEquals(check);
    }
    
    /**
     * Changes the decision to the dominant decision decided by the certainty factor
     * 
     * @see addCertaintyFactor
     */
    public void changeToDominantDecision(){
        if(this.getDoubleCertaintyFactor() < 80){
            decisionChanged = true;
            if(getDecision() == 1) {
                this.setDecision(0);
            } else {
                this.setDecision(1);
            }
            
            this.cover[0] += 1; // Increase cover so that it still counts itself
            
            // Recalculate certainty
            this.setCertaintyFactor(String.format("%, .2f", ((this.cover[0] / this.cover[1]) * 100)));
        } else
            decisionChanged = false;
    }

    /**
     * Returns true if the decision values of each record are equal to each other
     */
    public boolean isWinner(Record check) {
        return this.getDecision() == check.getDecision();
    }
    
    /**
     * Return a string containing the cover tailored for readability
     */
    public String printCover() {
        String print = "";
        print += String.format("%.0f",this.getCoverFirst())
                + "/" 
                + String.format("%.0f",this.getCoverSecond()) 
                + "";
        return print;
    }

    /**
     * Return a string containing the record tailored for readability
     */
    public String printRecord() {
        String print = "[";
        for (int i = 0; i < getSize(); i++) {
            if (i < getSize() - 1) { 
                print += " " + String.format("%6s", String.format("%.2f", registry[i])+", ");
            } else {
                print += " D: " + String.format("%.2f", registry[i]);
            }
        }
        return print + "]";
    }

    /**
     * Return a string containing the record tailored to be read from a file
     */
    @Override
    public String print() {
        String print = "";
        for (int i = 0; i < getSize(); i++) {
            if (i < getSize() - 1) {
                print += String.format("%.2f", registry[i]) + "\t";
            } else {
                print += String.format("%.2f", registry[i]);
            }
        }
        return print;
    }
}
