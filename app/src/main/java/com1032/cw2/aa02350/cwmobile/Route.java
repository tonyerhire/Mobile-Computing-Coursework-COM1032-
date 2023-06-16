package com1032.cw2.aa02350.cwmobile;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class Route {

    //This class is self explanatory and does not need commenting
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;



    public List<LatLng> points;


    private long Id;



    public  String getStartAddress(){
        return startAddress;
    }

    public  String getEndAddress(){
        return  endAddress;
    }
    public Duration getDuration(){
        return duration;
    }

    public void setEndAddress(String endAddress){
        endAddress=endAddress;
    }
    public void setStartAddress(String startAddress){
        startAddress=startAddress;
    }
    public Distance getDistance(){
        return  distance;
    }

    public void setDistance(Distance distance){
        distance=distance;
    }


    public void setDuration(Duration duration){
        duration=duration;
    }


    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }
}
