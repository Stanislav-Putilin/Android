package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private Animation spawnAnimation, collapseAnimation;
    private final int N = 4;
    private final int[][] cells = new int[N][N];
    private final TextView[][] tvCells = new TextView[N][N];
    private Animation scale_2048;
    private int score, bestScore;
    private TextView tvScore, tvBestScore;

    private final Random random = new Random();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scale_2048 = AnimationUtils.loadAnimation(this, R.anim.scale_2048);
        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.game_spawn);
        collapseAnimation = AnimationUtils.loadAnimation(this, R.anim.game_collapse);

        LinearLayout gameField = findViewById(R.id.game_ll_field);
        tvScore = findViewById( R.id.game_tv_score );
        tvBestScore = findViewById( R.id.game_tv_best_score );
        findViewById(R.id.game_btn_new).setOnClickListener(this::startNewGame);

        gameField.post(() -> {
            int vw = this.getWindow().getDecorView().getWidth();
            int fieldMargin = 20;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(vw-2*fieldMargin,vw-2*fieldMargin);

            layoutParams.setMargins(fieldMargin,fieldMargin,fieldMargin,fieldMargin);
            layoutParams.gravity = Gravity.CENTER;
            gameField.setLayoutParams(layoutParams);
        });

        startNewGame();

        //NumberFormat.getInstance(Locale.ROOT).parse("1.23").doubleValue();
        gameField.setOnTouchListener(new OnSwipeListener(GameActivity.this)
        {
            @Override
            public void onSwipeLeft() {
                if(moveLeft()){
                    spawnCell();
                    showField();
                }
                else{
                    Toast.makeText(GameActivity.this, "No movement to the left", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSwipeRight() {
                if(moveRight()){
                    spawnCell();
                    showField();
                }
                else{
                    Toast.makeText(GameActivity.this, "No movement to the right", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSwipeTop()
            {
                if(moveUp()){
                    spawnCell();
                    showField();
                }
                else{
                    Toast.makeText(GameActivity.this, "No upward movement", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSwipeBottom()
            {
                if(moveDown()){
                    spawnCell();
                    showField();
                }
                else{
                    Toast.makeText(GameActivity.this, "No downward movement", Toast.LENGTH_SHORT).show();
                }
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
                            score += cells[i][j];
                            tvCells[i][j].setTag(collapseAnimation);
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
                    tvCells[i][j0].setTag(tvCells[i][j].getTag());
                    cells[i][j] = 0;
                    tvCells[i][j].setTag(null);
                    j0 += 1;
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean moveRight(){
        boolean result = false;
        for (int i = 0; i < N; i++) {
            boolean wasShift;
            do {
                wasShift = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i][j - 1] != 0) {
                        cells[i][j] = cells[i][j - 1];
                        cells[i][j - 1] = 0;
                        wasShift = result = true;
                    }
                }
            }while (wasShift);

            for (int j = N - 1; j > 0; j--) {
                if (cells[i][j] == cells[i][j - 1] && cells[i][j] != 0) {
                    cells[i][j] *= 2;
                    score += cells[i][j];
                    tvCells[i][j].setTag(collapseAnimation);

                    for (int k = j -1; k > 0; k--) {
                        cells[i][k] = cells[i][k - 1];
                    }
                    cells[i][0] = 0;
                    result = true;
                }
            }
        }

        return result;
    }

    private boolean moveUp() {
        boolean result = false;

        for (int j = 0; j < N; j++) {
            boolean wasShift;
            do {
                wasShift = false;
                for (int i = 1; i < N; i++) {
                    if (cells[i - 1][j] == 0 && cells[i][j] != 0) {
                        cells[i - 1][j] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = result = true;
                    }
                }
            } while (wasShift);

            for (int i = 1; i < N; i++) {
                if (cells[i - 1][j] == cells[i][j] && cells[i][j] != 0) {
                    cells[i - 1][j] *= 2;
                    score += cells[i - 1][j];
                    tvCells[i - 1][j].setTag(collapseAnimation);

                    for (int k = i; k < N - 1; k++) {
                        cells[k][j] = cells[k + 1][j];
                    }
                    cells[N - 1][j] = 0;
                    result = true;
                }
            }
        }

        return result;
    }

    private boolean moveDown() {
        boolean result = false;

        for (int j = 0; j < N; j++)
        {
            boolean wasShift;
            do {
                wasShift = false;
                for (int i = N - 2; i >= 0; i--) {
                    if (cells[i + 1][j] == 0 && cells[i][j] != 0) {
                        cells[i + 1][j] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = result = true;
                    }
                }
            } while (wasShift);

            for (int i = N - 2; i >= 0; i--) {
                if (cells[i + 1][j] == cells[i][j] && cells[i][j] != 0) {
                    cells[i + 1][j] *= 2;
                    score += cells[i + 1][j];
                    tvCells[i + 1][j].setTag(collapseAnimation);

                    for (int k = i; k > 0; k--) {
                        cells[k][j] = cells[k - 1][j];
                    }
                    cells[0][j] = 0;
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

        tvCells[randomCoordinates.x][randomCoordinates.y].setTag(spawnAnimation);

        return true;
    }

    private void showField(){
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {

                tvCells[i][j].setText(String.valueOf(cells[i][j]));
                tvCells[i][j].getBackground().setColorFilter(
                        getResources().getColor(
                                getResources().getIdentifier(
                                        cells[i][j] <= 2048
                                                ? "game_tile_" + cells[i][j]
                                                : "game_tile_other",
                                        "color",
                                        getPackageName()
                                ),
                        getTheme()), PorterDuff.Mode.SRC_ATOP);
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

                if(tvCells[i][j].getTag() instanceof Animation){

                    tvCells[i][j].startAnimation((Animation)tvCells[i][j].getTag());
                    tvCells[i][j].setTag(null);
                }
            }
        }

        tvScore.setText( getString( R.string.game_tv_score, String.valueOf( score ) ) );
        if( score > bestScore ) {
            bestScore = score;
            tvBestScore.startAnimation(scale_2048);
        }
        tvBestScore.setText( getString( R.string.game_tv_best, String.valueOf( bestScore ) ) );
    }

    private void startNewGame(View view)
    {
        view.startAnimation(scale_2048);
        startNewGame();
    }
    private void startNewGame()
    {
        score = 0;
        bestScore = 0;
        initField();
        spawnCell();
        showField();
    }

    static class Coordinates{
        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
        int x, y;
    }
}