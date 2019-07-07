package ca.nait.dmit2504.fishpunch;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WearablePunchActivity extends WearableActivity {
    TextView mFeedbackTextView;
    TextView mPunchTextView;
    LinearLayout layout;

    //TEST SECTION
    double punchValue;

    private static final String TAG = "WearablePunchActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch);
        mFeedbackTextView = (TextView) findViewById(R.id.punchFeedbackTextview);
        mPunchTextView = findViewById(R.id.punchTextView);
        layout = findViewById(R.id.punch_layout);

        // Enables Always-on
        setAmbientEnabled();
    }

    public void testButtonClick(View v){
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                mPunchTextView.setText("" + (millisUntilFinished / 1000));

            }

            public void onFinish() {
                layout.setBackgroundColor(Color.GREEN);
                mPunchTextView.setTextColor(Color.BLACK);
                mPunchTextView.setText("PUNCH!");
            }
        }.start();
    }
}
