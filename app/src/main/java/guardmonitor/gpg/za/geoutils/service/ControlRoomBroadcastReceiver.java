package guardmonitor.gpg.za.geoutils.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ControlRoomBroadcastReceiver extends BroadcastReceiver {

    private final static String TAG = "CRBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG,"onReceive starting GeoUtilsService");
        Intent startServiceIntent = new Intent(context, GeoUtilsService.class);
        context.startService(startServiceIntent);
    }
}