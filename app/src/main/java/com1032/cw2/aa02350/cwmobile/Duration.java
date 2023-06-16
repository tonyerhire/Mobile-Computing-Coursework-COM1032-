package com1032.cw2.aa02350.cwmobile;

/**
 * Created by anthony
 */
public class Duration {
    public String text;
    public int value;

    public Duration(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public String getText(){
        return text;
    }
}
