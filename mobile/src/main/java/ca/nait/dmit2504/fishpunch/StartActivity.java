package ca.nait.dmit2504.fishpunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;


public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    int dialogueCounter = 0, punchCounter =0;
    long fishCurrentHP, fishDefaultHP = 90;
    ImageView dialogueImageView, fishImageView;
    Button mNextButton;

    //handler for message queue
    protected Handler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fishCurrentHP = fishDefaultHP;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        dialogueImageView = findViewById(R.id.dialogueImageView);
        fishImageView = findViewById(R.id.fishImageView);
        dialogueImageView.setImageResource(android.R.color.transparent);
        fishImageView.setImageResource(android.R.color.transparent);
        mNextButton = findViewById(R.id.next_button);

        //use the handler to get data
        myHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Bundle stuff = msg.getData();
                return true;
            }
        });

        //registering the broadcast receiver to our activity
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        Receiver messageReceiver = new Receiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

    }

    //click through because no loop
    public void dialogueClick(View v) {
        switch (dialogueCounter){
            case (0):
                dialogueImageView.setImageResource(R.drawable.wonderfuldayone);
                dialogueCounter++;
                break;
            case (1):
                fishImageView.setImageResource(R.drawable.fish1);
                dialogueImageView.setImageResource(R.drawable.expression);
                dialogueCounter++;
                break;
            case (2):
                dialogueImageView.setImageResource(R.drawable.ohnoloopone);
                dialogueCounter++;
                mNextButton.setText("PUNCH IT!");
                break;
            case (3):
                //dialoguecounter should be taken over by onReceive from the broadcast receiver,
                    // as receiving the message from the wearable will kick off the next event

                //send message to the thread
                //message doesn't matter because we're just starting event on the wearable
                new NewThread("/FISHPUNCH", "punchit").start();
                Log.i(TAG, "dialogueClick: Clicked");
                break;
        }

    }



    //Broadcast receiver to receive stuff from message service
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            //RECEIVE MESSAGE
            // PARSE STRING VALUE TO INT
            // INCREMENT PUNCH COUNTER
            long punchVelocity = intent.getLongExtra("punchValue", 0);
/*            try{

            catch(Exception ex) {
                Log.i(TAG, "onReceive: failed to parse punchvalue " + ex);
            }*/
            punchCounter++;
            fishCurrentHP = fishCurrentHP - punchVelocity;
            mNextButton.setText("PUNCH IT! AGAIN!");
            // CHANGE DIALOGUE IMAGEVIEW DEPENDING ON FISHHP
            if (fishCurrentHP > fishDefaultHP*.8){
                Log.i(TAG, "onReceive: FISHHP: "+fishCurrentHP+ "PV:" + punchVelocity);

            } else{
                if (fishCurrentHP > fishDefaultHP*.6 && fishCurrentHP < fishDefaultHP*.8){
                    Log.i(TAG, "onReceive: FISHHP: "+fishCurrentHP+ "PV:" + punchVelocity);

                }else{
                    if (fishCurrentHP > fishCurrentHP*.25 && fishCurrentHP < fishDefaultHP*.6){
                        Log.i(TAG, "onReceive: FISHHP: "+fishCurrentHP+ "PV:" + punchVelocity);
                    }else{
                        if (fishCurrentHP < 0){
                            Log.i(TAG, "onReceive: FISHHP: "+fishCurrentHP+ "PV:" + punchVelocity);
                            mNextButton.setText("IT'S PUNCHED");
                        }
                    }
                }
            }
            //end of nested if/else

            // DIALOGUECOUNTER STAYS AT 4 until FISHHP is 0 or punch counter is 3
        }
    }

    //this method will put the Message object into the queue via the handler class
    public void queueMessage(String messageText) {
        Bundle bundle = new Bundle();
        bundle.putString("messageText", messageText);
        Message msg = myHandler.obtainMessage();
        msg.setData(bundle);
        myHandler.sendMessage(msg);
    }


    //thread class to run the send Message
    class NewThread extends Thread {
        String path;
        String message;
        NewThread(String p, String m){
            Log.i(TAG, "NewThread: new thread created");
            path = p;
            message = m;
        }
        public void run(){
            //gets a list of connected devices
            Task<List<Node>> wearableList = Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                Log.i(TAG, "run: nodes investigated");
                List<Node> nodes = Tasks.await(wearableList);
                //iterates through all devices
                for (Node node : nodes) {
                    //sends the message to the device, with the path, and the message string from above
                    Task<Integer> sendMessageTask =
                            //send message
                            Wearable.getMessageClient(StartActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    try {
                        Integer result = Tasks.await(sendMessageTask);
                        queueMessage("I just sent the wearable a message");
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
}
