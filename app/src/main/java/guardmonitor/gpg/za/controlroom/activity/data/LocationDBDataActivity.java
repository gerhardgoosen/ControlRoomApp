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

import org.json.JSONException;
import org.json.JSONObject;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.db.EntityManager;
import guardmonitor.gpg.za.db.structure.LocationContract;

/**
 * Created by Gerhard on 2016/10/03.
 */

public class LocationDBDataActivity extends AppCompatActivity  {
    private static final String TAG = "LocationDBDataActivity";

    private View mDBView;
    private Context mContext;
    private EntityManager entityManager;
    private Bundle mSavedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        this.mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_view_locations);
        mDBView = findViewById(R.id.locations_db_view);
        mContext = this;

        entityManager = new EntityManager(this, getApplicationContext());



        try {
            showLocationQueryResults(entityManager.findAllLocations(null,null));
        }catch (Exception e){
            e.printStackTrace();
            return;
        }


    }


    public void showLocationQueryResults(JSONObject data) {


        Log.v(TAG, "showLocationQueryResultsCallBack");
        Log.v(TAG, data.toString());

        TableLayout locationsTable  = (TableLayout) findViewById(R.id.table_locations);

        buildTableHeader(locationsTable);

        JSONObject jsonData = new JSONObject();
        try {
            jsonData = (JSONObject) data.get("data");

        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject row = ((JSONObject) ((JSONObject) jsonData.get("row_" + i)).get("row"));

                buildTableRow(locationsTable, row);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void buildTableRow(TableLayout locationsTable, JSONObject row) throws JSONException {
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

        TextView rowDate = new TextView(this);
        rowDate.setTextColor(Color.WHITE);
        rowDate.setGravity(Gravity.CENTER);
        rowDate.setText(row.getString(LocationContract.LocationEntry.COLUMN_NAME_capturedDate));
        tbrow.addView(rowDate);


        locationsTable.addView(tbrow);
    }

    private void buildTableHeader(TableLayout locationsTable) {
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

        TextView date = new TextView(this);
        date.setText(" Date ");
        date.setTextColor(Color.WHITE);
        tbrow0.addView(date);

        locationsTable.addView(tbrow0);
    }


}

