package ch.usi.geolocker.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import ch.usi.geolocker.data.RetrofitSingleton;
import ch.usi.geolocker.R;
import ch.usi.geolocker.data.APIService;
import ch.usi.geolocker.data.ConverterUtil;
import ch.usi.geolocker.data.Spot;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddFragment extends Fragment {
    private final int CAMERA_REQUEST_CODE = 1;
    private Uri photoUri;
    private File photoFile;

    private ImageView imageView;
    private Button imageSelectButton;
    private TextInputEditText messageEditText;
    private Slider visibilityRangeSlider;
    private TextInputEditText dateEditText;
    private Calendar expirationDateCalendar;
    private Button sendButton;
    private CircularProgressIndicator progressIndicator;

    private boolean imageWasChanged = false;

    private Double lastKnownLongitude;
    private Double lastKnownLatitude;

    private APIService apiService;
    private FusedLocationProviderClient fusedLocationClient;

    public AddFragment() {
        // Required empty public constructor
    }

    public static AddFragment newInstance() {
        return new AddFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        apiService = RetrofitSingleton.getRetrofitInstance().create(APIService.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "App has no permission to access location", Snackbar.LENGTH_LONG).show();
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), location -> {
                if (location != null) {
                    lastKnownLatitude = location.getLatitude();
                    lastKnownLongitude = location.getLongitude();
                }
            });
        }

        this.progressIndicator = view.findViewById(R.id.progressIndicatorAdd);

        this.messageEditText = view.findViewById(R.id.messageTextInputEditText);

        this.visibilityRangeSlider = view.findViewById(R.id.rangeSlider);
        visibilityRangeSlider.setLabelFormatter(value -> {
            double roundedValue = Math.round(value * 4) / 4.0;
            // Integers should not be displayed with a trailing zero
            return (roundedValue % 1 == 0)
                    ? ((int) roundedValue + " km")
                    : (roundedValue + " km");
        });

        dateEditText = view.findViewById(R.id.dateEditText);
        dateEditText.setOnClickListener(v -> showDatePickerDialog());

        // Unlike the message view the date view does not automatically remove the error symbol
        // after the user selects a date. Therefore, the error is removed manually.
        dateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    dateEditText.setError(null);  // Clear the error when the user enters text
                }
            }
        });

        imageView = view.findViewById(R.id.imageViewPreview);

        imageSelectButton = view.findViewById(R.id.imageSelectButton);
        imageSelectButton.setOnClickListener(v -> openCamera());

        sendButton = view.findViewById(R.id.submitSpotButton);
        sendButton.setOnClickListener(v -> sendSpot());
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }

    // This method was implemented with the help of generative AI.
    private Bitmap correctImageOrientation(Bitmap bitmap, String imagePath) {
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                    matrix.postScale(-1, 1);
                    break;
                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                    matrix.postScale(1, -1);
                    break;
            }
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
            return bitmap;
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(getActivity(), "ch.usi.geolock.fileprovider", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }
            } catch (IOException ex) {
                Snackbar.make(getActivity().findViewById(android.R.id.content),
                        "Error creating file for photo", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "No camera app found", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Load the image from the file
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            // For some reason the returned image can be rotated wrongly
            // Thus, we need to undo the rotation
            bitmap = correctImageOrientation(bitmap, photoFile.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
            imageWasChanged = true;
        }
    }

    private void showDatePickerDialog() {
        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Expiration Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(
                        new CalendarConstraints.Builder()
                                .setValidator(DateValidatorPointForward.from(MaterialDatePicker.todayInUtcMilliseconds())) // Only allow today and future dates
                                .build()
                )
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
            expirationDateCalendar = Calendar.getInstance();
            expirationDateCalendar.setTimeInMillis(selection);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String formattedDate = dateFormat.format(expirationDateCalendar.getTime());

            dateEditText.setText(formattedDate);
        });
        materialDatePicker.show(getActivity().getSupportFragmentManager(), materialDatePicker.toString());
    }

    private void sendSpot() {
        boolean hasError = false;

        String date = dateEditText.getText().toString().trim();
        if (date.isEmpty()) {
            dateEditText.setError("Expiration Date is required");
            hasError = true;
        }
        String message = messageEditText.getText().toString().trim();
        if (message.isEmpty()) {
            messageEditText.setError("Message is required");
            hasError = true;
        }
        if (!imageWasChanged) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Please take an image", Snackbar.LENGTH_LONG).show();
            hasError = true;
        }
        if (lastKnownLatitude == null || lastKnownLongitude == null) {
            Snackbar.make(getActivity().findViewById(android.R.id.content), "Can not find your location", Snackbar.LENGTH_LONG).show();
            hasError = true;
        }
        if (!hasError) {
            // Send, put message in snackbar after receiving response, reset everything
            Spot spot = new Spot();
            spot.setMessage(messageEditText.getText().toString());
            spot.setVisibilityRangeRadiusInMeters(Math.round(visibilityRangeSlider.getValue()) * 1000);

            Bitmap bitmap = null;
            if (imageView.getDrawable() instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Invalid image selected", Snackbar.LENGTH_LONG).show();
            }
            spot.setImageString(ConverterUtil.bitmapToBase64(bitmap));

            // Set expiration time to the end of the day
            expirationDateCalendar.set(Calendar.HOUR_OF_DAY, 23);
            expirationDateCalendar.set(Calendar.MINUTE, 59);
            expirationDateCalendar.set(Calendar.SECOND, 59);
            expirationDateCalendar.set(Calendar.MILLISECOND, 999);
            spot.setExpirationDateTime(expirationDateCalendar.getTime());

            spot.setLongitude(lastKnownLongitude);
            spot.setLatitude(lastKnownLatitude);

            progressIndicator.setVisibility(View.VISIBLE);
            sendButton.setEnabled(false);

            Call<Spot> spotCall = apiService.addSpot(spot);
            spotCall.enqueue(new Callback<Spot>() {
                @Override
                public void onResponse(Call<Spot> call, Response<Spot> response) {
                    progressIndicator.setVisibility(View.INVISIBLE);
                    sendButton.setEnabled(true);

                    Snackbar.make(getActivity().findViewById(android.R.id.content), "Spot created", Snackbar.LENGTH_LONG).show();

                    // Reset the form
                    imageView.setImageResource(R.drawable.image_placeholder);
                    messageEditText.setText(null);
                    int middle = Math.round((visibilityRangeSlider.getValueFrom() + visibilityRangeSlider.getValueTo())/2);
                    visibilityRangeSlider.setValue(middle);
                    dateEditText.setText(null);
                }

                @Override
                public void onFailure(Call<Spot> call, Throwable throwable) {
                    progressIndicator.setVisibility(View.INVISIBLE);
                    sendButton.setEnabled(true);
                    try {
                        throw throwable;
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        }
    }
}