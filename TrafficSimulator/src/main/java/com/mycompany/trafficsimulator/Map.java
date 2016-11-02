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
 *          <li> 1.04a | 11/2/2016: Added supporting methods, run, and made Map a runnable </li> 
 *      </ul>
 */
public class Map implements Runnable{
    private LinkedList<Actor> actors = new LinkedList();
    private ArrayList<SignalGroup> nodes;
    private ArrayList<TrafficSignal> signals;
    private Queue<Car> spawnCars;
    private ArrayList<Car> despawnedCars;
    private int currentRunningSecond = 0;
    //these are the variables for the car creation curve. abs(sin(PERIOD*x))*AMPLITUDE
    private final int TIMETORUN = 43200; //this is the amount of seconds for this method to run. Default = 43200 (12 hrs)
    private final double AMPLITUDE = 15; //peak number of cars to enter (cars per second)
    private final double PERIOD = (2*Math.PI)/TIMETORUN; //this has a period of 12 hrs, with peaks at 1/4 and 3/4 of the time (9 and 15)hrs
    private final double ySHIFT = 3; //this is the base car amount (amount of cars to add each second no matter what)
    
    /**
     *
     * @param signalGroups
     * @param trafficSignals
     * @param cars
     */
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
    
    /**
     *
     */
    @Override
    public void run() {
        for(currentRunningSecond =0; currentRunningSecond <TIMETORUN; currentRunningSecond++){
            addCars(currentRunningSecond);
            for(int inner = 0; inner<actors.size(); inner++){
                actors.get(inner).act();
            }
            for(TrafficSignal e:signals){ //this is the loop that moves the outgoing cars from their traffic signals to their new destination.
                for(int outCarsIterator =0; outCarsIterator <e.getOutgoingCars().size(); outCarsIterator++){
                    Car outCar = e.getOutgoingCars().get(outCarsIterator);
                    String nextQueue = outCar.passContinueSignal();
                    if(verifyNextDirection(nextQueue,e)){
                        
                    }
                }
                e.getOutgoingCars().clear(); //clears the outGoingCars AL; maybe better implemented with a queue.
            }
        }
    } 
    
    /**
     *
     * @return
     */
    public double getProgress(){
        return ((double)currentRunningSecond)/TIMETORUN;
    }
    
    private int carCurve(int currentMoment){
        return  (int) ((Math.abs(Math.sin(PERIOD*currentMoment))*AMPLITUDE) + ySHIFT);
    }
    
    private void addCars(int currentMoment){
        if(spawnCars.peek()==null){
            requestMoreCars();
        }
        for(int x = 0; x<carCurve(currentMoment); x++){
            actors.addFirst(spawnCars.poll());
        }
    }

    private void requestMoreCars() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private boolean verifyNextDirection(String nextQueue, TrafficSignal e) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

   
    
    
}
