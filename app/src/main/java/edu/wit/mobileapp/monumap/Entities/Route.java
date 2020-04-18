package edu.wit.mobileapp.monumap.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.wit.mobileapp.monumap.Mapping.IBeaconCallBackInterface;
import edu.wit.mobileapp.monumap.Mapping.IBeaconID;

public class Route implements Serializable {
    private ArrayList<Instruction> instructions;
    private Location start;
    private Location destination;
    private int duration;
    private int distance;
    private int id;

    public Route(ArrayList<Instruction> instructions, Location start, Location destination, int duration, int distance, int id) {
        this.instructions = instructions;
        this.start = start;
        this.destination = destination;
        this.duration = duration;
        this.distance = distance;
        this.id = id;
    }

    public ArrayList<Instruction> getInstructions() {
        return this.instructions;
    }

    public Location getStart() {
        return this.start;
    }

    public Location getDestination() {
        return this.destination;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getDistance() {
        return this.distance;
    }

    public int getId() {
        return this.id;
    }

    public List<IBeaconID> getBeacons(){
        LinkedList<IBeaconID> toReturn = new LinkedList<>();

        for(int i = 0; i < instructions.size(); i++){
            if(instructions.get(i).getIBeacon() != null){
                toReturn.add(instructions.get(i).getIBeacon());
            }
        }

        return toReturn;
    }
}
