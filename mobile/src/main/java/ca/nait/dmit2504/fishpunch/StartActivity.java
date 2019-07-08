package ca.nait.dmit2504.fishpunch;

import android.animation.ObjectAnimator;
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
                //dialoguecounter should be taken over elsewhere in program,
                // as receiving the message from the wearable will kick off the next event

                //send message to the thread
                //  message doesn't matter because we're just starting event on the wearable
                new NewThread("/FISHPUNCH", "punchit").start();
                Log.i(TAG, "dialogueClick: Clicked");
                break;
            case (9):
                Intent startActivityintent = new Intent(this, MainActivity.class);
                startActivity(startActivityintent);
                break;
        }

    }



    //Broadcast receiver to receive stuff from message service
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            //RECEIVE MESSAGE
            //get punch value from the intent
            long punchVelocity = intent.getLongExtra("punchValue", 0);
            punchCounter++;
            //remove punchvalue from fish hp
            fishCurrentHP = fishCurrentHP - punchVelocity;

            //shake the fish if the punch value is over 20
            if (punchVelocity > 20) {
                ObjectAnimator rotate = ObjectAnimator.ofFloat(fishImageView, "rotation", 0f, 20f, 0f, -20f, 0f); // rotate o degree then 20 degree and so on for one loop of rotation.
                rotate.setRepeatCount(5);
                rotate.setDuration(100);
                rotate.start();
            }

            switch(punchCounter){
                case 1:
                    dialogueImageView.setImageResource(R.drawable.punchagain);
                    break;
                case 2:
                    dialogueImageView.setImageResource(R.drawable.itscoming);
                    break;
            }

            //check how many punches
            if (punchCounter <= 3) {
                mNextButton.setText("PUNCH IT! AGAIN!");
                //TODO - CHANGE DIALOGUE IMAGEVIEW DEPENDING ON FISHHP
                //check HP
                if (fishCurrentHP > fishDefaultHP * .8) {
                    Log.i(TAG, "onReceive: FISHHP: " + fishCurrentHP + "PV:" + punchVelocity);

                } else {
                    if (fishCurrentHP > fishDefaultHP * .6 && fishCurrentHP < fishDefaultHP * .8) {
                        Log.i(TAG, "onReceive: FISHHP: " + fishCurrentHP + "PV:" + punchVelocity);

                    } else {
                        if (fishCurrentHP > fishCurrentHP * .25 && fishCurrentHP < fishDefaultHP * .6) {
                            Log.i(TAG, "onReceive: FISHHP: " + fishCurrentHP + "PV:" + punchVelocity);
                        } else {
                            if (fishCurrentHP < 0) {
                                Log.i(TAG, "onReceive: FISHHP: " + fishCurrentHP + "PV:" + punchVelocity);
                                dialogueImageView.setImageResource(R.drawable.victory);
                                fishImageView.setImageResource(android.R.color.transparent);
                                dialogueCounter = 9;
                                mNextButton.setText("YOU WIN");
                            }
                        }
                    }
                }
            }
            if (punchCounter >= 3 && fishCurrentHP > 0)
            {
                dialogueImageView.setImageResource(R.drawable.gameover);
                dialogueCounter = 9;
                mNextButton.setText("YOU LOSE");
            }
            //end of nested if/else
        }
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
