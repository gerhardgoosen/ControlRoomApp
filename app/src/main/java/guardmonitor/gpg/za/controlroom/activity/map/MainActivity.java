
package guardmonitor.gpg.za.controlroom.activity.map;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.geojson.GeoJsonLayer;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.kml.KmlContainer;
import com.google.maps.android.kml.KmlLayer;
import com.google.maps.android.kml.KmlPlacemark;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import guardmonitor.gpg.za.controlroom.R;
import guardmonitor.gpg.za.controlroom.activity.access_control.ScannerActivity;
import guardmonitor.gpg.za.controlroom.activity.data.DBDataActivity;
import guardmonitor.gpg.za.controlroom.activity.user_feedback.ReportIncidentActivity;
import guardmonitor.gpg.za.controlroom.widget.MapAppWidget;
import guardmonitor.gpg.za.db.EntityManager;
import guardmonitor.gpg.za.db.pojo.Route;
import guardmonitor.gpg.za.geoutils.async.MapIT_KMLServiceTask;
import guardmonitor.gpg.za.geoutils.customGeoFence.PolyGeoFenceService;
import guardmonitor.gpg.za.geoutils.geoFence.Constants;
import guardmonitor.gpg.za.geoutils.geoFence.GeoFenceService;
import guardmonitor.gpg.za.geoutils.service.GeoUtilsService;
import guardmonitor.gpg.za.geoutils.service.LocationRequestor;
import guardmonitor.gpg.za.geoutils.service.ServiceUtils;
import guardmonitor.gpg.za.geoutils.utils.NotificationUtils;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, OnMapLongClickListener {

    protected static final String TAG = "MainActivity";

    public static MainActivity instance;
    public static Context mContext;

    protected static final String ROUTE_START = "START";
    protected static final String ROUTE_END = "END";

    private View mMainView;
    private GoogleMap mMap;
    private LocationRequestor mLocationRequestor;
    private GeoFenceService mGeoFenceService;
    private boolean trackPositionEnabled = false;
    private boolean updateLocationEnabled = false;
    private SharedPreferences mSharedPreferences;

    private static final String MAPIT_URL = "http://mapit.code4sa.org";

    private KmlLayer wardKML, municipalityKML;
    private List<Marker> wardKMLPlacemarkMarkers, municipalityKMLPlacemarkMarkers;


    private boolean recordingRoute = false,
            alarmTriggered = false,
            showMunicipality = false,
            showWard = false;

    private ContextMenu mMapContextMenu;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private String tmpRouteName;
    private ArrayList<Location> tmpRouteData;

    private PolyGeoFenceService mPolyFenceService;


    private EntityManager entityManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //register for context menu
        mMainView = mapFragment.getView();
        registerForContextMenu(mMainView);


        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        // Enable the GeoFencing Service.
        mGeoFenceService = new GeoFenceService(this, this);
        mPolyFenceService = new PolyGeoFenceService(this, this);
        // Kick off the request to build GoogleApiClient.
        mLocationRequestor = new LocationRequestor(this, this, mGeoFenceService);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        //init entitymanager
        entityManager = new EntityManager(this, getApplicationContext());

        wardKMLPlacemarkMarkers = new ArrayList<Marker>();
        municipalityKMLPlacemarkMarkers = new ArrayList<Marker>();


        mContext = this;
        instance = this;


        //auto start service if its not running
        if (!ServiceUtils.isMyServiceRunning(GeoUtilsService.class, getApplicationContext())) {
            Log.v(TAG, "GeoUtilsService starting....");
            Intent startServiceIntent = new Intent(getApplicationContext(), GeoUtilsService.class);
            getApplicationContext().startService(startServiceIntent);
        } else {
            Log.v(TAG, "GeoUtilsService seems to be running....");
        }

    }


    public static MainActivity getInstance() {
        return instance;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.v(TAG, "onMapReady :");
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        initMapSettings();
        initGeoFenceSettings(mGeoFenceService);

        //give a hint
        NotificationUtils.makeToast(this, "Long press for Options", 1);

    }

    private void addGeoJSONLayer() {
        try {
            // GeoJsonLayer munisipalities = new GeoJsonLayer(mMap, R.raw.geojson_country_za_munisipaities, getApplicationContext());
            GeoJsonLayer wards = new GeoJsonLayer(mMap, R.raw.geojson_country_za_wards, getApplicationContext());

//            munisipalities.addLayerToMap();
            wards.addLayerToMap();

//            GeoJsonLayer wc = new GeoJsonLayer(mMap, R.raw.geojson_province_wc, getApplicationContext());
//            GeoJsonLayer nc = new GeoJsonLayer(mMap,R.raw.geojson_province_nc, getApplicationContext());
//            GeoJsonLayer mp = new GeoJsonLayer(mMap, R.raw.geojson_province_mp, getApplicationContext());
//            GeoJsonLayer lim = new GeoJsonLayer(mMap, R.raw.geojson_province_lim, getApplicationContext());
//
//            wc.addLayerToMap();
//            nc.addLayerToMap();
//            mp.addLayerToMap();
//            lim.addLayerToMap();
//
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addWardKMLLayers(@NonNull InputStream kml) {
        Log.v(TAG, "addWardKMLLayers");
        try {
            if (instance.wardKML != null) {
                instance.wardKML.removeLayerFromMap();
            }

            instance.wardKML = new KmlLayer(mMap, kml, getApplicationContext());
            wardKMLPlacemarkMarkers.addAll(createPlaceMarkMarkers(instance.wardKML,Color.GREEN,Color.RED));
            instance.wardKML.addLayerToMap();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMunicipalityKMLLayers(@NonNull InputStream kml) {
        Log.v(TAG, "addMunicipalityKMLLayers");
        try {
            if (instance.municipalityKML != null) {
                instance.municipalityKML.removeLayerFromMap();
            }
            instance.municipalityKML = new KmlLayer(mMap, kml, getApplicationContext());

            municipalityKMLPlacemarkMarkers.addAll(createPlaceMarkMarkers(instance.municipalityKML,Color.BLUE,Color.BLACK));
            instance.municipalityKML.addLayerToMap();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private List<Marker> createPlaceMarkMarkers(KmlLayer layer, int kmlFillColor, int kmStrokeColor) {
        ArrayList<Marker> placeMarkerList = new ArrayList<>();
        Log.v(TAG, "createPlaceMarkMarkers "  );
        Log.v(TAG, "hasPlacemarks " + layer.hasPlacemarks() );
        Log.v(TAG, "hasContainers " + layer.hasContainers() );


        for (KmlContainer container : layer.getContainers()  ) {
            // Do something to container
            if (container.hasContainers()) {
                Log.v(TAG, "container  hasContainers" + container.hasContainers());
            }
            if (container.hasPlacemarks()) {
                Log.v(TAG, "container hasPlacemarks " + container.hasPlacemarks() );
                for (KmlPlacemark placemark : container.getPlacemarks()) {
                    Log.v(TAG, placemark.getProperty("name"));
                    Log.v(TAG, placemark.getGeometry().getGeometryType());

                    LatLng markerPoint = null;

                    if (placemark.getGeometry().getGeometryType().equals("Polygon")) {
                        Log.v(TAG, "Geom is Polygon");
                        List a =    ( (ArrayList) placemark.getGeometry().getGeometryObject() ) ;
                        List b = (ArrayList) a.get(0);


                                        Log.v(TAG,"aluist sizze : "+ a.size());
                        Log.v(TAG,"b luist sizze : "+ b.size());

                        markerPoint = (LatLng) b.get(0);
                     //  p.setFillColor(kmlFillColor);
                     //   p.setStrokeColor(kmlFillColor);

                          //markerPoint = ((Polygon) placemark.getGeometry().getGeometryObject()).getPoints().get(0);
                    }

                    placeMarkerList.add(
                            getMainMap().addMarker(new MarkerOptions()
                                    .position(markerPoint)
                                    .title(placemark.getProperty("name")))
                    );


                }
            }

        }

        return placeMarkerList;
    }

    public void removeWardKMLLayers() {


        try {
            Log.v(TAG, "removeWardKMLLayers " + instance.wardKML.toString());

            this.showWard = false;
            //if (wardKML != null)
            instance.wardKML.removeLayerFromMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeMunicipalityKMLLayers() {


        try {
            Log.v(TAG, "removeMunicipalityKMLLayers " + instance.municipalityKML.toString());
            this.showMunicipality = false;
            instance.municipalityKML.removeLayerFromMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


//
//    public void addKMLLayers() {
//        try {

//            KmlLayer all = new KmlLayer(mMap, R.raw.kml_province_all, getApplicationContext());
//            all.addLayerToMap();

//            KmlLayer ec = new KmlLayer(mMap, R.raw.kml_province_ec, getApplicationContext());
//            KmlLayer fs = new KmlLayer(mMap, R.raw.kml_province_fs, getApplicationContext());
//            KmlLayer gt = new KmlLayer(mMap, R.raw.kml_province_gt, getApplicationContext());
//            KmlLayer kzn = new KmlLayer(mMap, R.raw.kml_province_kzn, getApplicationContext());
//            KmlLayer lim = new KmlLayer(mMap, R.raw.kml_province_lim, getApplicationContext());
//            KmlLayer mp = new KmlLayer(mMap, R.raw.kml_province_mp, getApplicationContext());
//            KmlLayer nc = new KmlLayer(mMap, R.raw.kml_province_nc, getApplicationContext());
//            KmlLayer nw = new KmlLayer(mMap, R.raw.kml_province_nw, getApplicationContext());
//            KmlLayer wc = new KmlLayer(mMap, R.raw.kml_province_wc, getApplicationContext());

    //  ec.addLayerToMap();
    //  fs.addLayerToMap();
//            gt.addLayerToMap();
    //   kzn.addLayerToMap();
    //   lim.addLayerToMap();
    //    mp.addLayerToMap();
    //  nc.addLayerToMap();
    //  nw.addLayerToMap();
    //wc.addLayerToMap();

    //        KmlLayer ec_mun = new KmlLayer(mMap, R.raw.kml_province_ec_mn, getApplicationContext());
//            KmlLayer fs_mun = new KmlLayer(mMap, R.raw.kml_province_fs_mn, getApplicationContext());
//            KmlLayer gt_mun = new KmlLayer(mMap, R.raw.kml_province_gt_mn, getApplicationContext());
//            KmlLayer kzn_mun = new KmlLayer(mMap, R.raw.kml_province_kzn_mn, getApplicationContext());
//            KmlLayer lim_mun = new KmlLayer(mMap, R.raw.kml_province_lim_mn, getApplicationContext());
//            KmlLayer mp_mun = new KmlLayer(mMap, R.raw.kml_province_mp_mn, getApplicationContext());
//            KmlLayer nc_mun = new KmlLayer(mMap, R.raw.kml_province_nc_mn, getApplicationContext());
//            KmlLayer nw_mun = new KmlLayer(mMap, R.raw.kml_province_nw_mn, getApplicationContext());
//            KmlLayer wc_mun = new KmlLayer(mMap, R.raw.kml_province_wc_mn, getApplicationContext());

    //   ec_mun.addLayerToMap();
//            fs_mun.addLayerToMap();
//            gt_mun.addLayerToMap();
//            kzn_mun.addLayerToMap();
//            lim_mun.addLayerToMap();
//            mp_mun.addLayerToMap();
//            nc_mun.addLayerToMap();
//            nw_mun.addLayerToMap();
//            wc_mun.addLayerToMap();

    // KmlLayer ec_ward = new KmlLayer(mMap, R.raw.kml_province_ec, getApplicationContext());
    // KmlLayer fs_ward = new KmlLayer(mMap, R.raw.kml_province_fs_wd, getApplicationContext());
//            KmlLayer gt_ward = new KmlLayer(mMap, R.raw.kml_province_gt_wd, getApplicationContext());
    //  KmlLayer kzn_ward= new KmlLayer(mMap, R.raw.kml_province_kzn, getApplicationContext());
    //   KmlLayer lim_ward = new KmlLayer(mMap, R.raw.kml_province_lim_wd, getApplicationContext());
    //    KmlLayer mp_ward = new KmlLayer(mMap, R.raw.kml_province_mp_wd, getApplicationContext());
    //   KmlLayer nc_ward = new KmlLayer(mMap, R.raw.kml_province_nc_wd, getApplicationContext());
    //   KmlLayer nw_ward = new KmlLayer(mMap, R.raw.kml_province_nw_wd, getApplicationContext());
    //     KmlLayer wc_ward = new KmlLayer(mMap, R.raw.kml_province_wc_wd, getApplicationContext());

    //  ec.addLayerToMap();
    //  fs_ward.addLayerToMap();
//            gt_ward.addLayerToMap();
//            kzn.addLayerToMap();
    //   lim_ward.addLayerToMap();
    //    mp_ward.addLayerToMap();
    //   nc_ward.addLayerToMap();
    //   nw_ward.addLayerToMap();
//            wc_ward.addLayerToMap();


//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * Set initState of map
     */
    public void initMapSettings() throws SecurityException {
        Log.v(TAG, "initMapSettings");
        //mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException se) {
            se.printStackTrace();
        }
    }


    public void initGeoFenceSettings(GeoFenceService fenceService) {
        if (fenceService.isGeofencesAdded()) {
            fenceService.drawGeoFences();
        }
        //if (mPolyFenceService.isGeofencesAdded()) {
        mPolyFenceService.drawGeoFences();
        //}
    }


    @Override
    public void onMapLongClick(LatLng point) {
        //  mGeoFenceService.drawUserGeoFence(point);
        openContextMenu(mMainView);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        handleMenuCreation(menu, v, info);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return handleMenuActions(item);
    }


    private void handleMenuCreation(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        Log.v(TAG, "handleMenuCreation");

        super.onCreateContextMenu(menu, v, info);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_context_menu, menu);
        this.mMapContextMenu = menu;
        //ini state

        if (this.alarmTriggered) {
            this.mMapContextMenu.setGroupVisible(R.id.group_start_panic_alarm, false);
            this.mMapContextMenu.setGroupVisible(R.id.group_stop_panic_alarm, true);
        } else {
            this.mMapContextMenu.setGroupVisible(R.id.group_start_panic_alarm, true);
            this.mMapContextMenu.setGroupVisible(R.id.group_stop_panic_alarm, false);
        }

        if (this.recordingRoute) {
            this.mMapContextMenu.setGroupVisible(R.id.group_start_route_options, false);
            this.mMapContextMenu.setGroupVisible(R.id.group_stop_route_options, true);
        } else {
            this.mMapContextMenu.setGroupVisible(R.id.group_start_route_options, true);
            this.mMapContextMenu.setGroupVisible(R.id.group_stop_route_options, false);
        }


        MenuItem mItem;
        for (int a = 0; a <= this.mMapContextMenu.size(); a++) {
            mItem = this.mMapContextMenu.getItem(a);

            if (this.mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                if (mItem.getTitle().equals("Normal Map")) {
                    mItem.setChecked(true);
                    break;
                }
            } else if (this.mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
                if (mItem.getTitle().equals("Satellite Map")) {
                    mItem.setChecked(true);
                    break;
                }
            } else if (this.mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
                if (mItem.getTitle().equals("Hybrid Map")) {
                    mItem.setChecked(true);
                    break;
                }
            } else if (this.mMap.getMapType() == GoogleMap.MAP_TYPE_TERRAIN) {
                if (mItem.getTitle().equals("Terrain Map")) {
                    mItem.setChecked(true);
                    break;
                }
            }
        }

        MenuItem mBItem;
        for (int a = 0; a <= this.mMapContextMenu.size() - 1; a++) {
            mBItem = this.mMapContextMenu.getItem(a);

            Log.v(TAG, "mBItem" + mBItem.getTitle());

            if (mBItem.getItemId() == (R.id.show_municipailty_item_button)) {
                Log.v(TAG, "show_municipailty_item_button");
                if (this.showMunicipality) {
                    Log.v(TAG, "showMunicipality == true is checked");
                    mBItem.setChecked(true);
                } else {
                    Log.v(TAG, "showMunicipality == fasle not checked");
                }
            }

            if (mBItem.getItemId() == (R.id.show_ward_item_button)) {
                Log.v(TAG, "show_ward_item_button");
                if (this.showWard) {
                    Log.v(TAG, "showWard == true is checked");
                    mBItem.setChecked(true);
                } else {
                    Log.v(TAG, "showWard == false not checked");
                    mBItem.setChecked(false);
                }
            }
        }

    }


    private boolean handleMenuActions(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.start_panic_alarm_button:
                this.alarmTriggered = true;
                this.updateLocationEnabled = true;
                NotificationUtils.makeToast(this, "Panic Alarm Started", 0);
                return true;


            case R.id.stop_panic_alarm_button:
                this.updateLocationEnabled = false;
                this.alarmTriggered = false;
                NotificationUtils.makeToast(this, "Panic Alarm Cancelled", 0);
                return true;

            case R.id.report_item_button:
                NotificationUtils.makeToast(this, "Reporting Incident", 0);
                startActivity(new Intent(this, ReportIncidentActivity.class));
                return true;

            case R.id.recordpatrol_item_button:
                this.startRecordingRoute();
                NotificationUtils.makeToast(this, "Recording Route ..", 0);
                return true;

            case R.id.stoprecordpatrol_item_button:
                this.stopRecordingRoute();
                NotificationUtils.makeToast(this, "Stopped Route Recording..", 0);
                return true;

            case R.id.map_view_item_button:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;

            case R.id.satellite_item_button:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;

            case R.id.hybrid_item_button:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;

            case R.id.terrain_item_button:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;

            case R.id.start_scanner_button:
                NotificationUtils.makeToast(this, "Starting Camera for Scanner", 0);
                startActivity(new Intent(this, ScannerActivity.class));
                return true;

            case R.id.print_db_data_button:
                startActivity(new Intent(this, DBDataActivity.class));
                return true;

            case R.id.show_municipailty_item_button:
                return toggleMunicipailtyLayer(item.isChecked());

            case R.id.show_ward_item_button:
                return toggleWardLayer(item.isChecked());

            default:
                return super.onContextItemSelected(item);
        }
    }

    private boolean toggleWardLayer(boolean checked) {
        if (checked) {
            removeWardKMLLayers();
        } else {
            this.showWard = true;
            requestMapIT_KML();
        }
        return true;
    }

    private boolean toggleMunicipailtyLayer(boolean checked) {
        if (checked) {
            removeMunicipalityKMLLayers();
        } else {
            this.showMunicipality = true;
            requestMapIT_KML();
        }
        return true;
    }

    private void requestMapIT_KML() {
        try {
            Log.v(TAG, "requestMapIT_KML");
            MapIT_KMLServiceTask getKml = new MapIT_KMLServiceTask(instance, mContext, mLocationRequestor.getLastLocation());
            URL url = new URL(MAPIT_URL);
            getKml.execute(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isMunicipalityShown() {
        return this.showMunicipality;
    }

    public boolean isWardShown() {
        return this.showWard;
    }

    public void startRecordingRoute() {
        Log.v(TAG, "startRecordingRoute ");
        this.recordingRoute = true;
        this.tmpRouteData = new ArrayList();
        this.tmpRouteName = "route [" + new Date().toString() + "]";
        if (mLocationRequestor.getLastLocation() != null) {
            tmpRouteData.add(mLocationRequestor.getLastLocation());
        }

    }

    public void stopRecordingRoute() {
        Log.v(TAG, "stopRecordingRoute " + this.tmpRouteData.size());
        try {
            //Method callback = this.getClass().getMethod("drawSavedRoute", ArrayList.class);
            Route tmpR = entityManager.saveRoute(this.tmpRouteName, this.tmpRouteData);
            if (tmpR != null) {
                drawSavedRoute(tmpR, this.tmpRouteData);
            }

            this.recordingRoute = false;
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    public void drawSavedRoute(Route r, ArrayList<Location> routePoints) {
        Log.v(TAG, "drawSavedRoute " + routePoints.size());

        Marker mRouteStart = getMainMap().addMarker(new MarkerOptions()
                .position(
                        new LatLng(routePoints.get(0).getLatitude(), routePoints.get(0).getLongitude())
                )
                .title(ROUTE_START + " - " + r.getId())
                .snippet("Captured :  " + new Date(routePoints.get(0).getTime())));

        Marker mRouteEnd = getMainMap().addMarker(new MarkerOptions()
                .position(
                        new LatLng(routePoints.get(routePoints.size() - 1).getLatitude(), routePoints.get(routePoints.size() - 1).getLongitude())
                )
                .title(ROUTE_END + " - " + r.getId())
                .snippet("Captured :  " + new Date(routePoints.get(routePoints.size() - 1).getTime())));


        PolylineOptions routeLineOptions = new PolylineOptions().geodesic(true);
        routeLineOptions.width(20f);
        routeLineOptions.color(Color.GREEN);
        routeLineOptions.clickable(true);


        for (Location l : routePoints) {
            if (l != null) {
                routeLineOptions.add(new LatLng(l.getLatitude(), l.getLongitude()));
                Log.v(TAG, "l.getLatitude() " + l.getLatitude());
                Log.v(TAG, "l.getLongitude() " + l.getLongitude());
            }
        }

        Polyline routePolyline = mMap.addPolyline(routeLineOptions);
        routePolyline.setVisible(true);

    }

    public boolean isTrackPositionEnabled() {
        return this.trackPositionEnabled;
    }

    public boolean isUpdateLocationEnabled() {
        return this.updateLocationEnabled;
    }

    public boolean isRecordingRoute() {
        return this.recordingRoute;
    }


    public GoogleMap getMainMap() {
        return mMap;
    }

    public SharedPreferences getSharedPrefs() {
        return this.mSharedPreferences;
    }


    private void updateWidget(Location l) {
        try {
            if (l != null) {

                Context context = this;
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.map_app_widget);
                ComponentName mapWidget = new ComponentName(context, MapAppWidget.class);
                remoteViews.setTextViewText(R.id.appwidget_position, "Lat/Lng : " + l.getLatitude() + "," + l.getLongitude() + " ");
                remoteViews.setTextViewText(R.id.appwidget_speed, "Speed : " + +l.getSpeed() + " m/s");
                remoteViews.setTextViewText(R.id.appwidget_time, "FG Captured : " + new Date(l.getTime()).toString() + " ");

                appWidgetManager.updateAppWidget(mapWidget, remoteViews);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateLocation(Location loc) {
        if (loc == null) {
            this.mLocationRequestor.requestCurrentLocation();
        } else {
            Log.v(TAG, "updateLocation : provider : " + loc.getProvider());

//lets try
            updateWidget(loc);


            LatLng latlong = new LatLng(loc.getLatitude(), loc.getLongitude());

            //add to locations table in db
            entityManager.saveLocation(loc);

            if (isRecordingRoute()) {
                //this.recordedRoute.add(latlong);  // Current Location
                this.tmpRouteData.add(loc);
            }

            if (isUpdateLocationEnabled() || isRecordingRoute()) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latlong));

                if (loc.hasBearing()) {

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latlong)             // Sets the center of the map to current location
                            .zoom(mMap.getMaxZoomLevel() - 2)                   // Sets the zoom
                            .bearing(loc.getBearing()) // Sets the orientation of the camera to east
                            .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                } else {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latlong)             // Sets the center of the map to current location
                            .zoom(mMap.getMaxZoomLevel() - 2)                   // Sets the zoom
                            .bearing(loc.getBearing()) // Sets the orientation of the camera to east
                            .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder

                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }

            //check polyfences
            mPolyFenceService.checkFenceTransitions(latlong);

        }
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://guardmonitor.gpg.za.controlroom/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://guardmonitor.gpg.za.controlroom/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}