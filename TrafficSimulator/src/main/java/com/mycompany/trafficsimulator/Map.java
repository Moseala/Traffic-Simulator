/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trafficsimulator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The Map class contains all the actors for the Traffic Simulation. It also contains the 
 * logic for the simulation's execution.
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.03a
 * <p> <b>Date Created: </b>November 1, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.03a | 11/1/2016: Initial commit </li> 
 *      </ul>
 */
public class Map {
    LinkedList<Actor> actors = new LinkedList();
    ArrayList<SignalGroup> nodes;
    ArrayList<TrafficSignal> signals;
    Queue<Car> spawnCars;
    ArrayList<Car> despawnedCars;
    
    
    public Map(ArrayList<SignalGroup> signalGroups, ArrayList<TrafficSignal> trafficSignals, Queue<Car> cars){
        nodes = signalGroups;
        signals = trafficSignals;
        spawnCars = cars;
        
        //empty map starting configuration
        for(SignalGroup e: signalGroups){
            actors.add(e);
        }
        for(TrafficSignal e: trafficSignals){
            actors.add(e);
        }
        
    }
    
    
}
