package guardmonitor.gpg.za.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseManager extends SQLiteOpenHelper {
    private static final String LOG_TAG = "GuardMonitorControl";
    private static final String DB_NAME = "GuardMonitorControl.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseManager(Context context){
        super(context, DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LocationDbHelper.SQL_CREATE_TABLE);
        db.execSQL(RouteDbHelper.SQL_CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Here you can perform updates when the database structure changes
        // Begin transaction
        db.beginTransaction();

        try {
            if(oldVersion<2){
                // Upgrade database structure from Version 1 to 2
                String alterTable = "ALTER ....";

                db.execSQL(alterTable);
                Log.i(LOG_TAG,"Successfully upgraded to Version 2");
            }
            // This allows you to upgrade from any version to the next most 
            // recent one in multiple steps as you don't know if the user has
            // skipped any of the previous updates
            if(oldVersion<3){
                // Upgrade database structure from Version 2 to 3
                String alterTable = "ALTER ....";

                db.execSQL(alterTable);
                Log.i(LOG_TAG,"Successfully upgraded to Version 3");
            }

            // Only when this code is executed, the changes will be applied 
            // to the database
            db.setTransactionSuccessful();
        } catch(Exception ex){
            ex.printStackTrace();
        } finally {
            // Ends transaction
            // If there was an error, the database won't be altered
            db.endTransaction();
        }
    }
}