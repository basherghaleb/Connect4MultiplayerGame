package edu.msu.holsche2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class WinActivity extends AppCompatActivity {

//    GET THE CURRENT ACTIVE PLAYER

    /**
     * Player name who won
     */
    private String playerWon;

    /**
     * Player name who lost
     */
    private String playerLost;

    /**
     * The bundle key for the winning player
     */
    private static final String PLAYER_WON = "player_won";

    /**
     * The bundle key for the losing player
     */
    private static final String PLAYER_LOST = "player_lost";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_win);

        // set player names
        playerWon = getIntent().getExtras().getString(PLAYER_WON);
        playerLost = getIntent().getExtras().getString(PLAYER_LOST);

        setDlg();
    }

    @SuppressWarnings("unused")
    public void onPlayAgain(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void setDlg(){
        final TextView winDisplayDlg = findViewById(R.id.winDisplay);
        winDisplayDlg.setText(getResources().getString(R.string.dummyWin, playerWon, playerLost));
    }
}