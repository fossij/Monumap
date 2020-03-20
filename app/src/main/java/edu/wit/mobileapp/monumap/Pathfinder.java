package edu.wit.mobileapp.monumap;

import java.util.ArrayList;
import java.util.List;

import edu.wit.mobileapp.monumap.Entities.Location;
import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.Mapping.Edge;
import edu.wit.mobileapp.monumap.Mapping.JsonMapParser;
import edu.wit.mobileapp.monumap.Mapping.Map;
import edu.wit.mobileapp.monumap.Mapping.Node;

public class Pathfinder {

    //map from JSONMapParser
    private Map map;
    //previous and distance arrays
    private int previous[];
    private double distance[];

    //constructor, should be called in Home activity
    public Pathfinder(String jsonfile){
//        JsonMapParser parseme = new JsonMapParser();
        this.map = JsonMapParser.testMap();
        this.previous = new int[0];
        this.distance = new double[0];
    }

    //runs dijkstra's algorithm with designated source node
    private void findPaths(Node source)
    {
        //dijkstra's algorithm
        ArrayList<Integer> unvisited = new ArrayList<Integer>();
        ArrayList<Integer> visited = new ArrayList<Integer>();
//        int src = source.getId();
        int src = source.getId();
        distance = new double[map.getNodes().size()];
        previous = new int[map.getNodes().size()];
        //initializing distances and unvisited
        for(int i=0; i<distance.length; i++)
        {
            distance[i] = Double.MAX_VALUE;
            unvisited.add(i);
        }
        distance[src] = 0;
        //main loop
        while(visited.size() != map.getNodes().size())
        {
            //get the id of the node with the shortest distance from src
            int shortestID = 0;
            double shortest = Double.MAX_VALUE;
            for(int i=0; i<unvisited.size(); i++)
            {
                if(visited.contains(i))
                {
                    //skip over nodes that have been visited
                    continue;
                }
                if(distance[i] < shortest)
                {
                    shortestID = i;
                    shortest = distance[i];
                }
            }
            //check the neighbors of the unvisited node with the shortest distance to src
            List<Edge> neighbors = map.getEdges(shortestID);
            for(Edge e : neighbors)
            {
                if(visited.contains(e.getPointB().getId()) || visited.contains(e.getPointA().getId()))
                {
                    //skip visited nodes again
                    continue;
                }
                //separate by nodeA or nodeB
                //this is because I don't know if the relevant neighbor is nodeA or nodeB and i can't tell if nodes are adjacent without checking the edges
                Boolean isNodeB = e.getPointB().getId() == shortestID;
                //add distance of unvisited neighbor to distance array if shorter
                double newDistance = distance[shortestID] + e.getDistance();
                System.out.println("Calculated distance for " + e.getPointB().getId() + ": " + newDistance);
                if(isNodeB)
                {
                    if(newDistance <= distance[e.getPointA().getId()])
                    {
                        distance[e.getPointA().getId()] = newDistance;
                        previous[e.getPointA().getId()] = shortestID;
                    }
                } else {
                    if(newDistance <= distance[e.getPointB().getId()])
                    {
                        distance[e.getPointB().getId()] = newDistance;
                        previous[e.getPointB().getId()] = shortestID;
                    }
                }
            }
            //add shortestID (current visiting node) to visited list
            visited.add(shortestID);
        }
        previous[src] = -1;
    }

    public Map getMap()
    {
        return this.map;
    }

    private ArrayList<Edge> findRoute(Node dest)
    {
        //this goes through the paths made by dijkstra's algo and picks the one with the
        //relevant endpoint, converts it into a Route object
        int currNode = dest.getId();
        ArrayList<Edge> route = new ArrayList<Edge>();
        while(currNode != -1)
        {
            //add an edge to route
            List<Edge> possibleEdges = map.getEdges(currNode);
            for(Edge e : possibleEdges)
            {
                if(e.contains(previous[currNode]))
                {
                    route.add(e);
                }
            }
            //set currNode to previous[currNode]
            currNode = previous[currNode];
        }

        return route;
    }

    public Route makeRoute(Node source, Node destination)
    {
        //this converts the whole thing into a Route object for Jose's side of things.
        Location start = new Location(source.getFloorName(), source.getFloor(), source.getId());
        Location dest = new Location(destination.getFloorName(), destination.getFloor(), destination.getId());

        findPaths(source);
        ArrayList<Edge> route = findRoute(destination);

        //need to agree on a mapping format (either add strings to Nodes or add Locations to Nodes, settle on int or double distance, etc)

        return null;
    }

    public static void main(String args[])
    {
        //make the sample map
        Pathfinder temp = new Pathfinder("sample.json");
        //pretend the start is ________
        Node src = temp.getMap().getNode(4);
        //run the thing
        temp.findPaths(src);
        //see if it's true?
        System.out.println("NodeID---Distance---Previous");
        for(int i=0; i<temp.map.getNodes().size(); i++)
        {
            System.out.println(i + "---" + temp.distance[i] + "---" + temp.previous[i]);
        }
        ArrayList<Edge> rooot = temp.findRoute(temp.getMap().getNode(11));
        System.out.println("Route from node 11 to node 4: ");
        double totalDistance = 0.0;
        for(Edge e : rooot)
        {
            System.out.println(e.toString());
            totalDistance += e.getDistance();
        }
        System.out.println("Total distance in route: " + totalDistance);
    }
}
