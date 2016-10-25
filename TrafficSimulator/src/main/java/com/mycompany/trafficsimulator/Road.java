/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trafficsimulator;

/**
 * Road class is an object to be used to encapsulate a road's name, distance, direction, and speed limit
 * 
 * 
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.0a
 * <p> <b>Date Created: </b>October 25, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.00a | 10/25/2016: Initial commit </li> 
 *      </ul>
 */
public class Road {
    private final String roadName;
    private final String roadDirection;
    private final double distance;
    private final int speedLimit;
    
    /**
     * Constructor for this class, accepts String name, direction, double distance, and
     * int speed limit as the attributes of this class.
     * 
     * @param name          the name of this road 
     * @param direction     the direction that this road goes (TBD: maybe compass directions? N/NE/NW etc
     * @param distance      the distance of this road from end to end
     * @param speedLimit    the speed limit of this road.
     * @author Erik Clary
     * @since 1.00a
     */
    public Road(String name, String direction, double distance, int speedLimit){
        this.roadName = name;
        this.roadDirection = direction;
        this.distance = distance;
        this.speedLimit = speedLimit;
    }
    
    public String getName(){
        return roadName;
    }
    
    public String getRoadDirection(){
        return roadDirection;
    }
    
    public double getDistance(){
        return distance;
    }
    
    public int getSpeedLimit(){
        return speedLimit;
    }
}
