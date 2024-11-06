package itstep.learning.android_pv_221;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;

public class AnimActivity extends AppCompatActivity {
    private Animation alphaDemo;
    private Animation scaleDemo;
    private Animation bellDemo;
    private Animation rotateDemo;

    private Animation translateDemo;
    private AnimationSet animationSet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anim);

        translateDemo = AnimationUtils.loadAnimation(this, R.anim.translate_demo);
        rotateDemo = AnimationUtils.loadAnimation(this, R.anim.rotate_demo);
        scaleDemo = AnimationUtils.loadAnimation(this, R.anim.scale_demo);
        alphaDemo = AnimationUtils.loadAnimation(this, R.anim.alpha_demo);
        bellDemo = AnimationUtils.loadAnimation(this, R.anim.bell);

        findViewById(R.id.anim_alpha).setOnClickListener(this::alphaClick);
        findViewById(R.id.anim_scale).setOnClickListener(this::scaleClick);
        findViewById(R.id.anim_rotate).setOnClickListener(this::rotateClick);
        findViewById(R.id.anim_translate).setOnClickListener(this::translateClick);
        findViewById(R.id.anim_bell).setOnClickListener(this::bellClick);

        animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleDemo);
        animationSet.addAnimation( rotateDemo);

        findViewById(R.id.anim_combo).setOnClickListener(this::comboClick);
    }

    private void rotateClick(View view)
    {
        view.startAnimation(rotateDemo);
    }

    private void alphaClick(View view)
    {
        view.startAnimation(alphaDemo);
    }

    private void scaleClick(View view)
    {
        view.startAnimation(scaleDemo);
    }
    private void translateClick(View view)
    {
        view.startAnimation(translateDemo);
    }

    private void bellClick(View view)
    {
        view.startAnimation(bellDemo);
    }

    private void comboClick(View view)
    {
        view.startAnimation(animationSet);
    }
}