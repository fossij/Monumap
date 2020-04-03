package edu.wit.mobileapp.monumap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import edu.wit.mobileapp.monumap.Adapters.RecentRoutesListAdapter;
import edu.wit.mobileapp.monumap.Entities.Route;

public class RecentRoutes extends AppCompatActivity {
    private Route selectedRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_routes);

        // take in list and initialize
        ArrayList<Route> tmpRecentRoutesList = (ArrayList<Route>) getIntent().getSerializableExtra("recentRoutesList");
        ArrayList<Route> recentRoutesList = new ArrayList<>();

        // created shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
        int numRoutes = sharedPreferences.getInt(getString(R.string.sp_recentRoutesNumber), 1);
        if(numRoutes > tmpRecentRoutesList.size()) {
            numRoutes = tmpRecentRoutesList.size();
        }
        for(int i = 0; i < numRoutes; i++) {
            recentRoutesList.add(tmpRecentRoutesList.get(i));
        }

        // set home button listener
        Button homeButton = findViewById(R.id.recent_routes_home);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RecentRoutes.this, Home.class);
                startActivity(i);
            }
        });

        // set calculate route button listener
        final Button calculateButton = findViewById(R.id.recent_routes_calculate_route);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // add route to top of recent routes

                Intent i = new Intent(RecentRoutes.this, Instructions.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("currentRoute", selectedRoute);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        // set adapter
        final RecentRoutesListAdapter adapter = new RecentRoutesListAdapter(getApplicationContext(), 0, recentRoutesList);
        ListView recentRoutesListView = findViewById(R.id.recent_routes_list);
        recentRoutesListView.setAdapter(adapter);
        recentRoutesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedRoute = adapter.getItem(i);
                calculateButton.setVisibility(View.VISIBLE);
            }
        });
    }
}
