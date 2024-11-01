package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private final int N = 4;
    private final int[][] cells = new int[N][N];
    private final TextView[][] tvCells = new TextView[N][N];

    private final Random random = new Random();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout gameField = findViewById(R.id.game_ll_field);
        gameField.post(() -> {
            int vw = this.getWindow().getDecorView().getWidth();
            int fieldMargin = 20;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(vw-2*fieldMargin,vw-2*fieldMargin);

            layoutParams.setMargins(fieldMargin,fieldMargin,fieldMargin,fieldMargin);
            layoutParams.gravity = Gravity.CENTER;
            gameField.setLayoutParams(layoutParams);
        });

        initField();
        spawnCell();
        showField();

        //NumberFormat.getInstance(Locale.ROOT).parse("1.23").doubleValue();
        gameField.setOnTouchListener(new OnSwipeListener(GameActivity.this){


            @Override
            public void onSwipeBottom() {
                Toast.makeText(GameActivity.this, "Bott", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onSwipeLeft() {
                if(moveLeft()){
                    spawnCell();
                    showField();
                }
                else{
                    Toast.makeText(GameActivity.this, "Left", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSwipeRight() {
                Toast.makeText(GameActivity.this, "Right", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSwipeTop() {
                Toast.makeText(GameActivity.this, "Top", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean moveLeft(){
        boolean result = false;

        for (int i = 0; i < N; i++)
        {
            int j0 = -1;
            for (int j = 0; j < N; j++) {
                if(cells[i][j] != 0)
                {
                    if(j0 == -1)
                    {
                        j0 = j;
                    }
                    else {
                        if(cells[i][j] == cells[i][j0]){
                            cells[i][j] *= 2;
                            cells[i][j0] = 0;
                            result = true;
                            j0 = -1;
                        }
                        else{
                            j0 = j;
                        }
                    }
                }
            }

            j0 = -1;
            for (int j = 0; j < N; j++) {
                if(cells[i][j] == 0)
                {
                    if(j0 == -1){
                        j0=j;
                    }
                }
                else if(j0 != -1){
                    cells[i][j0] = cells[i][j];
                    cells[i][j] = 0;
                    j0 += 1;
                    result = true;
                }
            }
        }
        return result;
    }

    private void initField(){

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                cells[i][j] = 0;
                //cells[i][j] = (int)Math.pow(2, i * N + j);
                tvCells[i][j] = findViewById( getResources().getIdentifier("game_cell_" + i + j,"id",getPackageName()));
            }
        }

    }

    private boolean spawnCell()
    {
        List<Coordinates> freeCells = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(cells[i][j] == 0){
                    freeCells.add(new Coordinates(i, j));
                }
            }
        }

        if(freeCells.isEmpty()){
            return false;
        }

        Coordinates randomCoordinates = freeCells.get(random.nextInt(freeCells.size()));
        cells[randomCoordinates.x][randomCoordinates.y] = random.nextInt(10) == 0 ? 4 : 2;
        return true;
    }

    private void showField(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {

                tvCells[i][j].setText(String.valueOf(cells[i][j]));
                tvCells[i][j].setBackgroundColor(getResources().getColor(
                        getResources().getIdentifier(cells[i][j] <= 2048 ? "game_tile_" + cells[i][j] : "game_tile_other",
                                "color",
                                getPackageName()),
                        getTheme()));
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {

                tvCells[i][j].setText(String.valueOf(cells[i][j]));
                tvCells[i][j].setTextColor(getResources().getColor(
                        getResources().getIdentifier(cells[i][j] <= 2048 ? "game_text_" + cells[i][j] : "game_text_other",
                                "color",
                                getPackageName()),
                        getTheme()));
            }
        }
    }

    static class Coordinates{
        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
        int x, y;
    }
}