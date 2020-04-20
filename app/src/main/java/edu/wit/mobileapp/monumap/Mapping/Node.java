package edu.wit.mobileapp.monumap.Mapping;
import java.util.ArrayList;
import java.util.List;

public class Node {
    private String m_Name;
    private int m_Id;
    private Point m_Location = new Point(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
    private int m_Floor = Integer.MIN_VALUE;
    private IBeaconID m_BeaconID;
    private String m_FloorName;
    private List<NodeAttribute> m_Attributes = new ArrayList<>();

    // Description:
    // a node is defined as an id number and a name
    public Node(int id) {
        this(id, "Node " + id);
    }

    public Node(int id, String name) {
        this.m_Id = id;
        this.m_Name = name;
    }

    public Node(int id, String name, double x, double y) {
        this.m_Id = id;
        this.m_Name = name;
        this.m_Location = new Point(x, y);
    }

    // Description:
    // Set the x value
    public void setX(double x) {
        m_Location.x = x;
    }

    // Description:
    // Get the x value
    public double getX() {
        return m_Location.x;
    }

    // Description:
    // Set the y value
    public void setY(double y) {
        m_Location.y = y;
    }

    // Description:
    // Get the x value
    public double getY() {
        return m_Location.y;
    }

    // Description:
    // Set the floor value
    public void setFloor(int floor) {
        this.m_Floor = floor;
    }

    // Description:
    // Get the floor value
    public int getFloor() {
        return m_Floor;
    }

    // Description:
    // Set the floor name
    public void setFloorName(String floorName) {
        this.m_FloorName = floorName;
    }

    // Description:
    // Get the floor name
    public String getFloorName() {
        return m_FloorName;
    }

    public boolean hasBeacon(){
        return getBeaconID() != null;
    }

    // Description:
    // Get the IBeaconID
    public IBeaconID getBeaconID() {
        return m_BeaconID;
    }

    // Description:
    // Set the IBeaconID
    public void setBeaconID(IBeaconID BeaconID) {
        this.m_BeaconID = BeaconID;
    }

    // Description:
    // Get the x value
    public int getId() {
        return m_Id;
    }

    public String getName(){
        return m_Name;
    }

    @Override
    public String toString() {
        String toReturn = m_Name;
        if (m_FloorName != null && !m_FloorName.equals("")) {
            toReturn = toReturn + ", on " + m_FloorName;
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object a) {
        if (a == null) {
            return false;
        } else if (!(a instanceof Node)) {
            return false;
        } else {
            return (this.m_Id == ((Node) a).getId());
        }
    }

    // Description:
    // Add strings to label a Node as a classroom, hallway, etc.
    public boolean addAttribute(NodeAttribute s) {
        if (!m_Attributes.contains(s)) {
            m_Attributes.add(s);
            return true;
        }
        return false;
    }

    // Description:
    // Return the whether a node has a particular attribute
    public boolean getAttribute(NodeAttribute att) {
        return m_Attributes.contains(att);
    }

    // Description:
    // Lists all the attributes for the node
    public List<NodeAttribute> getAttributes() {
        return m_Attributes;
    }
}

