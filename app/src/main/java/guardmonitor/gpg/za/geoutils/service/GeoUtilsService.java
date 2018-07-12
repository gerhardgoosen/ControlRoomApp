package guardmonitor.gpg.za.geoutils.service;

import android.Manifest;
import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.text.DateFormat;
import java.util.Date;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.activity.map.MainActivity;
import guardmonitor.gpg.za.controlroom.widget.MapAppWidget;
import guardmonitor.gpg.za.geoutils.utils.NotificationUtils;
import guardmonitor.gpg.za.geoutils.utils.PermissionHelper;

public class GeoUtilsService extends IntentService  {


    private final static String TAG = "GeoUtilsService";

    private static GeoUtilsService mInstance;

    private LocationRequest mLocationRequest;

    private GeoUtilsLocationRequestor mGeoUtilsLocationRequestor;
    private Context mContext;

    public void onStatusChanged(String provider, int status, Bundle extras) {}
    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}

    public GeoUtilsService() {
        super("GeoUtilsService");
        mInstance = this;
        Log.v(TAG, "GeoUtilsService constructed");
    }

    public static GeoUtilsService getInstance() {
        Log.v(TAG, "GeoUtilsService getInstance");
        if (mInstance == null) mInstance = new GeoUtilsService();
        return mInstance;

    }


    public void updateWidget(Location l) {
        Log.v(TAG, "updateWidget");
        try {
            if (l != null) {

                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.map_app_widget);
                ComponentName mapWidget = new ComponentName(mContext, MapAppWidget.class);
                remoteViews.setTextViewText(R.id.appwidget_name, "Background Service Update");
                remoteViews.setTextViewText(R.id.appwidget_position, "Lat/Lng : " + l.getLatitude() + "," + l.getLongitude() + " ");
                remoteViews.setTextViewText(R.id.appwidget_speed, "Speed : " + +l.getSpeed() + " m/s");
                remoteViews.setTextViewText(R.id.appwidget_time, "BG Captured : " + new Date(l.getTime()).toString() + " ");

                appWidgetManager.updateAppWidget(mapWidget, remoteViews);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {

        Log.v(TAG, "onBind");
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;

    }

    @Override
    public void onCreate() {
        Log.v(TAG, "onCreate");
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        mContext = getApplicationContext();
        mGeoUtilsLocationRequestor = new GeoUtilsLocationRequestor(mContext, this);




    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "onHandleIntent \n\n ====================== \n\n =========================== \n\n ==========================");

        //mGeoUtilsLocationRequestor.requestCurrentLocation();
    }


}


/**
 * Created by Gerhard on 2016/09/21.
 */
class GeoUtilsLocationRequestor implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String TAG = "GeoLocationRequestor";
    private static final String TRACKING_URL = "http://197.85.186.13/oopswhatnow/restapi/geo/track/";
    private GeoUtilsService mGeoUtilsService;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private Location mCurrentLocation;
    private Context mContext;


    public GeoUtilsLocationRequestor(Context context, GeoUtilsService geoUtilsService) {
        Log.v(TAG, "Constructed GeoUtilsLocationRequestor : ");
        this.mGeoUtilsService = geoUtilsService;
        this.mContext = context;
        mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

    }


    public void requestCurrentLocation() {
        Log.v(TAG, "requestCurrentLocation");
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }


//implemented overrides

    @Override
    public void onLocationChanged(final Location location) {
        Log.v(TAG, "GeoUtils Service onLocationChanged");


        if (location != null) {
            Log.v(TAG, "updating widget");
            GeoUtilsService.getInstance().updateWidget(mCurrentLocation);

//            if (mainActivity.isTrackPositionEnabled()) {
//                try {
//                    TrackLocationTask trackLocationTask = new TrackLocationTask(mContext, location);
//                    String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
//
//                    URL url = new URL(TRACKING_URL + deviceId);
//                    trackLocationTask.execute(url);
//
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//            }


        } else {
            Log.v(TAG, "service location null widget");
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "onConnected");

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10);//(2 * 1000); // Update location every 2 seconds
        mLocationRequest.setFastestInterval(10);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.v(TAG, "LocationSettingsStatusCodes.SUCCESS");
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        if (!PermissionHelper.hasPermission(MainActivity.getInstance(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                            PermissionHelper.requestPermissions(MainActivity.getInstance(), 0, new String[]{Manifest.permission.ACCESS_FINE_LOCATION});
                        }
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.v(TAG, "LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.getInstance(), 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.v(TAG, "LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

        this.requestCurrentLocation();

    }


    /**
     * Callback received when a permissions request has been completed.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.v(TAG, "onRequestPermissionsResult");
        if (requestCode == 0) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.requestCurrentLocation();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int code) {
        Log.v(TAG, "onConnectionSuspended");
        NotificationUtils.makeToast(mContext, "onConnectionSuspended" + code, 1);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.v(TAG, "onConnectionFailed " + result.getErrorMessage());
        NotificationUtils.makeToast(mContext, "onConnectionFailed ", 1);
    }


    public Location getLastLocation() {
        return mCurrentLocation;
    }
}
