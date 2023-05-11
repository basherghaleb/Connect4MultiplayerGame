package edu.msu.holsche2.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import edu.msu.holsche2.project1.Cloud.Cloud;
import edu.msu.holsche2.project1.Cloud.Models.StateResult;

@SuppressWarnings("unused")
public class ConnectFourActivity extends AppCompatActivity {

    /**
     * The bundle key for all the data that needs to be saved
     */
    private static final String PARAMETERS = "parameters";

    /**
     * The bundle key for the winning player
     */
    private static final String PLAYER_WON = "player_won";

    /**
     * The bundle key for the losing player
     */
    private static final String PLAYER_LOST = "player_lost";

    /**
     * Bundle key for boolean marking the player as room host (player 1)
     */
    public static final String PLAYER_HOST = "player_host";

    /**
     * Bundle key for room id
     */
    public static final String ROOM_ID = "room_id";

    /**
     * Bundle key for user id
     */
    public static final String USER_ID = "user_id";

    /**
     * Bundle key for host's username
     */
    public static final String HOST = "host";

    /**
     * Bundle key for guest's username
     */
    public static final String GUEST = "guest";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_connect_four);

        // Send player names to the view
        getConnectFourView().setPlayer1(getIntent().getExtras().getString(HOST));
        getConnectFourView().setPlayer2(getIntent().getExtras().getString(GUEST));

        /*
         * Restore any state
         */
        if(bundle != null) {
            getConnectFourView().getFromBundle(PARAMETERS, bundle);
            // above also sets host!
            // update UI
            getConnectFourView().updatePlayerLabel();
        } else {
            getConnectFourView().setActivePlayer(getIntent().getExtras().getString(HOST));
            getConnectFourView().setHost(getIntent().getExtras().getBoolean(PLAYER_HOST));
            getConnectFourView().setHostName(getIntent().getExtras().getString(HOST));
            getConnectFourView().setGuestName(getIntent().getExtras().getString(GUEST));
            getConnectFourView().setRoomId(getIntent().getExtras().getString(ROOM_ID));
            getConnectFourView().setUserId(getIntent().getExtras().getString(USER_ID));

            Log.i("ConnectFourActivity", "isHost="+getConnectFourView().getHost());
            Log.i("ConnectFourActivity", "hostName="+getConnectFourView().getHostName());
            Log.i("ConnectFourActivity", "guestName="+getConnectFourView().getGuestName());

            //update UI
            getConnectFourView().updatePlayerLabel();
        }
    }

    /**
     * Save the instance state into a bundle
     * @param bundle the bundle to save into
     */
    @Override
    protected void onSaveInstanceState(@SuppressWarnings("NullableProblems") Bundle bundle) {
        super.onSaveInstanceState(bundle);
        getConnectFourView().putToBundle(PARAMETERS, bundle);
    }

    /**
     * Get the connect four game view
     * @return ConnectFourView reference
     */
    private ConnectFourView getConnectFourView() {
        return this.findViewById(R.id.connectFourView);
    }

    /**
     * Logic for ending the current players turn
     * @param view The view of the button this method is
     * attached to
     */
    public void onEndTurn(View view){
        // switch the active player
        if (!getGame().hasPlayerWon()){
            getConnectFourView().switchActivePlayer();
        }

        // player has ended their turn, send game state to the server
        // serialize the parameters class
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(getGame().getState());
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] paramsByte = bos.toByteArray();
        // convert byte array to Base64 so it can be stored in the database
        String params = Base64.encodeToString(paramsByte, Base64.DEFAULT);
        Log.i("onEndTurn", ""+params.length());
        // create a thread to save the state to the cloud
        new Thread(() -> {
            Cloud cloud = new Cloud();
            // TODO: update game status string
            final boolean ok = cloud.saveGameState(getGame().getRoomId(), getGame().getUserId(),
                    params, getGame().hasPlayerWon() ? "victory" : "playing");
            if(!ok) {
                // display a toast if we fail to save
                getConnectFourView().post(() -> {
                    Toast.makeText(getConnectFourView().getContext(),
                        R.string.endturn_fail,
                        Toast.LENGTH_SHORT).show();
                });
            }
        }).start();

        if (getGame().hasPlayerWon())
            switchToWin();
    }

    /**
     * Logic for undoing the active player's last move
     * @param view The view of the button this method is
     * attached to
     */
    public void onUndo(View view) {
        // undo the player's last move
        getConnectFourView().undoMove();
    }

    public void onBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onSurrender(View view){

        Intent intent = new Intent(this, WinActivity.class);

        // set winner and loser names
        String won = getGame().getActivePlayer().equals(getGame().getPlayer1()) ?
                getGame().getPlayer2() : getGame().getPlayer1();

        intent.putExtra(PLAYER_WON, won);
        intent.putExtra(PLAYER_LOST, getGame().getActivePlayer());

        startActivity(intent);
        finish();
    }

    public ConnectFour getGame() {
        return getConnectFourView().getGame();
    }

    public void switchToWin() {
        Intent switchActivityIntent = new Intent(this, WinActivity.class);

        switchActivityIntent.putExtra(PLAYER_WON, getGame().getActivePlayer());

        if(getGame().getActivePlayer().equals(getGame().getPlayer1())) {
            switchActivityIntent.putExtra(PLAYER_LOST, getGame().getPlayer2());
        } else {
            switchActivityIntent.putExtra(PLAYER_LOST, getGame().getPlayer1());
        }

        startActivity(switchActivityIntent);
        finish();
    }

}