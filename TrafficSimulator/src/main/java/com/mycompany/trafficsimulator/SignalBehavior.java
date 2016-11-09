package com.mycompany.trafficsimulator;

/**
 * SignalBehavior class is an object to be used to encapsulate behaviors and actions
 * of a TrafficSignal for it to reference.
 * 
 * @author Erik Clary
 * @version %I%, %G%
 * @since 1.02a
 * <p> <b>Date Created: </b>October 25, 2016 
 * <p> <b>Version Comments:</b> 
 *      <ul> 
 *          <li> 1.02a | 10/26/2016: Initial commit </li> 
 *      </ul>
 */
class SignalBehavior {
    public final int signalType;
    public final int DESPAWNER = 0;
    public final int PASSTHROUGH = 1;
    public final int STOPSIGN = 2;          //releases a car every 2 sec (this is only at T intersections so there are no 4-way stop signs)
    public final int TRAFFICLIGHTA = 3;     //green for 96 sec
    public final int TRAFFICLIGHTB = 4;     //green for 36 sec
    public final int TRAFFICLIGHTC = 5;     //green for 24 sec
    
    public SignalBehavior(int signalType) {
        this.signalType = signalType;
    }
    
    public int timeToFunction(){
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getCarAmountToRelease() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
