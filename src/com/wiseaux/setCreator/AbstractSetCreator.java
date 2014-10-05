package com.wiseaux.setCreator;

import java.util.Queue;

/**
 * Abstract class to be extended by SetCreator.
 *
 * @author Daniel Swain
 */
abstract class AbstractSetCreator {

    protected int neighborhoods, epochs, numOfRecords, numOfAttributes;
    protected double alpha, beta, delta, radius;
    protected Record[] allRecords;
    protected Queue[] trainingSet, testSet;
    protected MatrixEnhanced[] matrices;
}
