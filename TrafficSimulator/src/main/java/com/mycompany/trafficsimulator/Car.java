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
 *      </ul>
 */
public class Car implements Actor{
    public static final int WAITING_AT_SIGNAL = 0;
    public static final int TRAVELLING = 1;
    public static final int WAITING_TO_ENTER_SIGNAL_QUEUE = 2;
    
    private double timeAlive; //this should be in seconds.
    private Queue<String> directions; //directions MUST BE UNIQUE TRAFFIC SIGNAL IDs otherwise map's transfer algo will die.
    private final int carType;
    private double timeRemainingOnCurrentRoad;
    private int carStatus;
    
    public Car(int CAR_TYPE, Queue<String> directions){
        timeAlive = 0;
        this.directions = directions;
        carType = CAR_TYPE;
        carStatus = WAITING_TO_ENTER_SIGNAL_QUEUE;
    }
    
    public String passContinueSignal(){
        String nextRoad = directions.poll();
        carStatus = TRAVELLING;
        timeRemainingOnCurrentRoad = CarBehavior.getTime(carType, nextRoad);
        return nextRoad;
    }

    public int getCarStatus(){
        return carStatus;
    }
    
    public void carAddedToSignal(){
        carStatus = WAITING_AT_SIGNAL; //this is for readability and limiting outside class functionality of changing the car's status.
    }
    
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
}
