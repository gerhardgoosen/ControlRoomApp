package guardmonitor.gpg.za.geoutils.async;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

import guardmonitor.gpg.za.controlroom.activity.map.MainActivity;
import guardmonitor.gpg.za.geoutils.utils.HttpRequest;

/**
 * Created by Gerhard on 2016/09/21.
 */
public class MapIT_KMLServiceTask extends AsyncTask<URL, Integer, Long> {
    private final static String TAG = "MapIT_KMLServiceTask";
    private Context mContext;
    private Object mCallerActivity;
    private BufferedReader reader;
    private Location trackedLocation;

    private InputStream muni_stream;
    private InputStream ward_stream;

    public MapIT_KMLServiceTask(Object callerActivity,Context context, Location locationData) {
        this.mContext = context;
        this.trackedLocation = locationData;
        this.mCallerActivity=callerActivity;
    }

    protected Long doInBackground(URL... urls) {
        int count = urls.length;
        long totalSize = 0;
        for (int i = 0; i < count; i++) {


            //totalSize += Downloader.downloadFile(urls[i]);
            try {
                String deviceUUID = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
                //http://mapit.code4sa.org

                this.getPointInfo(urls[i], trackedLocation);


            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            publishProgress((int) ((i / (float) count) * 100));
            // Escape early if cancel() is called
            if (isCancelled()) break;
        }
        return totalSize;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        Log.v(TAG, "onProgressUpdate");
    }

    @Override
    protected void onPostExecute(Long result) {
        //showDialog("Position Tracked " + result + " bytes");

        if(muni_stream != null){
            ((MainActivity) mCallerActivity).addMunicipalityKMLLayers(muni_stream);
        }

        if(ward_stream != null){
            ((MainActivity) mCallerActivity).addWardKMLLayers(ward_stream);
        }


    }

    @Override
    protected void onPreExecute() {
        Log.v(TAG, "onPreExecute");
    }

//http://mapit.code4sa.org/area/MDB:63701006.geojson

    private void getPointInfo(URL MAPIT_URL, Location location) {
        StringBuilder sb = new StringBuilder();
        String WARD_URL = MAPIT_URL + "/point/4326/"+location.getLongitude()+","+location.getLatitude()+"?type=WD";
        String MUNICIPALITY_URL = MAPIT_URL + "/point/4326/"+location.getLongitude()+","+location.getLatitude()+"?type=MN";

        try {

            if( ((MainActivity) mCallerActivity).isWardShown()) {
                Log.v(TAG, "URL :::: > " + WARD_URL);
                HttpRequest wd_request = new HttpRequest(WARD_URL);
                wd_request.withHeaders("Content-Type: application/json");//add request header: "Content-Type: application/json"
                wd_request.prepare(HttpRequest.Method.GET);//Set HttpRequest method as PUT
                //req.withData(location.toString());//Add json data to request body
                JSONObject wd_response = wd_request.sendAndReadJSON();
                Log.v(TAG, ":::: >  response : " + wd_response.toString());

                JSONObject wardCodesDetails = (JSONObject) ((JSONObject) wd_response.get(wd_response.keys().next())).get("codes");

                String WARD_ID = wardCodesDetails.getString("MDB");
                URL WARD_KML_URL = new URL(MAPIT_URL + "/area/MDB:" + WARD_ID + ".kml");


                ward_stream = getKMLLayer(WARD_KML_URL);
            }

            if( ((MainActivity) mCallerActivity).isMunicipalityShown()) {
                Log.v(TAG, "URL :::: > " + MUNICIPALITY_URL);
                HttpRequest mn_request = new HttpRequest(MUNICIPALITY_URL);
                mn_request.withHeaders("Content-Type: application/json");//add request header: "Content-Type: application/json"
                mn_request.prepare(HttpRequest.Method.GET);//Set HttpRequest method as PUT
                //req.withData(location.toString());//Add json data to request body

             //  Log.v(TAG, ":::: >  string response : "  + mn_request.sendAndReadString());

                JSONObject mn_response = mn_request.sendAndReadJSON();
                Log.v(TAG, ":::: >  response : " + mn_response.toString());

                JSONObject municipalityCodesDetails = (JSONObject) ((JSONObject) mn_response.get(mn_response.keys().next())).get("codes");

                String MUNICIPALITY_ID = municipalityCodesDetails.getString("MDB");

                URL MUNICIPALITY_KML_URL = new URL(MAPIT_URL + "/area/MDB:" + MUNICIPALITY_ID + ".kml");


               muni_stream = getKMLLayer(MUNICIPALITY_KML_URL);

            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "Exception  :::: >  e : " + e.getMessage());
        }



    }



    private InputStream getKMLLayer (URL MAPIT_KML_URL ) {
        byte[] kml_response = null;

        try {
            Log.v(TAG, "URL :::: > " + MAPIT_KML_URL);
            HttpRequest kml_request =new HttpRequest(MAPIT_KML_URL);
            kml_request.withHeaders("Content-Type: application/json");//add request header: "Content-Type: application/json"
            kml_request.prepare(HttpRequest.Method.GET);//Set HttpRequest method as PUT

            kml_response = kml_request.sendAndReadBytes() ;
            Log.v(TAG,":::: >  kml_request response : " + kml_response.toString());


        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "Exception  :::: >  e : " + e.getMessage());
        }

        return new ByteArrayInputStream(kml_response);


    }
}