package com.mycompany.trafficsimulator;

/**
 * This interface is created for the purpose of allowing the Map/runner to implement a generic array list for 
 * all objects within the map (TrafficSignals, SignalGroups, and Cars.
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.02a
 * <p> <b>Date Created: </b>October 26, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.02a | 10/26/2016: Initial commit </li> 
 *      </ul>
 */
interface Actor {
    void act();
}
