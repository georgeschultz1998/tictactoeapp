package com.example.tictactoe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount;
    private int player1Points;
    private int player2Points;

    private TextView textViewPlayer1;
    private TextView textViewPlayer2;


    private TextView textViewPlayer1Losses; // New TextView for Player 1 losses
    private TextView textViewPlayer2Losses; // New TextView for Player 2 losses

    private int player1Losses = 0; // New variable to keep track of Player 1 losses
    private int player2Losses = 0; // New variable to keep track of Player 2 losses

    private TextView textViewNumGames;

    private int numberOfTimesPlayed;

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";


    public void setPlayerNames(String name1, String name2) {
        player1Name = name1;
        player2Name = name2;
        updateValues();
    }

    public int getNumberOfTimesPlayed() {
        return numberOfTimesPlayed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the content view first

        textViewPlayer1 = findViewById(R.id.text_view_p1);
        textViewPlayer2 = findViewById(R.id.text_view_p2);
        textViewNumGames = findViewById(R.id.text_view_num_games); // Initialize textViewNumGames after setContentView

        SharedPreferences sharedPreferences = getSharedPreferences("gameData", MODE_PRIVATE);
        roundCount = sharedPreferences.getInt("roundCount", 0);
        player1Points = sharedPreferences.getInt("player1Points", 0);
        player2Points = sharedPreferences.getInt("player2Points", 0);
        player1Losses = sharedPreferences.getInt("player1Losses", 0);
        player2Losses = sharedPreferences.getInt("player2Losses", 0);
        player1Turn = sharedPreferences.getBoolean("player1Turn", true);
        player1Name = sharedPreferences.getString("player1Name", "Player 1");
        player2Name = sharedPreferences.getString("player2Name", "Player 2");
        numberOfTimesPlayed = sharedPreferences.getInt("numberOfTimesPlayed", 0);
        updateValues();

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this);
            }
        }

        Button buttonSetNames = findViewById(R.id.button_set_names);
        buttonSetNames.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayerNameDialog();
            }
        });

        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
    }


    private void showPlayerNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_player_names, null);
        builder.setView(dialogView);

        final EditText editTextPlayer1Name = dialogView.findViewById(R.id.edit_text_player1_name);
        final EditText editTextPlayer2Name = dialogView.findViewById(R.id.edit_text_player2_name);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String player1Name = editTextPlayer1Name.getText().toString();
                String player2Name = editTextPlayer2Name.getText().toString();
                setPlayerNames(player1Name, player2Name);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    @Override
    public void onClick(View v) {
        if (!((Button) v).getText().toString().equals("")) {
            return;
        }

        if (player1Turn) {
            ((Button) v).setText("X");
        } else {
            ((Button) v).setText("O");
        }

        roundCount++;

        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        // Check rows
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
        }

        // Check columns
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }

        // Check diagonals
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

        private void player1Wins () {
            player1Points++;
            numberOfTimesPlayed++;
            player2Losses++;
            updateValues();
            resetBoard();
        }

        private void player2Wins () {
            player2Points++;
            numberOfTimesPlayed++;
            player1Losses++;
            updateValues();
            resetBoard();
        }

        private void draw () {
            numberOfTimesPlayed++;
            updateValues();
            resetBoard();
        }

    // Update the updatePointsText() method to display player names
    private void updateValues() {
        textViewPlayer1.setText(player1Name + "(Xs) Wins: " + player1Points + " Losses: " + player1Losses);
        textViewPlayer2.setText(player2Name + "(Os) Wins: " + player2Points + " Losses: " + player2Losses);
        textViewNumGames.setText("Games Played: " + numberOfTimesPlayed);
    }
        private void resetBoard () {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    buttons[i][j].setText("");
                }
            }
            roundCount = 0;
            player1Turn = true;
        }

    private void resetGame() {
        player1Points = 0;
        player2Points = 0;
        player1Losses = 0;
        player2Losses = 0;
        updateValues();
        resetBoard();
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getSharedPreferences("gameData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("roundCount", roundCount);
        editor.putInt("player1Points", player1Points);
        editor.putInt("player2Points", player2Points);
        editor.putInt("player1Losses", player1Losses);
        editor.putInt("player2Losses", player2Losses);
        editor.putBoolean("player1Turn", player1Turn);
        editor.putString("player1Name", player1Name);
        editor.putString("player2Name", player2Name);
        editor.putInt("numberOfTimesPlayed", numberOfTimesPlayed);
        editor.apply();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("roundCount", roundCount);
        outState.putInt("player1Points", player1Points);
        outState.putInt("player2Points", player2Points);
        outState.putInt("player1Losses", player1Losses); // Save player1Losses
        outState.putInt("player2Losses", player2Losses); // Save player2Losses
        outState.putBoolean("player1Turn", player1Turn);
        // Save the new variables
        outState.putString("player1Name", player1Name);
        outState.putString("player2Name", player2Name);
        outState.putInt("numberOfTimesPlayed", numberOfTimesPlayed);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        roundCount = savedInstanceState.getInt("roundCount");
        player1Points = savedInstanceState.getInt("player1Points");
        player2Points = savedInstanceState.getInt("player2Points");
        player1Losses = savedInstanceState.getInt("player1Losses"); // Restore player1Losses
        player2Losses = savedInstanceState.getInt("player2Losses"); // Restore player2Losses
        player1Turn = savedInstanceState.getBoolean("player1Turn");
        // Restore the new variables
        player1Name = savedInstanceState.getString("player1Name");
        player2Name = savedInstanceState.getString("player2Name");
        numberOfTimesPlayed = savedInstanceState.getInt("numberOfTimesPlayed");
        textViewNumGames.setText("Games Played: " + numberOfTimesPlayed);
        // Update the points text and losses text to reflect the restored values
        updateValues();
    }



}
