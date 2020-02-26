package edu.wit.mobileapp.monumap.Mapping;
import android.content.Context;
import com.google.gson.Gson;
import java.io.InputStream;

public class JsonMapParser {

    private Context m_Context;

    // Description:
    // Used to parse map json files, context is needed to read files.
    public JsonMapParser(Context context){
        this.m_Context = context;
    }

    // Description:
    // Reads in a Json file from the filename/path
    public Map parseMap(String jsonFile){
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
    public Map testMap(){
        return parseMap("sample.json");
    }

}
