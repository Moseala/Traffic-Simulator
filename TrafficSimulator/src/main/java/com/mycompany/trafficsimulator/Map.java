package com.mycompany.trafficsimulator;

import com.sun.media.jfxmedia.logging.Logger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

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
 *          <li> 1.09a | 11/23/2016:    Added heavy multithreading support and fixed handoff bugs.</li>
 *          <li> 1.10b | 11/27/2016:    Added user controls, and the ability to utilize custom car and time allotments in this class' runtime.
 *      </ul>
 */
public class Map implements Runnable{
    private final ArrayList<SignalGroup> nodes;
    private final ArrayList<TrafficSignal> signals;
    private ArrayList<Car> runningCars;
    private Queue<Car> spawnCars;       //this cannot be final, it is added to on empty poll.
    private ArrayList<Car> despawnedCars;
    private int currentRunningSecond = 0;
    private int rng = 12345;
    private Random rand = new Random(rng);
    private boolean forceUserOverride;
    private int userTime;
    private int userCarAmount;
    
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
     * @author Erik Clary
     */
    public Map(ArrayList<SignalGroup> signalGroups, ArrayList<TrafficSignal> trafficSignals){
        nodes = signalGroups;
        signals = trafficSignals;
        runningCars = new ArrayList();
        despawnedCars = new ArrayList();
        forceUserOverride = false;
    }
    
    /**
     * This will insert the initial queue of cars for the map to use; this should be called before the Map is run, otherwise it will automatically request
     * more cars to run.
     * @param cars              A queue of cars to pull from and insert into the system.
     * @since 1.07a
     * @author Erik Clary
     */
    public void addInitialCars(Queue<Car> cars){
        spawnCars = cars;
        Collections.sort(signals);
    }
    
    /**
     * This method will 
     * @param carAmount
     * @param runningTime
     * @author Erik Clary
     * @since 1.10b
     */
    public void userSettings(int carAmount, int runningTime){
        forceUserOverride = true;
        userCarAmount = carAmount;
        userTime = runningTime;
    }
    
    /**
     * This method is used to display how many actors are currently functioning in the system.
     * @return The number of actors currently running in the system.
     */
    public int actorsInSystem(){
        return nodes.size() + signals.size() + runningCars.size();
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
     * @author Erik Clary
     */
    @Override
    public void run() {
        int runtime = TIMETORUN;
        if(forceUserOverride)
            runtime = userTime;
        for(currentRunningSecond =0; currentRunningSecond <runtime; currentRunningSecond++){
            if(despawnedCars.size() == userCarAmount && forceUserOverride)
                break; //if the user specifies a car amount, and all cars have finished execution, exit the simulation.
            addCars(currentRunningSecond);
            //Break actor list into cars/trafficsignals
            ExecutorService taskExec = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()); //or use newCachedThreadPool, works too
            for(Car e:runningCars){
                taskExec.execute(e);
            }
            
            for(SignalGroup e:nodes){
                taskExec.execute(e);
            }
            //these probably cannot run at the same time. unless regressive on the second i.e. changes to signals from group is pushed after run instead of before.
            for(TrafficSignal e:signals){
                taskExec.execute(e);
            }
            
            taskExec.shutdown(); //improvement: this still works
            try{
                taskExec.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
            }catch(InterruptedException e){
                System.out.println("Task Signal await has been interrupted");
            }
            //finish multithread
            
            //group run section
            for(SignalGroup e: nodes){
                ArrayList<TrafficSignal> activate = e.getSignalsOn();
                for(TrafficSignal t: activate){
                    findTrafficSignal(t.getIdentifier(),0,signals.size()-1).thisSignalOn();
                }
            }
            //signal run section
            for(TrafficSignal e:signals){ //this is the loop that moves the outgoing cars from their traffic signals to their new destination.
                if(e.getOutgoingCars().size()!=0){
                    //System.out.println("Signal " + e.getIdentifier() + " has " + e.getOutgoingCars().size() + " outgoing Cars"); //debug
                    for(int outCarsIterator =0; outCarsIterator <e.getOutgoingCars().size(); outCarsIterator++){
                        Car outCar = e.getOutgoingCars().get(outCarsIterator);
                        String nextQueue = outCar.passContinueSignal(); //gives the car its continue flag, pull the ID NOTE: if this is null, that means despawn car
                        //System.out.println("Car " + outCar.getCarID() + " is now on " + nextQueue); //debug
                        if(nextQueue == null){ // a null nextQueue indicates the car has reached its destination.
                            despawnedCars.add(outCar);
                            runningCars.remove(outCar); //needs to be tested
                            //System.out.println("A car has exited the System!"); //debug
                        }
                        else{
                            if(verifyNextDirection(nextQueue,e)){ 
                                findTrafficSignal(nextQueue,0,signals.size()-1).addCar(outCar); //adds the car to its next destination traffic signal
                            }
                            else{
                                System.out.println("Car " + outCar +" does not have a good next path.");
                                despawnedCars.add(outCar);
                                runningCars.remove(outCar);
                            }
                        }
                    }
                    e.clearOutGoingCars(); //clears the outGoingCars AL; 
                }
            }
        }
    } 
    
