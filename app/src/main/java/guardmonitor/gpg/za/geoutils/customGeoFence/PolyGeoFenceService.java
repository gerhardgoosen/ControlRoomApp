package guardmonitor.gpg.za.geoutils.customGeoFence;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.internal.ParcelableGeofence;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Map;

import guardmonitor.gpg.za.controlroom.activity.map.MainActivity;
import guardmonitor.gpg.za.geoutils.geoFence.Constants;

/**
 * Created by Gerhard on 2016/10/12.
 */

public class PolyGeoFenceService {

    private final static String TAG = "PolyGeoFenceService";
    private MainActivity pMainActivity;
    private Context pContext;
    private boolean mGeofencesAdded;

    protected ArrayList<PolyFence> mPolyFenceList;
    protected ArrayList<Marker> mPolygonMarkersList;
    protected ArrayList<Polygon> mPolygonList;

    public PolyGeoFenceService(Context context, MainActivity mainActivity) {

        super();
        this.pContext = context;
        this.pMainActivity = mainActivity;
        mPolygonList = new ArrayList<  >();
        mPolygonMarkersList = new ArrayList< >();
        mPolyFenceList = new ArrayList<>();
        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = this.pMainActivity.getSharedPrefs().getBoolean(Constants.GEOFENCES_ADDED_KEY, false);

        populateGeofenceList();

    }


    public void populateGeofenceList() {

        PolyFencesStatic ps = new PolyFencesStatic();


        for (PolyFence f : ps.polyfences) {
            Log.v(TAG, "Poly Fence " + f.getName());
            mPolyFenceList.add(f);
        }
    }


    public void drawGeoFences() {
        for (PolyFence fence : mPolyFenceList) {

            if (fence == null) break;

            LatLng fenceStart = fence.getLocations().get(0);
            Marker m = pMainActivity.getMainMap().addMarker(new MarkerOptions()
                    .position(new LatLng(fenceStart.latitude, fenceStart.longitude))
                    .title("Fence " + fence.getName())
                    .snippet("Vertices Count :  " + fence.getLocations().size()));

            mPolygonMarkersList.add(m);

            m.showInfoWindow();

            //draw shape
            Polygon polygon = pMainActivity.getMainMap().addPolygon(
                    new PolygonOptions()
                            .addAll(fence.getLocations())
                            .strokeColor(Color.TRANSPARENT)
                            .fillColor(0x40ff0000)
                            .strokeWidth(5)
            );


            mPolygonList.add(polygon);

        }


    }

    public void cleanGeoFences() {
        for (Polygon p : mPolygonList) {
            p.remove();
        }
        for (Marker marker : mPolygonMarkersList) {
            marker.remove();
        }


    }

    public boolean isGeofencesAdded() {
        return mGeofencesAdded;
    }


    public void checkFenceTransitions(LatLng l) {
        Log.v(TAG, "checkFenceTransitions : " + l.toString());


        for (PolyFence f : mPolyFenceList) {
            Log.v(TAG, "Checking Fence : " + f.getName());
            if (isPointInPoly(l, f.getLocations())) {
                Log.v(TAG, "Point FOUND IN PolyFence : " + f.getName());
                break;
            }else{
                Log.v(TAG, "Point NOT IN PolyFence : " + f.getName());
            }
        }

    }


    private boolean isPointInPoly(LatLng point, ArrayList<LatLng> thePoly) {
        int crossings = 0;

        int count = thePoly.size();
        // for each edge
        for (int i = 0; i < count; i++) {
            LatLng a = thePoly.get(i);
            int j = i + 1;
            if (j >= count) {
                j = 0;
            }
            LatLng b = thePoly.get(j);
            if (RayCrossesSegment(point, a, b)) {
                crossings++;
            }
        }
        // odd number of crossings?
        return (crossings % 2 == 1);
    }

    private boolean RayCrossesSegment(LatLng point, LatLng a, LatLng b) {
        double px = point.longitude;
        double py = point.latitude;
        double ax = a.longitude;
        double ay = a.latitude;
        double bx = b.longitude;
        double by = b.latitude;
        if (ay > by) {
            ax = b.longitude;
            ay = b.latitude;
            bx = a.longitude;
            by = a.latitude;
        }
        // alter longitude to cater for 180 degree crossings
        if (px < 0) {
            px += 360;
        }
        ;
        if (ax < 0) {
            ax += 360;
        }
        ;
        if (bx < 0) {
            bx += 360;
        }
        ;

        if (py == ay || py == by) py += 0.00000001;
        if ((py > by || py < ay) || (px > Math.max(ax, bx))) return false;
        if (px < Math.min(ax, bx)) return true;

        double red = (ax != bx) ? ((by - ay) / (bx - ax)) : Float.MAX_VALUE;
        double blue = (ax != px) ? ((py - ay) / (px - ax)) : Float.MAX_VALUE;
        return (blue >= red);
    }
}
