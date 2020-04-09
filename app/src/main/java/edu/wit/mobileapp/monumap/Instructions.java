package edu.wit.mobileapp.monumap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import edu.wit.mobileapp.monumap.Adapters.InstructionsListAdapter;
import edu.wit.mobileapp.monumap.Dialogs.Cancel;
import edu.wit.mobileapp.monumap.Dialogs.Complete;
import edu.wit.mobileapp.monumap.Dialogs.InstructionInfo;
import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.Mapping.Edge;
import edu.wit.mobileapp.monumap.Mapping.Node;

public class Instructions extends AppCompatActivity implements BeaconConsumer {
    private Route currentRoute;
    private InstructionsListAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<Instruction> instructions;
    private Stack<Instruction> previousInstructions;
    private BottomNavigationView mBottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_prev:
                    previousOperation(item);
                    return true;
                case R.id.nav_next:
                    nextOperation(item);
                    return true;
            }
            return false;
        }
    };
    //Christian
    // hold list of JSON NODES FROM PATHER FINDER ALOGRITM\
    private BeaconManager beaconManager = null;
    Region expectedRegion;
    Region unexpectedRegion;
    private LinkedList<Edge> beaconOrder; // Intended to get the edgelist that the pathfinding produces for the shortest path
    private Edge beaconOrderArray [];// maybe change this for StartScanning method not sure if Im using this correctly
    int beaconEdgeIndexer;
    int beaconIdListSize = beaconOrder.size();
    Boolean onCorrectPath= true;



    Boolean isScanning = null;
    int currentBeaconListIndex =0;// used to index beaconlist from pathfinding


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);


        // initialize data
        currentRoute = (Route) getIntent().getSerializableExtra("currentRoute");
        instructions = (ArrayList<Instruction>) currentRoute.getInstructions().clone();     // clone to not overwrite base instructions for route
        //christian
        beaconOrder = currentRoute.getBeaconOrder();
        previousInstructions = new Stack<>();

        // store new route in recent routes
        RecentRoutes.updateRecentRoutes(getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE), currentRoute);

        // create nav menu
        mBottomNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (instructions.size() == 1) {
            MenuItem nextButton = mBottomNavigationView.getMenu().findItem(R.id.nav_next);
            nextButton.setTitle("Complete Route");
        }

        // create progress bar
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setMax(instructions.size());

        // generate title
        TextView routeDescriptionText = findViewById(R.id.route_description);
        routeDescriptionText.setText(createRouteDescriptionText());

        // generate instructions
        adapter = new InstructionsListAdapter(this, 0, instructions);
        ListView listView = findViewById(R.id.instructions_list);
        listView.setAdapter(adapter);

        // create click listener for list items
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InstructionInfo instructionInfo = new InstructionInfo();
                instructionInfo.setInstruction(instructions.get(i), (i + 1) + previousInstructions.size());
                instructionInfo.show(getSupportFragmentManager(), "InstructionInfoDialog");
            }
        });

        //Create Beacon Manager Instance CHRISTIAN
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                // Android Beacon Library works with Multiple formats; this is the format for IBeacons
                        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //allows beacon manager to have access to android resources
        beaconManager.bind(this);
        startScanning();
    }
    // added
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    // options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.cancel_button) {
            Cancel cancel = new Cancel();
            cancel.show(this.getSupportFragmentManager(), "CancelDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    // helper methods
    private String createRouteDescriptionText() {
        return getString(R.string.route_description, this.currentRoute.getStart().getLocationName(),
                this.currentRoute.getDestination().getLocationName());
    }

    private void previousOperation(MenuItem item) {
        // get previous instruction (shift list down) --> just add last item from stack to instructions list
        Instruction previousInstruction = previousInstructions.pop();
        instructions.add(0, previousInstruction);
        adapter.notifyDataSetChanged();
        progressBar.setProgress(progressBar.getProgress() - 1);

        // if next instruction was last, change next button text back to "next" and remove
        if (instructions.size() == 2) {
            // change button text back to "next"
            MenuItem next = mBottomNavigationView.getMenu().findItem(R.id.nav_next);
            next.setTitle("Next");
        }

        // if return back to first instruction, make previous button invisible again
        if (previousInstructions.isEmpty()) {
            item.setVisible(false);
        }
    }

    private void nextOperation(MenuItem item) {
        // get next instruction (shift list up) --> just remove first item from instructions list and push to stack
        Instruction currentInstruction = instructions.remove(0);
        previousInstructions.push(currentInstruction);
        adapter.notifyDataSetChanged();
        progressBar.setProgress(progressBar.getProgress() + 1);

        // if last instruction, change button to complete route
        if (instructions.size() == 1) {
            // change next button text to "complete route"
            item.setTitle("Complete Route");
        }

        // if route completed, create complete dialog
        if (instructions.isEmpty()) {
            Complete complete = new Complete();
            complete.show(this.getSupportFragmentManager(), "CompleteDialog");
            return;
        }

        // set previous button to visible if passed first instruction
        MenuItem prev = mBottomNavigationView.getMenu().findItem(R.id.nav_prev);
        if (!prev.isVisible()) {
            prev.setVisible(true);
        }
    }

    // Monitoring Example altbeacon.github.io sample code
    @Override
    //TODO figure out/ finalize  logic for continuing on to the next node
    // make sure that the null wild card is  only triggered if  the it runs into anything excpet the expected region
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
               /* displayAlert("didEnterRegion", "EnteringRegion: " + region.getUniqueId() +
                        "  Beacon detected UUID/major/minor: " + region.getId1() + "/ " + region.getId2() + "/ " + region.getId3());

                */

                if (region.getId1() == null) {
                    //reRoute()
                    // update beaconmanager with new route

                } else {
                    //the correct node was found, increment indexer and  start scanning
                   beaconEdgeIndexer ++;
                   // TODo create/find method that shall wait fo did exit region
                }


            }


            @Override
            public void didExitRegion(Region region) {
                /*
                displayAlert("didExitRegion", "ExitingRegion" + region.getUniqueId() +
                        "Beacon lost UUID/major/minor:" + region.getId1() + "/ " + region.getId2() + "/ " + region.getId3());
                */
                //could use for location; need to find good use for this. depends on range size; with a small range size as desided on right now to decrease potental going through celings
                //I cannot think of a use

                // Use stop scanning for the specific region, already passed
                stopScanning(region);
                beaconEdgeIndexer ++;
            }

            @Override// need to implement because of  Monitor Notifier interface
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });
    }

    // method called when startScanning button pressed; specifies region and calls beaconManger to search for Raspberry Pi
    private void startScanning() {
        //Log.d(TAG, "-----------startScanning()----");
        //Define Region to search for; Region is made up of Identifers that correspoindg a specific beacon
        //define strings for IDs
        String uU = "";
        String major;
        String minor;

        //ToDo debug datatype disagreement do i have to create an Edge[] constructor
        beaconOrderArray= beaconOrder.toArray(); //trying to convert Linked list of edge to Array list so I can index everytime a beacon is successfully found

        // get current node iterating through
        Node expectedNode = beaconOrderArray[beaconEdgeIndexer].getPointB();
        major =  expectedNode.getbeaconMajorID();
        minor = expectedNode.getName();// possibly name of room? Parse TOdO parse room number from name
        //ToDo confused on why this dosent work maybeFIX: https://altbeacon.github.io/android-beacon-library/javadoc/org/altbeacon/beacon/Identifier.html#Identifier-byte:A-
        Identifier uUID = Identifier.parse(uU);
        Identifier majorId = Identifier.parse(major);
        Identifier minorId = Identifier.parse(minor);

        //if we havent incremented through each Region  provided from the pathfinding  keep going though the list
        if (beaconEdgeIndexer< beaconIdListSize)
            try {

                expectedRegion = new Region("nextRegion", uUID, majorId,minorId);
                //null is a wildcard which means search for any beacon
                unexpectedRegion = new Region("unExpectedRegion",
                        null, null, null);

                beaconManager.startMonitoringBeaconsInRegion(expectedRegion);
                beaconManager.startMonitoringBeaconsInRegion(unexpectedRegion);

                isScanning = true;
                //RemoteException to catch Errors with the Android services beaconManager is accessing
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        else
        {
            //TODo implement
            // You have made it to your destination
        }

    }

    // comments same as startScanning()
    private void stopScanning(Region region) {
        //Log.d(TAG, "------------stopScanning()---");

        try {
            /*
            Region region = new Region("RegionPiDemo",
                    null, null, null);*/
            beaconManager.stopMonitoringBeaconsInRegion(region);
            //isScanning = false;
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    ///////////////
    //
    ///////////////
}
