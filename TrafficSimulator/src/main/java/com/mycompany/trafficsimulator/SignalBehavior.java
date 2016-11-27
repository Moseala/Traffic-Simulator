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
 *          <li> 1.09a | 11/23/2016: Made final variables static so they may be referenced outside this class.</li>
 *          <li> 1.10b | 11/27/2016: Balanced car amount to release, added javadoc.</li>
 *      </ul>
 */
public class SignalBehavior {
    public final int signalType;
    public static final int DESPAWNER = 0;
    public static final int PASSTHROUGH = 1;
    public static final int STOPSIGN = 2;          //releases a car every 2 sec (this is only at T intersections so there are no 4-way stop signs)
    public static final int TRAFFICLIGHTA = 3;     //green for 96 sec
    public static final int TRAFFICLIGHTB = 4;     //green for 36 sec
    public static final int TRAFFICLIGHTC = 5;     //green for 24 sec
    
    /**
     * Constructor for signal type, should only be used with static variables included in this class.
     * @param signalType 
     * @author Erik Clary
     * @since 1.02a
     */
    public SignalBehavior(int signalType) {
        this.signalType = signalType;
    }
    
    /**
     * This method returns the time that a signal should function based on its type.
     * @return The time as int in seconds that this signal should stay on for.
     * @author Erik Clary
     * @since 1.02a
     */
    public int timeToFunction(){
        switch(signalType){
            case STOPSIGN: return 2;
            case TRAFFICLIGHTA: return 96;
            case TRAFFICLIGHTB: return 36;
            case TRAFFICLIGHTC: return 24;
            default: return 1;
        }
    }

    /**
     * This method returns the amount of cars a signal should release based on its type.
     * @return The amount of cars to release during one cycle (second) as int
     * @author Erik Clary
     * @since 1.02a
     */
    public int getCarAmountToRelease() {
        switch(signalType){
            case DESPAWNER: return 15; //always release all cars
            case PASSTHROUGH: return 15;
            case STOPSIGN: return 1;
            case TRAFFICLIGHTA: return 5;
            case TRAFFICLIGHTB: return 3;
            case TRAFFICLIGHTC: return 1;
            default: return 1;
        }
    }
    
}
