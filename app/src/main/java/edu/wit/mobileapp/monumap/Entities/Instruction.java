package edu.wit.mobileapp.monumap.Entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.Serializable;

import edu.wit.mobileapp.monumap.R;

public class Instruction implements Serializable {
    private String text;
    private Direction direction;
    private int duration;
    private int distance;
    private Context context;

    public Instruction(String text, Direction direction, int duration, int distance) {
        this.text = text;
        this.direction = direction;
        this.duration = duration;
        this.distance = distance;
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
}
