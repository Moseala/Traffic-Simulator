package com.mycompany.trafficsimulator;

import java.util.ArrayList;
/**
 * Signal class is an object to be used to encapsulate a group of signals that it 
 * operates, and the exit roads attached to it.
 * 
 * 
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.01a
 * <p> <b>Date Created: </b>October 25, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.01a | 10/25/2016: Initial commit </li> 
 *          <li> 1.02a | 10/26/2016: Added javadoc for existing methods. Added act method and finished it up, may require some tuning.</li> 
 *          <li> 1.04a | 11/02/2016: Cleaned up javadoc </li>
 *          <li> 1.05a | 11/07/2016: Revamped functionality for easier car passing; the exit road list is now populated by the traffic signals instead of the roads.
 *                                   Added hasEntrance and getExits for functionality with DirectionCreation.</li>
 *      </ul>
 */
public class SignalGroup implements Actor{
    private int operationIterator;
    private boolean readyForNextOperation;
    private int[] signalTimeArray;
    private final ArrayList<TrafficSignal> exitRoads;
    private final ArrayList<TrafficSignal> trafficSignals;
    private final ArrayList<TrafficSignal[]> operationOrder; //the order of the array list is the order 
                                                            //that the signals operate, the TrafficSignal[] stored 
                                                            //at each index are simultanious signals, so anything 
                                                            //contained here is operated simultaniously.
    private ArrayList<TrafficSignal> signalsOnThisTick;
    /**
     * Constructor for this class. 
     * 
     * @param exitRoads             the roads that exit from this signalGroups
     * @param trafficSignals        an ArrayList of trafficSignals that is part of this group
     * @param operationOrder        an ArrayList of trafficSignal arrays. Each signal in the array must be contained in the trafficSignals ArrayList.
     *          `                   Items in the array are ones that function simultaneously on a tick.
     * @author Erik Clary
     * @since 1.01a
     */
    public SignalGroup(ArrayList<TrafficSignal> exitRoads, ArrayList<TrafficSignal> trafficSignals, ArrayList<TrafficSignal[]> operationOrder){
        this.exitRoads = exitRoads;
        this.trafficSignals = trafficSignals;
        this.operationOrder = operationOrder;
        operationIterator = -1;
        readyForNextOperation = true;
        signalsOnThisTick = new ArrayList();
    }   
    /**
     * act works through these steps: 
     * <ol>
     *      <li>Check to see if the group is ready for the next operation(if the current signals are done with their acts)<br>
     *          If so, reconstruct the signalCount array to match the next operationIterator group</li>
     *      <li>Check to see if the signalTime array has been depleted.<br>
     *          If so, set ready for next operation flag to true, and prevent the signals without time left from acting.</li>
     *      <li>Set signals that need to execute to signalOn. Remember, the map/stage will make them act, not this object.</li>
     * </ol>
     * <br>
     * <b>Remember, the order of ticks must go: Car - SignalGroup - TrafficSignal, this way the en-route cars are moved into position before being flag checked by the TrafficSignals.</b>
     * <br>
     * Movement algorithm: Car acts are called first, then SignalGroup (which contain multiple TrafficSignals)
     * make their TrafficSignals act, finally the signal groups pull Cars from the outGoingCars
     * Array and sends them (adds them to the next TrafficSignals feederArray) to their next destination.
     * 
     * @author Erik Clary
     * @since 1.01a
     */
    public void act(){ 
        if(readyForNextOperation){ //after initialization, this flag will be true, so it will increment operation iterator to 0.
            if(++operationIterator > operationOrder.size()-1)
                operationIterator = 0;
            signalTimeArray = new int[operationOrder.get(operationIterator).length]; //creates an array the same size as the concurrent traffic signals
            for(int i = 0; i<signalTimeArray.length; i++){
                signalTimeArray[i] = operationOrder.get(operationIterator)[i].getBehavior().timeToFunction(); //sets the elements of the array to the amount of time that the corresponding signals will run
            }
        }
        if(timeArrayDepleted(signalTimeArray)){
            readyForNextOperation = true;
        }
        else{
            for(int i = 0; i<signalTimeArray.length; i++){
                if(signalTimeArray[i]-->0){
                    signalsOnThisTick.add(operationOrder.get(operationIterator)[i]);
                }
            }
        }
        
    }

    public ArrayList<TrafficSignal> getSignalsOn(){
        return signalsOnThisTick;
    }
    
    /**
     * this method returns true if all times in the array are depleted (all elements are <=0), otherwise
     * returns false.
     * 
     * @param signalTimeArray an int[] array
     * @return  <ul><li>False: one element in the given array is >0</li>
     *              <li>True: otherwise.</li>
     *          </ul>
     * @author Erik Clary
     * @since 1.02a
     */
    private boolean timeArrayDepleted(int[] signalTimeArray) {
        for(int i = 0; i<signalTimeArray.length; i++){
            if(signalTimeArray[i]>0)
                return false;
        }
        return true;
    }

    /**
     * This method is used by map's verifyNextDirection to check if both of the unique id's are contained in this signal group.
     * 
     * @param id1   unique signal ID for one signal
     * @param id2   unique signal ID for the other signal
     * @return True if both of the unique ID's are contained in this signal group.  False otherwise.
     * @see Map
     * @since 1.05a
     */
    public boolean containsBoth(String id1, String id2) {
        boolean exit= false, feeder = false;
        for(int x = 0; x<exitRoads.size(); x++){
            if(exitRoads.get(x).getIdentifier().equalsIgnoreCase(id2) || exitRoads.get(x).getIdentifier().equalsIgnoreCase(id1)){
                exit = true;
            }
        }
        for(int x = 0; x<trafficSignals.size(); x++){
            if(trafficSignals.get(x).getIdentifier().equalsIgnoreCase(id2) || trafficSignals.get(x).getIdentifier().equalsIgnoreCase(id1)){
                feeder = true;
            }
        }
        return (feeder && exit);
    }

    /**
     * This method returns true if the passed TrafficSignal's ID is contined in this group's traffic signals.
     * 
     * @param currentPoint The traffic signal to be found in this group.
     * @return True if the parameter is contained within this signal group's TrafficSignals.
     * @since 1.05a
     */
    public boolean hasEntrance(TrafficSignal currentPoint) {
        return trafficSignals.contains(currentPoint);
    }

    /**
     * This method returns the exit signals contained in this group.
     * @return The exit roads of this SignalGroup.
     * @since 1.05a
     */
    ArrayList<TrafficSignal> getExitSignals() {
        return exitRoads;
    }
    
}
