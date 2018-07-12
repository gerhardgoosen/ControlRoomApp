package guardmonitor.gpg.za.db;

import guardmonitor.gpg.za.db.structure.RouteContract;

/**
 * Created by Gerhard on 2016/10/05.
 */

public class RouteDbHelper {//extends SQLiteOpenHelper {
    protected static final String TAG = "RouteDbHelper";

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + RouteContract.RouteEntry.TABLE_NAME
            + " ("
            + RouteContract.RouteEntry._ID + " INTEGER PRIMARY KEY,"
            + RouteContract.RouteEntry.COLUMN_NAME_device_uuid  + "  TEXT NOT NULL ,"
            + RouteContract.RouteEntry.COLUMN_NAME_route_name + "  TEXT NOT NULL ,"
            + RouteContract.RouteEntry.COLUMN_NAME_capturedDate + "  TEXT "
            + ")";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + RouteContract.RouteEntry.TABLE_NAME;


    public static final String SQL_DELETE_ENTRIES =
            "DELETE FROM " + RouteContract.RouteEntry.TABLE_NAME;


//    public RouteDbHelper(Context context) {
//        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//
//        Log.v(TAG, " constructed RouteDbHelper :");
//    }
//    public void onCreate(SQLiteDatabase db) {
//        Log.v(TAG, "onCreate :");
//        db.execSQL(SQL_CREATE_ENTRIES);
//    }
//
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.v(TAG, "onUpgrade :");
//        // This database is only a cache for online data, so its upgrade policy is
//        // to simply to discard the data and start over
//        db.execSQL(SQL_DROP_TABLE);
//        onCreate(db);
//    }
//
//    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        Log.v(TAG, "onDowngrade :");
//        onUpgrade(db, oldVersion, newVersion);
//    }


}
