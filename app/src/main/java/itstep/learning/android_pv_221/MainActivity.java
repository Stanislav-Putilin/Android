package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.main_btn_calc).setOnClickListener(this::onCalcButtonClick);
    }

    private void onCalcButtonClick(View view)
    {
        Toast.makeText(this, "Тут будет калькулятор", Toast.LENGTH_SHORT).show();
    }
}