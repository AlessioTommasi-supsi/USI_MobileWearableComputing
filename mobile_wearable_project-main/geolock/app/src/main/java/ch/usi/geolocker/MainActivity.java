package ch.usi.geolocker;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import ch.usi.geolocker.fragments.MapFragment;
import ch.usi.geolocker.fragments.AddFragment;
import ch.usi.geolocker.fragments.NearFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ACTIVITY_RECOGNITION_PERMISSION = 45;
    BottomNavigationView bottomNavigationView;
    Fragment mapFragment;
    Fragment addFragment;
    Fragment nearFragment;

    // This method is required for centering the map in the MapFragment when clicking
    // on a spot in the NearFragment.
    public void setMapLocation(double longitude, double latitude) {
        Bundle bundle = new Bundle();
        bundle.putDouble("longitude", longitude);
        bundle.putDouble("latitude", latitude);
        this.mapFragment.setArguments(bundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getActivityPermission();

        // Setup navigation bar
        this.mapFragment = new MapFragment();
        this.addFragment = new AddFragment();
        this.nearFragment = new NearFragment();

        bottomNavigationView = findViewById(R.id.menu);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.map_fragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_placeholder, mapFragment, "MapFragment")
                        .commit();
                return true;
            } else if (id == R.id.add_fragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_placeholder, addFragment)
                        .commit();
                return true;
            } else if (id == R.id.near_fragment) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_placeholder, nearFragment)
                        .commit();
                return true;
            }
            return false;
        });

        // Note: this needs to be after the definition of the selection listener
        bottomNavigationView.setSelectedItemId(R.id.map_fragment);
    }

    // Ask for necessary permissions
    private void getActivityPermission() {
        List<String> permissionsToRequest = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA);
        }
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        Object[] array = permissionsToRequest.toArray();
        ActivityCompat.requestPermissions(this, Arrays.copyOf(array, array.length, String[].class), REQUEST_ACTIVITY_RECOGNITION_PERMISSION);
    }
}