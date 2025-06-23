package ch.usi.geolocker.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.caverock.androidsvg.BuildConfig;
import ch.usi.geolocker.R;
import ch.usi.geolocker.data.APIService;
import ch.usi.geolocker.data.ConverterUtil;
import ch.usi.geolocker.data.RetrofitSingleton;
import ch.usi.geolocker.data.Spot;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MapFragment extends Fragment {
    private MapView mapView;
    private FloatingActionButton refreshButton;
    private FloatingActionButton centreToUserPositionButton;
    private CircularProgressIndicator progressIndicator;
    private Location userLocation;
    private Map<Marker, Spot> markerSpotMap;

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

    private double getDistanceInMetersFromUserToSpot(Spot spot) {
        float[] distance =new float[1];
        Location.distanceBetween(
                userLocation.getLatitude(),
                userLocation.getLongitude(),
                spot.getLatitude(),
                spot.getLongitude(),
                distance);
        return distance[0];
    }

    private void setMarkerColorAndInfoWindow(Marker marker, Spot spot) {
        // Set the info window and the color of the marker based on whether the spot is unlocked or locked
        if (userLocation != null && getDistanceInMetersFromUserToSpot(spot) <= spot.getVisibilityRangeRadiusInMeters()) {
            marker.setIcon(ContextCompat.getDrawable(getContext(), org.osmdroid.library.R.drawable.marker_default));
            CustomInfoWindow window = new CustomInfoWindow(R.layout.info_window, mapView, getParentFragmentManager());
            marker.setInfoWindow(window);
        } else {
            marker.setIcon(ContextCompat.getDrawable(getContext(), R.drawable.marker_default_red));
            marker.setInfoWindow(null);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = getActivity().getApplicationContext();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        APIService apiService = RetrofitSingleton.getRetrofitInstance().create(APIService.class);
        this.progressIndicator = getActivity().findViewById(R.id.progressIndicatorMap);

        // Add user's current location to the map
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView) {
            @Override
            public void onLocationChanged(Location location, IMyLocationProvider source) {
                super.onLocationChanged(location, source);

                // Update user's location in the fragment
                userLocation = location;

                if (markerSpotMap != null) {
                    for (Map.Entry<Marker, Spot> entry : markerSpotMap.entrySet()) {
                        Marker marker = entry.getKey();
                        Spot spot = entry.getValue();
                        setMarkerColorAndInfoWindow(marker, spot);
                    }
                }
            }
        };

        mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(mLocationOverlay);

        this.refreshButton = getActivity().findViewById(R.id.map_refresh_button);
        this.refreshButton.setOnClickListener((View v) -> {
            progressIndicator.setVisibility(View.VISIBLE);
            refreshButton.setEnabled(false);

            Call<List<Spot>> spotsCall = apiService.getSpots();
            spotsCall.enqueue(new Callback<List<Spot>>() {
                @Override
                public void onResponse(Call<List<Spot>> call, Response<List<Spot>> response) {
                    Log.d("API", "Response received" + response.body() + "END body  CODE: " + response.code());

                    // Remove all existing markers and circles except the GPS overlay from the map
                    // to avoid displaying the same elements twice after the refresh
                    for (int i = mapView.getOverlays().size() - 1; i >= 0; i--) {
                        if (mapView.getOverlays().get(i) instanceof Marker || mapView.getOverlays().get(i) instanceof Polygon) {
                            mapView.getOverlays().remove(i);
                        }
                    }

                    List<Spot> spots = response.body(); // Store in fragment

                    Date now = new Date();
                    markerSpotMap = new HashMap<>();

                    try {
                        // Remove expired spots since they are not displayed on the map
                        spots.removeIf(spot -> spot.getExpirationDateTime().before(now));
                        for (Spot spot : spots) {
                            Log.d("CURRENT LOCATION"," spot Longitude: " + spot.getLongitude() + ",spot Latitude: " + spot.getLatitude());
                            Marker marker = new Marker(mapView);
                            marker.setPosition(new GeoPoint(spot.getLatitude(), spot.getLongitude()));
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                            marker.setTitle("Spot " + spot.getId());
                            marker.setSubDescription(spot.getMessage());
                            Bitmap image = ConverterUtil.loadImageFromBase64(spot.getImageString());
                            marker.setImage(new BitmapDrawable(getResources(), image));
                            setMarkerColorAndInfoWindow(marker, spot);

                            markerSpotMap.put(marker, spot); // Add to HashMap in fragment

                            // Draw circle around the marker
                            Polygon circlePolygon = new Polygon(mapView);

                            // Allow touch events to pass through
                            // Otherwise, the polygon blocks the clicking of the markers
                            circlePolygon.setOnClickListener((polygon, mapView, eventPos) -> false);
                            circlePolygon.setInfoWindow(null);  // Otherwise you get a default info window when clicking in the area of the circle
                            Paint paint = circlePolygon.getOutlinePaint();
                            paint.setColor(ContextCompat.getColor(context, R.color.md_theme_primary));
                            paint.setStrokeWidth(5);
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setPathEffect(new DashPathEffect(new float[]{20, 20}, 0)); // Creates dashes

                            ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>();
                            for (float f = 0; f < 360; f += 1){
                                circlePoints.add(new GeoPoint(spot.getLatitude() ,spot.getLongitude()).destinationPoint(spot.getVisibilityRangeRadiusInMeters(), f));
                            }
                            circlePolygon.setPoints(circlePoints);
                            mapView.getOverlays().add(circlePolygon);
                            mapView.getOverlays().add(marker);
                        }
                    } catch (Exception e) {
                        Log.e("MapFragment", "Error while adding spots to the map: " + e.getMessage());
                    }
                    progressIndicator.setVisibility(View.INVISIBLE);
                    refreshButton.setEnabled(true);
                }

                @Override
                public void onFailure(Call<List<Spot>> call, Throwable throwable) {
                    try {
                        Log.e("API", "Error: " + throwable.getMessage());
                    } catch (Throwable e) {
                        Log.e("API", "Error: " + e.getMessage());
                    }
                    progressIndicator.setVisibility(View.INVISIBLE);
                    refreshButton.setEnabled(true);
                }
            });
        });
        this.refreshButton.callOnClick();

        this.centreToUserPositionButton = getActivity().findViewById(R.id.map_centre_user_position);
        this.centreToUserPositionButton.setOnClickListener(v -> mapView.getController().setCenter(mLocationOverlay.getMyLocation()));

        // Initialize the map view
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setZoomLevel(17);
        mapView.setHorizontalMapRepetitionEnabled(false);
        mapView.setVerticalMapRepetitionEnabled(false);
        mapView.getZoomController().setVisibility(CustomZoomButtonsController. Visibility.NEVER);

        // Set initial location of the map
        GeoPoint startPoint;
        if (getArguments() != null) {  // Should center on spot received from NearFragment?
            double latitude = getArguments().getDouble("latitude");
            double longitude = getArguments().getDouble("longitude");
            startPoint = new GeoPoint(latitude, longitude);
        } else {
            startPoint = new GeoPoint(46.003677, 8.951052); // Lugano
            // Problem: Sometimes the first GPS location provided in the emulator is a wrong location (Google Headquarters in California)
            // Then the app centres on the wrong initial location.
            // However, it works on physical devices.
            mLocationOverlay.runOnFirstFix(() -> {
                GeoPoint updatedPoint = new GeoPoint(userLocation.getLatitude(), userLocation.getLongitude());
                getActivity().runOnUiThread(() -> mapView.getController().setCenter(updatedPoint));
            });
        }
        // Since it takes some time to receive the user's location we initially center on Lugano.
        mapView.getController().setCenter(startPoint);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        this.mapView = view.findViewById(R.id.map);

        return view;
    }
}