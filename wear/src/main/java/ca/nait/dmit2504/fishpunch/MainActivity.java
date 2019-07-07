package ca.nait.dmit2504.fishpunch;

import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends WearableActivity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);
        Button startButton = findViewById(R.id.watchStartButton);

        // Enables Always-on
        setAmbientEnabled();
    }
    public void startClick(View v){
        Intent intent = new Intent(this, WearablePunchActivity.class);
        startActivity(intent);

    }
}
