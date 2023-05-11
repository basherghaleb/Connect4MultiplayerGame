package edu.msu.holsche2.project1;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Custom view class for our Puzzle.
 */
public class ConnectFourView extends View {

    // Class member variables and constants

    /**
     * The ConnectFour game object
     */
    public ConnectFour game;

    /**
     * Toast for error status
     */
    Toast errorToast;

    public ConnectFourView(Context context) {
        super(context);
        init(null, 0);
    }

    public ConnectFourView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ConnectFourView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    @SuppressWarnings("unused")
    private void init(AttributeSet attrs, int defStyle) {
        game = new ConnectFour(getContext(), this);

        errorToast = Toast.makeText(getContext(), "", Toast.LENGTH_SHORT);
    }

    public boolean getHost(){
        return game.getHost();
    }

    public void setHost(boolean host){
        game.setHost(host);
    }

    public String getRoomId() { return game.getRoomId(); }

    public void setRoomId(String roomId) { game.setRoomId(roomId); }

    public String getHostName() { return game.getPlayer1(); }

    public void setHostName(String name) { game.setPlayer1(name); }

    public String getGuestName() { return game.getPlayer2(); }

    public void setGuestName(String name) { game.setPlayer2(name); }

    public String getUserId() { return game.getUserId(); }

    public void setUserId(String id) { game.setUserId(id); }

    /**
     * Sets the name of player one
     * @param name Player1's name
     */
    public void setPlayer1(String name) {
        game.setPlayer1(name);
    }

    /**
     * Sets the name of player two
     * @param name Player2's name
     */
    public void setPlayer2(String name) {
        game.setPlayer2(name);
    }


    public void setActivePlayer(String player) { game.setActivePlayer(player); }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        game.draw(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return game.onTouchEvent(this, event, getContext());
    }

    /**
     * Pass the bundle to the game so it can save the current state
     * @param key key name to use in the bundle
     * @param bundle bundle to save to
     */
    public void putToBundle(String key, Bundle bundle) {
        // save the state of the game
        game.putToBundle(key, bundle);
        bundle.putBoolean(ConnectFourActivity.PLAYER_HOST, getHost());
        bundle.putString(ConnectFourActivity.ROOM_ID, getRoomId());
        bundle.putString(ConnectFourActivity.USER_ID, getUserId());
    }

    /**
     * Pass the bundle to the game so it can restore the state
     * @param key key name to use in the bundle
     * @param bundle bundle to load from
     */
    public void getFromBundle(String key, Bundle bundle) {
        // load the game object
        game.getFromBundle(key, bundle);
        setHost(bundle.getBoolean(ConnectFourActivity.PLAYER_HOST));
        setRoomId(bundle.getString(ConnectFourActivity.ROOM_ID));
        setUserId(bundle.getString(ConnectFourActivity.USER_ID));
    }

    /**
     * Switches the game's active player member
     */
    public void switchActivePlayer() {
        if (game.playedHasMoved() && !game.hasPlayerWon()) {
            game.switchPlayer();
            // update the text view
            updatePlayerLabel();
        } else if (!game.hasPlayerWon()) {
            this.post(() -> {
                errorToast.cancel();
                errorToast.setText(R.string.player_invalid_move);
                errorToast.show();
            });
        }
    }

    /**
     * Updates the active player label UI
     */
    public void updatePlayerLabel() {
        TextView playerLabel = ((Activity)getContext()).findViewById(R.id.textView3);
        playerLabel.setText(getResources().getString(R.string.active_label,
                game.getActivePlayer()));
    }

    /**
     * Tells the game object to undo the active player's last move
     */
    public void undoMove() {
        if (game.playedHasMoved()) {
            game.undoMove(this);
        } else {
            this.post(() -> {
                errorToast.cancel();
                errorToast.setText(R.string.player_invalid_undo);
                errorToast.show();
            });
        }
    }

    /**
     * Return the game object
     */
    public ConnectFour getGame() { return game; }

}