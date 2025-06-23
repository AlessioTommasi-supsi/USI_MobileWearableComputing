package com.example.pocopenstreetmap.fragments;

import android.content.Context;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.caverock.androidsvg.BuildConfig;
import com.example.pocopenstreetmap.MainActivity;
import com.example.pocopenstreetmap.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment {
    private MapView mapView;

    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getActivity().getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // Initialize the map view

        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setZoomLevel(15);
        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController. Visibility.NEVER);

        // Add user's current location to the map
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView) {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);

                // Here we can use the user's new location
                Log.d("CURRENT LOCATION","Longitude: " + location.getLongitude() + ", Latitude: " + location.getLatitude());
            }
        };

        mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(mLocationOverlay);

        // Set a location for the marker
        GeoPoint startPoint = new GeoPoint(46.003677, 8.951052); // Lugano
        mapView.getController().setCenter(startPoint);

        // Add a marker to the map
        Marker marker = new Marker(mapView);
        marker.setPosition(startPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle("Some Title");
        marker.setSubDescription("Some Description");
        marker.setImage(getResources().getDrawable(R.drawable.usi_logo));
        MarkerInfoWindow window = new MarkerInfoWindow(R.layout.info_window, mapView);
        marker.setInfoWindow(window);

        // Draw circle around the marker
        Polygon oPolygon = new Polygon(mapView);
        oPolygon.setInfoWindow(null);  // Otherwise you get a default info window when clicking in the area of the circle
        Paint paint = oPolygon.getOutlinePaint();
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE); // Make sure it's set to stroke, not fill
        paint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0)); // Creates dashes (10 units on, 20 units off)

        final double radius = 300;
        ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>();
        for (float f = 0; f < 360; f += 1){
            circlePoints.add(new GeoPoint(startPoint.getLatitude() , startPoint.getLongitude()).destinationPoint(radius, f));
        }
        oPolygon.setPoints(circlePoints);
        mapView.getOverlays().add(oPolygon);

        // Set click listeners for the marker
        marker.setOnMarkerClickListener((m, mapView) -> {
            m.showInfoWindow();
            Toast.makeText(getActivity(), "Marker Clicked", Toast.LENGTH_SHORT).show();
            return true;
        });

        mapView.getOverlays().add(marker);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        this.mapView = view.findViewById(R.id.map);

        return view;
    }
}