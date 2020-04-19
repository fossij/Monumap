package edu.wit.mobileapp.monumap;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import java.util.Collection;
import java.util.List;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import edu.wit.mobileapp.monumap.Mapping.IBeaconCallBackInterface;
import edu.wit.mobileapp.monumap.Mapping.IBeaconID;


public class BeaconListener implements BeaconConsumer {
    AppCompatActivity m_app;
    IBeaconCallBackInterface m_Callback;
    List<IBeaconID> m_IDsToLookFor;

    private double passedThreshold = 2.00;// acceptable value based off of measured distance analysis that you have passed the beacon.
    private int foundMinorId = -1; // keeps track of beacon recently passed to prevent false unexpected becons
    private BeaconManager beaconManager = null;
    private String uuIdString = "11111111-1111-1111-1111-111111111111";// a univeral id (Constant)to differentiate differnt proximity networks. picked for simplicity.
    private Identifier uuId = Identifier.parse(uuIdString); // regions are defined by Identifer objects
    private Region region =new Region("RegionPiDemo",uuId,null,  null);

    public BeaconListener(AppCompatActivity app, IBeaconCallBackInterface callback) {
        m_app = app;
        m_Callback = callback;
        beaconManager = BeaconManager.getInstanceForApplication(app);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                // holds ibeacon specific data like a manufacturer ID and Beacon IDs
                        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    public void setContext(AppCompatActivity app) {
        m_app = app;
    }

    public void setBeacons(List<IBeaconID> ids) {
        m_IDsToLookFor = ids;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            private int beaconIndexer = 0;
            private IBeaconID thisBeacon;

            @Override
            //shall keep calling didRangeBeaconsInRegion until we stop scanning
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) { // will range every 1001 ms until stop ranging is called
                if (beaconIndexer < m_IDsToLookFor.size()) {// prevent out of bounds
                    thisBeacon = m_IDsToLookFor.get(beaconIndexer);
                }
                Log.d("Ranging", "RangingIteration: looking for beacon Major: " + thisBeacon.getMajorID() + " Minor: " + thisBeacon.getMinorID());//testing
                for (Beacon oneBeacon : beacons) {// loop through all of the available beacons found by the ranging call
                    if (oneBeacon.getDistance() < passedThreshold && oneBeacon.getId2().toInt() == thisBeacon.getMajorID() && oneBeacon.getId3().toInt() == thisBeacon.getMinorID())// if  we have passed the  the beacon we were expecting
                    {
                        Log.d("Ranging", "Expected beacon found  found Major:" + thisBeacon.getMajorID() + " Minor:" + thisBeacon.getMajorID() + " Measured distance:" + oneBeacon.getDistance());
                        m_Callback.CorrectBeaconReached(thisBeacon);
                        beaconIndexer++;// points to next beacon identifiers
                        foundMinorId = thisBeacon.getMinorID();

                    } else if (oneBeacon.getDistance() < passedThreshold && !(oneBeacon.getId3().toInt() == foundMinorId))// we have passed an unexpected beacon //current logic does not account for different building with adjacent same minor id's could ensure not an issue through placemnt
                    {
                        Log.d("Ranging", " Before stopScanning():UNEXPECTED");
                        Log.d("Ranging", "Unxpected beacon found  found Major:" + oneBeacon.getId2().toString() + " Minor:" + oneBeacon.getId3().toString() + " Measured distance:" + oneBeacon.getDistance());
                        m_Callback.IncorrectBeaconReached(thisBeacon);
                    }
                }


            }
        });


    }

    public void startScanning() {
        Log.d("Ranging", "-----------startScanning()----");
        try {
            //range for all beacons within our proximity network
            Region region = new Region("RegionPiDemo",uuId
                    ,null,  null);
            beaconManager.startRangingBeaconsInRegion(region);
            //RemoteException to catch Errors with the Android services beaconManager is accessing
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void stopScanning() {
        Log.d("Ranging", "------------stopScanning()---");
        try {
            Region region = new Region("RegionPiDemo",
                    uuId, null, null);
            beaconManager.stopRangingBeaconsInRegion(region);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public Context getApplicationContext() {
        return null;
    }


    public void unbindService(ServiceConnection serviceConnection) {

    }


    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return false;
    }
}

