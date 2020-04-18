package edu.wit.mobileapp.monumap.Mapping;
import android.content.Context;

import com.google.gson.JsonObject;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JsonMapParser {

    static Context m_Context;

    // Description:
    // Set Context so files can be opened
    public static void setContext(Context context) {
        m_Context = context;
    }

    // Description:
    // Reads in a Json file from the filename/path
    public static Map parseMap(String jsonFile) {
        if(m_Context == null){
            return new Map("Error");
        }
        try {
            InputStream is = m_Context.getAssets().open(jsonFile);
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String json = s.hasNext() ? s.next() : "";
            s.close();
            return stringToMap(json);
        } catch (Exception e) {
            return new Map("Error");
        }
    }

    // Description:
    // returns a testmap for testing
    public static Map testMap() {
        if(m_Context!= null){
            return parseMap("sample.json");
        }
        Map map = new Map("Sample Building");

        Node[] nodes = new Node[27];

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i, "Node " + i, (i % 3), (i / 3) % 3);
            nodes[i].setFloor((i / 9));
            nodes[i].setFloorName("Floor " + (nodes[i].getFloor() + 1));
            map.addNode(nodes[i]);
        }

        nodes[0].addAttribute(NodeAttribute.ENTRANCE);

        Edge elevator12 = new Edge(nodes[4], nodes[13]);
        elevator12.setFixedDistance(1);
        elevator12.addAttribute(EdgeAttribute.ELEVATOR);
        map.addEdge(elevator12);

        Edge elevator23 = new Edge(nodes[13], nodes[22]);
        elevator23.setFixedDistance(1);
        elevator23.addAttribute(EdgeAttribute.ELEVATOR);
        map.addEdge(elevator23);


        for (int i = 0; i < 27; i += 9) {
            map.addEdge(nodes[0 + i].getId(), nodes[1 + i].getId());
            map.addEdge(nodes[1 + i].getId(), nodes[2 + i].getId());
            map.addEdge(nodes[2 + i].getId(), nodes[5 + i].getId());
            map.addEdge(nodes[5 + i].getId(), nodes[8 + i].getId());
            map.addEdge(nodes[8 + i].getId(), nodes[7 + i].getId());
            map.addEdge(nodes[7 + i].getId(), nodes[6 + i].getId());
            map.addEdge(nodes[6 + i].getId(), nodes[3 + i].getId());
            map.addEdge(nodes[3 + i].getId(), nodes[0 + i].getId());

            //walk into the elevator
            map.addEdge(nodes[5 + i].getId(), nodes[4 + i].getId());
        }

        Edge stairs12 = new Edge(nodes[1], nodes[11]);
        stairs12.setFixedDistance(1.41);
        stairs12.addAttribute(EdgeAttribute.STAIRS);
        map.addEdge(stairs12);

        Edge stairs23 = new Edge(nodes[16], nodes[24]);
        stairs23.setFixedDistance(1.41);
        stairs23.addAttribute(EdgeAttribute.STAIRS);
        map.addEdge(stairs23);
        return map;
    }

    // Description:
    // Turn a Map into a JSON String
    public static String toJson(Map map){
        JSONObject obj = new JSONObject();

        JSONArray nodes = new JSONArray();

        for(Node n: map.getNodes()){
            JSONObject node = new JSONObject();
            node.put("ID", n.getId());
            node.put("X", n.getX());
            node.put("Y", n.getY());
            node.put("Floor", n.getFloor());
            node.put("FloorName", n.getFloorName());
            node.put("Name", n.getName());
            if(n.hasBeacon()){
                JSONObject ibec = new JSONObject();
                ibec.put("MajorID", n.getBeaconID().getMajorID());
                ibec.put("MinorID", n.getBeaconID().getMinorID());
                node.put("IBeaconID", ibec);
            }
            JSONArray list = new JSONArray();
            for(int i =0; i < n.getAttributes().size(); i++){
                NodeAttribute nodeAttribute = n.getAttributes().get(i);
                if(nodeAttribute == null){
                    continue;
                }
                //System.out.println(nodeAttribute.toString());
                list.add(nodeAttribute.toString());
            }
            node.put("Attributes", list);
            nodes.add(node);
        }

        JSONArray edges = new JSONArray();
        for(Edge e: map.getEdges()){
            JSONObject edge = new JSONObject();
            edge.put("P1", e.getPointA().getId());
            edge.put("P2", e.getPointB().getId());
            edge.put("Distance", e.getDistance());
            JSONArray list = new JSONArray();
            for(int i =0; i < e.getAttributes().size(); i++){
                EdgeAttribute edgeAttribute = e.getAttributes().get(i);
                if(edgeAttribute == null){
                    continue;
                }
                //System.out.println(edgeAttribute.toString());
                list.add(edgeAttribute.toString());
            }
            edge.put("Attributes", list);
            edges.add(edge);
        }
        obj.put("Name", map.getName());
        obj.put("Nodes", nodes);
        obj.put("Edges", edges);
        return obj.toString();
    }

    // Description:
    // Turn a JSON string into a Map
    public static Map stringToMap(String json){
        JSONParser parser = new JSONParser();

        try {
            JSONObject jsonObject = (JSONObject) parser.parse(json);

            JSONArray nodes = (JSONArray) jsonObject.get("Nodes");
            JSONArray edges = (JSONArray) jsonObject.get("Edges");
            String name = (String) jsonObject.get("Name");
            Map toReturn = new Map(name);
            Iterator<JSONObject> iterator = nodes.iterator();
            while (iterator.hasNext()) {
                JSONObject n = iterator.next();
                int id = (int)(long)n.get("ID");
                Node node = new Node(id, (String)n.get("Name"), (double)n.get("X"), (double)n.get("Y"));
                node.setFloorName((String) n.get("FloorName"));
                node.setFloor((int) (long)n.get("Floor"));
                if(n.keySet().contains("IBeaconID")){
                    JSONObject bec = (JSONObject) n.get("IBeaconID");
                    int becMajor = (int) bec.get("MajorID");
                    int becMinor = (int) bec.get("MinorID");
                    if(becMajor >= 0 && becMinor >= 0)
                        node.setBeaconID(new IBeaconID(becMajor, becMinor));
                }
                JSONArray atts = (JSONArray) n.get("Attributes");
                Iterator<String> attsItt = atts.iterator();
                while(attsItt.hasNext()){
                    String s = attsItt.next();
                    node.addAttribute(NodeAttribute.valueOf(s));
                }
                toReturn.addNode(node);
            }
            iterator = edges.iterator();
            while (iterator.hasNext()) {
                JSONObject e = iterator.next();
                //System.out.println(e);
                Edge edge = new Edge(toReturn.getNode((int)(long)e.get("P1")), toReturn.getNode((int)(long)e.get("P2")));
                edge.setFixedDistance((double)e.get("Distance"));

                JSONArray atts = (JSONArray) e.get("Attributes");
                Iterator<String> attsItt = atts.iterator();
                while(attsItt.hasNext()){
                    String s = attsItt.next();
                    edge.addAttribute(EdgeAttribute.valueOf(s));
                }
                toReturn.addEdge(edge);
            }
            return toReturn;
        } catch (Exception exception) {
            //exception.printStackTrace();
            return new Map("Error");
        }
    }
}
