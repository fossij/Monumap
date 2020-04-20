package edu.wit.mobileapp.monumap.Entities;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;

import edu.wit.mobileapp.monumap.Mapping.*;

public class RouteParser {
    public static Route parse(List<Edge> list) {
        return parse(list, "Building");
    }

    public static Route parse(List<Edge> list, String building) {
        return parse(list, building, building);
    }

    public static Route parse(List<Edge> list, String building1, String building2) {
        if (list.size() < 2) {
            return null;
        }

        for (int i = 1; i < list.size(); i++) {
            orientEdges(list.get(i-1), list.get(i));
        }

        ArrayList<Instruction> instructions = new ArrayList<>();

        double feetPerSecond = 1/(3.1*60);
        double totalDistance = 0;
        List<IBeaconID> acceptableBeacons = new ArrayList<>();
        double continuedDistance = 0;
        for (int i = 0; i < list.size(); i++) {
            Edge currentEdge = list.get(i);

            double distance = currentEdge.getDistance();
            totalDistance += distance;
            Direction d;
            if (i == 0) {
                d = Direction.STRAIGHT;
            } else {
                d = getDirection(list.get(i - 1), currentEdge);
            }

            String text = getText(d, currentEdge.getPointB().getFloor());
            if(currentEdge.getPointB().hasBeacon()){
                acceptableBeacons.add(currentEdge.getPointB().getBeaconID());
            }
            continuedDistance += currentEdge.getDistance();
            if (d == Direction.STRAIGHT && i != list.size() - 1) {
                continue;
            }else{
                if(currentEdge.getPointB().hasBeacon()){
                    IBeaconID thisBeacon = acceptableBeacons.remove(acceptableBeacons.size()-1);
                    int timing = (int) (continuedDistance * feetPerSecond);
                    if (timing<=0){
                        timing = 1;
                    }
                    instructions.add(new Instruction(text, d, timing, (int) continuedDistance, currentEdge.getPointB().getId(), thisBeacon, acceptableBeacons));
                }
//                else if(currentEdge.getPointA().hasBeacon()){
//                    instructions.add(new Instruction(text, d, (int) (continuedDistance * feetPerSecond), (int) continuedDistance, currentEdge.getPointB().getId(), currentEdge.getPointA().getBeaconID()));
//                }
                else{
                    instructions.add(new Instruction(text, d, (int) (continuedDistance * feetPerSecond), (int) continuedDistance, currentEdge.getPointB().getId()));
                }
                continuedDistance = 0;
                acceptableBeacons = new ArrayList<>();
            }

            //instructions.add(new Instruction(text, d, (int) (distance * feetPerSecond), (int) distance));
        }
        Location source = new Location(building1, list.get(0).getPointA().getFloor(), 0);
        Location dest = new Location(building2, list.get(list.size() - 1).getPointB().getFloor(), 0);

        return new Route(instructions, source, dest, (int) (totalDistance * feetPerSecond), (int) totalDistance, 0);
    }

    private static String getText(Direction d, int floor){
        String text;
        switch (d) {
            case LEFT:
                text = "Take a left turn";
                break;
            case RIGHT:
                text = "Take a right turn";
                break;
            case STRAIGHT:
                text = "Go straight to your destination";
                break;
            case BACKWARDS:
                text = "Turn around";
                break;
            case STAIRS:
                text = "Take the stairs to the " + ordinal(floor) + " floor";
                break;
            case ELEVATOR:
                text = "Take the elevator to the " + ordinal(floor) + " floor";
                break;
            default:
                text = "this shouldn't have happened";
        }
        return text;
    }

    private static Node otherNode(Node n, Edge e) {
        if (e.getPointA().equals(n)) {
            return e.getPointB();
        }
        return e.getPointA();
    }

    private static Direction getDirection(Node n1, Node n2, Node n3) {
        // We're assuming the fixed distances of the edges aren't being used here.
        // This is because we need a (x,y) point.

        // Fixed distances should only be used for stairs or elevators, so this should
        // only deal with points on the same plane
        double angle = getAngle(n1, n2, n3);

        if (angle < 45 && angle > -45) {
            return Direction.BACKWARDS;
        } else if (angle > 135 || angle < -135) {
            return Direction.STRAIGHT;
        } else if (angle >= 45) {
            return Direction.RIGHT;
        } else if (angle <= -45) {
            return Direction.LEFT;
        }

        return Direction.STRAIGHT;

    }

    private static double getAngle(Node n1, Node n2, Node n3) {
        double clockwisemul = 1;
        if (n1.getX() - n2.getX() == 0) {
            if (n1.getY() > n2.getY()) {
                clockwisemul *= -1;
            }

            if (n3.getX() < n1.getX()) {
                clockwisemul *= -1;
            }
        } else {
            double slope = (n1.getY() - n2.getY()) / (n1.getX() - n2.getX());
            double b = n1.getY() - slope * n1.getX();
            if (n3.getY() > slope * n3.getX() + b) {
                clockwisemul *= -1;
            }
            if (n1.getX() > n2.getX()) {
                clockwisemul *= -1;
            }
        }

        //System.out.printf("n1: %.2f, %.2f; n2: %.2f, %.2f; n3: %.2f, %.2f; clockwise: %.2f\n",n1.getX(), n1.getY(), n2.getX(), n2.getY(), n3.getX(), n3.getY(), clockwisemul);
        return Math.acos((Math.pow(distance(n2, n1), 2) + Math.pow(distance(n2, n3), 2) - Math.pow(distance(n1, n3), 2))
                / (2 * distance(n2, n1) * distance(n2, n3))) * (180 / Math.PI) * clockwisemul;
    }

    private static double distance(Node P1, Node P2) {
        return Math.sqrt(Math.pow((P1.getX() - P2.getX()), 2) + Math.pow((P1.getY() - P2.getY()), 2));
    }

    private static Direction getDirection(Edge e1, Edge e2) {
        // If the attribute is special, like stairs or elevators,
        // no need to calculate the direction, we have a special direction
        if (e2.getAttribute(EdgeAttribute.ELEVATOR)) {
            return Direction.ELEVATOR;
        } else if (e2.getAttribute(EdgeAttribute.STAIRS)) {
            return Direction.STAIRS;
        } else {
            return getDirection(e1.getPointA(), e2.getPointA(), e2.getPointB());
        }
    }

    private static void orientEdges(Edge e1, Edge e2) {
        // If the last point of the first node is not
        // contained in the second node, the first node must be flipped
        if (!e2.contains(e1.getPointB().getId())) {
            e1.swapNodes();
        }

        // If the first point of the second node isn't the last
        // node of the first point, flip the second node
        if (!e2.getPointA().equals(e1.getPointB())) {
            e2.swapNodes();
        }
    }

    private static String ordinal(int i) {
        String[] sufixes = new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"};
        switch (i % 100) {
            case 11:
            case 12:
            case 13:
                return i + "th";
            default:
                return i + sufixes[i % 10];

        }
    }
}
