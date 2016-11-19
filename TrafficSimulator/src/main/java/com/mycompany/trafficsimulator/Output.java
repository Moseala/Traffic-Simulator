/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.trafficsimulator;

/**
 *
 * @author Joey Potter
 */
public class Output 

{
    private String destination;
    private int signalType;
    private int waitTime;
    private int carAlive;
    
    public Output()
    {
        this.destination = "";
        this.carAlive = 0;
        this.signalType = 0;
        this.waitTime = 0;
    }
    
    public Output(String destination, int carAlive, int signalType, int waitTime)
    {
        this.destination = destination;
        this.carAlive = carAlive;
        this.signalType = signalType;
        this.waitTime = waitTime;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getSignalType() {
        return signalType;
    }

    public void setSignalType(int signalType) {
        this.signalType = signalType;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public int getCarAlive() {
        return carAlive;
    }

    public void setCarAlive(int carAlive) {
        this.carAlive = carAlive;
    }
    
    
    
}
    

