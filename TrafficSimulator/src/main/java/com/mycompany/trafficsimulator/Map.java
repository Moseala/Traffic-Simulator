package com.mycompany.trafficsimulator;

import com.sun.media.jfxmedia.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

/**
 * The Map class contains all the actors for the Traffic Simulation. It also contains the 
 * logic for the simulation's execution. It should be noted that this class is the stage that
 * other classes reference.
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.03a
 * <p> <b>Date Created: </b>November 1, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.03a | 11/1/2016: Initial commit </li> 
 *          <li> 1.04a | 11/2/2016: Added supporting methods, run, and made Map a runnable </li> 
 *          <li> 1.05a | 11/7/2016: Added javadoc, finished map's execution logic 
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
     *  This is the constructor for Map. It requires two array lists, containing the signal groups and traffic signals in the map.
     *  It also requires a queue of cars to be inserted into the system, which should be queued to at least the integral of the car curve
     *  equation, otherwise the map will poll the UI to generate more cars.
     * 
     * @param signalGroups      An array list containing all the signal groups(nodes) in the map.
     * @param trafficSignals    An array list containing all the traffic signals in the map.
     * @param cars              A queue of cars to pull from and insert into the system.
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
        
        Collections.sort(signals);
        //remember to sort traffic signal arrayL
        
    }
    
    /**
     * This is map's run method.  
     * Logic: For each second of the simulation do the following:
     *          <ol>
     *              <li>Add the new cars to the actor queue</li>
     *              <li>Have all actors take their act</li>
     *              <li>Poll each traffic signal for their exit queues</li>
     *              <li>Take all cars in the exit queue and place them in their next road if the movement is valid</li>
     *              <li><b>If the move is invalid, the cars will be despawned, and will be logged that they didnt have the correct pathing.</b></li>
     *              <li>The traffic signal's exit queue is then cleared to prevent duplicate cars.</li> 
     *          </ol>
     */
    @Override
    public void run() {
        for(currentRunningSecond =0; currentRunningSecond <TIMETORUN; currentRunningSecond++){
            addCars(currentRunningSecond);
            for(int inner = 0; inner<actors.size(); inner++){
                actors.get(inner).act();
            }
            for(TrafficSignal e:signals){ //this is the loop that moves the outgoing cars from their traffic signals to their new destination.
                //if(e.getBehavior() == SignalBehavior.) This is not needed, the behavior will be decided by the traffic signal itself. always empty outgoing bins.
                for(int outCarsIterator =0; outCarsIterator <e.getOutgoingCars().size(); outCarsIterator++){
                    Car outCar = e.getOutgoingCars().get(outCarsIterator);
                    String nextQueue = outCar.passContinueSignal(); //gives the car its continue flag, pull the ID NOTE: if this is null, that means despawn car
                    if(nextQueue == null){ // a null nextQueue indicates the car has reached its destination.
                        despawnedCars.add(outCar);
                        actors.remove(outCar); //needs to be tested
                    }
                    else{
                        if(verifyNextDirection(nextQueue,e)){ 
                            findTrafficSignal(nextQueue,0,signals.size()).addCar(outCar); //adds the car to its next destination traffic signal
                        }
                        else{
                            Logger.logMsg(0, "Car " + outCar +" does not have a good next path.");
                            despawnedCars.add(outCar);
                            actors.remove(outCar);
                        }
                    }
                }
                e.getOutgoingCars().clear(); //clears the outGoingCars AL; maybe better implemented with a queue.
            }
        }
    } 
    
    /**
     * This method returns the progress of the map's simulation
     * @return A percentage representing the completion progress of the simulation.
     */
    public double getProgress(){
        return ((double)currentRunningSecond)/TIMETORUN;
    }
    
    /**
     * This method returns the value of the curve equation (|sin(P*x)*A|+C) at a given moment.
     * 
     * @param currentMoment The current running second of the simulation.
     * @return The curve value based on the passed value.
     */
    private int carCurve(int currentMoment){
        return  (int) ((Math.abs(Math.sin(PERIOD*currentMoment))*AMPLITUDE) + ySHIFT);
    }
    
    /**
     * This method adds the amount of cars(based on the current running second) to the actor queue, then adds them to their respective
     * feeder roads by popping their first direction (which is their spawn signal) and searching for its traffic signal.
     * @param currentMoment 
     */
    private void addCars(int currentMoment){
        if(spawnCars.peek()==null){
            requestMoreCars();
        }
        for(int x = 0; x<carCurve(currentMoment); x++){
            Car spawned = spawnCars.poll();
            actors.addFirst(spawned); //this adds the car to the stage(actor queue)
            findTrafficSignal(spawned.passContinueSignal(),0,signals.size()).addCar(spawned); //this adds the car to its spawner traffic signal.
        }
    }

    /**
     * This method will poll the XML/main for more cars to add to the feeder queue.
     */
    private void requestMoreCars() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * This method will return true if the nextQueue(trafficSignalID) is contained in the signal group that owns e(traffic signal parameter)
     * @param nextQueue The unique TrafficSignal ID pulled from a car's direction queue.
     * @param e         The traffic signal that the car is currently being released from.
     * @return          True if the nextQueue is contained in the same traffic group as e, false otherwise.
     * @see SignalGroup
     */
    private boolean verifyNextDirection(String nextQueue, TrafficSignal e) {
        for(SignalGroup group:nodes){
            if(group.containsBoth(nextQueue,e.getIdentifier()))
                return true;
        }
        return false;
    }

    /**
     * This method finds the traffic signal by unique identifier, and then returns the object in the signal list.
     * 
     * @param identifier    The traffic signal's unique identifier.
     * @param begin         Beginning index to search (should always be 0)
     * @param end           End index to search (should always be list.size())
     * @return The TrafficSignal that has a unique identifier that matches the parameter, null otherwise.
     */
    private TrafficSignal findTrafficSignal(String identifier, int begin, int end){//needs to be tested
        if(begin<=end){
            int mid = ((end-begin)/2)+begin; //midpoint
            int compared = signals.get(mid).compareTo(end);
            if(compared ==0) //completed escape
                return signals.get(mid); 
            if(compared < 0)
                return findTrafficSignal(identifier,begin,mid-1);
            if(compared > 0)
                return findTrafficSignal(identifier,mid+1,end);
        }
        return null;
    }
    
    
}
