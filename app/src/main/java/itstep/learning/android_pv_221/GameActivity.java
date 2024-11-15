package itstep.learning.android_pv_221;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Stack;

public class GameActivity extends AppCompatActivity {

    private final String bestScoreFilename = "beast_score.2048";
    private Animation spawnAnimation, collapseAnimation;
    private final int N = 4;
    private final int[][] cells = new int[N][N];
    private Stack<int[][]> historyStack;
    private Stack<Integer> prevScoreStack;
    private final TextView[][] tvCells = new TextView[N][N];
    private Animation scale_2048;
    private int score, bestScore;
    private TextView tvScore, tvBestScore;
    private boolean isRecord = false;
    private final Random random = new Random();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        historyStack = new Stack<>();
        prevScoreStack = new Stack<>();
        scale_2048 = AnimationUtils.loadAnimation(this, R.anim.scale_2048);
        spawnAnimation = AnimationUtils.loadAnimation(this, R.anim.game_spawn);
        collapseAnimation = AnimationUtils.loadAnimation(this, R.anim.game_collapse);

        LinearLayout gameField = findViewById(R.id.game_ll_field);
        tvScore = findViewById( R.id.game_tv_score );
        tvBestScore = findViewById( R.id.game_tv_best_score );
        findViewById(R.id.game_btn_new).setOnClickListener(this::startNewGame);
        findViewById(R.id.game_btn_undo).setOnClickListener(v->{
            v.startAnimation(scale_2048);
            undoMove();
        });

        gameField.post(() -> {
            int vw = this.getWindow().getDecorView().getWidth();
            int fieldMargin = 20;

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(vw-2*fieldMargin,vw-2*fieldMargin);

            layoutParams.setMargins(fieldMargin,fieldMargin,fieldMargin,fieldMargin);
            layoutParams.gravity = Gravity.CENTER;
            gameField.setLayoutParams(layoutParams);
        });

        startNewGame();

