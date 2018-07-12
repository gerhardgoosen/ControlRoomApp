package guardmonitor.gpg.za.controlroom.activity.data;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONObject;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.db.EntityManager;
import guardmonitor.gpg.za.db.structure.RouteContract;

/**
 * Created by Gerhard on 2016/10/03.
 */

public class RoutesDBDataActivity extends AppCompatActivity  {
    private static final String TAG = "RoutesDBDataActivity";

    private View mDBView;
    private Context mContext;
    private EntityManager entityManager;
    private Bundle mSavedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        this.mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_view_routes);
        mDBView = findViewById(R.id.routes_db_view);
        mContext = this;

        entityManager = new EntityManager(this, getApplicationContext());


        try {
            //Method callback = this.getClass().getMethod("showRouteQueryResultsCallBack", JSONObject.class);

            showRouteQueryResults(entityManager.findSavedRouteNames());
        }catch (Exception e){
            e.printStackTrace();
            return;
        }

    }




    public void showRouteQueryResults(JSONObject data) {
        Log.v(TAG, "showRouteQueryResultsCallBack");
        Log.v(TAG, data.toString());
        //routedb_content.setText(routedb_content.getText() + "\n" + data.toString());
        TableLayout  routesTable = (TableLayout) findViewById(R.id.table_routes);

        TableRow tbhead0 = new TableRow(this);
        TextView nameHeadCol = new TextView(this);
        nameHeadCol.setText(" Name ");
        nameHeadCol.setTextColor(Color.WHITE);
        tbhead0.addView(nameHeadCol);

        TextView actionHeadCol = new TextView(this);
        actionHeadCol.setText(" Action ");
        actionHeadCol.setTextColor(Color.WHITE);
        tbhead0.addView(actionHeadCol);

        routesTable.addView(tbhead0);

        JSONObject jsonData = new JSONObject();
        try {
            jsonData = (JSONObject) data.get("data");

        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < jsonData.length(); i++) {

            try {
                JSONObject row = ((JSONObject) ((JSONObject) jsonData.get("row_" + i)).get("row"));

                final String routeName = row.getString(RouteContract.RouteEntry.COLUMN_NAME_route_name);

                TableRow tbrow = new TableRow(this);

                TextView nameRowCol = new TextView(this);
                nameRowCol.setTextColor(Color.WHITE);
                nameRowCol.setGravity(Gravity.CENTER);
                nameRowCol.setText(routeName);
                tbrow.addView(nameRowCol);


                Button actionRowCol = new Button(this);
                actionRowCol.setTextColor(Color.WHITE);
                actionRowCol.setGravity(Gravity.CENTER);
                actionRowCol.setText(R.string.action_see_route);
                actionRowCol.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent see_route_points = new Intent(getApplicationContext(), RoutePointsDBDataActivity.class);
                        see_route_points.putExtra("PARM_ROUTE_NAME", routeName);

                        startActivity(see_route_points);
                    }
                });
                tbrow.addView(actionRowCol);


                routesTable.addView(tbrow);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}

