package guardmonitor.gpg.za.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class DBContentProvider {//extends ContentProvider {

    private static final String TAG = "DBContentProvider";

    private SQLiteDatabase db;


    public DBContentProvider(Context context) {
        Log.v(TAG, "DBContentProvider");
        // Initialize the database and assign it to the private variable
        DatabaseManager sqlHelper = new DatabaseManager(context);
        db = sqlHelper.getReadableDatabase();


    }


    public int delete(@NonNull String tableName, @Nullable String selection,
                               @Nullable String[] selectionArgs){
        Log.v(TAG, "delete");
        return  db.delete(tableName,selection,selectionArgs);


    }



    public  int update(@NonNull String tableName, @Nullable ContentValues values,
                       @Nullable String selection, @Nullable String[] selectionArgs){
        Log.v(TAG, "update");
       return  db.update(tableName,values,selection,selectionArgs);


    }


    public  Long insert(@NonNull String tableName, @Nullable ContentValues values){
        Log.v(TAG, "insert");

        return  db.insert(tableName, "" ,values);
    }


   public Cursor query (String tableName, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.v(TAG, "query");
        // handle the query here, form it, do your checks and then access the DB
        return db.query(tableName,projection,selection,selectionArgs,null,null,sortOrder);
    }
}

