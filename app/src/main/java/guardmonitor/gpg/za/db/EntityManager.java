package guardmonitor.gpg.za.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.provider.Settings;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import guardmonitor.gpg.za.db.pojo.Route;
import guardmonitor.gpg.za.db.structure.LocationContract;
import guardmonitor.gpg.za.db.structure.RouteContract;

/**
 * Created by Gerhard on 2016/10/05.
 */

public class EntityManager {

    protected static final String TAG = "EntitiyManager";

    private Object mCallerActivity;
    private Context mContext;
    /**
     * DB
     */
    //private LocationDbHelper mLocationDbHelper;
    //private RouteDbHelper mRouteDbHelper;
    //private ContentResolver dbContentResolver;
    private DBContentProvider dbContentProvider;


    public EntityManager(Object activity, Context context) {
        super();
        this.mContext = context;
        this.mCallerActivity = activity;
        dbContentProvider = new DBContentProvider(this.mContext);
        Log.v(TAG, "Constructed EntityManager");
    }


    //LOCATION
    public boolean saveLocation(Location loc) {

        try {
            // Gets the data repository in write mode
            // SQLiteDatabase db = mLocationDbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(LocationContract.LocationEntry.COLUMN_NAME_latitude, loc.getLatitude());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_longitude, loc.getLongitude());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_altitude, loc.getAltitude());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_accuracy, loc.getAccuracy());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_speed, loc.getSpeed());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_bearing, loc.getBearing());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_provider, loc.getProvider());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_time, loc.getTime());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_elapsedRealtimeNanos, loc.getElapsedRealtimeNanos());
            values.put(LocationContract.LocationEntry.COLUMN_NAME_capturedDate, new Date().toString());

//        HashMap<String, Object> insertParms = new HashMap<>();
//        insertParms.put("TABLE_NAME", LocationContract.LocationEntry.TABLE_NAME);
//        insertParms.put("SQL_ACTION", "INSERT");
//        insertParms.put("SQL_CONTENT_VALUES", values);
//
//        insertParms.put("SQL_RAW_DATA", loc);
//
//        try {
//            insertParms.put("CALLBACK", callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }

            this.dbContentProvider.insert(LocationContract.LocationEntry.TABLE_NAME, values);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        //     AsyncDatabaseTask insertCurrentLocation = new AsyncDatabaseTask(mCallerActivity, mContext, db, insertParms);
        //     insertCurrentLocation.execute((Void) null);


    }




    public JSONObject findAllLocations(String where, String[] whereArgs) {

//        if (this.mLocationDbHelper == null) {
//            this.mLocationDbHelper = new LocationDbHelper(this.mContext);
//        }

//        SQLiteDatabase db = mLocationDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                LocationContract.LocationEntry._ID,
                LocationContract.LocationEntry.COLUMN_NAME_latitude,
                LocationContract.LocationEntry.COLUMN_NAME_longitude,
                LocationContract.LocationEntry.COLUMN_NAME_altitude,
                LocationContract.LocationEntry.COLUMN_NAME_accuracy,
                LocationContract.LocationEntry.COLUMN_NAME_speed,
                LocationContract.LocationEntry.COLUMN_NAME_bearing,
                LocationContract.LocationEntry.COLUMN_NAME_provider,
                LocationContract.LocationEntry.COLUMN_NAME_time,
                LocationContract.LocationEntry.COLUMN_NAME_elapsedRealtimeNanos,
                LocationContract.LocationEntry.COLUMN_NAME_capturedDate
        };
        String selection = "";
        String[] selectionArgs = null;

        if (where != null) {
            selection = where;
        } else {
            selection = LocationContract.LocationEntry.COLUMN_NAME_accuracy + " <= ?";
        }
        if (whereArgs != null) {
            selectionArgs = whereArgs;
        } else {
            selectionArgs = new String[]{"100"};
        }

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                LocationContract.LocationEntry.COLUMN_NAME_time + " ASC";


        Cursor c = this.dbContentProvider.query(LocationContract.LocationEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);

//        HashMap<String, Object> selectParms = new HashMap<>();
//        selectParms.put("TABLE_NAME", LocationContract.LocationEntry.TABLE_NAME);
//        selectParms.put("SQL_ACTION", "SELECT");
//        selectParms.put("PROJECTION", projection);
//        selectParms.put("WHERE_CLAUSE", selection);
//        selectParms.put("WHERE_CLAUSE_DATA", selectionArgs);
//        selectParms.put("PARM_GROUPBY", null);
//        selectParms.put("PARM_FILTERBY", null);
//        selectParms.put("SORT_ORDER", sortOrder);
//
//        try {
//            selectParms.put("CALLBACK", callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        AsyncDatabaseTask selectAllLocations = new AsyncDatabaseTask(mCallerActivity, mContext, db, selectParms);
//        selectAllLocations.execute((Void) null);

        return CursorToJSON(c);

    }

