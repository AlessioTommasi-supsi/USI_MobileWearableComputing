package com.example.stepappv4.ui.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.stepappv4.R;
import com.example.stepappv4.StepAppOpenHelper;
import com.example.stepappv4.databinding.FragmentProfileBinding;
import com.example.stepappv4.databinding.FragmentReportBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ProfileFragment extends Fragment {


    public int todaySteps = 0;
    TextView numStepsTextView;
    AnyChartView anyChartView;

    Date cDate = new Date();
    String current_time = new SimpleDateFormat("yyyy-MM-dd").format(cDate);

    public Map<Integer, Integer> stepsByHour = null;

    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create column chart
        anyChartView = root.findViewById(R.id.hourBarChart);
        anyChartView.setProgressBar(root.findViewById(R.id.loadingBar));

        Cartesian cartesian = createColumnChart();
        anyChartView.setBackgroundColor("#00000000");
        anyChartView.setChart(cartesian);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public Cartesian createColumnChart(){
        //***** Read data from SQLiteDatabase *********/
        // TODO 1 (YOUR TURN): Get the map with hours and number of steps for today
        //  from the database and assign it to variable stepsByHour
        stepsByHour = StepAppOpenHelper.loadStepsByDay(getContext(), current_time);

        // TODO 2 (YOUR TURN): Creating a new map that contains hours of the day from 0 to 23 and
        //  number of steps during each hour set to 0
        Map<Integer, Integer> graph_map = new TreeMap<>();
        for (int day = 1; day < 31; day++) {
            graph_map.put(day, 0);
        }


        // TODO 3 (YOUR TURN): Replace the number of steps for each hour in graph_map
        //  with the number of steps read from the database

        graph_map.putAll(stepsByHour);
        /*Equivalente:
        for (Map.Entry<Integer, Integer> entry : stepsByHour.entrySet()) {
            graph_map.put(entry.getKey(), entry.getValue());
        }
         */


        //***** Create column chart using AnyChart library *********/
        // TODO 4: Create and get the cartesian coordinate system for column chart
        Cartesian cartesian = AnyChart.column();

        // TODO 5: Create data entries for x and y axis of the graph
        List<DataEntry> data = new ArrayList<>();

        for (Map.Entry<Integer,Integer> entry : graph_map.entrySet())
            data.add(new ValueDataEntry(entry.getKey(), entry.getValue()));

        // TODO 6: Add the data to column chart and get the columns
        Column column = cartesian.column(data);

        //***** Modify the UI of the chart *********/
        // TODO 7 (YOUR TURN): Change the color of column chart and its border
        int backgroundColor = ContextCompat.getColor(getContext(), R.color.md_theme_dark_primary);
        String primaryColor = String.format("#%06X", (0xFFFFFF & backgroundColor));

        int Hex_secondaryColor = ContextCompat.getColor(getContext(), R.color.md_theme_dark_secondary);
        String secondaryColor = String.format("#%06X", (0xFFFFFF & Hex_secondaryColor));

        column.color(primaryColor);
        column.stroke(secondaryColor);


        // TODO 8: Modifying properties of tooltip
        column.tooltip()
                .titleFormat("At hour: {%X}")
                .format("{%Value} Steps")
                .anchor(Anchor.RIGHT_BOTTOM);

        // TODO 9 (YOUR TURN): Modify column chart tooltip properties
        column.tooltip()
                .position(Position.RIGHT_TOP)
                .offsetX(0d)
                .offsetY(5);

        // Modifying properties of cartesian
        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
        cartesian.interactivity().hoverMode(HoverMode.BY_X);
        cartesian.yScale().minimum(0);


        // TODO 10 (YOUR TURN): Modify the UI of the cartesian
        cartesian.yAxis(0).title("Number of Steps");
        cartesian.xAxis(0).title(" Day");
        cartesian.background().fill( "#00000000");

        return cartesian;
    }
}