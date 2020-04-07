package edu.wit.mobileapp.monumap.Models;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wit.mobileapp.monumap.Controllers.HomeController;
import edu.wit.mobileapp.monumap.Home;
import edu.wit.mobileapp.monumap.Mapping.JsonMapParser;
import edu.wit.mobileapp.monumap.Mapping.Map;

public class HomeModel implements MonumapModel {
    protected HomeController m_Controller;
    protected Home m_View;
    protected Context m_Context;

    public HomeModel(HomeController controller, Home view, Context context){
        m_View = view;
        m_Context = context;
        m_Controller = controller;
    }

    @Override
    public void open() {

    }

    @Override
    public void close() {

    }

    public List<Map> getListOfMaps(){
        List<Map> toReturn = new ArrayList<>();
        try {
            String path = "";
            String[] list = m_Context.getAssets().list(path);
            if (list.length > 0) {
                // This is a folder
                for(int i = 0; i < list.length; i++){
                    Map m = JsonMapParser.parseMap(list[i]);
                    if(!m.getName().equals("Error")){
                        toReturn.add(m);
                    }
                }
            }
        }
        catch (IOException e) {
            //return false;
        }

        return toReturn;
    }
}
