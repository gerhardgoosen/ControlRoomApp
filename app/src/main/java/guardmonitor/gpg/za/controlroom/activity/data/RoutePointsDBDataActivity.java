package guardmonitor.gpg.za.controlroom.activity.data;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONObject;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.db.EntityManager;
import guardmonitor.gpg.za.db.structure.LocationContract;

/**
 * Created by Gerhard on 2016/10/10.
 */


public class RoutePointsDBDataActivity extends AppCompatActivity {
    private static final String TAG = "RoutePntsDBAct";

    private View mDBView;
    private Context mContext;
    private EntityManager entityManager;
    private Bundle mSavedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        this.mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_view_routepoints);
        mDBView = findViewById(R.id.route_points_db_view);
        mContext = this;
        entityManager = new EntityManager(this, getApplicationContext());


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String routeName = extras.getString("PARM_ROUTE_NAME");

            if (routeName != null) {
                try {
                   // Method callback = this.getClass().getMethod("showRoutePointsCallBack", JSONObject.class);

                    showRoutePointsCallBack(entityManager.findAllRoutePointsByRouteName(routeName));
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }
            else{


                TableLayout locationsTable  = (TableLayout) findViewById(R.id.table_route_points);
                TableRow row = new TableRow(this);
                TextView nodata = new TextView(this);
                nodata.setText(" No Data ");
                row.addView(nodata);

            }
        }




    }



    public void showRoutePointsCallBack(JSONObject data) {


        Log.v(TAG, "showRoutePointsCallBack");
        Log.v(TAG, data.toString());

        TableLayout locationsTable  = (TableLayout) findViewById(R.id.table_route_points);
        TableRow tbrow0 = new TableRow(this);
        TextView lat = new TextView(this);
        lat.setText(" Latitude ");
        lat.setTextColor(Color.WHITE);
        tbrow0.addView(lat);

        TextView lng = new TextView(this);
        lng.setText(" Longitude ");
        lng.setTextColor(Color.WHITE);
        tbrow0.addView(lng);

        TextView alt = new TextView(this);
        alt.setText(" Altitude ");
        alt.setTextColor(Color.WHITE);
        tbrow0.addView(alt);


        TextView acc = new TextView(this);
        acc.setText(" Accuracy ");
        acc.setTextColor(Color.WHITE);
        tbrow0.addView(acc);

        locationsTable.addView(tbrow0);

        JSONObject jsonData = new JSONObject();
        try {
            jsonData = ((JSONObject)((JSONObject) data.get("points")).get("data"));

        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject row = ((JSONObject) ((JSONObject) jsonData.get("row_" + i)).get("row"));

                TableRow tbrow = new TableRow(this);

                TextView rowLat = new TextView(this);
                rowLat.setTextColor(Color.WHITE);
                rowLat.setGravity(Gravity.CENTER);
                rowLat.setText(row.getString(LocationContract.LocationEntry.COLUMN_NAME_latitude));
                tbrow.addView(rowLat);

                TextView rowLng = new TextView(this);
                rowLng.setTextColor(Color.WHITE);
                rowLng.setGravity(Gravity.CENTER);
                rowLng.setText(row.getString(LocationContract.LocationEntry.COLUMN_NAME_longitude));
                tbrow.addView(rowLng);

                TextView rowAlt = new TextView(this);
                rowAlt.setTextColor(Color.WHITE);
                rowAlt.setGravity(Gravity.CENTER);
                rowAlt.setText(row.getString(LocationContract.LocationEntry.COLUMN_NAME_altitude));
                tbrow.addView(rowAlt);

                TextView rowAcc = new TextView(this);
                rowAcc.setTextColor(Color.WHITE);
                rowAcc.setGravity(Gravity.CENTER);
                rowAcc.setText(row.getString(LocationContract.LocationEntry.COLUMN_NAME_accuracy));
                tbrow.addView(rowAcc);




                locationsTable.addView(tbrow);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



}

