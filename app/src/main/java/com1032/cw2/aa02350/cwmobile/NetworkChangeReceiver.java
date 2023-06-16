package com1032.cw2.aa02350.cwmobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by anthonyawobasivwe on 23/05/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String NETWORK_AVAILABLE_ACTION = "com1032.cw2.aa02350.cwmobile.NetworkAvailable";
    public static final String IS_NETWORK_AVAILABLE = "isNetworkAvailable";

    @Override
    public void onReceive(Context context, Intent intent) {

        // checks for  network network avilable and loads new intent
        Intent networkStateIntent = new Intent(NETWORK_AVAILABLE_ACTION);

        //puts in details of is network available when passing back data
        networkStateIntent.putExtra(IS_NETWORK_AVAILABLE,  isConnectedToInternet(context));

        //gets an instance of context
        LocalBroadcastManager.getInstance(context).sendBroadcast(networkStateIntent);
    }

    private boolean isConnectedToInternet(Context context) {
        try {
            //checks if there is connection to internet
            if (context != null) {
                //if context is null initialize connectivitymanager of type ConnectivityManager
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                return networkInfo != null && networkInfo.isConnected();
            }
            return false;
        } catch (Exception e) {
            Log.e(NetworkChangeReceiver.class.getName(), e.getMessage());
            return false;
        }
    }
}