package com.github.xzwj87.mineflea.market.ui.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.xzwj87.mineflea.R;
import com.github.xzwj87.mineflea.utils.UserPrefsUtil;

/**
 *  welcome activity to show some information to first time users
 */
public class WelcomeActivity extends AppCompatActivity {

    private static final String FIRST_TIME_VISITOR = "first_time_visitor";
    private static final int AUTO_HIDE_DELAY_MILLIS = 5000;
    private static final int COUNT_INTERVAL = 1000;
    private Button mBtnEnterHome;
    private CountDownTimer mTimer;
    private TextView mTvCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        hideStatusBar();

        mBtnEnterHome = (Button)findViewById(R.id.btn_enter_home);
        mTvCount = (TextView)findViewById(R.id.tv_count);

        mTvCount.setText(String.valueOf(AUTO_HIDE_DELAY_MILLIS/COUNT_INTERVAL));
        mTimer = new CountDownTimer(AUTO_HIDE_DELAY_MILLIS,COUNT_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvCount.setText(String.valueOf(millisUntilFinished/COUNT_INTERVAL));
            }

            @Override
            public void onFinish() {
                startHomeActivity();
                finish();
            }
        }.start();

        mBtnEnterHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimer.cancel();
                startHomeActivity();
                finish();
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();

        boolean isFirstTime = UserPrefsUtil.getBoolean(FIRST_TIME_VISITOR,true);
        if(!isFirstTime){
            UserPrefsUtil.setBooleanPref(this,FIRST_TIME_VISITOR,false);
            startHomeActivity();
            finish();
        }
    }

    private void startHomeActivity(){
        Intent intent = new Intent(WelcomeActivity.this,HomeActivity.class);
        startActivity(intent);
    }

    private void hideStatusBar(){
        View decor = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decor.setSystemUiVisibility(uiOptions);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.hide();
        }
    }

}
