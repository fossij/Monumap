package edu.wit.mobileapp.monumap.Entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import edu.wit.mobileapp.monumap.Mapping.IBeaconID;
import edu.wit.mobileapp.monumap.R;

public class Instruction implements Serializable {
    private String text;
    private Direction direction;
    private int duration;
    private int distance;
    private Context context;
    private IBeaconID beacon;
    private int nodeID;
    private List<IBeaconID> acceptableBeacons;

    public Instruction(String text, Direction direction, int duration, int distance, int nodeID, IBeaconID beacon, List<IBeaconID> acceptableBeacons)
    {
        this(text, direction, duration, distance, nodeID);
        this.beacon = beacon;
        this.acceptableBeacons = acceptableBeacons;
    }

    public Instruction(String text, Direction direction, int duration, int distance, int nodeID) {
        this.text = text;
        this.direction = direction;
        this.duration = duration;
        this.distance = distance;
        this.nodeID = nodeID;
    }

    public Bitmap getDirectionIcon() {
        if(this.direction == Direction.LEFT) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_icon_left);
        }
        else if(this.direction == Direction.RIGHT) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_icon_right);
        }
        else if(this.direction == Direction.STRAIGHT) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_icon_straight);
        }
        else if(this.direction == Direction.BACKWARDS) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_icon_backwards);
        }
        else if(this.direction == Direction.STAIRS) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_icon_stairs);
        }
        else if(this.direction == Direction.ELEVATOR) {
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_icon_elevator);
        } else {
            return null;
        }
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getText() {
        return this.text;
    }

    public Direction getDirection() {
        return this.direction;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getDistance() {
        return this.distance;
    }

    public int getNodeID() {
        return nodeID;
    }

    public IBeaconID getIBeacon(){
        return this.beacon;
    }

    public List<IBeaconID> getAcceptableBeacons() {
        if(acceptableBeacons == null)
        {
         return new ArrayList<IBeaconID>();
        }
        return acceptableBeacons;
    }
}
