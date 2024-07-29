package com.example.cheesechaseapi;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Random;

public class MyCanvasView extends View {

    private Paint lane_fill;
    private float jerryX = 540;
    public float jerryY = 1500;
    private float TomX = 540;
    private float TomY = 1800;

    private Bitmap tomBitmap;
    private Bitmap jerryBitmap;
    private Bitmap obstacleBitmap;
    private PositionedBitmap obstacle1;
    private PositionedBitmap obstacle3;
    private PositionedBitmap obstacle5;

    private int num_collisions;
    private int score;
    private int highscore = 0;
    private static final String TAG = "MyCanvasView"; // Define TAG here
    private int obstacle_limit;

    public MyCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public void setObstacleLimit(int limit) {
        this.obstacle_limit = limit;
    }
    public void setTomBitmap(Bitmap tomBitmap) {
        this.tomBitmap = tomBitmap;
    }
    public void setJerryBitmap(Bitmap jerryBitmap){
        this.jerryBitmap = jerryBitmap;
    }
    public void setObstacleBitmap(Bitmap obstacleBitmap){
        if (obstacleBitmap != null) {
            this.obstacleBitmap = obstacleBitmap;
            obstaclesetter(this.obstacleBitmap); // Ensure obstacles are initialized here
            Log.d(TAG, "Obstacle bitmap set and obstacles initialized");
        }
    }

    private void obstaclesetter(Bitmap obstacleBitmap) {
        if (obstacleBitmap != null) {
            obstacle1 = new PositionedBitmap(obstacleBitmap, 90, 0);
            obstacle3 = new PositionedBitmap(obstacleBitmap, 435, 400);
            obstacle5 = new PositionedBitmap(obstacleBitmap, 780, 800);
            Log.d(TAG, "Obstacles initialized");
        } else {
            Log.d(TAG, "Obstacle bitmap is null, obstacles not initialized");
        }
    }

    private void init() {
        lane_fill = new Paint();
        lane_fill.setColor(Color.parseColor("#e5dccd"));
        lane_fill.setStyle(Paint.Style.FILL);

        num_collisions = 0;
        score = 0;

        obstaclesetter(obstacleBitmap);

        startGame();
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        // Clear the canvas
        canvas.drawColor(Color.WHITE);
        drawBackground(canvas);
        drawCharacters(canvas,tomBitmap,jerryBitmap);
        drawObstacles(canvas);
    }


    private void drawObstacles(Canvas canvas) {
        if (obstacle1 != null) {
            drawObstacle(canvas, obstacle1);
        }
        if (obstacle3 != null) {
            drawObstacle(canvas, obstacle3);
        }
        if (obstacle5 != null) {
            drawObstacle(canvas, obstacle5);
        }
    }

    public void drawObstacle(Canvas canvas, PositionedBitmap positionedBitmap) {
        if (positionedBitmap.getBitmap() != null) {
            canvas.drawBitmap(positionedBitmap.getBitmap(), positionedBitmap.getX(),
                    positionedBitmap.getY(), null);
        } else {
            Log.d(TAG, "PositionedBitmap has a null bitmap");
        }
    }


    private void drawCharacters(Canvas canvas,Bitmap tomBitmap,Bitmap jerryBitmap) {
        if (tomBitmap != null && jerryBitmap != null) {
            canvas.drawBitmap(tomBitmap, TomX-200, TomY-125, null);
            canvas.drawBitmap(jerryBitmap,jerryX - 150, jerryY - 100,null);
        }
    }


    public void drawBackground(Canvas canvas) {
        int width = getWidth();  // to get the width of the canvas
        int height = getHeight();  // to get the height of the canvas

        // For demonstration, logging the width and height
//        Log.d(TAG, "Width: " + width + ", Height: " + height);

        // Drawing the three lanes
        drawLane(canvas, 60, 0, 330, height, lane_fill);
        drawLane(canvas, 390, 0, 690, height, lane_fill);
        drawLane(canvas, 750, 0, 1020, height, lane_fill);
    }
    private void drawLane(Canvas canvas, int x1, int y1, int x2, int y2, Paint paint) {
        Path lane = new Path();
        lane.moveTo(x1, y1);
        lane.lineTo(x1, y2);
        lane.lineTo(x2, y2);
        lane.lineTo(x2, y1);
        lane.lineTo(x1, y1);
        canvas.drawPath(lane, paint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:  // When the touch starts
            case MotionEvent.ACTION_MOVE:  // When the touch moves
                float touchX = event.getX();
                float touchY = event.getY();

                if (touchX > jerryX) {
                    moveJerryRight();
                }else if(touchX < jerryX){
                    moveJerryLeft();
                }
        }
        return super.onTouchEvent(event);  // For other actions, use default handling
    }


    private void moveJerryLeft() {
        jerryX -= 330; // Step size can be adjusted as required
        invalidate();
    }

    private void moveJerryRight() {
        jerryX += 330; // Step size can be adjusted as required
        invalidate();
    }
    private void moveTom(){
        if(TomX > 60 && TomX < 330){
            if(TomY - (obstacle1.getY() + obstacle1.getHeight()) < 10){
                moveTomRight();
            }
        }else if(TomX > 390 && TomX < 690){
            if(TomY - (obstacle3.getY() + obstacle3.getHeight()) < 10){
                if(TomY - (obstacle1.getY() + obstacle1.getHeight()) < 10){
                    moveTomRight();
                }else if(TomY - (obstacle5.getY() + obstacle5.getHeight()) < 10){
                    moveTomLeft();
                }else{
                    moveTomLeft();
                }
            }
        }else if(TomX > 750 && TomX < 1020){
            if(TomY - (obstacle5.getY() + obstacle5.getHeight()) < 10) {
                moveTomLeft();
            }
        }
    }
    private void moveTomLeft() {
        TomX -= 330; // Step size can be adjusted as required
        invalidate();
    }

