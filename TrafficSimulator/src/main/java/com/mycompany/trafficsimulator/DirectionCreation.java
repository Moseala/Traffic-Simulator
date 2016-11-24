package com.mycompany.trafficsimulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
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
 * <p> <b>Date Created: </b>October 3, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.00a | 10/03/2016: Initial commit </li> 
 *          <li> 1.05a | 11/07/2016: Created getDirections method & algorithm</li>
 *          <li> 1.08a | 11/14/2016: Added functionality for Chris' change from string finding on traffic signals to passing the object</li>
 *          <li> 1.09a | 11/23/2016: Continued balancing direction length. </li>
 *      </ul>
 */
public class DirectionCreation {
    private final Random rng;
    private final int MAXDIRECTIONLENGTH = 10;
    
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
    
    /**
     * This method generates a list of directions for a Car to follow based on the map and a start point and endpoint.
     * This algorithm functions as follows:
     * <ol>
     *      <li> Add the start point to the direction queue.</li>
     *      <li> Until you reach the endPoint, do the remaining steps:</li>
     *      <li> Get exits of the currentPoint.</li>
     *      <li> Take a random exit(weighted heavily towards the one closest to the endPoint) and add it to the direction queue.</li>
     *      <li> Update the current point to this new signal.</li>
     *      <li> Finally, return the directions.</li>
     * </ol>
     * @param map           The map to create directions off of.
     * @param startPoint    The point where you want the directions to start.
     * @param endPoint      The point where you want the directions to stop.
     * @return              A queue of directions as TrafficSignal for a car to follow.
     * @see Car
     * @since 1.05a
     */
    public Queue<TrafficSignal> getDirections(Map map, TrafficSignal startPoint, TrafficSignal endPoint){
        TrafficSignal currentPoint = startPoint;
        Queue<TrafficSignal> directions = new LinkedList();
        directions.add(currentPoint);
        while(!currentPoint.equals(endPoint)){
            ArrayList<TrafficSignal> exits = map.getExitsOf(currentPoint);
            Collections.sort(exits, endPoint); //sorts the exits so that the one that is closest to the endpoint is first. make sure to test this.
            if(exits.contains(endPoint)){       //short circuit if one of the exits is the final destination.
                directions.add(endPoint);
                return directions;
            }
            if(directions.size() > MAXDIRECTIONLENGTH){ //if the car has a queue of over 30, end the search and make the directions final.
                return directions;
            }
            if(exits.size()-1 <0){ // in the case that the algorithm encounters a signal group that had no exits, this becomes the final point
                directions.add(endPoint);
                return directions;
            }
            if(exits.size() == 1){
                currentPoint = exits.get(0);
            }
            else{
                int decision = rng.nextInt(exits.size()-1);
                if(decision <= (exits.size()*.5))       // This gives a large weight to a car taking a traffic signal that is closer to its final destination.
                    currentPoint = exits.get(0);
                else
                    currentPoint = exits.get(decision);
            }
            directions.add(currentPoint);
        }
        
        return directions;
    }
}
