package io.intelehealth.client.activities.introductionActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import io.intelehealth.client.R;
import io.intelehealth.client.activities.setupActivity.SetupActivity;

public class IntroductionActivity extends AppCompatActivity {

    Context context;
    Button btn_next;
    TextView tv_hi;
    TextView tv_ayu;
    TextView tv_ayu_intro;
    ImageView iv_plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction);

        //Initialize context object
        context = IntroductionActivity.this;

        //Binding layout components
        btn_next = findViewById(R.id.btn_next);
        tv_hi = findViewById(R.id.tv_hi);
        tv_ayu = findViewById(R.id.tv_ayu);
        tv_ayu_intro = findViewById(R.id.tv_ayu_intro);
        iv_plus = findViewById(R.id.iv_plus);

        animateView(tv_hi);
        //Using handlers to handle view timings
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateView(tv_ayu);
            }
        }, 2000);
        final Handler handler_ayu = new Handler();
        handler_ayu.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateView(tv_ayu_intro);
            }
        }, 4000);

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Redirecting user to Setup Activity first time
                Intent intent = new Intent(context, SetupActivity.class);
                startActivity(intent);
                finish();

            }
        });

}

    // Animate views and handle their visibility
    private void animateView(View v) {

        v.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(2000);
        fadeIn.setFillAfter(true);
        v.startAnimation(fadeIn);

    }
}
