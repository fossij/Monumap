package edu.wit.mobileapp.monumap;

import android.Manifest;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.wit.mobileapp.monumap.Dialogs.Settings;
import edu.wit.mobileapp.monumap.Entities.Direction;
import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Location;
import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.Mapping.Edge;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initializeStorage();

        //TODO CHECK IF IT IS okay to move to api 23 from 22 with team so we can getr a bluetooth prompt
        // for android 10 devices one must get permission for coarse location in order to use beacon services
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);

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

        // get list of options for each dropdown and display

        // get dropdown selections on button press
        Button calculateRoute = findViewById(R.id.calculate_route);
        calculateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get start location data --> save to route
                Location start = new Location("Wentworth", 2, 208);

                // get end location data --> save to route
                Location destination = new Location("Wentworth", 1, 107);

                // calculate route data
                ArrayList<Instruction> instructions = new ArrayList<>();
                instructions.add(new Instruction("Take a left", Direction.LEFT, 1, 1));
                instructions.add(new Instruction("Take a right", Direction.RIGHT, 1, 1));
                instructions.add(new Instruction("Take the stairs", Direction.STAIRS, 1, 1));
                //instructions.add(new Instruction("Take a left", Direction.LEFT, 1, 1));
                //instructions.add(new Instruction("Take a right", Direction.RIGHT, 1, 1));
                //instructions.add(new Instruction("Take the stairs", Direction.STAIRS, 1, 1));
                //instructions.add(new Instruction("Take a left", Direction.LEFT, 1, 1));
                //instructions.add(new Instruction("Take a right", Direction.RIGHT, 1, 1));
                //instructions.add(new Instruction("Take the stairs", Direction.STAIRS, 1, 1));
                int duration = 3;
                int distance = 3;
                //ToDo enter in Lniked LIst in order to pass Edge list to instructions
                // store in new route and go to instructions page
                Route route = new Route(instructions, start, destination, duration, distance, 0,);
                Intent i = new Intent(Home.this, Instructions.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("currentRoute", route);
                i.putExtras(bundle);
                startActivity(i);
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
}
