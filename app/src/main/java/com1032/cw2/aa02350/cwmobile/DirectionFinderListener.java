package com1032.cw2.aa02350.cwmobile;

import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public interface DirectionFinderListener {
    void onDirectionFinderStart();

    /*after compeleting json processing it will return List<Route> routes and listener nvokes interface function
    onDirectionFinderSucess to display the result to the map*/
    void onDirectionFinderSuccess(List<Route> route);
}
