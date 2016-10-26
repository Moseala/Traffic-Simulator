package com.mycompany.trafficsimulator;

/**
 * Road class is an object to be used to encapsulate a road's name, distance, direction, and speed limit
 * 
 * 
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.01a
 * <p> <b>Date Created: </b>October 25, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.01a | 10/25/2016: Initial commit </li> 
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
     * @since 1.01a
     */
    public Road(String name, String direction, double distance, int speedLimit){
        this.roadName = name;
        this.roadDirection = direction;
        this.distance = distance;
        this.speedLimit = speedLimit;
    }
    
    /**
     * returns this road's name.
     * 
     * @return the name of the road.
     * @author Erik Clary
     * @since 1.01a
     */
    public String getName(){
        return roadName;
    }
    
    /**
     * returns this road's direction
     * 
     * @return direction of the road, <b>Format TBD</b>
     * @author Erik Clary
     * @since 1.01a
     */
    public String getRoadDirection(){
        return roadDirection;
    }
    
    /**
     * returns this road's distance/length
     * 
     * @return the distance/length of the road.
     * @author Erik Clary
     * @since 1.01a
     */
    public double getDistance(){
        return distance;
    }
    
    /**
     * returns this road's speed limit.
     * 
     * @return the speed limit of the road.
     * @author Erik Clary
     * @since 1.01a
     */
    public int getSpeedLimit(){
        return speedLimit;
    }
}
