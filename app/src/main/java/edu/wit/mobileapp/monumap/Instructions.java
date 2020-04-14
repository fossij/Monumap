package edu.wit.mobileapp.monumap;

import android.os.Bundle;
import android.os.RemoteException;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Stack;

import edu.wit.mobileapp.monumap.Adapters.InstructionsListAdapter;
import edu.wit.mobileapp.monumap.Dialogs.Cancel;
import edu.wit.mobileapp.monumap.Dialogs.Complete;
import edu.wit.mobileapp.monumap.Dialogs.InstructionInfo;
import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Route;

public class Instructions extends AppCompatActivity implements BeaconConsumer {
    private Route currentRoute;
    private InstructionsListAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<Instruction> instructions;
    private Stack<Instruction> previousInstructions;

    //CHRISTIAN for beacons
    private BeaconManager beaconManager = null;
    private Region anyRegion;
    private Region expectedRegion;
    private ArrayList<String> idListDelimeter;// one imp where pathfinding returns a string with both the Major and Minor Id with a delimeter in the middle
    private ArrayList <String> majorIdList;
    private ArrayList <String> minorIdList;
    private String minorIdArray[]={ "0","1","2","3"};
    private String majorIdArray[]={"0","0","0","0"};
    private String majorId;
    private String minorId;

    private int beaconIndexer=0;
    private int beaconIdListSize=4;
    private boolean firstScan= true;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        // initialize data
        currentRoute = (Route) getIntent().getSerializableExtra("currentRoute");
        instructions = (ArrayList<Instruction>) currentRoute.getInstructions().clone();     // clone to not overwrite base instructions for route
        previousInstructions = new Stack<>();

        // store new route in recent routes
        RecentRoutes.updateRecentRoutes(getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE), currentRoute);

        // create nav menu
        mBottomNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if(instructions.size() == 1) {
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

        /////////
        // BEACONS   PLEASE NOTE FOR DEMO PURPOSES I SHALL MAKE  EACH BEACON 1 FOR 1 WITH INSTRUCTIONS; on successful beacon confirmation I shall  move to the next instrcution
        ////////
        //populate the List array with the BeaconIDList generated by the pathfinding
        //TODO populate with actual data


        //create beaconManager object to run scanning of beacons
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                // Android Beacon Library works with Multiple formats; this is the format for IBeacons
                        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //allows beacon manager to have access to android resources
        beaconManager.bind(this);
        startScanning();
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
        if(item.getItemId() == R.id.cancel_button) {
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
        if(instructions.size() == 2) {
            // change button text back to "next"
            MenuItem next = mBottomNavigationView.getMenu().findItem(R.id.nav_next);
            next.setTitle("Next");
        }

        // if return back to first instruction, make previous button invisible again
        if(previousInstructions.isEmpty()) {
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
        if(instructions.size() == 1) {
            // change next button text to "complete route"
            item.setTitle("Complete Route");
        }

        // if route completed, create complete dialog
        if(instructions.isEmpty()) {
            Complete complete = new Complete();
            complete.show(this.getSupportFragmentManager(), "CompleteDialog");
            return;
        }

        // set previous button to visible if passed first instruction
        MenuItem prev = mBottomNavigationView.getMenu().findItem(R.id.nav_prev);
        if(!prev.isVisible()) {
            prev.setVisible(true);
        }
    }

    ////////////
    // BEACON METHODS
    //////////

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
                // if null then a random beacon was found, now we are going to check for the specified beacon in the pathfinder
                if (region.getId2() == null) {

                    startScanning();

                } else {
                    //the correct node was found, set firstScan back to true so that if the program happens to exit the null region it found first it wont reroute
                    firstScan=true;
                }


            }


            @Override
            public void didExitRegion(Region region) {
                /*
                displayAlert("didExitRegion", "ExitingRegion" + region.getUniqueId() +
                        "Beacon lost UUID/major/minor:" + region.getId1() + "/ " + region.getId2() + "/ " + region.getId3());
                */

                // if null region is exitied without entering the expected region, then the null region detectd was something other than expected
                if(region.getId2()==null && firstScan == false)
                {
                    stopScanning(anyRegion);
                    stopScanning(expectedRegion);
                    //reRoute();
                    Toast.makeText(Instructions.this, "Inncorrect beacon Identified!: REROUTE USER", Toast.LENGTH_LONG).show();

                }
                else if (region.getId2().equals(majorId) && firstScan== true)
                {// successful exiting of the expected region; start process over again
                    stopScanning(anyRegion);
                    stopScanning(expectedRegion);
                    beaconIndexer ++;
                    Toast.makeText(Instructions.this, "Correct beacon Identified!: Move on to the next step", Toast.LENGTH_LONG).show();
                    //nextOperation(item);
                    //TODO increment the instructions possibly(dont know how instructions get made from nodes i.e how many nodes per instruction? how many nodes are passed b4 moving to next instruction/ how the could corr)
                    //TODO figure out possible end case so we dont get a null pointer !!!!!!!!!!SOLVED!!!!!- by creating the Identifiers in the conditional statement we should never over incement
                    startScanning();
                }

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

        // create identifiers to specifiy the ID's of the beacons; uUID is constant for it defines a network of proximity beacons
        Identifier uUID = Identifier.parse("11111111-1111-1111-1111-111111111111");

        //if we havent incremented through each Region  provided from the pathfinding  keep going though the list
        if (beaconIndexer < beaconIdListSize)
            try {
                //search for any beacon connection, once conected we shall search for the more specified region
                if (firstScan) {
                    Region anyRegion = new Region("anyRegion", null, null, null);
                    firstScan=false;
                    beaconManager.startMonitoringBeaconsInRegion(anyRegion);

                }
                else//an unspecified beacon has been found, scann for the specified one now to check if that beacon is the correct one
                {
                    Identifier majorId = Identifier.parse(majorIdArray[beaconIndexer]);
                    Identifier minorId = Identifier.parse(minorIdArray[beaconIndexer]);
                    expectedRegion= new Region("specifiedRegion", uUID,majorId,minorId);
                    beaconManager.startMonitoringBeaconsInRegion(expectedRegion);
                }

                //RemoteException to catch Errors with the Android services beaconManager is accessing
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        else
        {
            Toast.makeText(Instructions.this, "End of beacon pathfinding: You made it to your Destination", Toast.LENGTH_LONG).show();
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

}
