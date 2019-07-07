package ca.nait.dmit2504.fishpunch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = findViewById(R.id.mobile_main_start_button);

    }
    public void onStartMenuClick (View v) {
        Intent intent = new Intent(this, StartActivity.class);
        Log.i(TAG, "onClick: before intent");
        startActivity(intent);
    }
}
