package guardmonitor.gpg.za.geoutils.service;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Gerhard on 2016/10/18.
 */

public class ServiceUtils {


    public static boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


}
