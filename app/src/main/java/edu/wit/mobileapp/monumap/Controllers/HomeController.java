package edu.wit.mobileapp.monumap.Controllers;

import android.content.Context;

import java.util.LinkedList;
import java.util.List;

import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.Entities.RouteParser;
import edu.wit.mobileapp.monumap.Home;
import edu.wit.mobileapp.monumap.Mapping.Map;
import edu.wit.mobileapp.monumap.Mapping.Node;
import edu.wit.mobileapp.monumap.Models.HomeModel;
import edu.wit.mobileapp.monumap.Pathfinder;
import edu.wit.mobileapp.monumap.R;

import static android.content.Context.MODE_PRIVATE;

public class HomeController implements Controller {
    protected Home m_View;
    protected HomeModel m_Model;
    protected Context m_Context;
    protected List<Map> m_AvailableMaps;

    public HomeController(Home home, Context context) {
        m_View = home;
        m_Context = context;
    }

    @Override
    public void open() {
        m_Model = new HomeModel(this, m_View, m_Context);
    }

    public HomeModel getModel() {
        return m_Model;
    }

    public List<String> getBuildingNames() {
        if(m_AvailableMaps == null){
            fetchMaps();
        }

        List<String> toReturn = new LinkedList<>();
        for(int i = 0; i < m_AvailableMaps.size(); i++){
            toReturn.add(m_AvailableMaps.get(i).getName());
        }
        return toReturn;
    }

    protected void fetchMaps(){
        m_AvailableMaps = m_Model.getListOfMaps();
    }

    public Route pathFind(String building1, String room1, String building2, String room2){
        Map foundMap = null;
        Node begin = null;
        Node end = null;
        for(Map m: m_AvailableMaps){
            if(m.getName().equals(building1)){
                foundMap = m;
                for(Node n: m.getNodes()){
                    if(n.getName().equals(room1)){
                        begin = n;
                    }
                    if(n.getName().equals(room2)){
                        end = n;
                    }
                    if(begin!=null && end!=null){
                        break;
                    }
                }
                break;
            }
        }
        if(foundMap == null){
            return null;
        }
        else{
            boolean wheelChairAccess = m_Context.getSharedPreferences(m_Context.getString(R.string.preferences), MODE_PRIVATE).getBoolean(m_Context.getString(R.string.sp_wheelchairAccessibilityEnabled), false);
            Pathfinder pf = new Pathfinder(foundMap, wheelChairAccess);
            return RouteParser.parse(pf.makeRoute(begin, end), foundMap.getName());
        }
    }

    public List<String> getRooms(String buildingName){
        LinkedList<String> toReturn = new LinkedList<>();
        for(Map m :m_AvailableMaps){
            if(m.getName().equals(buildingName)){
                for(Node n : m.getNotableNodes()){
                    toReturn.add(n.getName());
                }
                break;
            }
        }
        return toReturn;
    }

    public Map getBuilding(String name){
        fetchMaps();
        for(Map m: m_AvailableMaps){
            if(m.getName().equals(name)){
                return m;
            }
        }
        return null;
    }

    @Override
    public void close() {
        m_Model.close();
    }
}