        gameField.setOnTouchListener(new OnSwipeListener(GameActivity.this)
        {
            @Override
            public void onSwipeLeft() {
                if (canMoveLeft()) {
                    saveField();
                    moveLeft();
                    if(!isRecord){
                        if(checkForWin())
                        {
                            showGameWinMessage();
                        }
                    }
                    spawnCell();
                    showField();
                } else {
                    if (canMoveRight() || canMoveUp() || canMoveDown()) {
                        Toast.makeText(GameActivity.this, "No movement to the left", Toast.LENGTH_SHORT).show();
                    } else {
                        showNoMoveMessage();
                    }
                }
            }

            @Override
            public void onSwipeRight() {
                if (canMoveRight()) {
                    saveField();
                    moveRight();
                    if(!isRecord){
                        if(checkForWin())
                        {
                            showGameWinMessage();
                        }
                    }
                    spawnCell();
                    showField();
                } else {
                    if (canMoveLeft() || canMoveUp() || canMoveDown()) {
                        Toast.makeText(GameActivity.this, "No movement to the right", Toast.LENGTH_SHORT).show();
                    } else {
                        showNoMoveMessage();
                    }
                }
            }

            @Override
            public void onSwipeTop() {
                if (canMoveUp()) {
                    saveField();
                    moveUp();
                    if(!isRecord){
                        if(checkForWin())
                        {
                            showGameWinMessage();
                        }
                    }
                    spawnCell();
                    showField();
                } else {
                    if (canMoveLeft() || canMoveRight() || canMoveDown()) {
                        Toast.makeText(GameActivity.this, "No upward movement", Toast.LENGTH_SHORT).show();
                    } else {
                        showNoMoveMessage();
                    }
                }
            }

            @Override
            public void onSwipeBottom() {
                if (canMoveDown()) {
                    saveField();
                    moveDown();
                    if(!isRecord){
                        if(checkForWin())
                        {
                            showGameWinMessage();
                        }
                    }
                    spawnCell();
                    showField();
                } else {
                    if (canMoveLeft() || canMoveRight() || canMoveUp()) {
                        Toast.makeText(GameActivity.this, "No downward movement", Toast.LENGTH_SHORT).show();
                    } else {
                        showNoMoveMessage();
                    }
                }
            }
        });
    }

    private boolean canMoveLeft()
    {
        for (int i = 0; i < N; i++)
        {
            for (int j = 1; j < N; j++)
            {
                if(cells[i][j] != 0  && (cells[i][j - 1] == 0 ||  cells[i][j-1] == cells[i][j]))
                {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean canMoveRight() {
        for (int i = 0; i < N; i++) {
            for (int j = N - 2; j >= 0; j--) {
                if (cells[i][j] != 0 && (cells[i][j + 1] == 0 || cells[i][j + 1] == cells[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean canMoveUp() {
        for (int j = 0; j < N; j++) {
            for (int i = 1; i < N; i++) {
                if (cells[i][j] != 0 && (cells[i - 1][j] == 0 || cells[i - 1][j] == cells[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean canMoveDown() {
        for (int j = 0; j < N; j++) {
            for (int i = N - 2; i >= 0; i--) {
                if (cells[i][j] != 0 && (cells[i + 1][j] == 0 || cells[i + 1][j] == cells[i][j])) {
                    return true;
                }
            }
        }
        return false;
    }

    private void moveLeft(){

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
                }
            }
        }
    }

    private void moveRight(){

        for (int i = 0; i < N; i++) {
            boolean wasShift;
            do {
                wasShift = false;
                for (int j = N - 1; j > 0; j--) {
                    if (cells[i][j] == 0 && cells[i][j - 1] != 0) {
                        cells[i][j] = cells[i][j - 1];
                        cells[i][j - 1] = 0;
                        wasShift = true;
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
                }
            }
        }
    }

    private void moveUp() {


        for (int j = 0; j < N; j++) {
            boolean wasShift;
            do {
                wasShift = false;
                for (int i = 1; i < N; i++) {
                    if (cells[i - 1][j] == 0 && cells[i][j] != 0) {
                        cells[i - 1][j] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = true;
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
                }
            }
        }
    }

    private void moveDown() {

        for (int j = 0; j < N; j++)
        {
            boolean wasShift;
            do {
                wasShift = false;
                for (int i = N - 2; i >= 0; i--) {
                    if (cells[i + 1][j] == 0 && cells[i][j] != 0) {
                        cells[i + 1][j] = cells[i][j];
                        cells[i][j] = 0;
                        wasShift = true;
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
                }
            }
        }
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
            saveBestScore();
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
        prevScoreStack.clear();
        historyStack.clear();
        score = 0;
        loadBestScore();
        initField();
        spawnCell();
        showField();
    }

    private void saveBestScore(){
        try(FileOutputStream fos = openFileOutput(bestScoreFilename, Context.MODE_PRIVATE)) {
            DataOutputStream writer = new DataOutputStream(fos);
            writer.writeInt(bestScore);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            Log.e("GameActivity::saveBestScore", ex.getMessage() != null ? ex.getMessage() : "Error writing file" );
        }
    }

    private void loadBestScore(){
        try(FileInputStream fis = openFileInput(bestScoreFilename)) {
            DataInputStream read = new DataInputStream(fis);
            bestScore = read.readInt();
            read.close();
        } catch (IOException ex) {
            Log.e("GameActivity::loadBestScore", ex.getMessage() != null ? ex.getMessage() : "Error reading file" );
        }
    }



    private void changeIsRecord(){
        isRecord = true;
    }

    private boolean checkForWin() {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (cells[i][j] >= 2048) {
                    return true;
                }
            }
        }
        return false;
    }
    private void saveField()
    {
        int[][] undo = new int[N][N];

        for (int i = 0; i < N; i++) {
            System.arraycopy(cells[i],0, undo[i], 0, N);
        }

        historyStack.push(undo);
        prevScoreStack.push(score);
    }
    private void undoMove(){
        if(historyStack.isEmpty() || prevScoreStack.isEmpty()){
            showUndoMessage();
            return;
        }

        score = prevScoreStack.pop();

        int[][] undo = historyStack.pop();

        for (int i = 0; i < N; i++) {
            System.arraycopy(undo[i],0, cells[i], 0, N);
        }
        showField();
    }

    private void showGameWinMessage(){
        new AlertDialog
                .Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Победа!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Выберите желаемое действие")
                .setPositiveButton("Попытаться побить рекорд",(dlg,btn) -> changeIsRecord())
                .setNeutralButton("Начать новую игру",(dlg,btn) -> startNewGame())
                .setNegativeButton("Закрити",(dlg,btn) ->{})
                .setCancelable(false)
                .show();
    }
    private void showNoMoveMessage(){
        new AlertDialog
                .Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
                .setTitle("Нет возможности сделать ход!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Выберите желаемое действие")
                .setPositiveButton("Сделать ход назад",(dlg,btn) -> undoMove())
                .setNeutralButton("Начать новую игру",(dlg,btn) -> startNewGame())
                .setNegativeButton("Закрити",(dlg,btn) ->{})
                .setCancelable(false)
                .show();
    }
    private void showUndoMessage(){
       new AlertDialog
               .Builder(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog_Alert)
               .setTitle("Обмеження")
               .setIcon(android.R.drawable.ic_dialog_alert)
               .setMessage("Скасування ходу неможливе")
               .setPositiveButton("Підписка",(dlg,btn) ->{})
               .setNeutralButton("Закрити",(dlg,btn) ->{})
               .setNegativeButton("Вийти",(dlg,btn) ->finish())
               .setCancelable(false)
               .show();
    }
    static class Coordinates{
        public Coordinates(int x, int y) {
            this.x = x;
            this.y = y;
        }
        int x, y;
    }
}