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
 * @since 1.0a
 * <p> <b>Date Created: </b>October 24, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.0a | 10/24/2016: Initial commit </li> 
 *      </ul>
 */
public class Car {
    public static final int WAITING_AT_SIGNAL = 0;
    public static final int TRAVELLING = 1;
    public static final int WAITING_TO_ENTER_SIGNAL_QUEUE = 2;
    
    private double timeAlive; //this should be in seconds.
    private Queue<String> directions; //maybe create a locations object to encapsulate compareTo, etc
    final private int carType;
    private double timeRemainingOnCurrentRoad;
    private int carStatus;
    
    public Car(int CAR_TYPE, Queue<String> directions){
        timeAlive = 0;
        this.directions = directions;
        carType = CAR_TYPE;
        carStatus = WAITING_TO_ENTER_SIGNAL_QUEUE;
    }
    
    public void passContinueSignal(){
        carStatus = TRAVELLING;
        timeRemainingOnCurrentRoad = CarBehavior.getTime(carType, directions.poll());
    }

    
    public int act(){
        timeAlive += 1;
        if(carStatus == WAITING_AT_SIGNAL){
            return carStatus; //if this car is waiting for a signal, do nothing.
        }
        
        if(--timeRemainingOnCurrentRoad<=0){
            carStatus = WAITING_TO_ENTER_SIGNAL_QUEUE;
            return carStatus;
        }
        
        return carStatus;
    }
}
