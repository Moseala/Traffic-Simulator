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
 *          <li> 1.05a | 11/07/2016: Added getTime method skeleton, incomplete.
 *      </ul>
 */
public class CarBehavior {
    //these are the final car type enums, more can be added.
    public final int REGULAR = 0;

    static double getTime(int carType, String nextRoad) {
        double length = XLSX.getRoadLength(nextRoad); //to be implemented with commit of xlsx class
        double speed = XLSX.getRoadSpeed(nextRoad);//to be implemented with commit of xlsx class
        switch(carType){
            case 0: return (length/(speed*3600));  //Speed calc is as such: length/speed * 60(mins) * 60(secs) to produce the time in seconds it would take for the car to run.
        }
        return -1; //if this is reached something has gone horribly wrong.
    }
    
}
