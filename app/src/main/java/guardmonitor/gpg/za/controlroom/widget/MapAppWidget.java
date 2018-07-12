package guardmonitor.gpg.za.controlroom.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.Random;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.activity.data.DBDataActivity;
import guardmonitor.gpg.za.controlroom.activity.map.MainActivity;
import guardmonitor.gpg.za.geoutils.utils.NotificationUtils;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link MapAppWidgetConfigureActivity MapAppWidgetConfigureActivity}
 */
public class MapAppWidget extends AppWidgetProvider implements OnMapReadyCallback {
    private static final String TAG = "MapAppWidget";


    private GoogleMap mMap;


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "onMapReady :");
        mMap = googleMap;

        Log.v(TAG,"mMap nullcheck :" +  (mMap==null));
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        Log.v(TAG,"updateAppWidget");

        CharSequence widgetText = MapAppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.map_app_widget);
        views.setTextViewText(R.id.appwidget_name, widgetText);




        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("some data", "txt");  // for extra data if needed..

        Random generator = new Random();

        PendingIntent pi =PendingIntent.getActivity(context, generator.nextInt(), intent,PendingIntent.FLAG_UPDATE_CURRENT);

        views.setOnClickPendingIntent( R.id.button_open_controlroom, pi );


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG,"onUpdate");
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }


    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.v(TAG,"onDeleted");

        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            MapAppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        Log.v(TAG,"onEnabled");

        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        Log.v(TAG,"onDisabled");
        // Enter relevant functionality for when the last widget is disabled
    }
}