    //ROUTES

    public Route saveRoute(String name, ArrayList<Location> points) {

        try {
//        if (this.mRouteDbHelper == null) {
//            this.mRouteDbHelper = new RouteDbHelper(this.mContext);
//        }

            String deviceId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

            Date audit = new Date();
            // Gets the data repository in write mode
            // SQLiteDatabase db = mRouteDbHelper.getWritableDatabase();
            ContentValues route = new ContentValues();
            route.put(RouteContract.RouteEntry.COLUMN_NAME_device_uuid, deviceId);
            route.put(RouteContract.RouteEntry.COLUMN_NAME_route_name, name);
            route.put(RouteContract.RouteEntry.COLUMN_NAME_capturedDate, audit.toString());

            Long routeId = this.dbContentProvider.insert(RouteContract.RouteEntry.TABLE_NAME, route);

            Route retRoute = new Route(routeId.intValue() ,name);
            retRoute.setCreateDate( audit ) ;

            for (Location loc : points) {
                // Create a new map of values, where column names are the keys
                ContentValues locValues = new ContentValues();
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_latitude, loc.getLatitude());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_longitude, loc.getLongitude());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_altitude, loc.getAltitude());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_accuracy, loc.getAccuracy());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_speed, loc.getSpeed());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_bearing, loc.getBearing());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_provider, loc.getProvider());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_time, loc.getTime());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_elapsedRealtimeNanos, loc.getElapsedRealtimeNanos());
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_routeId, routeId);
                locValues.put(LocationContract.LocationEntry.COLUMN_NAME_capturedDate, new Date().toString());


                this.dbContentProvider.insert(LocationContract.LocationEntry.TABLE_NAME, locValues);


//            // Insert the new row, returning the primary key value of the new row
//            long newRowId = db.insert(RouteContract.RouteEntry.TABLE_NAME, null, values);

