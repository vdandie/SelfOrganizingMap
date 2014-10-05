package com.wiseaux.setCreator;

/**
 * Abstract class to be extended by Record
 *
 * @author Daniel Swain
 */
public interface Registry {

    int getSize();
    
    void setRegistry(Record newRegistry);
    
    double[] getRegistry();
    
    String getName();
    
    void setName(int newName);
    
    int getIntName();

    double getDecision();
    
    void setDecision(double newValue);

    double getAttribute(int index);

    void setAttribute(int index, double newValue);
    
    String getCertaintyFactor();
    
    double getDoubleCertaintyFactor();
    
    void setCertaintyFactor(String certaintyFactor);

    String print();
}
