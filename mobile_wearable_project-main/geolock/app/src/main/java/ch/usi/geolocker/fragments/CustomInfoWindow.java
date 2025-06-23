package ch.usi.geolocker.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;

import ch.usi.geolocker.R;

public class CustomInfoWindow extends MarkerInfoWindow {
    private final FragmentManager fragmentManager;

    // Constructor that takes the FragmentManager for dialog handling
    public CustomInfoWindow(int layoutResId, MapView mapView, FragmentManager fragmentManager) {
        super(layoutResId, mapView);
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onOpen(Object item) {
        Marker marker = (Marker) item;
        View view = mView;

        // Set title and description in the info window
        TextView title = view.findViewById(R.id.bubble_title);
        title.setText(marker.getTitle());

        TextView subDescription = view.findViewById(R.id.bubble_subdescription);
        subDescription.setText(marker.getSubDescription());

        // Get the image from the marker and set it in the ImageView
        ImageView imageView = view.findViewById(R.id.bubble_image);
        Bitmap imageBitmap = ((BitmapDrawable) marker.getImage()).getBitmap();
        imageView.setImageDrawable(new BitmapDrawable(view.getResources(), imageBitmap));

        // Set the click listener to show full-screen dialog
        imageView.setOnClickListener(v -> {
            FullScreenImageFragmentDialog dialog = FullScreenImageFragmentDialog.newInstance(imageBitmap);
            dialog.show(fragmentManager, "FullScreenImage");
        });
    }
}
