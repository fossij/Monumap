package edu.wit.mobileapp.monumap.Mapping;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {
    private HashMap<Integer, Node> m_Nodes = new HashMap();
    private List<Edge> m_Edges = new ArrayList<>();
    private String m_Name;

    public Map(String name){
        m_Name = name;
    }

    // Description:
    // Used to retrieve the name of the map/building
    public String getName(){
        return m_Name;
    }

    // Description:
    // Used to add an edge to a map, returns true if path was made,
    // returns false if a path already exists, or nodes weren't found.
    public boolean addEdge(int id1, int id2) {
        if (id1 == id2) {
            return false;
        }

        Node a = m_Nodes.get(id1);
        Node b = m_Nodes.get(id2);
        if (a == null || b == null) {
            return false;
        }

        m_Edges.add(new Edge(a, b));

        return true;
    }

    // Description:
    // Used to add an edge to a map, returns true if path was made,
    // returns false if a path already exists, or nodes weren't found.
    public boolean addEdge(Edge edge) {
        if (m_Edges.contains(edge)) {
            return false;
        }

        int id1 = edge.getPointA().getId();
        int id2 = edge.getPointB().getId();

        Node a = m_Nodes.get(id1);
        Node b = m_Nodes.get(id2);
        if (a == null || b == null) {
            return false;
        }

        m_Edges.add(edge);
        return true;
    }

    // Description:
    // Used to add a node to the map, returns true if node was able to be added
    // returns false if the node already exists
    public boolean addNode(Node toAdd) {
        if (m_Nodes.containsKey(toAdd.getId())) {
            return false;
        }

        m_Nodes.put(toAdd.getId(), toAdd);
        return true;
    }

    // Description:
    // returns a list of edges connected to a node
    public List<Edge> getEdges(int id) {
        ArrayList<Edge> toReturn = new ArrayList();
        for (Edge e : m_Edges) {
            if (e.contains(id)) {
                toReturn.add(e);
            }
        }

        return toReturn;
    }

    // Description:
    // Returns a list of all the edges in the map
    public List<Edge> getEdges() {
        return m_Edges;
    }

    // Description:
    // returns a list of all the nodes
    public List<Node> getNodes() {
        return new ArrayList<>(m_Nodes.values());
    }

    // Description:
    // Returns a Node corresponding to an id,
    // if no node is found, null is returned
    public Node getNode(int id) {
        return m_Nodes.get(id);
    }

    // Description:
    // Returns whether or not the id to a node is contained in the map
    public boolean containsNode(int id) {
        return m_Nodes.containsKey(id);
    }

    // Description:
    // Returns a new Map object without Edges with attribute 'STAIRS'
    public Map getHandiMap()
    {
        Map newMap = new Map(this.m_Name + "_handimap");
        EdgeAttribute stairs = EdgeAttribute.STAIRS;
        for(Node n : getNodes())
        {
            newMap.addNode(n);
        }
        for(Edge e : m_Edges)
        {
            if(e.getAttribute(stairs))
            {
                continue;
            } else {
                newMap.addEdge(e);
            }
        }
        return newMap;
    }
}