    /**
     * This method returns the progress of the map's simulation
     * @return A percentage representing the completion progress of the simulation.
     * @since 1.04a
     * @author Erik Clary
     */
    public double getProgress(){
        if(!forceUserOverride)
            return ((double)currentRunningSecond)/TIMETORUN;
        else
            return ((double)currentRunningSecond)/userTime;
    }
    
    /**
     * This method returns the list of cars that have finished their routes (or got lost). 
     * Only call this method after run() has completed (you can check getProgress for a 1.0 value)
     * @return An ArrayList populated by the cars that have finished their routes.
     * @since 1.05a
     * @author Erik Clary
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
     * @author Erik Clary
     */
    private int carCurve(int currentMoment){
        if(forceUserOverride){
            if(currentMoment == 0)      //add user's specified car amount at the beginning, else do not add any.
                return userCarAmount;
            return 0;
        }
        return  (int) ((Math.abs(Math.sin(PERIOD*currentMoment))*AMPLITUDE) + ySHIFT);
    }
    
    /**
     * This method adds the amount of cars(based on the current running second) to the actor queue, then adds them to their respective
     * feeder roads by popping their first direction (which is their spawn signal) and searching for its traffic signal.
     * @param currentMoment 
     * @since 1.04a
     * @author Erik Clary
     */
    private void addCars(int currentMoment){
        for(int x = 0; x<carCurve(currentMoment); x++){
            if(spawnCars.peek()==null)
                requestMoreCars(carCurve(currentMoment));
            Car spawned = spawnCars.poll();
            runningCars.add(spawned); //this adds the car to the stage(actor queue)
            findTrafficSignal(spawned.passContinueSignal(),0,signals.size()-1).addCar(spawned); //this adds the car to its spawner traffic signal.
        }
    }

    /**
     * This method will poll the XML/main for more cars to add to the feeder queue.
     * @param amount        The amount of cars you want to add
     * @since 1.04a
     * @author Erik Clary
     */
    private void requestMoreCars(int amount) {
        DirectionCreation directions = new DirectionCreation(rng);
        for(int x = 0; x<amount; x++){
            Car newCar = new Car(Car.REGULAR_CAR, directions.getDirections(this, this.getRandomPoint(rand), this.getRandomPoint(rand)));
            spawnCars.add(newCar);
            //System.out.println("Created additional car: " + x + " of " + amount);
        }
    }

    /**
     * This method will return true if the nextQueue(trafficSignalID) is contained in the signal group that owns e(traffic signal parameter)
     * @param nextQueue The unique TrafficSignal ID pulled from a car's direction queue.
     * @param e         The traffic signal that the car is currently being released from.
     * @return          True if the nextQueue is contained in the same traffic group as e, false otherwise.
     * @see SignalGroup
     * @since 1.04a
     * @author Erik Clary
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
     * @author Erik Clary
     */
    private TrafficSignal findTrafficSignal(String identifier, int begin, int end){//needs to be tested
        if(begin<=end){
            int mid = ((end-begin)/2)+begin; //midpoint
            int compared = signals.get(mid).compareTo(identifier);
            if(compared ==0){ //completed escape
                return signals.get(mid); 
            }
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
     * @author Erik Clary
     */
    public ArrayList<TrafficSignal> getExitsOf(TrafficSignal currentPoint) {
        for(SignalGroup e: nodes){
            if(e.hasEntrance(currentPoint))
                return e.getExitSignals();
        }
        return null;
    }
    
    /**
     * This method is used to return the traffic signals present in the system. Should only be used to gather metrics after
     * this class has finished executing.
     * @return An ArrayList containing the traffic signals in this map.
     * @author Erik Clary
     * @since 1.09a
     */
    public ArrayList<TrafficSignal> getTrafficSignals(){
        return signals;
    }

    /**
     * Returns the integral of the car curve between 0 and TIMETORUN
     * @return the amount of cars that will be required for this map to run completely.
     * @since 1.07a
     * @author Erik Clary
     */
    public int getTotalCarsNeeded() {
        int returnable = 0;
        if(forceUserOverride)
            return userCarAmount;
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
     * @author Erik Clary
     */
    public TrafficSignal getRandomPoint(Random seed){
        return signals.get((seed.nextInt(signals.size())));
    }
    
}
