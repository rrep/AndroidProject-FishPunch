package ca.nait.dmit2504.fishpunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WearablePunchActivity extends WearableActivity implements SensorEventListener {
    TextView mFeedbackTextView;
    TextView mPunchTextView;
    LinearLayout layout;

    //TEST SECTION
    double punchValue;

    //SENSOR VARIABLES
    private SensorManager mSensorManager;
    Sensor accelerometer;
    double x, y;
    ArrayList<Double> xValues, yValues;

    private static final String TAG = "WearablePunchActivity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punch);
        mFeedbackTextView = (TextView) findViewById(R.id.punchFeedbackTextview);
        mPunchTextView = findViewById(R.id.punchTextView);
        layout = findViewById(R.id.punch_layout);

        //SENSOR STUFF
        xValues = new ArrayList<>();
        yValues = new ArrayList<>();
        Log.d(TAG, "onCreate: Initializing Sensor Service");
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mSensorManager.registerListener(WearablePunchActivity.this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        Log.d(TAG, "onCreate: Registered Accelerometer listener");

        //broadcastreceiver stuff
        IntentFilter newFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, newFilter);

        // Enables Always-on
        setAmbientEnabled();
    }

    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            testButtonClick();
        }
    }

    class SendMessage extends Thread {
        String path;
        String message;

        SendMessage(String p, String m){
            path = p;
            message = m;
        }
        public void run() {

            Task<List<Node>> wearableList = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask =
                            //send message
                            Wearable.getMessageClient(WearablePunchActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    try {
                        Integer result = Tasks.await(sendMessageTask);
                    } catch (ExecutionException exception) {
                        //handle exception
                    } catch (InterruptedException ex) {
                        //handle exception
                    }
                }
            }catch (ExecutionException ex){
                //handle exception
            } catch (InterruptedException ex){
                //handle exception
            }
        }

    }




    public void testButtonClick(){
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
                mPunchTextView.setText("Ready?  " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                layout.setBackgroundColor(Color.GREEN);
                mPunchTextView.setTextColor(Color.BLACK);
                mPunchTextView.setText("PUNCH!");

                new CountDownTimer(1000, 100) {

                    public void onTick(long millisUntilFinished) {
                        xValues.add(x);
                        yValues.add(y);
                    }

                    public void onFinish() {
                        punchValue = Collections.max(yValues) + Collections.max(xValues);
                        mFeedbackTextView.setText("Such a good puncher with value: " + punchValue);
                        layout.setBackgroundColor(Color.BLACK);
                        mPunchTextView.setTextColor(Color.LTGRAY);
                        xValues.clear();
                        yValues.clear();
                        new SendMessage("/FISHPUNCH", punchValue+"").start();
                    }
                }.start();
            }
        }.start();
    }

    //SENSOR STUFF
    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged: X" + event.values[0] + "Y:" + event.values[1] + "z:" + event.values[2] );
        x = event.values[0];
        y = event.values[1];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
