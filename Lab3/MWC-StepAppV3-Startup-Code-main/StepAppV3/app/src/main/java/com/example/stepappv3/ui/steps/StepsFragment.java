package com.example.stepappv3.ui.steps;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.stepappv3.R;
import com.example.stepappv3.databinding.FragmentStepsBinding;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.example.stepappv3.StepAppOpenHelper;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
public class StepsFragment extends Fragment {

    private FragmentStepsBinding binding;
    private MaterialButtonToggleGroup materialButtonToggleGroup;
    private TextView stepsTextView;
    private int stepsCounter = 0;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StepsViewModel homeViewModel =
                new ViewModelProvider(this).get(StepsViewModel.class);

        binding = FragmentStepsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        CircularProgressIndicator progressBar = (CircularProgressIndicator)  root.findViewById(R.id.progressBar);
        progressBar.setMax(6000);
        progressBar.setProgress(stepsCounter);

        stepsTextView = (TextView) root.findViewById(R.id.stepsCount_textview);
        stepsTextView.setText(""+stepsCounter);

        // Toggle group button
        materialButtonToggleGroup = (MaterialButtonToggleGroup) root.findViewById(R.id.toggleButtonGroup);
        materialButtonToggleGroup.addOnButtonCheckedListener(new MaterialButtonToggleGroup.OnButtonCheckedListener() {
            @Override
            public void onButtonChecked(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {

                if (checkedId == R.id.toggleStart_btn) {

                    //Place code related to Start button
                    Toast.makeText(getContext(), R.string.start_count, Toast.LENGTH_SHORT).show();
                    progressBar.setProgress(0);
                    stepsTextView.setText("0");


                }
                else if (checkedId == R.id.toggleCount_btn) {
                    stepsCounter ++;
                    progressBar.setProgress(stepsCounter);
                    stepsTextView.setText(""+stepsCounter);

                    //Timestamp
                    long timeInMillis = System.currentTimeMillis();
                    // Convert the timestamp to date
                    SimpleDateFormat jdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    jdf.setTimeZone(TimeZone.getTimeZone("GMT+2"));
                    final String dateTimestamp = jdf.format(timeInMillis);
                    String currentDay = dateTimestamp.substring(8,10);
                    String hour = dateTimestamp.substring(11,13);



                    // TODO 6.1: Instantiate our StepAppOpenHelper class
                    StepAppOpenHelper databaseOpenHelper =   new StepAppOpenHelper(getContext());

                    // TODO 6.2: Get a writeable database
                    SQLiteDatabase database = databaseOpenHelper.getWritableDatabase();

                    // TODO 7: Put new step event timestamp in ContentValues
                    ContentValues databaseEntry = new ContentValues();
                    databaseEntry.put(StepAppOpenHelper.KEY_TIMESTAMP, dateTimestamp);
                    databaseEntry.put(StepAppOpenHelper.KEY_DAY, currentDay);
                    databaseEntry.put(StepAppOpenHelper.KEY_HOUR, hour);

                    // TODO 8: Add new record in database
                    long id = database.insert(StepAppOpenHelper.TABLE_NAME, null, databaseEntry);

                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}