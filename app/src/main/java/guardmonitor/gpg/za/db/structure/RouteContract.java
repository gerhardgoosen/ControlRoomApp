package guardmonitor.gpg.za.db.structure;

import android.provider.BaseColumns;

/**
 * Created by Gerhard on 2016/10/03.
 */

public final class RouteContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RouteContract() {}

    /* Inner class that defines the table contents */
    public static class RouteEntry implements BaseColumns {
        public static final String TABLE_NAME = "routes";
        public static final String COLUMN_NAME_device_uuid ="device";
        public static final String COLUMN_NAME_route_name ="route_name";
        public static final String COLUMN_NAME_capturedDate ="capturedDate";
    }
}