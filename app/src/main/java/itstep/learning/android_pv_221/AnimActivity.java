package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimActivity extends AppCompatActivity {
    private Animation alphaDemo;
    private Animation scaleDemo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);

        scaleDemo = AnimationUtils.loadAnimation(this, R.anim.scale_demo);
        alphaDemo = AnimationUtils.loadAnimation(this, R.anim.alpha_demo);
        findViewById(R.id.anim_alpha).setOnClickListener(this::alphaClick);
        findViewById(R.id.anim_scale).setOnClickListener(this::scaleClick);
    }

    private void alphaClick(View view)
    {
        view.startAnimation(alphaDemo);
    }

    private void scaleClick(View view)
    {
        view.startAnimation(scaleDemo);
    }
}