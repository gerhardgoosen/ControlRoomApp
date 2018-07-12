package guardmonitor.gpg.za.db;

import guardmonitor.gpg.za.db.structure.LocationContract.LocationEntry;

public class LocationDbHelper {//extends SQLiteOpenHelper {

    protected static final String TAG = "LocationDbHelper";


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;


    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + LocationEntry.TABLE_NAME
            + " ( "
            +  LocationEntry._ID + " INTEGER PRIMARY KEY , "
            +  LocationEntry.COLUMN_NAME_latitude + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_longitude + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_altitude + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_accuracy + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_speed + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_bearing + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_provider + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_time + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_elapsedRealtimeNanos + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_capturedDate + "  TEXT , "
            +  LocationEntry.COLUMN_NAME_routeId + "  INTEGER "
            + " )";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME;

    public static final String SQL_DROP_ALL =
            "DELETE FROM " + LocationEntry.TABLE_NAME;


//    public LocationDbHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//
//        Log.v(TAG, " constructed LocationDbHelper :");
//    }
//
//    public void onCreate(SQLiteDatabase db) {
//        Log.v(TAG, "onCreate :");
//      //  db.execSQL(SQL_DELETE_ENTRIES);
//        db.execSQL(SQL_CREATE_ENTRIES);
//    }
//
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.v(TAG, "onUpgrade :");
//        // This database is only a cache for online data, so its upgrade policy is
//        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_ENTRIES);
//        onCreate(db);
//    }
//
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.v(TAG, "onDowngrade :");
//        onUpgrade(db, oldVersion, newVersion);
//    }
}
