package guardmonitor.gpg.za.geoutils.customGeoFence;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Gerhard on 2016/10/12.
 */

public class PolyFence {

    private String mName;
    private ArrayList<LatLng> mLocations;

    public PolyFence(String name, ArrayList<LatLng> points){
        super();
        this.mLocations = points;
        this.mName = name;

    }

    public String getName() {
        return mName;
    }

    public ArrayList<LatLng> getLocations() {
        return mLocations;
    }
}
