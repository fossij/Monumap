package edu.wit.mobileapp.monumap;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.wit.mobileapp.monumap.Adapters.RecentRoutesListAdapter;
import edu.wit.mobileapp.monumap.Dialogs.RecentRoutes;
import edu.wit.mobileapp.monumap.Dialogs.Settings;
import edu.wit.mobileapp.monumap.Entities.Direction;
import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Location;
import edu.wit.mobileapp.monumap.Entities.Route;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        initializeStorage();

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
                Location start = new Location("Wentworth", 2, 205);

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

                // store in new route and go to instructions page
                Route route = new Route(instructions, start, destination, duration, distance, 0);
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
                ArrayList<Route> recentRoutesList = createTestRoutes();
                int numRoutes = sharedPreferences.getInt(getString(R.string.sp_recentRoutesNumber), 1);
                RecentRoutes recentRoutes = new RecentRoutes();
                recentRoutes.setRecentRoutes(recentRoutesList, numRoutes);
                recentRoutes.show(this.getSupportFragmentManager(), "RecentRoutesDialog");
                drawerLayout.closeDrawer(GravityCompat.START);
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

    private ArrayList<Route> createTestRoutes() {
        ArrayList<Route> res = new ArrayList<>();
        for(int i = 0; i < 4; i++) {
            Location start = new Location("Wentworth", 1, i);
            Location destination = new Location("Wentworth", 1, 107);
            ArrayList<Instruction> instructions = new ArrayList<>();
            instructions.add(new Instruction("Take a right", Direction.RIGHT, 1, 1));
            instructions.add(new Instruction("Go down the stairs", Direction.STAIRS, 1, 1));
            int duration = 3;
            int distance = 3;
            res.add(new Route(instructions, start, destination, duration, distance, 0));
        }

        return res;
    }
}
