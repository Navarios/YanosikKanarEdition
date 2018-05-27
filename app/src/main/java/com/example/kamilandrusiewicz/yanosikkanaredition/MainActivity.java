package com.example.kamilandrusiewicz.yanosikkanaredition;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToMapsActivity(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    public void goToPlanActivity(View view) {
        Intent intent = new Intent(this, PlanActivity.class);
        startActivity(intent);
    }
    public void goToTimetableActivity(View view) {
        Intent intent = new Intent(this, TimetableActivity.class);
        startActivity(intent);
    }
}
