package edu.msu.holsche2.project1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

import android.util.Base64;

import edu.msu.holsche2.project1.Cloud.Cloud;
import edu.msu.holsche2.project1.Cloud.Models.StateResult;

@SuppressWarnings("FieldCanBeLocal")
public class ConnectFour {

    // Class member variables and constants

    private static class Parameters implements Serializable {
        /**
         * The first player's name, entered from the home
         * activity, or a default of Player 1
         */
        public String player1;

        /**
         * The second player's name, entered from the home
         * activity, or a default of Player 2
         */
        public String player2;

        /**
         * The player who is currently taking their turn
         */
        public String activePlayer;

        /**
         * Has the current player won the game?
         */
        public boolean playerWon;

        /**
         * Last relative x-position of piece
         */
        private float lastRelX;

        /**
         * Last relative y-position of piece
         */
        private float lastRelY;

        /**
         * Collection of game pieces for the players to use
         */
        private final ArrayList<ConnectFourPiece> pieces = new ArrayList<>();

        /**
         * Piece currently being dragged
         */
        private ConnectFourPiece dragging;

        /**
         * Bool for whether or not it's player1's turn
         */
        private boolean playerOneTurn;

        /**
         * Whether or not the player has placed a chip for their current turn.
         */
        private boolean placedChip;
    }

    public synchronized boolean getTurn(){ return params.playerOneTurn; }

    public synchronized void setTurn(boolean t){ params.playerOneTurn = t; }

    public synchronized void flipTurn() { params.playerOneTurn = !params.playerOneTurn; }

    private Parameters params = new Parameters();

    /**
     * True if the current player is the host.
     */
    private boolean isHost = false;

    private String roomId = null;

    private String userId = null;

    private View view = null;

    private final Bitmap backgroundImage;

    private static final float SCALE_IN_VIEW = 1.0f;

    /**
     * The relX location of the upper left cell
     */
    private final static float X0 = 0.1457f;

    /**
     * The relY location of the upper left cell
     */
    private final static float Y0 = 0.09f;

    /**
     * The margin between two adjacent cells
     * horizontally and vertically
     *
     */
    private final static float dX = 0.118f;

    private int boardSize;

    private int marginX;

    private int marginY;

    private float scaleFactor;

    private final static int BOARD_WIDTH = 7;
    private final static int BOARD_HEIGHT = 6;
    private final static int BOARD_CELLS = BOARD_HEIGHT*BOARD_WIDTH;

    private final static float CELL_EMPTY = -100f;

    public ConnectFour(Context context, View view) {
        InitializeBoard(context);
        this.view = view;

//        this.isHost = isHost;
        params.playerOneTurn = true;
        backgroundImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);