//            HashMap<String, Object> insertRouteParms = new HashMap<>();
//            insertRouteParms.put("TABLE_NAME", RouteContract.RouteEntry.TABLE_NAME);
//            insertRouteParms.put("SQL_ACTION", "INSERT");
//            insertRouteParms.put("SQL_CONTENT_VALUES", values);
//            insertRouteParms.put("SQL_RAW_DATA", points);
//
//            try {
//                insertRouteParms.put("CALLBACK", callback);
//            } catch (Exception e) {
//                e.printStackTrace();
//                return;
//            }
//
//            AsyncDatabaseTask insertCurrentRoute = new AsyncDatabaseTask(mCallerActivity, mContext, db, insertRouteParms);
//            insertCurrentRoute.execute((Void) null);

            }

            return retRoute;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JSONObject findSavedRouteNames() {


        //SQLiteDatabase db = mRouteDbHelper.getReadableDatabase();


        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {RouteContract.RouteEntry.COLUMN_NAME_route_name};

        // Filter results WHERE "title" = 'My Title'
        String selection = null;
        String[] selectionArgs = null;
        String groupByArgs = RouteContract.RouteEntry.COLUMN_NAME_route_name;
        String filterArgs = null;

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                RouteContract.RouteEntry.COLUMN_NAME_capturedDate + " DESC";


        Cursor c = this.dbContentProvider.query(RouteContract.RouteEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);


//        HashMap<String, Object> selectRouteParms = new HashMap<>();
//        selectRouteParms.put("TABLE_NAME", RouteContract.RouteEntry.TABLE_NAME);
//        selectRouteParms.put("SQL_ACTION", "SELECT");
//        selectRouteParms.put("PROJECTION", projection);
//        selectRouteParms.put("WHERE_CLAUSE", selection);
//        selectRouteParms.put("WHERE_CLAUSE_DATA", selectionArgs);
//        selectRouteParms.put("PARM_GROUPBY", groupByArgs);
//        selectRouteParms.put("PARM_FILTERBY", filterArgs);
//        selectRouteParms.put("SORT_ORDER", sortOrder);
//
//        try {
//            selectRouteParms.put("CALLBACK", callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        AsyncDatabaseTask selectAllRoutes = new AsyncDatabaseTask(mCallerActivity, mContext, db, selectRouteParms);
//        selectAllRoutes.execute((Void) null);

        return CursorToJSON(c);

    }

    public JSONObject findAllRoutePointsByRouteName(String rName) {

        JSONObject retData = new JSONObject();
//        if (this.mRouteDbHelper == null) {
//            this.mRouteDbHelper = new RouteDbHelper(this.mContext);
//        }


//        SQLiteDatabase db = mRouteDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                RouteContract.RouteEntry._ID,
                RouteContract.RouteEntry.COLUMN_NAME_device_uuid,
                RouteContract.RouteEntry.COLUMN_NAME_route_name,
                RouteContract.RouteEntry.COLUMN_NAME_capturedDate
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = RouteContract.RouteEntry.COLUMN_NAME_route_name + " = ?";
        String[] selectionArgs = {rName};
        String groupByArgs = null;
        String filterArgs = null;
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                RouteContract.RouteEntry.COLUMN_NAME_capturedDate + " DESC";



        Cursor routeCursor = this.dbContentProvider.query(RouteContract.RouteEntry.TABLE_NAME, projection, selection, selectionArgs, sortOrder);
        routeCursor.moveToFirst();
        Long routeId = routeCursor.getLong(0);

        JSONObject data = CursorToJSON(routeCursor);
        try {
            retData = (JSONObject) data.get("data");

        } catch (Exception e) {
            e.printStackTrace();
        }
        String where =  LocationContract.LocationEntry.COLUMN_NAME_routeId+ " <= ?";
        String[] whereArgs = new String[]{routeId+""};

        try {
            retData.put("points",findAllLocations(where,whereArgs));
        } catch (Exception e) {
            e.printStackTrace();
        }


//        HashMap<String, Object> selectRouteParms = new HashMap<>();
//        selectRouteParms.put("TABLE_NAME", RouteContract.RouteEntry.TABLE_NAME);
//        selectRouteParms.put("SQL_ACTION", "SELECT");
//        selectRouteParms.put("PROJECTION", projection);
//        selectRouteParms.put("WHERE_CLAUSE", selection);
//        selectRouteParms.put("WHERE_CLAUSE_DATA", selectionArgs);
//        selectRouteParms.put("PARM_GROUPBY", groupByArgs);
//        selectRouteParms.put("PARM_FILTERBY", filterArgs);
//        selectRouteParms.put("SORT_ORDER", sortOrder);
//
//        try {
//            selectRouteParms.put("CALLBACK", callback);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
//        AsyncDatabaseTask selectAllRoutePoints = new AsyncDatabaseTask(mCallerActivity, mContext, db, selectRouteParms);
//        selectAllRoutePoints.execute((Void) null);

        return retData;


    }


    private JSONObject CursorToJSON(Cursor c) {
        Log.v(TAG, "CursorToJSON");
        JSONObject return_data = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            c.moveToFirst();

            for (int row = 0; row < c.getCount(); row++) {
                JSONObject row_item = new JSONObject();

                for (int col = 0; col < c.getColumnCount(); col++) {
                    String colName = c.getColumnName(col);
                    try {
                        row_item.put(colName, c.getString(col));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    JSONObject rowdata = new JSONObject();
                    rowdata.put("index", row);
                    rowdata.put("row", row_item);
                    data.put("row_" + row, rowdata);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                c.moveToNext();
            }


            try {
                return_data.put("data", data);
            } catch (Exception e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return return_data;
    }


}
