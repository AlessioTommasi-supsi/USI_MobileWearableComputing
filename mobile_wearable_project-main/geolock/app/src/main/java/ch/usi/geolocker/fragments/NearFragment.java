package ch.usi.geolocker.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import ch.usi.geolocker.Constants;
import ch.usi.geolocker.adapters.SpotAdapter;
import ch.usi.geolocker.data.RetrofitSingleton;
import ch.usi.geolocker.R;
import ch.usi.geolocker.data.APIService;
import ch.usi.geolocker.data.Spot;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NearFragment extends Fragment {
    private FloatingActionButton refreshButton;
    private FloatingActionButton sortButton;
    private FloatingActionButton filterButton;
    private RecyclerView recyclerView;
    private SpotAdapter spotAdapter;
    private final List<Spot> spotList = new ArrayList<>();
    private final List<Spot> shownList = new ArrayList<>();
    private boolean isAscending = true;

    private boolean isFiltering = false;
    private ImageView placeholderImage;

    private CircularProgressIndicator progressIndicator;

    public NearFragment() {
        // Required empty public constructor
    }

    public static NearFragment newInstance() {
        return new NearFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        refreshButton = view.findViewById(R.id.refresh_button);
        sortButton = view.findViewById(R.id.sort_button);
        filterButton = view.findViewById(R.id.filter_button);
        placeholderImage = view.findViewById(R.id.placeholder_image);
        progressIndicator = view.findViewById(R.id.progressIndicatorNear);

        fetchSpots(); // Initial fetch
        shownList.clear();
        shownList.addAll(spotList);
        sortSpotsByDistance(); // Initial sort

        // Placeholder image needs to be compressed to avoid lags
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // Load only metadata to calculate scaling
        BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_placeholder, options);
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;
        int scaleFactor = Math.max(originalWidth / 500, originalHeight / 500);
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.image_placeholder, options);

        spotAdapter = new SpotAdapter(shownList, bitmap);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recyclerView.setAdapter(spotAdapter);

        refreshButton.setOnClickListener(v -> fetchSpots());
        sortButton.setOnClickListener(v -> sortButtonFunction());
        filterButton.setOnClickListener(v -> filteringButtonFunction());

        return view;
    }
    // This function was adopted from generative AI.
    private void updatePlaceholderVisibility() {
        if (spotList.isEmpty()) {
            placeholderImage.setVisibility(View.VISIBLE);
        } else {
            placeholderImage.setVisibility(View.GONE);
        }
    }
    // This function was adopted from generative AI.
    private void fetchSpots() {
        progressIndicator.setVisibility(View.VISIBLE);
        refreshButton.setEnabled(false);

        APIService apiService = RetrofitSingleton.getRetrofitInstance().create(APIService.class);
        Call<List<Spot>> spotsCall = apiService.getSpots();
        spotsCall.enqueue(new Callback<List<Spot>>() {
            @Override
            public void onResponse(Call<List<Spot>> call, Response<List<Spot>> response) {
                spotList.clear();
                if (response.body() != null) {
                    spotList.addAll(response.body());
                    Date now = new Date();
                    spotList.removeIf(spot -> spot.getExpirationDateTime().before(now));
                    calculateDistances();
                }
                spotAdapter.notifyDataSetChanged();
                updatePlaceholderVisibility();

                progressIndicator.setVisibility(View.GONE);
                refreshButton.setEnabled(true);
            }

            @Override
            public void onFailure(Call<List<Spot>> call, Throwable throwable) {
                Log.e("API", "Error: " + throwable.getMessage());
                updatePlaceholderVisibility();

                progressIndicator.setVisibility(View.GONE);
                refreshButton.setEnabled(true);
            }
        });
    }
    // This function was adopted from generative AI.
    @SuppressLint("MissingPermission")
    private void calculateDistances() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location currentLocation) {
                if (currentLocation != null) {
                    for (Spot spot : spotList) {
                        Location spotLocation = new Location("spotLocation");
                        spotLocation.setLatitude(spot.getLatitude());
                        spotLocation.setLongitude(spot.getLongitude());
                        float distanceInMeters = currentLocation.distanceTo(spotLocation);
                        spot.setDistance(distanceInMeters);
                    }
                    sortSpotsByDistance(); // Sort after setting distances
                }
            }
        });
    }
    // This function was adopted from generative AI.
    private void sortButtonFunction(){
        sortSpotsByDistance();
        isAscending = !isAscending;
    }

    // This function was adopted from generative AI.
    private void sortSpotsByDistance() {
        if (isAscending) {
            Collections.sort(spotList, new Comparator<Spot>() {
                @Override
                public int compare(Spot s1, Spot s2) {
                    return Float.compare(s1.getDistance(), s2.getDistance());
                }
            });
        } else {
            Collections.sort(spotList, new Comparator<Spot>() {
                @Override
                public int compare(Spot s1, Spot s2) {
                    return Float.compare(s2.getDistance(), s1.getDistance());
                }
            });
        }
        sortButton.setImageResource(!isAscending ? R.drawable.arrow_up : R.drawable.arrow_down);
        shownList.clear();
        shownList.addAll(spotList);
        filterSpots();

        if (spotAdapter != null)
            spotAdapter.notifyDataSetChanged();
    }

    // This function was adopted from generative AI.
    private void filteringButtonFunction(){
        isFiltering = !isFiltering;
        filterSpots();
        filterButton.setImageResource(isFiltering ? R.drawable.ic_filter_remove : R.drawable.ic_filter_add);
    }

    // This function was adopted from generative AI.
    private void filterSpots() {
        if (isFiltering){
            shownList.clear();
            for (Spot spot : spotList) {
                if (spot.getDistance() < Constants.NEAR_DISTANCE) {
                    shownList.add(spot);
                }
            }
        } else {
            shownList.clear();
            shownList.addAll(spotList);
        }
        if (spotAdapter != null) {
            spotAdapter.notifyDataSetChanged();
        }
    }
}