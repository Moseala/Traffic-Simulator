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
 *      </ul>
 */
public class SignalGroup implements Actor{
    private int operationIterator;
    private boolean readyForNextOperation;
    private int[] signalTimeArray;
    private final ArrayList<Road> exitRoads;
    private final ArrayList<TrafficSignal> trafficSignals;
    private final ArrayList<TrafficSignal[]> operationOrder; //the order of the array list is the order 
                                             //that the signals operate, the TrafficSignal[] stored 
                                             //at each index are simultanious signals, so anything 
                                             //contained here is operated simultaniously.
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
    public SignalGroup(ArrayList<Road> exitRoads, ArrayList<TrafficSignal> trafficSignals, ArrayList<TrafficSignal[]> operationOrder){
        this.exitRoads = exitRoads;
        this.trafficSignals = trafficSignals;
        this.operationOrder = operationOrder;
        operationIterator = -1;
        readyForNextOperation = true;
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
            if(++operationIterator > operationOrder.size())
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
                    operationOrder.get(operationIterator)[i].thisSignalOn();
                }
            }
        }
        
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
    
}
