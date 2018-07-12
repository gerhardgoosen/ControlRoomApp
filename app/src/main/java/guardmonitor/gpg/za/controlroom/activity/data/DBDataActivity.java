package guardmonitor.gpg.za.controlroom.activity.data;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.util.ArrayList;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.activity.map.MainActivity;
import guardmonitor.gpg.za.controlroom.fragments.LocationFragment;
import guardmonitor.gpg.za.controlroom.fragments.RouteFragment;
import guardmonitor.gpg.za.controlroom.fragments.interaction.listeners.OnLocationFragmentInteractionListener;
import guardmonitor.gpg.za.controlroom.fragments.interaction.listeners.OnRouteFragmentInteractionListener;
import guardmonitor.gpg.za.db.EntityManager;
import guardmonitor.gpg.za.db.pojo.Route;
import guardmonitor.gpg.za.db.structure.LocationContract;

/**
 * Created by Gerhard on 2016/10/03.
 */

public class DBDataActivity
        extends
        FragmentActivity
        implements
        View.OnClickListener,
        OnRouteFragmentInteractionListener,
        OnLocationFragmentInteractionListener {


    private static final String TAG = "DBDataActivity";

    private Button locationDBViewButton, routesDBViewButton;

    private EntityManager entityManager;
    private Bundle mSavedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        this.mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_activity);
        locationDBViewButton = (Button) findViewById(R.id.button_dbview_locations);
        locationDBViewButton.setOnClickListener(this);
        routesDBViewButton = (Button) findViewById(R.id.button_dbview_routes);
        routesDBViewButton.setOnClickListener(this);

        entityManager = new EntityManager(this, getApplicationContext());

    }

    public void onRouteFragmentInteraction(Route route) {
        //you can leave it empty
        Log.v(TAG, "OnRouteFragmentInteractionListener : " + route.toString());

        if (route != null) {

            ArrayList<Location> routePoints = new ArrayList<>();
            try {
                JSONObject returnData = entityManager.findAllRoutePointsByRouteName(route.getRouteName());

                JSONObject pointsData = (JSONObject) returnData.get("points");
                JSONObject pointsJsonData = (JSONObject) pointsData.get("data");

                routePoints = LocationJSONtoList(pointsJsonData);
            } catch (Exception e) {
                e.printStackTrace();
            }


            FragmentManager fragmentManager = DBDataActivity.this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            LocationFragment locationFragment = LocationFragment.newInstance(1, routePoints);

            fragmentTransaction.replace(R.id.fragment_container, locationFragment);
            fragmentTransaction.commit();

            MainActivity.getInstance().drawSavedRoute(route,routePoints);
        }


    }

    public void onLocationFragmentInteraction(Location loca) {
        //you can leave it empty
        Log.v(TAG, "OnLocationFragmentInteractionListener : " + loca.toString());
    }

    @Override
    public void onClick(View v) {
        Log.v(TAG, "onClick" + v.getId());

        if (v.getId() == R.id.button_dbview_locations) {
            // startActivity(new Intent(this, LocationDBDataActivity.class));

            //Find ALL Locations
            ArrayList<Location> locations = new ArrayList<>();
            try {
                JSONObject returnData = entityManager.findAllLocations(null, null);
                JSONObject jsonData = (JSONObject) returnData.get("data");
                locations = LocationJSONtoList(jsonData);
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = DBDataActivity.this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            LocationFragment locationFragment = LocationFragment.newInstance(1, locations);

            fragmentTransaction.replace(R.id.fragment_container, locationFragment);
            fragmentTransaction.commit();
        }

        if (v.getId() == R.id.button_dbview_routes) {
//            startActivity(new Intent(this, RoutesDBDataActivity.class));

            FragmentManager fragmentManager = DBDataActivity.this.getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


            RouteFragment routeFragment = RouteFragment.newInstance(1);

            fragmentTransaction.replace(R.id.fragment_container, routeFragment);
            fragmentTransaction.commit();
        }
    }


    public ArrayList<Location> LocationJSONtoList(JSONObject jsonData) {
        ArrayList<Location> retList = new ArrayList<>();

        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject row = ((JSONObject) ((JSONObject) jsonData.get("row_" + i)).get("row"));

                Location l = new Location(row.getString(LocationContract.LocationEntry.COLUMN_NAME_provider));
                l.setLatitude(Double.parseDouble(row.getString(LocationContract.LocationEntry.COLUMN_NAME_latitude)));
                l.setLongitude(Double.parseDouble(row.getString(LocationContract.LocationEntry.COLUMN_NAME_longitude)));
                l.setAltitude(Double.parseDouble(row.getString(LocationContract.LocationEntry.COLUMN_NAME_altitude)));
                l.setAccuracy(Float.parseFloat(row.getString(LocationContract.LocationEntry.COLUMN_NAME_accuracy)));
                l.setBearing(Float.parseFloat(row.getString(LocationContract.LocationEntry.COLUMN_NAME_bearing)));
                l.setSpeed(Float.parseFloat(row.getString(LocationContract.LocationEntry.COLUMN_NAME_speed)));
                l.setTime(Long.parseLong(row.getString(LocationContract.LocationEntry.COLUMN_NAME_time)));

                retList.add(l);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return retList;
    }
}

