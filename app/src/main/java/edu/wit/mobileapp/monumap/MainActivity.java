package edu.wit.mobileapp.monumap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import edu.wit.mobileapp.monumap.Mapping.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Map map = new JsonMapParser(getApplicationContext()).testMap();
//        List<Edge> e = map.getEdges(15);
//        for (int i = 0; i < e.size(); i++) {
//            Log.v("Mmap", e.get(i).toString());
//        }
    }

}
