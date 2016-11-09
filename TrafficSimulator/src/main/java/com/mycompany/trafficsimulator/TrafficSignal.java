package com.mycompany.trafficsimulator;

import com.sun.media.jfxmedia.logging.Logger;
import java.util.ArrayList;
import java.util.Comparator;
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
 *          <li> 1.05a | 11/07/2016:    Added missing javadoc, and @since tags.
 *                                      Implemented Comparator, and added required methods.
 *                                      Added overriding .equals for compatibility with ArrayList.contains</li>
 *      </ul>
 */
    public class TrafficSignal implements Actor, Comparable, Comparator<TrafficSignal>{
    private final int signalType;
    private Queue<Car> carQueue;
    private ArrayList<Car> roadCars;
    private ArrayList<Car> outGoingCars; //this cannot be final. It is dumped and inserted to often.
    private final Road sourceRoad;
    private final int[] coordinates;
    private final String identifier;
    private boolean lightActive;
    
    /**
     * Constructor for this class, accepts an integer as signal type,
     * MUST USE SignalBehavior.TYPE_#, in order to function properly
     * 
     * @param SIGNAL_TYPE       value of the signal type, must come from SignalBehavior class'
     *                          dictionary 
     * @param feedingRoad       the road that feeds into this traffic signal
     * @param uniqueIdentifier  the unique identifier that represents this traffic signal.
     * @param coordinates       an array of size 2 that contains the x,y coordinates of this traffic signal. <b>Note: This must be of size 2</b>
     * @author Erik Clary
     * @since 1.00a
     */
    public TrafficSignal(int SIGNAL_TYPE, Road feedingRoad, String uniqueIdentifier, int[] coordinates){
        this.signalType = SIGNAL_TYPE;
        sourceRoad = feedingRoad;
        identifier = uniqueIdentifier;
        lightActive = false;
        outGoingCars = new ArrayList(); // should find a better way do to outgoing cars via passing them directly to signal group actor
        this.coordinates = coordinates;
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
        public String getIdentifier() {
            return identifier;
        }


    /**
     * getSignalType method returns the signal attached to this
     * TrafficSignal
     *
     * @return an integer that is the signal type unique to this
     * TrafficSignal
     * @author Chris Tisdale
     * @since 1.06a
    */
        public int getSignalType() {
            return signalType;
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
     * equals compares two traffic signals, true if equal, false if they are not
     * this method compares the unique identifiers attached to the signal.
     * 
     * @param E                 The traffic signal to compare this to
     * @author Erik Clary
     * @return True if the two signal's unique identifiers are equal. False otherwise.
     * @since 1.05a
     */
    @Override
    public boolean equals(Object E){
        return this.identifier.equals(((TrafficSignal)E).getIdentifier());
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
     * This method returns an array of cars that are leaving this signal. This 
     * method will also clear the outGoingCar array.
     * 
     * @return an array that contains all cars that are leaving this signal.
     * @author Erik Clary
     * @since 1.02a
     */
    public ArrayList<Car> getOutgoingCars(){
        return outGoingCars;
    }

    /**
     * This method returns the difference between the two traffic signals unique ID's (string) compareTo.
     * <b> This method can only compare two traffic signals, otherwise it will fail.</b>
     * @param E The other Traffic Signal for this to be compared to.
     * @return Difference between the two traffic signals' unique identifiers as int.
     * @since 1.04a
     * @see Comparable
     */
    @Override
    public int compareTo(Object E) {
        if(E.getClass()!= TrafficSignal.class){
            Logger.logMsg(0, "CompareTo used incorrectly with " +this);
        }
        return this.identifier.compareTo(((TrafficSignal)E).getIdentifier());
    }
    
    /**
     * This method is functionally the same as the overrode compareTo, but uses a string as the paramter (which should always be another TrafficSignal's ID)
     * 
     * @param identifier The other Traffic Signal's identifier for this to be compared to.
     * @return Difference between the two traffic signals' unique identifiers as int.
     * @since 1.04a
     * @see Comparable
     */
    public int compareTo(String identifier) {
        return this.identifier.compareTo(identifier);
    }
    
    /**
     * This method returns a euclidian distance calculation between this traffic signal and the other(parameter).
     * @param other The traffic signal to find the distance to.
     * @return The distance between the two TrafficSignals as double.
     * @since 1.05a
     */
    public double getDistanceFrom(TrafficSignal other){
        if(getCoordinates().length !=2){
            Logger.logMsg(1, "Traffic Signal " + identifier + "was created with a wrong coordinate length. Shutdown operation and recreate map.");
            return -1;
        }
            
        double x = Math.pow(coordinates[0]-other.getCoordinates()[0], 2);
        double y = Math.pow(coordinates[1]-other.getCoordinates()[1], 2);
        return Math.sqrt(x+y);
    }
    
    /**
     * This method returns the coordinate array for this TrafficSignal.
     * @return The coordinate array as int[] of size 2.
     * @since 1.05a
     */
    public int[] getCoordinates(){
        return coordinates;
    }

    /**
     * This method clears this traffic signal's outgoing cars array, should be called after the Map's moved all cars out of the array to their new destinations.
     * @since 1.05a
     * @see Map
     */
    public void clearOutGoingCars() {
        outGoingCars.clear();
    }

    /**
     * This method is required by java's Comparator interface. It returns the difference of distance between two traffic signals as int.
     * @param t     A traffic signal to be compared.
     * @param t1    Another traffic signal to be comared.
     * @return      The distance between the two given TrafficSignals as int.
     * @since 1.05a
     */
    @Override
    public int compare(TrafficSignal t, TrafficSignal t1) {
        if(t1.getCoordinates().length !=2){
            Logger.logMsg(1, "Traffic Signal " + t1.getIdentifier() + "was created with a wrong coordinate length. Shutdown operation and recreate map.");
            return -1;
        }
            
        double x = Math.pow(t1.getCoordinates()[0]-t.getCoordinates()[0], 2);
        double y = Math.pow(t1.getCoordinates()[1]-t.getCoordinates()[1], 2);
        return (int)Math.sqrt(x+y);
    }

}
