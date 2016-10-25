/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
 * @since 1.0a
 * <p> <b>Date Created: </b>October 25, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.00a | 10/25/2016: Initial commit </li> 
 *      </ul>
 */
public class SignalGroup {
    private ArrayList<Road> exitRoads;
    private ArrayList<TrafficSignal> trafficSignals;
    private ArrayList<TrafficSignal[]> operationOrder; //the order of the array list is the order 
                                             //that the signals operate, the TrafficSignal[] stored 
                                             //at each index are simultanious signals, so anything 
                                             //contained here is operated simultaniously.
    
    public SignalGroup(ArrayList<Road> exitRoads, ArrayList<TrafficSignal> trafficSignals, ArrayList<TrafficSignal[]> operationOrder){
        this.exitRoads = exitRoads;
        this.trafficSignals = trafficSignals;
        this.operationOrder = operationOrder;
    }   
    
    public void act(){ //this act needs to be figured out, initial ideas with loop wont work right, need to embellish ticks & robot logic
        
    }
    
}
