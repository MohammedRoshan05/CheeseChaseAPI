package com.example.cheesechaseapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    public Bitmap bitmap_tom;
    public Bitmap bitmap_jerry;
    public Bitmap obstacle_bitmap;
    public int obstacleLimit;
    public static TextView scoreText;
    public static TextView HighScoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        HighScoreText = findViewById(R.id.highscore);

        MyCanvasView canvasView = findViewById(R.id.mainCanvas);
        Toast.makeText(this, "the obstacle limit is " + obstacleLimit, Toast.LENGTH_SHORT);
        scoreText = findViewById(R.id.scoreText);
        scoreText.setX(350);
        scoreText.setY(100);
        scoreText.setTextColor(Color.parseColor("#e99650"));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new APICall().getObstacleLimit(this, new APICall.ObstacleLimitCallback() {
            @Override
            public void onResponse(ObstacleLimit limit) {
                obstacleLimit = Integer.parseInt(limit.getObstacleLimit());
                Toast.makeText(MainActivity.this, "The obstacle limit is " + obstacleLimit, Toast.LENGTH_SHORT).show();

                // Initialize the canvas view after obstacle limit is fetched
                MyCanvasView canvasView = findViewById(R.id.mainCanvas);
                canvasView.setObstacleLimit(obstacleLimit);

            }
        });
//        sBr
        new APICall().getImage(this, new APICall.ImageCallback() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap != null) {
                    bitmap_tom = bitmap;
                    bitmap_tom = getResizedBitmap(bitmap_tom,400,400);
                    MyCanvasView canvasView = findViewById(R.id.mainCanvas);
                    if (canvasView != null) {
                        Log.d("MainActivity", "Setting Tom Bitmap");
                        canvasView.setTomBitmap(bitmap_tom);
                        canvasView.invalidate(); // Ensure the view redraws with the new bitmap

                    } else {
                        Log.d("MainActivity", "Canvas view not found!");
                    }
                } else {
                    Log.d("MainActivity", "Bitmap fetched is null");
                }
            }
        }, "tom");

        new APICall().getImage(this, new APICall.ImageCallback() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap != null) {
                    bitmap_jerry = bitmap;
                    bitmap_jerry = getResizedBitmap(bitmap_jerry,300,300);
                    MyCanvasView canvasView = findViewById(R.id.mainCanvas);
                    if (canvasView != null) {
                        Log.d("MainActivity", "Setting Tom Bitmap");
                        canvasView.setJerryBitmap(bitmap_jerry);
                        canvasView.invalidate(); // Ensure the view redraws with the new bitmap
                    } else {
                        Log.d("MainActivity", "Canvas view not found!");
                    }
                } else {
                    Log.d("MainActivity", "Bitmap fetched is null");
                }
            }
        }, "jerry");
        new APICall().getImage(this, new APICall.ImageCallback() {
            @Override
            public void onResponse(Bitmap bitmap) {
                if (bitmap != null) {
                    obstacle_bitmap = getResizedBitmap(bitmap,200,200);
                    MyCanvasView canvasView = findViewById(R.id.mainCanvas);
                    if (canvasView != null) {
                        canvasView.setObstacleBitmap(obstacle_bitmap);
                        canvasView.startGame();
                    } else {
                        Log.d("MainActivity", "Canvas view not found!");
                    }
                } else {
                    Log.d("MainActivity", "Bitmap fetched is null");
                }
            }
        }, "obstacle");
    }

    public Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
    }


    public void WinDialog(String score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        MyCanvasView canvasView = findViewById(R.id.mainCanvas);

        // Inflate and set the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.win_dialog, null);
        builder.setView(dialogView);

        // Get references to the buttons in the dialog box
        TextView playAgainButton = dialogView.findViewById(R.id.playAgain);
        EditText score_display = dialogView.findViewById(R.id.score_dialog);

        // Set score in the editText
        score_display.setText("Score = " + score);


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set click listeners for the buttons
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.resetGameState();
                canvasView.startGame();
                dialog.dismiss(); // Remove the dialog when "Play Again" button is clicked
            }
        });

        // Set the background of the dialog window to transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public void normal_Dialog(String score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        MyCanvasView canvasView = findViewById(R.id.mainCanvas);

        // Inflate and set the custom layout
        View dialogView = getLayoutInflater().inflate(R.layout.normal_dialogbox, null);
        builder.setView(dialogView);

        // Get references to the buttons in the dialog box
        TextView playAgainButton = dialogView.findViewById(R.id.playAgain_lose);
        EditText score_display = dialogView.findViewById(R.id.score_dialog_lose);

        // Set score in the editText
        score_display.setText("Score = " + score);


        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Set click listeners for the buttons
        playAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.resetGameState();
                canvasView.startGame();
                dialog.dismiss(); // Remove the dialog when "Play Again" button is clicked
            }
        });

        // Set the background of the dialog window to transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }
    public static void setScoreText(String score) {
        scoreText.setText("Score = " + score);
    }
    public static void setHighscore(String highscore) {
        HighScoreText.setText("Highscore = " + highscore);
    }
}