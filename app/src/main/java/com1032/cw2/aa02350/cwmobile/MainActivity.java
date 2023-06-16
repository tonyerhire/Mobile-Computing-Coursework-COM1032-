package com1032.cw2.aa02350.cwmobile;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com1032.cw2.aa02350.cwmobile.NetworkChangeReceiver.IS_NETWORK_AVAILABLE;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, DirectionFinderListener {

    private GoogleMap map;
    private LatLng myLocation;
    public double lati = 0.0;
    public double longi = 0.0;
    private static final String TAG = "distance";
    Route route;

    private SharedPreferences mPrefs;
    private BroadcastReceiver broadcastReceiver;
    private DatabaseHelper databaseHelper;
    private Button btnFindPath;
    private Button save;
    TextView Duration;
    TextView Distance;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

    Intent locatorService = null;
    AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        MapFragment mf = (MapFragment) getFragmentManager().findFragmentById(R.id.the_map);
        mf.getMapAsync( this );

        //checks for available connection and creaates intent
        IntentFilter intentFilter = new IntentFilter(NetworkChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //checks if network is available and displays toast message
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "connected" : "disconnected";
                Toast.makeText(MainActivity.this, "internet status has changed this will affect finding path!", Toast.LENGTH_SHORT).show();

            }
        }, intentFilter);

        databaseHelper = new DatabaseHelper(this);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);


        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when user clicks findpath it will sendrequest function

                sendRequest();


            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                //creates a alert if service doesnt start else it starts service and displays toast mesage
                if (!startService()) {
                    CreateAlert("Error!", "Service Cannot be started");
                } else {
                    Toast.makeText(MainActivity.this, "Service Started",
                            Toast.LENGTH_LONG).show();
                }

            }
        });


    }









    public boolean stopService() {
        if (this.locatorService != null) {
            this.locatorService = null;
        }
        return true;
    }

    public boolean startService() {
        try {
            // this.locatorService= new
            // Intent(MainActivity.this,LocatorService.class);
            // startService(this.locatorService);

            FetchCordinates fetchCordinates = new FetchCordinates();
            fetchCordinates.execute();
            return true;
        } catch (Exception error) {
            return false;
        }

    }

    public AlertDialog CreateAlert(String title, String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        //dialog

        alert.setTitle(title);

        alert.setMessage(message);

        return alert;

    }



    public class FetchCordinates extends AsyncTask<String, Integer, String> {
        ProgressDialog progDailog = null;
        Location loc;


        public LocationManager mLocationManager;
        public MYLocationListener myLocationListener;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            myLocationListener = new MYLocationListener();
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            loc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);







            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //Permission is granted
            }

            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0,
                    myLocationListener);


            //progreesdialog is created
            progDailog = new ProgressDialog(MainActivity.this);
            progDailog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    FetchCordinates.this.cancel(true);
                }
            });




        }


        @Override
        protected void onPostExecute(String result) {


//This moves the camera to the current location and adds a marker.It has a zoom of 15
            LatLng coords = new LatLng(loc.getLatitude(),  loc.getLongitude());

            map.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 15));
            originMarkers.add(map.addMarker(new MarkerOptions()
                    .title("current location")
                    .position(coords)));

        }



        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            while (lati == 0.0) {

            }
            return null;
        }


    }

    public class MYLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            int lat = (int) location.getLatitude(); // * 1E6);
            int log = (int) location.getLongitude(); // * 1E6);
            int acc = (int) (location.getAccuracy());

            String info = location.getProvider();
            try {

                // LocatorService.myLatitude=location.getLatitude();

                // LocatorService.myLongitude=location.getLongitude();

                lati = location.getLatitude();
                longi = location.getLongitude();

            } catch (Exception e) {
                // progDailog.dismiss();
                // Toast.makeText(getApplicationContext(),"Unable to get Location"
                // , Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.i("OnProviderDisabled", "OnProviderDisabled");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.i("onProviderEnabled", "onProviderEnabled");
        }

        @Override
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {
            Log.i("onStatusChanged", "onStatusChanged");

        }

    }


    private void sendRequest() {

        //gets text of origin and destination from edit text and checks if they are empty
        String origin = etOrigin.getText().toString();
        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            //looks for direction after it is confirmed none of fields is empty
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        double myLat=0;

        double myLng=0;

        map = googleMap;




    }


    // calls onMapLoaded when layout done


    private Route insertRoute() {
        Route route = new Route();
        route.setId(databaseHelper.insertTable(route));
        return route;
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline:polylinePaths ) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        DatabaseHelper db = new DatabaseHelper(this);
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
//Distance and time resultss are displayed in textviews
            //move camera to start location with a zoom of 16
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //we set text of time and distance to show
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

//adds a custom marker gotten from drawable folder at the start location
            originMarkers.add(map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));

            //adds a custom marker gotten from drawable folder at the end location
            destinationMarkers.add(map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            //polyline Oprions created and list of LatLng coords added to it
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    //colour of polyline is blue
                            color(Color.BLUE).
                            width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));
//add polyline option to map
            polylinePaths.add(map.addPolyline(polylineOptions));
            db.insertTable(route);
        }
    }


}

