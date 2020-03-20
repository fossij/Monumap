package edu.wit.mobileapp.monumap.Mapping;
import android.content.Context;
import com.google.gson.Gson;
import java.io.InputStream;

public class JsonMapParser {

    static Context m_Context;

    public static void setContext(Context context) {
        m_Context = context;
    }

    // Description:
    // Used to parse map json files, context is needed to read files.
//    public JsonMapParser(Context context){
//        this.m_Context = context;
//    }

    // Description:
    // Reads in a Json file from the filename/path
    public static Map parseMap(String jsonFile) {
        Gson gson = new Gson();
        try {
            InputStream is = m_Context.getAssets().open(jsonFile);
            java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
            String json = s.hasNext() ? s.next() : "";
            s.close();
            return gson.fromJson(json, Map.class);
        } catch (Exception e) {
            return new Map();
        }
    }

    // Description:
    // returns a testmap for testing
    public static Map testMap() {
        if(m_Context!= null){
            return parseMap("res/raw/sample.json");
        }


        Map map = new Map();

        Node[] nodes = new Node[27];

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = new Node(i, "Node " + i, (i % 3), (i / 3) % 3);
            nodes[i].setFloor((i / 9));
            nodes[i].setFloorName("Floor " + (nodes[i].getFloor() + 1));
            map.addNode(nodes[i]);
        }

        nodes[0].addAttribute("Entrance");

        Edge elevator12 = new Edge(nodes[4], nodes[13]);
        elevator12.setFixedDistance(1);
        elevator12.addAttribute("Elevator");
        map.addEdge(elevator12);

        Edge elevator23 = new Edge(nodes[13], nodes[22]);
        elevator23.setFixedDistance(1);
        elevator23.addAttribute("Elevator");
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
        stairs12.addAttribute("Stairs");
        map.addEdge(stairs12);

        Edge stairs23 = new Edge(nodes[16], nodes[24]);
        stairs23.setFixedDistance(1.41);
        stairs23.addAttribute("Stairs");
        map.addEdge(stairs23);
        return map;
    }

}
