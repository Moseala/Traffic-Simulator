package com.mycompany.trafficsimulator;

/**
 * CarBehavior class is an object to be used to encapsulate behaviors and actions
 * of a Car for it to reference.
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.02a
 * <p> <b>Date Created: </b>October 25, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.02a | 10/26/2016: Initial commit </li> 
 *          <li> 1.05a | 11/07/2016: Added getTime method skeleton, incomplete.</li>
 *          <li> 1.07a | 11/09/2016: Deleted uneccesary return from getTime</li>
 *          <li> 1.08a | 11/14/2016: Added functionality for Chris' change from string finding on traffic signals to passing the object</li>
 *      </ul>
 */
public class CarBehavior {
    //these are the final car type enums, more can be added.
    public final int REGULAR = 0;

    static double getTime(int carType, TrafficSignal nextRoad) {
        double length = ReadExcel.getRoadLength(nextRoad); //to be implemented with commit of xlsx class
        double speed = ReadExcel.getRoadSpeed(nextRoad);//to be implemented with commit of xlsx class
        switch(carType){
            case 0: return (length/(speed*3600));  //Speed calc is as such: length/speed * 60(mins) * 60(secs) to produce the time in seconds it would take for the car to run.
            default: return (length/(speed*3600));
        }
    }
}
