package edu.wit.mobileapp.monumap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Location;
import edu.wit.mobileapp.monumap.Entities.Route;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // get list of options for each dropdown and display

        // get dropdown selections on button press
        Button calculateRoute = findViewById(R.id.calculate_route);
        calculateRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get start location data --> save to route
                Location start = null;

                // get end location data --> save to route
                Location destination = null;

                // calculate route data
                ArrayList<Instruction> instructions = null;
                int duration = 0;
                int distance = 0;

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
}
