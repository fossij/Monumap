package edu.wit.mobileapp.monumap;

import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;

import edu.wit.mobileapp.monumap.Adapters.InstructionsListAdapter;
import edu.wit.mobileapp.monumap.Dialogs.Cancel;
import edu.wit.mobileapp.monumap.Dialogs.Complete;
import edu.wit.mobileapp.monumap.Dialogs.InstructionInfo;
import edu.wit.mobileapp.monumap.Entities.Instruction;
import edu.wit.mobileapp.monumap.Entities.Route;
import edu.wit.mobileapp.monumap.Entities.RouteParser;
import edu.wit.mobileapp.monumap.Mapping.IBeaconCallBackInterface;
import edu.wit.mobileapp.monumap.Mapping.IBeaconID;
import edu.wit.mobileapp.monumap.Mapping.JsonMapParser;
import edu.wit.mobileapp.monumap.Mapping.Map;
import edu.wit.mobileapp.monumap.Mapping.Node;


public class Instructions extends AppCompatActivity implements IBeaconCallBackInterface {
    private Route currentRoute;
    private InstructionsListAdapter adapter;
    private ProgressBar progressBar;
    private ArrayList<Instruction> instructions;
    private Stack<Instruction> previousInstructions;
    private TextToSpeech tts;
    private boolean ttsEnabled;
    private BeaconListener beaconListener;
    private Map map;

    private BottomNavigationView mBottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_prev:
                    previousOperation(item, true);
                    return true;
                case R.id.nav_next:
                    nextOperation(item, true);
                    return true;
            }
            return false;
        }
    };

    private void setUpRoute(Route r){
        currentRoute = r;
        instructions = (ArrayList<Instruction>) r.getInstructions().clone();     // clone to not overwrite base instructions for route
        previousInstructions = new Stack<>();
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

        beaconListener.setBeacons(currentRoute.getBeacons());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        // create nav menu
        mBottomNavigationView = findViewById(R.id.nav_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        beaconListener = new BeaconListener(this, this);

        // initialize data
        setUpRoute((Route) getIntent().getSerializableExtra("currentRoute"));
        JsonMapParser.setContext(getApplicationContext());
        for(Map m:JsonMapParser.getListOfMaps()){
            if(m.getName().equals(currentRoute.getStart().getBuilding())){
                map = m;
                break;
            }
        }
        // store new route in recent routes
        RecentRoutes.updateRecentRoutes(getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE), currentRoute);

        //instantiate beaconListener

        //create textToSpeech object
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int ttsLang = tts.setLanguage(Locale.US);

                    if (ttsLang == TextToSpeech.LANG_MISSING_DATA
                            || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                        //error
                        Log.e("error", "TTS error on Instructions page creation");
                    }
                    //success
                } else {
                    //some other error
                    Log.e("error", "TTS error on Instructions page creation");
                }
            }
        });
        ttsEnabled = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE).getBoolean(getString(R.string.sp_textToSpeechEnabled), false);

        //speak the first instruction
        if(ttsEnabled) {
            String data = instructions.get(0).getText();
            int speechStatus = tts.speak(data, TextToSpeech.QUEUE_FLUSH, null, null);

            if (speechStatus == TextToSpeech.ERROR) {
                //error
                Log.e("error", "TTS error on Instructions page creation");
            }
        }

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                IncorrectBeaconReached(new IBeaconID(1,7));
//            }
//        }, 2000);
        beaconListener.startScanning();
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
            beaconListener.stopScanning();
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

    private void previousOperation(MenuItem item, boolean speak) {
        // get previous instruction (shift list down) --> just add last item from stack to instructions list
        Instruction previousInstruction = previousInstructions.pop();
        instructions.add(0, previousInstruction);
        adapter.notifyDataSetChanged();
        progressBar.setProgress(progressBar.getProgress() - 1);

        //text to speech
        if(ttsEnabled && speak) {
            String data = previousInstruction.getText();
            int speechStatus = tts.speak(data, TextToSpeech.QUEUE_FLUSH, null);

            if (speechStatus == TextToSpeech.ERROR) {
                //error
                Log.e("error", "TTS error on previousOperation");
            }
        }

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

    private void nextOperation(MenuItem item, boolean speak) {
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
            beaconListener.stopScanning();
            return;
        }

        //text to speech
        if(ttsEnabled && speak) {
            String data = instructions.get(0).getText();
            int speechStatus = tts.speak(data, TextToSpeech.QUEUE_FLUSH, null, null);

            if (speechStatus == TextToSpeech.ERROR) {
                //error
                Log.e("error", "TTS error on nextOperation");
            }
        }

        // set previous button to visible if passed first instruction
        MenuItem prev = mBottomNavigationView.getMenu().findItem(R.id.nav_prev);
        if(!prev.isVisible()) {
            prev.setVisible(true);
        }
    }

    @Override
    public void CorrectBeaconReached(IBeaconID id) {
        for(Instruction i : currentRoute.getInstructions()){
            if(id.equals(i.getIBeacon())){
                int index = currentRoute.getInstructions().indexOf(i);
                // The thought here is that if index 0 beacon is hit,
                // previousInstructions should be empty, size = 0
                // Then an instruction should be pushed onto it,
                // But if the last instruction is hit,
                // then don't keep trying to move to the next instruction
                while(previousInstructions.size() <= index && !instructions.isEmpty()){
                    boolean speak = previousInstructions.size() == index;
                    MenuItem nextButton = mBottomNavigationView.getMenu().findItem(R.id.nav_next);
                    nextOperation(nextButton, speak);
                }
                break;
            }
        }
    }

    @Override
    public void IncorrectBeaconReached(IBeaconID id) {
        if(currentRoute.getAcceptableBeacons().contains(id)) return;
        Node newStart = map.getNode(id);
        if(newStart == null) return;
        beaconListener.stopScanning();
        ArrayList<Instruction> inst = currentRoute.getInstructions();
        Instruction i = inst.get(inst.size()-1);
        Node newDest = map.getNode(i.getNodeID());
        boolean wheelChairAccess = getApplicationContext().getSharedPreferences(getApplicationContext().getString(R.string.preferences), MODE_PRIVATE).getBoolean(getApplicationContext().getString(R.string.sp_wheelchairAccessibilityEnabled), false);
        Pathfinder pf = new Pathfinder(map, wheelChairAccess);
        Route newRoute = RouteParser.parse(pf.makeRoute(newStart, newDest));
        setUpRoute(newRoute);
        beaconListener.setBeacons(newRoute.getBeacons());
        beaconListener.startScanning();
    }
}
