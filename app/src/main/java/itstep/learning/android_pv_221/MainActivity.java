package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_btn_game).setOnClickListener(this::onGameButtonClick);
        findViewById(R.id.main_btn_calc).setOnClickListener(this::onCalcButtonClick);
        findViewById(R.id.main_btn_anim).setOnClickListener(this::onAnimButton);
        findViewById(R.id.main_btn_chat).setOnClickListener(this::onChatButton);
        findViewById(R.id.main_btn_exchange).setOnClickListener(this::onExchangeButtonClick);
    }

    private void onAnimButton(View view)
    {
        Intent intent = new Intent( MainActivity.this, AnimActivity.class);
        startActivity(intent);
        //Toast.makeText(this, "Тут будет калькулятор", Toast.LENGTH_SHORT).show();
    }

    private void onChatButton(View view)
    {
        Intent intent = new Intent( MainActivity.this, ChatActivity.class);
        startActivity(intent);
        //Toast.makeText(this, "Тут будет калькулятор", Toast.LENGTH_SHORT).show();
    }

    private void onCalcButtonClick(View view)
    {
        Intent intent = new Intent( MainActivity.this, CalcActivity.class);
        startActivity(intent);
        //Toast.makeText(this, "Тут будет калькулятор", Toast.LENGTH_SHORT).show();
    }

    private void onGameButtonClick(View view)
    {
        Intent intent = new Intent( MainActivity.this, GameActivity.class);
        startActivity(intent);
        //Toast.makeText(this, "Тут будет калькулятор", Toast.LENGTH_SHORT).show();
    }

    private void onExchangeButtonClick(View view)
    {
        Intent intent = new Intent( MainActivity.this, ExchangeRateActivity.class);
        startActivity(intent);
        //Toast.makeText(this, "Тут будет калькулятор", Toast.LENGTH_SHORT).show();
    }



}