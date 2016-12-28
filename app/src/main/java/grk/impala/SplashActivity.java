package grk.impala;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import grk.impala.db.ItemTbl;
import grk.impala.util.ConstantUtil;
import grk.impala.util.PrefUtil;


public class SplashActivity extends AppCompatActivity {

    private Handler myHandler = null;
    private Runnable myRunnable = null;

    private ItemTbl itemTbl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        itemTbl = new ItemTbl(SplashActivity.this);
        if (itemTbl.itemCount() > 0) {
            itemTbl.deleteAllItem();
            PrefUtil.setSetOrder(SplashActivity.this, false);
        }

        myRunnable = new Runnable() {
            @Override
            public void run() {
                if (!PrefUtil.getLoggedIn(SplashActivity.this)) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    if (PrefUtil.getUserType(SplashActivity.this) == 2  ||
                            PrefUtil.getUserType(SplashActivity.this) == 3 || PrefUtil.getUserType(SplashActivity.this) == 4) {
                        startActivity(new Intent(SplashActivity.this, StatusActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        finish();
                    }
                }
            }
        };
        myHandler = new Handler();
        myHandler.postDelayed(myRunnable, ConstantUtil.SPLASH_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(myHandler != null)
            myHandler.removeCallbacks(myRunnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(myHandler != null)
            myHandler.removeCallbacks(myRunnable);
    }
}
