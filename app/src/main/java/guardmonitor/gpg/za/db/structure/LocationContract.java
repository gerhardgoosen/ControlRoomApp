package guardmonitor.gpg.za.db.structure;

import android.provider.BaseColumns;

/**
 * Created by Gerhard on 2016/10/03.
 */

public final class LocationContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private LocationContract() {}

    /* Inner class that defines the table contents */
    public static class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "locations";
        public static final String COLUMN_NAME_latitude ="latitude";
        public static final String COLUMN_NAME_longitude ="longitude";
        public static final String COLUMN_NAME_altitude ="altitude";
        public static final String COLUMN_NAME_accuracy ="accuracy";
        public static final String COLUMN_NAME_speed ="speed";
        public static final String COLUMN_NAME_bearing ="bearing";
        public static final String COLUMN_NAME_provider ="provider";
        public static final String COLUMN_NAME_time ="time";
        public static final String COLUMN_NAME_elapsedRealtimeNanos ="elapsedRealtimeNanos";
        public static final String COLUMN_NAME_capturedDate ="capturedDate";
        public static final String COLUMN_NAME_routeId ="route_id";
    }
}