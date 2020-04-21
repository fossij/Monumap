package edu.wit.mobileapp.monumap;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import edu.wit.mobileapp.monumap.Mapping.Edge;
import edu.wit.mobileapp.monumap.Mapping.JsonMapParser;
import edu.wit.mobileapp.monumap.Mapping.Map;
import edu.wit.mobileapp.monumap.Mapping.Node;


public class Pathfinder {

    //map from JSONMapParser
    private Map map;
    //previous and distance arrays
    private HashMap<Integer, Integer> previous;
    private HashMap<Integer, Double> distance;

    //constructor, sets map attribute to input map and initializes previous and distance arrays
    public Pathfinder(Map m, boolean wheelChairAccess){
        this.previous = new HashMap<>();
        this.distance = new HashMap<>();
        for(Node n: m.getNodes()){
            distance.put(n.getId(), 0.0);
            previous.put(n.getId(), 0);
        }
        if(wheelChairAccess)
        {
            this.map = m.getHandiMap();
        } else {
            this.map = m;
        }
    }

    //runs dijkstra's algorithm with designated source node, modifies previous and distance arrays
    private void findPaths(Node source)
    {
        //dijkstra's algorithm
        ArrayList<Integer> unvisited = new ArrayList<Integer>();
        ArrayList<Integer> visited = new ArrayList<Integer>();
        int src = source.getId();
        for(Node n: map.getNodes()){
            distance.put(n.getId(), 0.0);
            previous.put(n.getId(), 0);
        }
        //initializing distances and unvisited
        for(int i: distance.keySet())
        {
            distance.put(i, Double.MAX_VALUE);
            unvisited.add(i);
        }
        distance.put(src, 0.0);
        //main loop
        while(visited.size() != map.getNodes().size())
        {
            //get the id of the node with the shortest distance from src
            int shortestID = 0;
            double shortest = Double.MAX_VALUE;
            for(int i: unvisited)
            {
                if(visited.contains(i))
                {
                    //skip over nodes that have been visited
                    continue;
                }
                if(distance.get(i) < shortest)
                {
                    shortestID = i;
                    shortest = distance.get(i);
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
                double newDistance = distance.get(shortestID) + e.getDistance();
//                System.out.println("Calculated distance for " + e.getPointB().getId() + ": " + newDistance);
                if(isNodeB)
                {
                    if(newDistance <= distance.get(e.getPointA().getId()))
                    {
                        distance.put(e.getPointA().getId(), newDistance);
                        previous.put(e.getPointA().getId(),  shortestID);
                    }
                } else {
                    if(newDistance <= distance.get(e.getPointB().getId()))
                    {
                        distance.put(e.getPointB().getId(), newDistance);
                        previous.put(e.getPointB().getId(), shortestID);
                    }
                }
            }
            //add shortestID (current visiting node) to visited list
            visited.add(shortestID);
        }
        previous.put(src, -1);
    }

    //for use in test
    public Map getMap()
    {
        return this.map;
    }

    //for choosing a different map
    public void setMap(Map m)
    {
        this.map = m;
    }

    //goes through previous array and saves edges from destination node to start node
    private LinkedList<Edge> findRoute(Node dest)
    {
        int currNode = dest.getId();
        LinkedList<Edge> route = new LinkedList<Edge>();
        while(currNode != -1)
        {
            //add an edge to route
            List<Edge> possibleEdges = map.getEdges(currNode);
            for(Edge e : possibleEdges)
            {
                if(e.contains(previous.get(currNode)))
                {
                    route.add(0, e);
                }
            }
            //set currNode to previous[currNode]
            currNode = previous.get(currNode);
        }

        return route;
    }

    //calls findPaths() and findRoute() for return to RouteParser
    //this is the main method that other classes in the app will use
    public LinkedList<Edge> makeRoute(Node source, Node destination)
    {
        findPaths(source);
        LinkedList<Edge> route = findRoute(destination);

        return route;
    }

    //small test using test map
    public static void main(String args[])
    {
        //make the sample map
        Pathfinder temp = new Pathfinder(JsonMapParser.testMap(), false);
        //pretend the start is ________
        Node src = temp.getMap().getNode(4);
        Node dest = temp.getMap().getNode(11);
        //run the thing
        LinkedList<Edge> rooot = temp.makeRoute(src, dest);
        //see if it's true?
        System.out.println("NodeID---Distance---Previous");
        for(int i=0; i<temp.map.getNodes().size(); i++)
        {
            System.out.println(i + "---" + temp.distance.get(i) + "---" + temp.previous.get(i));
        }
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
