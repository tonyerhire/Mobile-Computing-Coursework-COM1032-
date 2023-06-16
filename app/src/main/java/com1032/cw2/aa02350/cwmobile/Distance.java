package com1032.cw2.aa02350.cwmobile;


public class Distance {

    //This class is self explanatory and does not need commenting
    public String text;
    public int value;

    public Distance(String text, int value) {
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