    private void moveTomRight() {
        TomX += 330; // Step size can be adjusted as required
        invalidate();
    }
    boolean alreadycollided1 = false;
    boolean alreadycollided5 = false;
    boolean alreadycollided3 = false;
    public void Collission(){
        if((jerryX > 60 && jerryX < 330)){
            if(jerryY <= obstacle1.getY()+obstacle1.getHeight() && jerryY >= obstacle1.getY()){
                if(alreadycollided1 == false){
                    alreadycollided1 = true;
                    num_collisions++;
                    if(num_collisions == 1){
                        displayToast("Collision detected, Tom has closed in");
                        CloserTom1();
                    }else if(num_collisions == obstacle_limit){
                        CloserTom2();
                        displayToast("Game Over");
                    }
                }
            }
        }else if(jerryX > 390 && jerryX < 690){
            if(jerryY <= obstacle3.getY()+obstacle3.getHeight() && jerryY >= obstacle3.getY()){
                if(alreadycollided3 == false){
                    alreadycollided3 = true;
                    num_collisions++;
                    if(num_collisions == 1){
                        displayToast("Collision detected, Tom has closed in");
                        CloserTom1();
                    }else if(num_collisions == obstacle_limit){
                        CloserTom2();
                        displayToast("Game Over");
                    }
                }
            }
        }else if( (jerryX > 750 && jerryX < 1020)){
            if(jerryY <= obstacle5.getY()+obstacle5.getHeight() && jerryY >= obstacle5.getY()){
                if(alreadycollided5 == false){
                    alreadycollided5 = true;
                    num_collisions++;
                    if(num_collisions == 1){
                        displayToast("Collision detected, Tom has closed in");
                        CloserTom1();
                    }else if(num_collisions == obstacle_limit){
                        CloserTom2();
                        displayToast("Game Over");
                    }
                }
            }
        }
        if(alreadycollided1){
            if(jerryY > obstacle1.getY()+obstacle1.getHeight() || jerryY < obstacle1.getY()){
                alreadycollided1 = false;
            }
        }
        if(alreadycollided5){
            if(jerryY > obstacle5.getY()+obstacle5.getHeight() || jerryY < obstacle5.getY()){
                alreadycollided5 = false;
            }
        }
        if(alreadycollided3){
            if(jerryY > obstacle3.getY()+obstacle3.getHeight() || jerryY < obstacle3.getY()){
                alreadycollided3 = false;
            }
        }
        if (updateScore() >= 5000) {
            ((MainActivity) getContext()).WinDialog("5000");
            if(updateScore() > highscore){
                MainActivity.setHighscore(String.valueOf(updateScore()));
            }
        }
        if(num_collisions == obstacle_limit){
            ((MainActivity) getContext()).normal_Dialog(String.valueOf(score));
            MainActivity.setHighscore(String.valueOf(updateScore()));
        }
    }
    public void startGame() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (num_collisions < obstacle_limit) {
                    RegenerateObstacles();
                    moveTom();
                    Collission();
                    if(score >= 5000){
                        num_collisions = obstacle_limit;
                        displayToast("Jerry has escaped Tom, well done");
                    }
                    handler.postDelayed(this, 30); // 30 milliseconds interval for animation speed
                }
            }
        };
        handler.post(runnable);
    }
    private void moveObstacle(PositionedBitmap positionedBitmap){
        positionedBitmap.setY(positionedBitmap.getY() + 10);
        invalidate();
    }
    private void RegenerateObstacles() {
        moveObstacle(obstacle1);
        moveObstacle(obstacle3);
        moveObstacle(obstacle5);

        // Whichever obstacle appears to move off screen, reset it back on screen
        resetIfOffScreen(obstacle1,0,50);
        resetIfOffScreen(obstacle5,250,300);
        resetIfOffScreen(obstacle3,500,550);
    }

    private void resetIfOffScreen(PositionedBitmap positionedBitmap,int l_bound, int u_bound) {
        // Assuming getHeight() gives the height of your view
        int viewHeight = getHeight();
        if (positionedBitmap.getY() > TomY + 50) {
            positionedBitmap.setY(randomnum(l_bound,u_bound)); // Move it above the screen
            invalidate();
        }
    }
    private void displayToast(String s){
        Toast.makeText(getContext().getApplicationContext(),s,Toast. LENGTH_SHORT).show();
    }
    private void CloserTom1(){
        TomY -= 100;
    }
    private void CloserTom2(){
        TomY = jerryY;
    }
    public int updateScore(){
        score += (5-num_collisions);
        MainActivity.setScoreText("" + score);
        return score;
    }
    public void resetGameState() {
        // Reset all game-related variables
        score = 0;
        num_collisions = 0;
        alreadycollided1 = false;
        alreadycollided5 = false;
        alreadycollided3 = false;

        // Reset positions of Jerry and Tom
        jerryX = 540;
        jerryY = 1500;
        TomX = 540;
        TomY = 1800;

        // Reset obstacle positions
        obstaclesetter(obstacleBitmap);
        // Invalidate the canvas to reflect the changes
        invalidate();
    }
    private int randomnum(int l_bound,int u_bound){
        Random rand = new Random();
        int Y = rand.nextInt(u_bound - l_bound) + l_bound;
        return Y;
    }
}
