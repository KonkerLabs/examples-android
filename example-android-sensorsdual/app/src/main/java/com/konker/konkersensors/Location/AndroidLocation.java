package com.konker.konkersensors.Location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.konker.konkersensors.SensorsActivity;

public class AndroidLocation extends SensorsActivity implements LocationListener {

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;
    private TextView latitude, longitude;

    private String fusedLatitude = null;
    private  String fusedLongitude = null;
    private  String fusedSpeed = null;
    private  String fusedAltitude = null;
    private Context context;
    private Long interval;





    public void Initialize(Context context, Long interval) {
        // super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //initializeViews();
        this.context=context;
        this.interval=interval;



        if (checkPlayServices()) {
            startFusedLocation();
            registerRequestUpdate(this,interval);

        }

    }

    /*
    private void initializeViews() {
        latitude = (TextView) findViewById(R.id.textview_latitude);
        longitude = (TextView) findViewById(R.id.textview_longitude);
    }*/



    // check if google play services is installed on the device
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                Toast.makeText(context,
                        "This device is supported to get location services. Please download google play services", Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(context,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }


    public void startFusedLocation() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(context).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnectionSuspended(int cause) {
                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {


                        }
                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                        @Override
                        public void onConnectionFailed(ConnectionResult result) {

                        }
                    }).build();
            mGoogleApiClient.connect();
        } else {
            mGoogleApiClient.connect();
        }
    }

    public void stopFusedLocation() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }




    public void registerRequestUpdate(final LocationListener listener, final long interval) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(interval); // every second

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, listener);

                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!isGoogleApiClientConnected()) {
                        mGoogleApiClient.connect();
                    }
                    registerRequestUpdate(listener, interval);
                }
            }
        }, 0);
    }

    public boolean isGoogleApiClientConnected() {
        return mGoogleApiClient != null && mGoogleApiClient.isConnected();
    }



    @Override
    public void onLocationChanged(Location location) {
        setFusedLatitude(location.getLatitude());
        setFusedLongitude(location.getLongitude());
        setFusedSpeed(location.getSpeed());
        setFusedAltitude(location.getAltitude());

        //Toast.makeText(getApplicationContext(), "NEW LOCATION RECEIVED", Toast.LENGTH_LONG).show();

        //latitude.setText(getString(R.string.latitude_string) +" "+ getFusedLatitude());
        //longitude.setText(getString(R.string.longitude_string) +" "+ getFusedLongitude());
    }

    public void getLastLocation() {

        Location location =null;
        try{
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            setFusedLatitude(location.getLatitude());
            setFusedLongitude(location.getLongitude());
            setFusedSpeed(location.getSpeed());
            setFusedAltitude(location.getAltitude());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }





    public void setFusedLatitude(double lat) {
        fusedLatitude = String.valueOf(lat);
    }

    public void setFusedLongitude(double lon) {
        fusedLongitude = String.valueOf(lon);
    }


    public String getFusedLatitude() {
        return fusedLatitude;
    }

    public String getFusedLongitude() {
        return fusedLongitude;
    }


    public void setFusedSpeed(double speed){
        fusedSpeed = String.valueOf(speed);
    }
    public String getFusedSpeed(){
        return fusedSpeed;
    }


    public void setFusedAltitude(double altitude){
        fusedAltitude = String.valueOf(altitude);
    }
    public String getFusedAltitude(){
        return fusedAltitude;
    }

}
