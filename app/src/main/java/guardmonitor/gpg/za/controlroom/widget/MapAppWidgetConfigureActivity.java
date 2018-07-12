package guardmonitor.gpg.za.controlroom.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.activity.data.DBDataActivity;

/**
 * The configuration screen for the {@link MapAppWidget MapAppWidget} AppWidget.
 */
public class MapAppWidgetConfigureActivity extends Activity {
    private static final String TAG = "MapAppWidgetConfAct";
    private static final String PREFS_NAME = "guardmonitor.gpg.za.controlroom.widget.MapAppWidget";
    private static final String PREF_PREFIX_KEY = "appwidget_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    Button mLocationsButton;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            Log.v(TAG, "OnClickListener");

            final Context context = MapAppWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            saveTitlePref(context, mAppWidgetId, widgetText);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            MapAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };


    View.OnClickListener mOnLocationClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            Log.v(TAG, "mOnLocationClickListener");

        }
    };

    public MapAppWidgetConfigureActivity() {
        super();
        Log.v(TAG, "MapAppWidgetConfigureActivity");
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        Log.v(TAG, "saveTitlePref");
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_KEY + appWidgetId, text);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        Log.v(TAG, "loadTitlePref");
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.appwidget_name);
        }
    }

    static void deleteTitlePref(Context context, int appWidgetId) {
        Log.v(TAG, "deleteTitlePref");
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Log.v(TAG, "onCreate");
        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.map_app_widget_configure);
        mAppWidgetText = (EditText) findViewById(R.id.appwidget_name);
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);



        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }

        mAppWidgetText.setText(loadTitlePref(MapAppWidgetConfigureActivity.this, mAppWidgetId));
    }
}