        waitForOpponent(view);
    }

    /**
     * Creates an empty* 1 X 42 array that will hold the
     * connect four pieces
     */
    private void InitializeBoard(Context context) {
        for (int i = 0; i < BOARD_CELLS; i++) {
            // Adding a dummy piece in all the cells for now
            // A dummy piece has an interesting location of (-100, -100)
            params.pieces.add(i, new ConnectFourPiece(CELL_EMPTY, CELL_EMPTY, context, true));
        }
    }


    /**
     * @param rowNumber The row number of a piece (0-numbered)
     * @param colNumber The col number of a piece (0-numbered)
     * @return The index of the piece in the board array
     */
    int ConvertRowColToIndex(int rowNumber, int colNumber)
    {
        return rowNumber*BOARD_WIDTH + colNumber;
    }

    /**
     * @param pieceYLocation Y location of piece as scaled to background
     * @return integer row coordinate
     */
    int GetRowNumber(float pieceYLocation)
    {
        int pos = (int) Math.floor(((pieceYLocation - Y0 + dX/2) / dX));
        Log.i("piece", String.valueOf(pos));
        return (pos >= BOARD_HEIGHT || pos < 0) ? -1 : pos;
    }

    /**
     * @param pieceXLocation X location of piece as scaled to background
     * @return integer column coordinate
     */
    int GetColNumber(float pieceXLocation)
    {
        int pos = (int) Math.floor(((pieceXLocation - X0 + dX/2) / dX));
        return (pos >= BOARD_WIDTH || pos < 0) ? -1 : pos;
    }

    /**
     * Sets the name of player one
     * @param name Player1's name
     */
    public void setPlayer1(String name) {
        params.player1 = name;
    }

    /**
     * Sets the name of player two
     * @param name Player2's name
     */
    public void setPlayer2(String name) {
        params.player2 = name;
    }

    public boolean getHost(){
        return isHost;
    }

    public void setHost(boolean host){
        isHost = host;
    }

    public String getRoomId() { return roomId; }

    public void setRoomId(String roomId) { this.roomId = roomId; }

    public String getUserId() { return userId; }

    public void setUserId(String userid) { this.userId = userid; }

    /**
     * Handle a touch message. This is when we get an initial touch
     * @param x x location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    @SuppressWarnings("SameReturnValue")
    private boolean onTouched(View view, float x, float y, Context context) {
        if (params.dragging == null && !params.placedChip){
            // Create piece
            params.dragging = new ConnectFourPiece(x, y, context, params.playerOneTurn);
            // Add to pieces list
            params.pieces.add(params.dragging);
            // Move currently dragged piece
            params.lastRelX = x;
            params.lastRelY = y;

            view.invalidate();
        } else {
            params.lastRelX = x;
            params.lastRelY = y;
        }

        return true;
    }

    // Constants to indicate what piece is at a given index
    private static final int EMPTY_SLOT = 0;
    private static final int PLAYER_ONE_PIECE = 1;
    private static final int PLAYER_TWO_PIECE = 2;
    // when given coordinates aren't even in range
    private static final int OUT_OF_BOUNDS = -1;

    // for float comparisons
    private static final float EPSILON = 0.0001f;


    private int getPieceType(int row, int col){
        if (row >= BOARD_HEIGHT || row < 0 || col >= BOARD_WIDTH || col < 0){
            return OUT_OF_BOUNDS;
        }

        ConnectFourPiece piece = params.pieces.get(ConvertRowColToIndex(row, col));

        if (Math.abs(piece.getX() - CELL_EMPTY) < EPSILON){
            return EMPTY_SLOT;
        } else if (piece.getTurn()){
            return PLAYER_ONE_PIECE;
        } else {
            return PLAYER_TWO_PIECE;
        }
    }

    /**
     * Debug function to render the current board state
     * @param debugKey Key to pass to Log.d
     */
    private void debugRenderBoard(String debugKey){
        for (int r = 0; r < BOARD_HEIGHT; ++r) {
            StringBuilder row = new StringBuilder();
            for (int c = 0; c < BOARD_WIDTH; ++c) {
                row.append(getPieceType(r, c));
            }
            Log.d(debugKey, row.toString());
        }
    }

    public void waitForOpponent(View view){
        final ConnectFour game = this;
        new Thread(() -> {
            pollLoop:
            while (!hasPlayerWon()) {
                int timeouts = 0;
                while (!hasPlayerWon() && isHost != getTurn()) {
                    try {
                        Thread.sleep(2000);
                        timeouts++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (timeouts > 15){
                        break pollLoop;
                    }

                    Cloud cloud = new Cloud();
                    StateResult state = cloud.getGameState(getRoomId(), getUserId());
                    Log.i("waitForOpponent", getRoomId() + "," + getUserId());
                    if (state != null && Objects.equals(state.getStatus(), "yes")) {
                        Log.i("waitForOpponent", state.getState());

                        // deserialize etc.
                        byte[] paramsByte = Base64.decode(state.getState(), Base64.DEFAULT);
                        ByteArrayInputStream bis = new ByteArrayInputStream(paramsByte);

                        ObjectInputStream in;
                        try {
                            in = new ObjectInputStream(bis);
                            synchronized (game) {
                                params = (Parameters) in.readObject();
                            }
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.i("waitForOpponent", "Failed to get remote game state");
                    }
                }

                view.post(() -> {
                    view.invalidate();
                    ((ConnectFourView)view).updatePlayerLabel();
                });

                if (hasPlayerWon()) {
                    view.post(() -> {
                        ((ConnectFourActivity) view.getContext()).switchToWin();
                    });
                }

                while (isHost == getTurn()) {
                    // don't need to poll anything when it's our turn
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("waitForOpponent", "Opponent timed out");
            setTurn(isHost); // this player wins
            view.post(() -> {
                ((ConnectFourActivity) view.getContext()).switchToWin();
            });
        }).start();
    }

    /**
     * Handle a release of a touch message.
     * @param x x location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @param y y location for the touch release, relative to the puzzle - 0 to 1 over the puzzle
     * @return true if the touch is handled
     */
    private boolean onReleased(View view, float x, float y) {
        if(params.dragging != null) {

            int draggingRow = this.GetRowNumber(params.dragging.getY());
            int draggingCol = this.GetColNumber(params.dragging.getX());

            Log.i("piece position", "Row: " + draggingRow + "   Col: " + draggingCol);

            if (draggingCol != -1 && draggingRow != -1)
            {
                // piece on board snapping is possible
                // check if there are any available cells in that column
                //by going through all the cells in that column
                for (int rowNum = 5; rowNum >= 0; rowNum--)
                {
                    int i = rowNum*BOARD_WIDTH + draggingCol;

                    if (params.pieces.get(i).getX() == CELL_EMPTY)
                        // remember dummy piece?
                        // cell is empty
                    {
                        params.dragging.SetLocation(draggingCol* dX + X0,
                                rowNum* dX + Y0);
                        params.pieces.set(i, params.dragging);
                        // player has placed the chip, no longer drag it around
                        params.dragging = null;
                        params.placedChip = true;

                        params.playerWon = checkPlayerWon(rowNum, draggingCol);
                        break;
                    }
                }
            }
            debugRenderBoard("placePiece");
            view.invalidate();

            //params.dragging = null;
            return true;
        }
        return false;
    }


    /**
     * Draw the game state
     * @param canvas The canvas object to draw on
     */
    public void draw(Canvas canvas){
        int wid = canvas.getWidth();
        int hit = canvas.getHeight();

        // Determine the minimum of the two dimensions
        int minDim = Math.min(wid, hit);

        boardSize = (int)(minDim * SCALE_IN_VIEW);

        // Compute the margins so we center the puzzle
        marginX = (wid - boardSize) / 2;
        marginY = (hit - boardSize) / 2;

        //TODO: replace 800f backgroundImage.width() or something probably
        scaleFactor = (float)boardSize / backgroundImage.getWidth();

        canvas.save();
        canvas.translate(marginX, marginY);
        canvas.scale(scaleFactor, scaleFactor);
        canvas.drawBitmap(backgroundImage, 0, 0, null);
        canvas.restore();

        if(!params.pieces.isEmpty()) {
            for (ConnectFourPiece piece : params.pieces) {
                piece.draw(canvas, marginX, marginY, boardSize, scaleFactor);
            }
        }

        if (params.dragging != null){
            params.dragging.draw(canvas, marginX, marginY, boardSize, scaleFactor);
        }
    }

    /**
     * Handle a touch event.
     * @param view Current UI view object
     * @param event MotionEvent to handle touch movement
     * @return true if event handling was successful, else false
     */
    public boolean onTouchEvent(View view, MotionEvent event, Context context) {
        if (getTurn() != isHost){
            // only allow moves for the current turn
            return false;
        }
        //
        // Convert an x,y location to a relative location in the
        // puzzle.
        //
        float relX = (event.getX() - marginX) / boardSize;
        float relY = (event.getY() - marginY) / boardSize;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                Log.i("onTouchEvent", "ACTION_DOWN");
                return onTouched(view, relX, relY, context);

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.i("onTouchEvent",  "ACTION_CANCEL: " + event.getX() + "," + event.getY());
                return onReleased(view, relX, relY);

            case MotionEvent.ACTION_MOVE:
                Log.i("onTouchEvent",  "ACTION_MOVE: " + relX + "," + relY);
                // If we are dragging, move the piece and force a redraw
                if(params.dragging != null) {
                    params.dragging.move(relX - params.lastRelX,
                            relY - params.lastRelY);

                    params.lastRelX = relX;
                    params.lastRelY = relY;

                    view.invalidate();
                    return true;
                }
                break;
        }

        return false;
    }

    public void setActivePlayer(String player) { params.activePlayer = player; }
    public String getActivePlayer() { return params.activePlayer; }

    /**
     * Save the view state to a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to save to
     */
    public void putToBundle(String key, Bundle bundle) {
        // save
        bundle.putSerializable(key, params);
    }

    /**
     * Get the view state from a bundle
     * @param key key name to use in the bundle
     * @param bundle bundle to load from
     */
    public void getFromBundle(String key, Bundle bundle) {
        // load parameters class
        // this contains more or less all the important game state, so nothing more needs to be done
        params = (Parameters) bundle.getSerializable(key);
    }

    /**
     * Switch the currently active player
     */
    public void switchPlayer() {
        params.placedChip = false;
        flipTurn();
        // set name from boolean
        params.activePlayer = params.playerOneTurn ? params.player1 : params.player2;
    }

    public boolean playedHasMoved(){
        return params.placedChip;
    }

    /**
     * Check if a player won starting from a placed piece
     * Note: Assumes the given coordinates point to a valid piece.
     */

    private boolean checkPlayerWon(int startX, int startY){
        int toMatch = getPieceType(startX, startY);
        // should this check for empty or oob? maybe?

        // list of directions to check

        // Consider a placed piece. If a piece is added, any potentially new victory must depend
        // on this piece. So, we only need to check the area immediately around a placed piece X:
        // . O . O . O .
        // . . O O O . .
        // O O O X O O O
        // . . O O O . .
        // . O . O . O .
        // O . . O . . O
        // Additionally, each direction could be checked in either direction, since all that
        // matters is checking four of the same piece in a row, not the direction or anything
        // So, we check along all four axes (UD, LR, diagonal up and diagonal down)
        int[][] directions = {{1,0}, {0,1}, {1,1}, {-1,1}};
        for (int[] d : directions){
            int dx = d[0];
            int dy = d[1];
            // number of in-a-row pieces we found
            int consecutive = 0;

            // go from -3 to 3 past our start to make sure we get both sides of each direction
            // example:
            // -3    0     3
            // O O O X O O O
            for (int i = -3; i <= 3; ++i){
                int x = dx * i + startX;
                int y = dy * i + startY;

                if (getPieceType(x, y) == toMatch){
                    consecutive++;
                    if (consecutive >= 4) {
                        return true;
                    }
                } else {
                    consecutive = 0;
                }
            }
        }
        return false;
    }

    /**
     * Undoes the active player's last chip placement
     */
    public void undoMove(ConnectFourView view) {
        // check if user has a move to undo
        if(params.placedChip) {
            // we have "un-placed" the chip (this disables undo)
            params.placedChip = false;

            // get the newest addition to the array list
            // update it's location
            /*
             * The fact that this works at all is awful and should be considered a bug
             */
            params.pieces.get(params.pieces.size() - 1).SetLocation(CELL_EMPTY, CELL_EMPTY);

            debugRenderBoard("undoMove");
            // tell the screen to redraw
            view.invalidate();
        }
    }

    public boolean hasPlayerWon(){
        return params.playerWon;
    }

    public String getPlayer1() { return params.player1; }
    public String getPlayer2() { return params.player2; }
    public Parameters getState() { return params; }

}
