package com.example.lab1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private int stepCounter = 0;
    private boolean isActive = false;

    private TextView showCount;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        showCount = findViewById(R.id.Counter);
        startButton = findViewById(R.id.Start);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void startCount(View view) {
        Toast toast = Toast.makeText(this, R.string.counterStarted, Toast.LENGTH_LONG);
        toast.show();
        stepCounter = 0;
        showCount.setText(String.format("%d", stepCounter));
        isActive = true;
        startButton.setText(R.string.reset);
        startButton.setOnClickListener(this::resetCount);
    }

    public void countUp(View view) {
        if (isActive) {
            stepCounter++;
            showCount.setText(String.format("%d", stepCounter));
        }
    }

    public void resetCount(View view) {
        Toast toast = Toast.makeText(this, R.string.counterReset, Toast.LENGTH_LONG);
        toast.show();
        stepCounter = 0;
        showCount.setText(String.format("%d", stepCounter));
        isActive = false;
        startButton.setText(R.string.Start);
        startButton.setOnClickListener(this::startCount);
    }
}