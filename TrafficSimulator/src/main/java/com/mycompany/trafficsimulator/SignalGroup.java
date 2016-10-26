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
 *          <li> 1.02a | 10/26/2016: Initial commit </li> 
 *      </ul>
 */
public class SignalGroup implements Actor{
    private int state;
    private final int SIGNAL_GROUP_TYPE;
    private ArrayList<Road> exitRoads;
    private ArrayList<TrafficSignal> trafficSignals;
    private ArrayList<TrafficSignal[]> operationOrder; //the order of the array list is the order 
                                             //that the signals operate, the TrafficSignal[] stored 
                                             //at each index are simultanious signals, so anything 
                                             //contained here is operated simultaniously.
    /**
     * Constructor for this class, 
     * 
     * @param SIGNAL_GROUP_TYPE     value of the signal type, must come from SignalGroupBehavior class'
     *                              dictionary 
     * @param exitRoads             the roads that exit from this signalGroups
     * @param trafficSignals        an ArrayList of trafficSignals that is part of this group
     * @param operationOrder        an ArrayList of trafficSignal arrays. Each signal in the array must be contained in the trafficSignals ArrayList.
     *          `                   Items in the array are ones that function simultaneously on a tick.
     * @author Erik Clary
     * @since 1.01a
     */
    public SignalGroup(int SIGNAL_GROUP_TYPE, ArrayList<Road> exitRoads, ArrayList<TrafficSignal> trafficSignals, ArrayList<TrafficSignal[]> operationOrder){
        this.exitRoads = exitRoads;
        this.trafficSignals = trafficSignals;
        this.operationOrder = operationOrder;
        this.SIGNAL_GROUP_TYPE = SIGNAL_GROUP_TYPE;
        this.state = 0;
    }   
    /**
     * act works through these steps: 
     * <ol>
     *      <li>Check to see what state the signal group is in.
     * </ol>
     * <br>
     * <b>Remember, the order of ticks must go: Car -> SignalGroup -> TrafficSignal, this way the en-route cars are moved into position before being flag checked by the TrafficSignals.</b>
     * <br>
     * Movement algorithm: Car acts are called first, then SignalGroup (which contain multiple TrafficSignals)
     * make their TrafficSignals act, finally the signal groups pull Cars from the outGoingCars
     * Array and sends them (adds them to the next TrafficSignals feederArray) to their next destination.
     * 
     * @author Erik Clary
     * @since 1.01a
     */
    public void act(){ 
        
        
    }
    
}
