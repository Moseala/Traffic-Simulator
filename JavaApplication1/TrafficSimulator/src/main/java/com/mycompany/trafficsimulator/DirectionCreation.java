package com.mycompany.trafficsimulator;

import java.util.Random;

/**
 * Direction Creation is a class that encapsulates the algorithm that creates 
 * directions for the Car class to use.
 * 
 * 
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.0a
 * <p> <b> Date Created: </b>October 3, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.0a | 10/3/2016: Initial commit </li> 
 *      </ul>
 */
public class DirectionCreation {
    private Random rng;
    
    /**
     * Constructor for this class, accepts a seed for the Random
     * Number Generator used in this class.
     * 
     * @param seed          value of the seed to be used in the random 
     *                      number generation
     * @author Erik Clary
     * @since 1.0a
     */
    public DirectionCreation(int seed){
        rng = new Random(seed);
    }
}
