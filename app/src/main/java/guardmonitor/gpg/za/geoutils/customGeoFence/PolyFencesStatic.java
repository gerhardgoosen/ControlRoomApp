package guardmonitor.gpg.za.geoutils.customGeoFence;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Gerhard on 2016/10/12.
 */

public class PolyFencesStatic {
    public LatLng l = new LatLng(15, 15);

    public static ArrayList<LatLng> fA;// = new ArrayList<>();
    public static ArrayList<LatLng> fB;// = new ArrayList<>();
    public static ArrayList<LatLng> fC;// = new ArrayList<>();

    public   PolyFence polyfencesA;// = new PolyFence("PolyFence A", fA);
    public   PolyFence polyfencesB;// = new PolyFence("PolyFence B", fB);
    public   PolyFence polyfencesC;// = new PolyFence("PolyFence C", fC);

    public   PolyFence[] polyfences;// = new PolyFence[]{};

    PolyFencesStatic() {

        polyfencesA = new PolyFence("PolyFence A", setupFenceA());
        polyfencesB = new PolyFence("PolyFence B", setupFenceB());
        polyfencesC = new PolyFence("PolyFence C", setupFenceC());
        polyfences = new PolyFence[]{polyfencesA,polyfencesB,polyfencesC};
    }

    private ArrayList<LatLng> setupFenceA() {
        ArrayList<LatLng> retList = new ArrayList<>();
        retList.add(new LatLng(0, 0));
        retList.add(new LatLng(10, 0));
        retList.add(new LatLng(10, 10));
        retList.add(new LatLng(12, 10));
        retList.add(new LatLng(13, 10));
        retList.add(new LatLng(10, 12));
        retList.add(new LatLng(10, 13));
        retList.add(new LatLng(0, 10));
        retList.add(new LatLng(0, 0));
        return retList;
    }


    private ArrayList<LatLng> setupFenceB() {
        ArrayList<LatLng> retList = new ArrayList<>();
        retList.add(new LatLng(15, 15));
        retList.add(new LatLng(15, 25));
        retList.add(new LatLng(25, 25));
        retList.add(new LatLng(25, 15));
        retList.add(new LatLng(15, 15));
        return retList;
    }


    private ArrayList<LatLng> setupFenceC() {
        ArrayList<LatLng> retList = new ArrayList<>();
        retList.add(new LatLng(-25.9991795, 28.1262927));
        retList.add(new LatLng(-25, 28));
        retList.add(new LatLng(-24, 29));
        retList.add(new LatLng(-26.1393833, 28.2468148));
        retList.add(new LatLng(-25.9991795, 28.1262927));
        return retList;
    }
}