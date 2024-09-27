package com.example.lab1;

import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        showCount = findViewById(R.id.Counter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void startCount(View view) {
        Toast toast = Toast.makeText(this, R.string.counterStarted, Toast.LENGTH_LONG);
        toast.show();

        isActive = true;

    }

    public void countUp(View view) {
        if (isActive) {
            stepCounter++;
            showCount.setText(String.format("%d", stepCounter));
        }
    }
}