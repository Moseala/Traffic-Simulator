package com.mycompany.trafficsimulator;

import java.util.Queue;

/**
 * The Car class is a class containing the reporting methods of car, and statistics 
 * attributes used for reporting the car's path and time.
 * 
 * 
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.00a
 * <p> <b>Date Created: </b>October 24, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.00a | 10/24/2016: Initial commit </li>
 *          <li> 1.02a | 10/26/2016: Added actor implement, and changed act
 *                                      method to match the interface's requirements.</li>
 *          <li> 1.05a | 11/07/2016: Added missing javadoc, added getTimeAlive method for metrics.</li>
 *          <li> 1.07a | 11/09/2016: Added start/end points, needs to be finished.</li>
 *          <li> 1.08a | 11/14/2016: Added functionality for Chris' change from string finding on traffic signals to passing the object (implements serializable)</li>
 *      </ul>
 */
public class Car implements Actor, Runnable{
    public static final int WAITING_AT_SIGNAL = 0;
    public static final int TRAVELLING = 1;
    public static final int WAITING_TO_ENTER_SIGNAL_QUEUE = 2;
    
    public static final int REGULAR_CAR = 0;
    
    private double timeAlive; //this should be in seconds.
    private Queue<TrafficSignal> directions; //directions MUST BE UNIQUE TRAFFIC SIGNALS otherwise map's transfer algo will die.
    private final int carType;
    private double timeRemainingOnCurrentRoad;
    private int carStatus;
    private String startPoint;
    private String endPoint;
    private String id;
    
    /**
     * Constructor for car object, car status defaults to WAITING_TO_ENTER_SIGNAL_QUEUE, as it should be waiting to be spawned.
     * 
     * @param CAR_TYPE      The type of car that this car is. Must be one of the final car types: REGULAR_CAR etc.
     * @param directions    A queue of the signal id's that this car will visit. These MUST be in the order that the car will visit; otherwise, the car will get lost.
     *                      This should only be inserted by the direction creation algorithm.
     * @author Erik Clary
     * @since 1.00a
     */
    public Car(int CAR_TYPE, Queue<TrafficSignal> directions){
        timeAlive = 0;
        this.directions = directions;
        carType = CAR_TYPE;
        carStatus = WAITING_TO_ENTER_SIGNAL_QUEUE;
        id = "" +carType + "--" + directions.peek().getIdentifier();
    }
    
    /**
     * This method passes the continue flag to this car object. It returns the id of the next traffic signal that 
     * it needs to go to.
     * 
     * @return the unique id of the next traffic signal to go to.
     * @author Erik Clary
     * @since 1.00a
     */
    public String passContinueSignal(){
        try{
            //System.out.println("Car " + id + " has " + directions.size()+ " directions"); //debug
            TrafficSignal nextRoad = directions.poll();
            carStatus = TRAVELLING;
            timeRemainingOnCurrentRoad = CarBehavior.getTime(carType, nextRoad);
            return nextRoad.getIdentifier();
        }
        catch(NullPointerException e){
            return null;
        }
    }

    /**
     * This method returns the amount of time the car has been alive for.
     * @return Time this car has been alive for [as double].
     * @since 1.05a
     */
    public double getAliveTime(){
        return timeAlive;
    }
    
    /**
     * Returns this car's status; e.g. WAITING_TO_ENTER_SIGNAL_QUEUE as int. Be sure 
     * to only compare the result of this method with this car's behavioral finals.
     * 
     * @return the current status of this car.
     * @author Erik Clary
     * @since 1.00a
     */
    public int getCarStatus(){
        return carStatus;
    }
    
    public String getCarID(){
        return id;
    }
    
    /**
     * This method sets the car's flag as waiting at a signal. Functionally, this 
     * makes the car's act() short-circuit and only count the time it is waiting.
     * 
     * @author Erik Clary
     * @since 1.00a
     */
    public void carAddedToSignal(){
        carStatus = WAITING_AT_SIGNAL; //this is for readability and limiting outside class functionality of changing the car's status.
    }
    
    /**
     * This method makes the car take a step. This should only be called by the stage.
     * Note: a step for this actor is incrementing its alive-time, then checking if
     * it has reached the end of the road. If so, it changes its flag to WAITING_TO_ENTER_SIGNAL_QUEUE
     * in order for the signal to add it to said queue.
     * 
     * @author Erik Clary
     * @since 1.02a
     */
    @Override
    public void act(){
        timeAlive += 1;
        if(carStatus == WAITING_AT_SIGNAL){
            return; //if this car is waiting for a signal, do nothing.
        }
        
        if(--timeRemainingOnCurrentRoad<=0){
            carStatus = WAITING_TO_ENTER_SIGNAL_QUEUE;
            return;
        }
    }

    @Override
    public void run() {
        act();
    }
}
