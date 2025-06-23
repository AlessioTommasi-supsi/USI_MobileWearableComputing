package ch.usi.geolocker.adapters;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import ch.usi.geolocker.Constants;
import ch.usi.geolocker.MainActivity;
import ch.usi.geolocker.R;
import ch.usi.geolocker.data.ConverterUtil;
import ch.usi.geolocker.data.Spot;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {

    private final List<Spot> spotList;
    private final Bitmap placeHolderBitmap;

    public SpotAdapter(List<Spot> spotList, Bitmap placeHolderBitmap) {
        this.spotList = spotList;
        this.placeHolderBitmap = placeHolderBitmap;
    }

    @Override
    public int getItemViewType(int position) {
        Spot spot = spotList.get(position);
        // Differentiate between the two types of spots (unlocked, locked) since the state of the recycled items is not reset.
        // Without this, the recycled locked spots would have the images from the unlocked spots.
        return spot.getDistance() > Constants.NEAR_DISTANCE ? 0 : 1;
    }

    @NonNull
    @Override
    public SpotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spot, parent, false);

        return new SpotViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull SpotViewHolder holder, int position) {
        Spot spot = spotList.get(position);

        // Get the current location
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(holder.itemView.getContext());
        if (ActivityCompat.checkSelfPermission(holder.itemView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location currentLocation) {
                if (currentLocation != null) {
                    // Create a Location object for the Spot
                    Location spotLocation = new Location("spotLocation");
                    spotLocation.setLatitude(spot.getLatitude());
                    spotLocation.setLongitude(spot.getLongitude());

                    // Calculate the distance in meters
                    float distanceInMeters = currentLocation.distanceTo(spotLocation);

                    spot.setDistance(distanceInMeters);

                    // Set the distance in the TextView
                    String distanceString = "";
                    if (distanceInMeters > 1000) {
                        distanceString = String.format("%.3f km", distanceInMeters / 1000.0);
                    } else {
                        distanceString = Math.round(distanceInMeters) +" m";
                    }
                    holder.descriptionTextView.setText("Distance: "+ distanceString);

                    // Is the spot unlocked?
                    if (distanceInMeters > spot.getVisibilityRangeRadiusInMeters()) {
                        holder.titleTextView.setText(R.string.spot_loked_title);
                        float unlockDistance = distanceInMeters - spot.getVisibilityRangeRadiusInMeters();
                        String unlockDistanceString = "";
                        if (unlockDistance > 1000) {
                            unlockDistanceString = String.format("%.3f km", unlockDistance / 1000.0);
                        } else {
                            unlockDistanceString = Math.round(unlockDistance) +" m";
                        }
                        holder.descriptionTextView.setText("Unlock distance: " + unlockDistanceString);
                        holder.statusBar.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));

                        holder.imageView.setImageBitmap(placeHolderBitmap);
                    } else {
                        holder.titleTextView.setText(" " + spot.getMessage());
                        holder.statusBar.setBackgroundColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));

                        // Decode image from Base64
                        try {
                            holder.imageView.setImageBitmap(ConverterUtil.getSmallerBitmap(spot.getImageString(), holder.imageView.getWidth(), holder.imageView.getHeight()));
                        } catch (Exception e) {
                            Log.e("NearFragment", "Error setting image for spot with id " + spot.getId() + ": " + e.getMessage());
                        }
                    }
                }
            }
        });
        // If the user clicks on a spot navigate to the MapFragment and center map on the location of the spot
        holder.itemView.setOnClickListener(v -> centerMapOnSpot(spot, (Activity) holder.itemView.getContext()));
    }

    @Override
    public int getItemCount() {
        return spotList.size();
    }

    static class SpotViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        ImageView imageView;
        View statusBar;

        public SpotViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.spot_title);
            descriptionTextView = itemView.findViewById(R.id.spot_description);
            imageView = itemView.findViewById(R.id.spot_image);
            statusBar = itemView.findViewById(R.id.status_bar);
        }
    }

    private void centerMapOnSpot(Spot spot, Activity activity) {
        ((MainActivity) activity).setMapLocation(spot.getLongitude(), spot.getLatitude());
        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.menu);
        bottomNavigationView.setSelectedItemId(R.id.map_fragment);
    }
}