package com.mycompany.trafficsimulator;

import com.sun.media.jfxmedia.logging.Logger;
import java.util.ArrayList;
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
 *          <li> 1.00a | 10/24/2016:    Initial commit </li> 
 *          <li> 1.01a | 10/25/2016:    Added source road that feeds this traffic 
 *                                      signal for functionality with signal groups 
 *                                      and map. Also added .equals comparator and getIdentifier</li>
 *          <li> 1.02a | 10/26/2016:    Implemented Actor interface and its corresponding methods,
 *                                      added flag implementation through thisSignalOn method.</li> 
 *          <li> 1.04a | 11/02/2016:    Cleaned up javadoc, added comparable to ready this class
 *                                      for use in sorts.</li> 
 *      </ul>
 */
    public class TrafficSignal implements Actor, Comparable{
    final int signalType;
    private Queue<Car> carQueue;
    private ArrayList<Car> roadCars;
    private ArrayList<Car> outGoingCars;
    private final Road sourceRoad;
    private final String identifier;
    private boolean lightActive;
    
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
        lightActive = false;
        outGoingCars = new ArrayList(); // should find a better way do to outgoing cars via passing them directly to signal group actor
    }
    
    /**
     * addCar method adds a car to this TrafficSignal's feeder road.
     * 
     * @param inCar         The car to be added to this signals feeder road.
     * @author Erik Clary
     * @since 1.00a
     */
    public void addCar(Car inCar){
        if(!roadCars.add(inCar))
            Logger.logMsg(1, "Car feeder array out of space, in object: " + this);
    }
    
    /**
     * removeNextCar dequeues the next car in this TrafficSignal's queue and returns it.
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
     * @param E                 The traffic signal to compare this to
     * @author Erik Clary
     * @return True if the two signal's unique identifiers are equal. False otherwise.
     * @since 1.01a
     */
    public boolean equals(TrafficSignal E){
        return this.identifier.equals(E.getIdentifier());
    }
    
    /**
     * thisSignalOn sets the lightActive flag to true. This is to limit out of
     * class access to this variable.
     * 
     * @author Erik Clary
     * @since 1.02a
     */
    public void thisSignalOn(){
        lightActive = true;
    }

    /**
     * act works in three steps: 
     * <ol>
     *      <li>Add all cars on the feeder road to the signal's pending queue.</li>
     *      <li>Dequeue amount of cars based on this signal's behavior, and adds them to the outgoing 
     *          car array.</li>
     *      <li>Set the active flag to false, remember this act is 1 second, and the SignalGroup controls
     *          which signals act at each cycle(tick).</li>
     * </ol>
     * <br>
     * <b>Remember, the order of ticks must go: Car - SignalGroup - TrafficSignal, this way the en-route cars are moved into position before being flag checked by the TrafficSignals.</b>
     * <br>
     * Movement algorithm: Car acts are called first, then SignalGroup (which contain multiple TrafficSignals)
     * make their TrafficSignals act, finally the signal groups pull Cars from the outGoingCars
     * Array and sends them (adds them to the next TrafficSignals feederArray) to their next destination.
     * 
     * @author Erik Clary
     * @since 1.02a
     */
    @Override
    public void act() {
        //Step 1: add all cars on road 
        for(Car feeder: roadCars){
            if(feeder.getCarStatus() == Car.WAITING_TO_ENTER_SIGNAL_QUEUE){
                carQueue.add(feeder);
                feeder.carAddedToSignal();
                roadCars.remove(feeder);// this needs to be tested to ensure that the car is removed correctly
            }
        }
        //Step 2: Check to see if signal is active: if so, then dequeue a car on tick, add to outgoing array, then sets light to false.
        if(lightActive){
            for(int i = 0; i< this.getBehavior().getCarAmountToRelease(); i++){
                outGoingCars.add(carQueue.poll());
            }
            lightActive = false;
        }
    }
    
    /**
     * This method returns an array of cars that are leaving this signal.
     * 
     * @return an array that contains all cars that are leaving this signal.
     * @author Erik Clary
     * @since 1.02a
     */
    public ArrayList<Car> getOutgoingCars(){
        return outGoingCars;
    }

    @Override
    public int compareTo(Object E) {
        if(E.getClass()!= TrafficSignal.class){
            Logger.logMsg(0, "CompareTo used incorrectly with " +this);
        }
        return this.identifier.compareTo(((TrafficSignal)E).getIdentifier());
    }
}
