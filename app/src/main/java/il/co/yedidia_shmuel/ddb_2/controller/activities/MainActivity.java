package il.co.yedidia_shmuel.ddb_2.controller.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import il.co.yedidia_shmuel.ddb_2.R;


public class MainActivity extends AppCompatActivity {
    private static int SPLASH_SCREEN = 3000;
    private Animation _topAnimation;
    private ImageView _image;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        _topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        _image = findViewById(R.id.logo);

        _image.setAnimation(_topAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}
