package ch.usi.geolocker.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.io.ByteArrayOutputStream;

import ch.usi.geolocker.R;

public class FullScreenImageFragmentDialog extends DialogFragment {
    private static final String ARG_IMAGE_BYTE_ARRAY = "image_byte_array";

    // Factory method to create a new instance
    public static FullScreenImageFragmentDialog newInstance(Bitmap image) {
        if (image == null) {
            throw new IllegalArgumentException("Bitmap cannot be null");
        }

        FullScreenImageFragmentDialog dialog = new FullScreenImageFragmentDialog();

        // Convert the Bitmap to a byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        // Add the byte array to the arguments
        Bundle args = new Bundle();
        args.putByteArray(ARG_IMAGE_BYTE_ARRAY, byteArray);
        dialog.setArguments(args);
        return dialog;
    }
    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_screen_image, container, false);

        SubsamplingScaleImageView imageView = view.findViewById(R.id.full_screen_image_view);

        // Retrieve the byte array from arguments and decode it back to Bitmap
        byte[] byteArray = getArguments().getByteArray(ARG_IMAGE_BYTE_ARRAY);
        if (byteArray != null) {
            Bitmap bitmap = android.graphics.BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            if (bitmap != null) {
                imageView.setImage(ImageSource.bitmap(bitmap));
            } else {
                Log.e("FullScreenImageDialog", "Failed to decode Bitmap from byte array");
            }
        } else {
            Log.e("FullScreenImageDialog", "Byte array is null");
        }

        // Close button functionality
        View closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }
}
