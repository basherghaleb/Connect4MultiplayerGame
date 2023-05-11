package edu.msu.holsche2.project1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.Serializable;

public class ConnectFourPiece implements Serializable {

    private float x;

    private float y;

    private final boolean turn;

    private static transient Bitmap chipGreen = null;

    private static transient Bitmap chipWhite = null;

    public static void initialize(Context con) {
        chipGreen = BitmapFactory.decodeResource(con.getResources(), R.drawable.green_chip);
        chipWhite = BitmapFactory.decodeResource(con.getResources(), R.drawable.white_chip);
    }

    public ConnectFourPiece(float x, float y, Context con, boolean playerOneTurn){
        if (chipGreen == null){
            initialize(con);
        }
        this.x = x;
        this.y = y;

        this.turn = playerOneTurn;
    }

    public void move(float dx, float dy){
        this.x += dx;
        this.y += dy;
    }

    public void SetLocation(float newX, float newY)
    {
        this.x = newX;
        this.y = newY;
    }

    public float getX()
    {
        return this.x;
    }

    public float getY()
    {
        return this.y;
    }

    public void draw(Canvas canvas, int marginX, int marginY, int boardSize, float scaleFactor){
        canvas.save();

        // Convert x,y to pixels and add the margin, then draw
        canvas.translate(marginX + x * boardSize, marginY + y * boardSize);

        // Scale it to the right size
        canvas.scale(scaleFactor/2.2f, scaleFactor/2.2f);

        Bitmap chipImage = turn ? chipGreen : chipWhite;
        // This magic code makes the center of the piece at 0, 0
        canvas.translate(-chipImage.getWidth() / 2f, -chipImage.getHeight() / 2f);

        canvas.drawBitmap(chipImage, 0, 0, null);

        canvas.restore();
    }

    public boolean getTurn() {
        return turn;
    }
}
