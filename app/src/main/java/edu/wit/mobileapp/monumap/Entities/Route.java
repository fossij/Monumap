package edu.wit.mobileapp.monumap.Entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import edu.wit.mobileapp.monumap.Mapping.Edge;

public class Route implements Serializable {
    private ArrayList<Instruction> instructions;
    private Location start;
    private Location destination;
    private int duration;
    private int distance;
    private int id;

    //Christian
    private LinkedList <Edge> beaconOrder; // looks like the pathfinder .getRoute() returns the route  with this route we can get the beacon ID's from the nodes

    public Route(ArrayList<Instruction> instructions, Location start, Location destination, int duration, int distance, int id, LinkedList<Edge> beaconOrder) {
        this.instructions = instructions;
        this.start = start;
        this.destination = destination;
        this.duration = duration;
        this.distance = distance;
        this.id = id;
        //christian
        this.beaconOrder = beaconOrder;
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
    //christian
    public LinkedList<Edge> getBeaconOrder(){return this.beaconOrder;}
}
