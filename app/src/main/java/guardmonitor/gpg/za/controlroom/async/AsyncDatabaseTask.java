package guardmonitor.gpg.za.controlroom.async;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class AsyncDatabaseTask extends AsyncTask<Void, Void, Boolean> {

        protected static final String TAG = "AsyncDatabaseTask";

        private Object mCallerActivity;
        private static final String PARM_CALLBACK = "CALLBACK";
        private static final String PARM_SQL_ACTION = "SQL_ACTION";
        private static final String PARM_TABLE_NAME = "TABLE_NAME";
        private static final String PARM_SQL_CONTENT_VALUES = "SQL_CONTENT_VALUES";

        private static final String PARM_PROJECTION = "PROJECTION";
        private static final String PARM_WHERE_CLAUSE = "WHERE_CLAUSE";
        private static final String PARM_WHERE_CLAUSE_DATA = "WHERE_CLAUSE_DATA";

        private static final String PARM_GROUPBY = "GROUPBY";
        private static final String PARM_FILTERBY = "FILTERBY";

        private static final String PARM_SORT_ORDER = "SORT_ORDER";


        private static final String INSERT = "INSERT";
        private static final String UPDATE = "UPDATE";
        private static final String DELETE = "DELETE";
        private static final String SELECT = "SELECT";

        private final SQLiteDatabase db;
        private final Context mContext;
        private HashMap<String, Object> mParms;

        public AsyncDatabaseTask(Object callerActivity,Context pContext, SQLiteDatabase db, HashMap<String, Object> parms) {
            this.db = db;
            this.mParms = parms;
            this.mContext = pContext;
            this.mCallerActivity=callerActivity;

            try {
                switch ((String) mParms.get(PARM_SQL_ACTION)) {
                    case INSERT: {
                        validateInsert(mParms);
                        break;
                    }
                    case UPDATE: {
                        validateUpdate(mParms);
                        break;
                    }
                    case DELETE: {
                        validateDelete(mParms);
                        break;
                    }
                    case SELECT: {
                        validateSelect(mParms);
                        break;
                    }
                    default: {
                        Log.v(TAG, "AsyncDatabaseTask.switch.default - ?? ");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            Log.v(TAG, "AsyncDatabaseTask.doInBackground");

            try {
                switch ((String) mParms.get(PARM_SQL_ACTION)) {
                    case INSERT: {
                        handleInsert(mParms);
                        break;
                    }
                    case UPDATE: {
                        handleUpdate(mParms);
                        break;
                    }
                    case DELETE: {
                        handleDelete(mParms);
                        break;
                    }
                    case SELECT: {
                        handleSelect(mParms);
                        break;
                    }
                    default: {
                        Log.v(TAG, "AsyncDatabaseTask.switch.default - ?? ");
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Log.v(TAG, "AsyncDatabaseTask.onPostExecute");
            // showProgress(false);

            if (success) {
               // NotificationUtils.makeToast(mContext, "Report Captured", 1);
            } else {
                // mPasswordView.setError(getString(R.string.error_incorrect_password));
                // mPasswordView.requestFocus();
            }
            return;
        }

        @Override
        protected void onCancelled() {
            Log.v(TAG, "AsyncDatabaseTask.onCancelled");

            //showProgress(false);
        }


        //Insert
        //================
        private void validateInsert(HashMap<String, Object> parmdata) throws Exception {
            Log.v(TAG, "AsyncDatabaseTask validateInsert");

            if (parmdata.get(PARM_SQL_ACTION) != null &&
                    parmdata.get(PARM_TABLE_NAME) != null &&
                    parmdata.get(PARM_SQL_CONTENT_VALUES) != null) {

            } else {
                throw new Exception("Insert Data Not Valid");
            }

        }

        private long handleInsert(HashMap<String, Object> parmdata) {
            Log.v(TAG, "AsyncDatabaseTask handleInsert");
            long newRowId = 0L;
            try {
                // Insert the new row, returning the primary key value of the new row
                newRowId = db.insert((String) parmdata.get(PARM_TABLE_NAME), null, (ContentValues) parmdata.get(PARM_SQL_CONTENT_VALUES));

                Method callback = (Method) parmdata.get(PARM_CALLBACK);
                try {
                    if(callback!=null && mCallerActivity !=null){
                        callback.invoke(mCallerActivity,parmdata.get("SQL_RAW_DATA"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                newRowId = -100L;
                e.printStackTrace();
            }

            return newRowId;
        }


        //Update
        //================
        private void validateUpdate(HashMap<String, Object> parmdata) {
            Log.v(TAG, "AsyncDatabaseTask validateUpdate");
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleUpdate(HashMap<String, Object> parmdata) {
            Log.v(TAG, "AsyncDatabaseTask handleUpdate");
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Delete
        //================
        private void validateDelete(HashMap<String, Object> parmdata) {
            Log.v(TAG, "AsyncDatabaseTask validateDelete");
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void handleDelete(HashMap<String, Object> parmdata) {
            Log.v(TAG, "AsyncDatabaseTask handleDelete");
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        //Select
        //================
        private void validateSelect(HashMap<String, Object> parmdata) throws Exception {
            Log.v(TAG, "AsyncDatabaseTask validateSelect");
            if (parmdata.get(PARM_SQL_ACTION) != null &&
                    parmdata.get(PARM_TABLE_NAME) != null &&
                    parmdata.get(PARM_PROJECTION) != null &&
                    parmdata.get(PARM_SORT_ORDER) != null) {

            } else {
                throw new Exception("Select Data Not Valid");
            }
        }

        private void handleSelect(HashMap<String, Object> parmdata) {
            Log.v(TAG, "AsyncDatabaseTask handleSelect");
            JSONObject return_data = new JSONObject();
            Method callback = null;
            try {

                String tableName = (String) parmdata.get(PARM_TABLE_NAME);
                String[] projection = (String[]) parmdata.get(PARM_PROJECTION);
                String selection = (String) parmdata.get(PARM_WHERE_CLAUSE);
                String[] selectionArgs = (String[]) parmdata.get(PARM_WHERE_CLAUSE_DATA);
                String sortOrder = (String) parmdata.get(PARM_SORT_ORDER);
                String groupBy = (String) parmdata.get(PARM_GROUPBY);
                String filterBy = (String) parmdata.get(PARM_FILTERBY);

                callback = (Method) parmdata.get(PARM_CALLBACK);


                Cursor c = db.query(true,
                        tableName,                 // The table to query
                        projection,                               // The columns to return
                        selection,                                // The columns for the WHERE clause
                        selectionArgs,                            // The values for the WHERE clause
                        groupBy,                                     // don't group the rows
                        filterBy,                                     // don't filter by row groups
                        sortOrder                                 // The sort order
                        ,"" //limit
                );


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


            try {
                if(callback!=null && mCallerActivity !=null){
                    callback.invoke(mCallerActivity,return_data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }