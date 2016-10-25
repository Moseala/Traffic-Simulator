package com.mycompany.trafficsimulator;

import com.sun.media.jfxmedia.logging.Logger;
import java.util.Queue;

/**
 * Traffic Signal class is an object to be used as a part of a traffic signal group.
 * 
 * 
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.0a
 * <p> <b>Date Created: </b>October 24, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.00a | 10/24/2016: Initial commit </li> 
 *          <li> 1.01a | 10/25/2016: Added source road that feeds this traffic 
 *                                  signal for functionality with signal groups 
 *                                  and map. Also added .equals comparator and getIdentifier</li> 
 *      </ul>
 */
public class TrafficSignal {
    final int signalType;
    private Queue<Car> carQueue;
    private Road sourceRoad;
    private String identifier;
    
    /**
     * Constructor for this class, accepts an integer as signal type,
     * MUST USE SignalBehavior.TYPE_#, in order to function properly
     * 
     * @param SIGNAL_TYPE   value of the signal type, must come from SignalBehavior class'
     *                      dictionary 
     * @param feedingRoad   the road that feeds into this traffic signal
     * @param uniqueIdentifier  the unique identifier that represents this traffic signal.
     * @author Erik Clary
     * @since 1.00a
     */
    public TrafficSignal(int SIGNAL_TYPE, Road feedingRoad, String uniqueIdentifier){
        this.signalType = SIGNAL_TYPE;
        sourceRoad = feedingRoad;
        identifier = uniqueIdentifier;
    }
    
    /**
     * addCar method adds a car to this TrafficSignal's queue.
     * 
     * @param inCar         The car to be added to this queue.
     * @author Erik Clary
     * @since 1.00a
     */
    public void addCar(Car inCar){
        if(!carQueue.add(inCar))
            Logger.logMsg(1, "Car queue out of space, with object: " + this);
    }
    
    /**
     * removeNextCar dequeue's the next car in this TrafficSignal's queue and returns it.
     * 
     * @return a Car object
     * @author Erik Clary
     * @since 1.00a
     */
    public Car removeNextCar(){
        return carQueue.poll();
    }
    
    /**
     * getBehavior returns this TrafficSignal's behavior 
     * 
     * @return a SignalBehavior object representative of this signal's behavior
     * @author Erik Clary
     * @since 1.00a
     */
    public SignalBehavior getBehavior(){
        return new SignalBehavior(signalType); //OPTIMIZATION: move this creation to constructor, then return the contained object if runtimes are large, and memory is not
    }
    
    /**
     * getIdentifier method returns the unique identifier attached to this TrafficSignal
     * 
     * @return String: the identifier unique to this TrafficSignal
     * @author Erik Clary
     * @since 1.01a
     */
    public String getIdentifier(){
        return identifier;
    }
    
    /**
     * equals compares two traffic signals, true if equal, false if they are not
     * this method compares the unique identifiers attached to the signal.
     * 
     * @param TrafficSignal     The traffic signal to compare this to
     * @author Erik Clary
     * @since 1.01a
     */
    public boolean equals(TrafficSignal E){
        return this.identifier.equals(E.getIdentifier());
    }
    
}
