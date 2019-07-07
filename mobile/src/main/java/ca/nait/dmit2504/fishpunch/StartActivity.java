package ca.nait.dmit2504.fishpunch;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;


public class StartActivity extends AppCompatActivity {
    int dialogueCounter = 0;
    ImageView dialogueImageView, fishImageView;
    Button mNextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        dialogueImageView = findViewById(R.id.dialogueImageView);
        fishImageView = findViewById(R.id.fishImageView);
        dialogueImageView.setImageResource(android.R.color.transparent);
        fishImageView.setImageResource(android.R.color.transparent);
        mNextButton = findViewById(R.id.next_button);
    }

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
            case (4):
                dialogueCounter++;
                break;
        }

    }
}
