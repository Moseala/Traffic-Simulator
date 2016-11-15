package com.mycompany.trafficsimulator;

import com.sun.media.jfxmedia.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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
 *          <li> 1.03a | 11/01/2016:    Initial commit </li> 
 *          <li> 1.04a | 11/02/2016:    Added supporting methods, run, and made Map a runnable </li> 
 *          <li> 1.05a | 11/07/2016:    Added javadoc, finished map's execution logic, added metric pull methods</li>
 *          <li> 1.07a | 11/09/2016:    Added supporting methods getTotalCarsNeeded and getRandomPoint for use in creation logic.
 *                                      Shifted the addition of cars to the queue outside of the constructor into its own method.</li>
 *          <li> 1.08a | 11/14/2016:    Changed car curve equation to something more managable for testing. **on average needs to be around 63,000</li>
 *      </ul>
 */
public class Map implements Runnable{
    private LinkedList<Actor> actors = new LinkedList(); //this cannot be final, actors are removed and added.
    private final ArrayList<SignalGroup> nodes;
    private final ArrayList<TrafficSignal> signals;
    private Queue<Car> spawnCars;       //this cannot be final, it is added to on empty poll.
    private ArrayList<Car> despawnedCars;
    private int currentRunningSecond = 0;
    
    //these are the variables for the car creation curve. abs(sin(PERIOD*x))*AMPLITUDE
    private final int TIMETORUN = 43200; //this is the amount of seconds for this method to run. Default = 43200 (12 hrs)
    private final double AMPLITUDE = 1; //peak number of cars to enter (cars per second)
    private final double PERIOD = (2*Math.PI)/TIMETORUN; //this has a period of 12 hrs, with peaks at 1/4 and 3/4 of the time (9 and 15)hrs
    private final double ySHIFT = 1; //this is the base car amount (amount of cars to add each second no matter what)
    
    /**
     *  This is the constructor for Map. It requires two array lists, containing the signal groups and traffic signals in the map.
     *  It also requires a queue of cars to be inserted into the system, which should be queued to at least the integral of the car curve
     *  equation, otherwise the map will poll the UI to generate more cars.
     * 
     * @param signalGroups      An array list containing all the signal groups(nodes) in the map.
     * @param trafficSignals    An array list containing all the traffic signals in the map.
     */
    public Map(ArrayList<SignalGroup> signalGroups, ArrayList<TrafficSignal> trafficSignals){
        nodes = signalGroups;
        signals = trafficSignals;
        
        //empty map starting configuration
        for(SignalGroup e: signalGroups){
            actors.add(e);
        }
        for(TrafficSignal e: trafficSignals){
            actors.add(e);
        }
        
    }
    
    /**
     * This will insert the initial queue of cars for the map to use; this should be called before the Map is run, otherwise it will automatically request
     * more cars to run.
     * @param cars              A queue of cars to pull from and insert into the system.
     * @since 1.07a
     */
    public void addInitialCars(Queue<Car> cars){
        spawnCars = cars;
        Collections.sort(signals);
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
     * @since 1.04a
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
                            findTrafficSignal(nextQueue,0,signals.size()-1).addCar(outCar); //adds the car to its next destination traffic signal
                        }
                        else{
                            Logger.logMsg(0, "Car " + outCar +" does not have a good next path.");
                            despawnedCars.add(outCar);
                            actors.remove(outCar);
                        }
                    }
                }
                e.clearOutGoingCars(); //clears the outGoingCars AL; 
            }
        }
    } 
    
    /**
     * This method returns the progress of the map's simulation
     * @return A percentage representing the completion progress of the simulation.
     * @since 1.04a
     */
    public double getProgress(){
        return ((double)currentRunningSecond)/TIMETORUN;
    }
    
    /**
     * This method returns the list of cars that have finished their routes (or got lost). 
     * Only call this method after run() has completed (you can check getProgress for a 1.0 value)
     * @return An ArrayList populated by the cars that have finished their routes.
     * @since 1.05a
     */
    public ArrayList<Car> getDespawnedCars(){
        return despawnedCars;
    }
    
    /**
     * This method returns the value of the curve equation (|sin(P*x)*A|+C) at a given moment.
     * 
     * @param currentMoment The current running second of the simulation.
     * @return The curve value based on the passed value.
     * @since 1.04a
     */
    private int carCurve(int currentMoment){
        return  (int) ((Math.abs(Math.sin(PERIOD*currentMoment))*AMPLITUDE) + ySHIFT);
    }
    
    /**
     * This method adds the amount of cars(based on the current running second) to the actor queue, then adds them to their respective
     * feeder roads by popping their first direction (which is their spawn signal) and searching for its traffic signal.
     * @param currentMoment 
     * @since 1.04a
     */
    private void addCars(int currentMoment){
        if(spawnCars.peek()==null){
            requestMoreCars();
        }
        for(int x = 0; x<carCurve(currentMoment); x++){
            Car spawned = spawnCars.poll();
            actors.addFirst(spawned); //this adds the car to the stage(actor queue)
            findTrafficSignal(spawned.passContinueSignal(),0,signals.size()-1).addCar(spawned); //this adds the car to its spawner traffic signal.
        }
    }

    /**
     * This method will poll the XML/main for more cars to add to the feeder queue.
     * @since 1.04a
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
     * @since 1.04a
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
     * @since 1.04a
     */
    private TrafficSignal findTrafficSignal(String identifier, int begin, int end){//needs to be tested
        if(begin<=end){
            int mid = ((end-begin)/2)+begin; //midpoint
            int compared = signals.get(mid).compareTo(signals.get(end));
            if(compared ==0) //completed escape
                return signals.get(mid); 
            if(compared > 0)
                return findTrafficSignal(identifier,begin,mid-1);
            if(compared < 0)
                return findTrafficSignal(identifier,mid+1,end);
        }
        return null;
    }

    /**
     * This method returns an array of exits for the given parameter.
     * @param currentPoint  The traffic signal that you want the exits of.
     * @return The exits of the parameter given.
     * @since 1.05a
     * @see DirectionCreation
     */
    public ArrayList<TrafficSignal> getExitsOf(TrafficSignal currentPoint) {
        for(SignalGroup e: nodes){
            if(e.hasEntrance(currentPoint))
                return e.getExitSignals();
        }
        return null;
    }

    /**
     * Returns the integral of the car curve between 0 and TIMETORUN
     * @return the amount of cars that will be required for this map to run completely.
     * @since 1.07a
     */
    public int getTotalCarsNeeded() {
        int returnable = 0;
        for(int x = 0; x<TIMETORUN; x++){
            returnable += carCurve(x);
        }
        return returnable;
    }

    /**
     * This will return a random trafficSignal in the map.
     * @param seed  The random object for this method to use. Make sure to pass it the object you've been using for the rest of the seeds, otherwise
     *              the run-to-run operation of this method will be compromised.
     * @return A random traffic signal pulled from the list in this map.
     * @since 1.07a
     */
    public TrafficSignal getRandomPoint(Random seed){
        return signals.get((seed.nextInt(signals.size())));
    }
    
}
