package edu.wit.mobileapp.monumap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wit.mobileapp.monumap.Controllers.HomeController;
import edu.wit.mobileapp.monumap.Dialogs.NoRoute;
import edu.wit.mobileapp.monumap.Dialogs.Settings;
import edu.wit.mobileapp.monumap.Entities.Direction;
import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Location;
import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.Mapping.JsonMapParser;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;
    private HomeController m_HomeController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initializeStorage();
        JsonMapParser.setContext(getApplicationContext());

        m_HomeController = new HomeController(this, getApplicationContext());
        m_HomeController.open();
        final String tag = "buildings";
        Spinner startBuilding = (Spinner) findViewById(R.id.start_building);
        startBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> newRooms = m_HomeController.getRooms(parent.getSelectedItem().toString());
                setStartRooms(newRooms);
                //Log.v(tag, "Attemping to update");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setStartRooms(new ArrayList<String>());
            }
        });
        setStartBuildings(m_HomeController.getBuildingNames());


        Spinner endBuilding = (Spinner) findViewById(R.id.destination_building);
        endBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<String> newRooms = m_HomeController.getRooms(parent.getSelectedItem().toString());
                setEndRooms(newRooms);
                //Log.v(tag, "Attemping to update");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setEndRooms(new ArrayList<String>());
            }
        });
        setEndBuildings(m_HomeController.getBuildingNames());

        // create toolbar
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);

        // create drawer
        drawerLayout = findViewById(R.id.home_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // listen to nav view clicks
        NavigationView navigationView = findViewById(R.id.home_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // get dropdown selections on button press
        final Button calculateRoute = findViewById(R.id.calculate_route);
        calculateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get dropdown entries
                Spinner startRooms = (Spinner) findViewById(R.id.start_room);
                Spinner endRooms = (Spinner) findViewById(R.id.destination_room);
                Spinner startBuilding = (Spinner) findViewById(R.id.start_building);
                Spinner endBuilding = (Spinner) findViewById(R.id.destination_building);

                Route route = m_HomeController.pathFind(startBuilding.getSelectedItem().toString(), startRooms.getSelectedItem().toString(), endBuilding.getSelectedItem().toString(), endRooms.getSelectedItem().toString());
                if(route == null) {
                    NoRoute noRoute = new NoRoute();
                    noRoute.show(getSupportFragmentManager(), "NoRouteDialog");
                } else {
                    // send route to instructions page
                    Intent i = new Intent(Home.this, Instructions.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("currentRoute", route);
                    i.putExtras(bundle);
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_settings:
                Settings settings = new Settings();
                settings.setSharedPreferences(sharedPreferences);
                settings.show(this.getSupportFragmentManager(), "SettingsDialog");
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_recent_routes:
                Intent i = new Intent(Home.this, RecentRoutes.class);
                startActivity(i);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        // ensure back button closes drawer if open, rather than going back to prev. activity
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    private void initializeStorage() {
        sharedPreferences = getSharedPreferences(getString(R.string.preferences), Context.MODE_PRIVATE);
    }

    private void setStartBuildings(List<String> buildings){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, buildings);
//        String tag = "buildings";
//        Log.v(tag, String.valueOf(buildings.size()));
//        for(int i = 0; i < adapter.getCount(); i++){
//            Log.v(tag, adapter.getItem(i));
//        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner startBuilding = (Spinner) findViewById(R.id.start_building);
        startBuilding.setAdapter(adapter);
        startBuilding.invalidate();
    }

    private void setStartRooms(List<String> rooms){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rooms);
//        String tag = "buildings";
//        Log.v(tag, String.valueOf(rooms.size()));
//        for(int i = 0; i < adapter.getCount(); i++){
//            Log.v(tag, adapter.getItem(i));
//        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner startRooms = (Spinner) findViewById(R.id.start_room);
        startRooms.setAdapter(adapter);
        startRooms.invalidate();
    }

    private void setEndBuildings(List<String> buildings){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, buildings);
//        String tag = "buildings";
//        Log.v(tag, String.valueOf(buildings.size()));
//        for(int i = 0; i < adapter.getCount(); i++){
//            Log.v(tag, adapter.getItem(i));
//        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner endBuilding = (Spinner) findViewById(R.id.destination_building);
        endBuilding.setAdapter(adapter);
        endBuilding.invalidate();
    }

    private void setEndRooms(List<String> rooms){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, rooms);
//        String tag = "buildings";
//        Log.v(tag, String.valueOf(rooms.size()));
//        for(int i = 0; i < adapter.getCount(); i++){
//            Log.v(tag, adapter.getItem(i));
//        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner endRooms = (Spinner) findViewById(R.id.destination_room);
        endRooms.setAdapter(adapter);
        endRooms.invalidate();
    }
}
