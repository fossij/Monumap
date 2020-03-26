package edu.wit.mobileapp.monumap;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import edu.wit.mobileapp.monumap.Entities.Direction;
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
                Location start = new Location("Wentworth", 2, 205);

                // get end location data --> save to route
                Location destination = new Location("Wentworth", 1, 107);

                // calculate route data
                ArrayList<Instruction> instructions = new ArrayList<>();
                instructions.add(new Instruction("Take a left", Direction.LEFT, 1, 1));
                instructions.add(new Instruction("Take a right", Direction.RIGHT, 1, 1));
                instructions.add(new Instruction("Go down the stairs", Direction.STAIRS, 1, 1));
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
}
