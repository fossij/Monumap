package edu.wit.mobileapp.monumap.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Edge {
    private Node m_PointA;
    private Node m_PointB;
    private double m_FixedDistance = Double.NEGATIVE_INFINITY;
    private List<EdgeAttribute> m_Attributes = new ArrayList<>();

    // Description:
    // Two nodes must be supplied to define an edge
    public Edge(Node a, Node b) {
        m_PointA = a;
        m_PointB = b;
    }

    // Description:
    // If a null point is part of the edge, returns true
    public boolean isNotInitialized() {
        return (m_PointA == null || m_PointB == null);
    }

    // Description:
    // Set the fixed distance between the two points
    // as opposed to using the distance function
    public void setFixedDistance(double FixedDistance) {
        this.m_FixedDistance = FixedDistance;
    }

    // Description:
    // Stop using the fixed distance, returns the distance that was used instead
    public double removeFixedDistance() {
        if (m_FixedDistance == Double.NEGATIVE_INFINITY) {
            return m_FixedDistance;
        }
        double toReturn = m_FixedDistance;
        m_FixedDistance = Double.NEGATIVE_INFINITY;
        return toReturn;
    }

    // Description:
    // Returns the distance between the two nodes.
    // Does not take into account multiple floors,
    // so a fixed distance must be used for stairs/elevators.
    public double getDistance() {
        if (isNotInitialized()) {
            return -1;
        }

        if (m_FixedDistance != Double.NEGATIVE_INFINITY) {
            return m_FixedDistance;
        }

        double ax = m_PointA.getX();
        double ay = m_PointA.getY();
        double bx = m_PointB.getX();
        double by = m_PointB.getY();
        return Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(ay - by, 2));
    }

    // Description:
    // Checks whether the part of the edge contains the node
    public boolean contains(int id) {
        if (isNotInitialized()) {
            return false;
        }
        return (m_PointA.getId() == id || m_PointB.getId() == id);
    }

    // Description:
    // Returns Point A
    public Node getPointA() {
        return m_PointA;
    }

    // Description:
    // Retruns Point B
    public Node getPointB() {
        return m_PointB;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Objects.equals(m_PointA, edge.m_PointA) &&
                Objects.equals(m_PointB, edge.m_PointB);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_PointA, m_PointB);
    }

    @Override
    public String toString() {
        return String.format("It is %.2f from %s, (%.2f, %.2f) to %s, (%.2f, %.2f).", getDistance(), m_PointA, m_PointA.getX(), m_PointA.getY(), m_PointB, m_PointB.getX(), m_PointB.getY());
    }

    // Description:
    // Add strings to label an edge as stairs, an elevator, hallway, etc.
    public boolean addAttribute(EdgeAttribute s) {
        if (!m_Attributes.contains(s)) {
            m_Attributes.add(s);
            return true;
        }
        return false;
    }

    // Description:
    // Return the whether an edge has a particular attribute
    public boolean getAttribute(EdgeAttribute att) {
        return m_Attributes.contains(att);
    }

    // Description
    // Swaps the order of the nodes, which is A which is B
    public void swapNodes() {
        Node temp = m_PointA;
        m_PointA = m_PointB;
        m_PointB = temp;
    }

}

