package edu.wit.mobileapp.monumap;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Stack;

import edu.wit.mobileapp.monumap.Adapters.InstructionsListAdapter;
import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Route;

public class Instructions extends AppCompatActivity {
    private Route currentRoute;
    private InstructionsListAdapter adapter;

    private ArrayList<Instruction> instructions;
    private Stack<Instruction> previousInstructions;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_prev:
                    // get previous instruction (shift list down) --> just add last item from stack to instructions list
                    // if last, complete dialog
                    adapter.notifyDataSetChanged();
                    return true;
                case R.id.nav_next:
                    // get next instruction (shift list up) --> just remove first item from instructions list and push to stack
                    adapter.notifyDataSetChanged();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        // create nav menu
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // initialize data
        currentRoute = (Route) getIntent().getSerializableExtra("currentRoute");
        instructions = (ArrayList<Instruction>) currentRoute.getInstructions().clone();     // clone to not overwrite base instructions for route
        previousInstructions = new Stack<>();

        // generate title
        TextView routeDescriptionText = findViewById(R.id.route_description);
        routeDescriptionText = findViewById(R.id.route_description);
        routeDescriptionText.setText(createRouteDescriptionText());

        // generate instructions
        adapter = new InstructionsListAdapter(this, 0, instructions);
        ListView listView = findViewById(R.id.instructions_list);
        listView.setAdapter(adapter);

        // create click listener for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // do something with instruction 'i' on click?
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.cancel_button) {
            // cancel route dialog
        }

        return super.onOptionsItemSelected(item);
    }

    private String createRouteDescriptionText() {
        return getString(R.string.route_description, this.currentRoute.getStart().getLocationName(),
                this.currentRoute.getDestination().getLocationName());
    }
}
