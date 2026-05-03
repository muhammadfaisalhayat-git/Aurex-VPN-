package com.willdev.openvpn.view;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.willdev.openvpn.api.WillDevWebAPI;
import com.willdev.openvpn.R;
import com.startapp.sdk.adsbase.StartAppAd;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.oneconnectapi.app.api.OneConnect;

public class SplashActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_willdev_splash_screen);

        StartAppAd.disableSplash();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                OneConnect oneConnect = new OneConnect();
                                oneConnect.initialize(SplashActivity.this, "ONE_CONNECT_SDK");
                                OkHttpClient okHttpClient = new OkHttpClient();
                                Request request = new Request.Builder().url(WillDevWebAPI.ADMIN_PANEL_API+"includes/api.php?frWillServer").build();
                                Response response = okHttpClient.newCall(request).execute();
                                WillDevWebAPI.FREE_SERVERS = response.body().string();

                                request = new Request.Builder().url(WillDevWebAPI.ADMIN_PANEL_API+"includes/api.php?prWillServer").build();
                                response = okHttpClient.newCall(request).execute();
                                WillDevWebAPI.PREMIUM_SERVERS = response.body().string();

                                request = new Request.Builder().url(WillDevWebAPI.ADMIN_PANEL_API+"includes/api.php?allWillAds").build();
                                response = okHttpClient.newCall(request).execute();
                                String body = response.body().string();
                                try {
                                    JSONArray jsonArray = new JSONArray(body);
                                    for (int i=0; i < jsonArray.length();i++){
                                        JSONObject object = (JSONObject) jsonArray.get(0);
                                        WillDevWebAPI.ADMOB_ID = object.getString("admobID");
                                        WillDevWebAPI.ADMOB_BANNER = object.getString("bannerID");
                                        WillDevWebAPI.ADMOB_INTERSTITIAL = object.getString("interstitialID");
                                        WillDevWebAPI.ADMOB_REWARD_ID = object.getString("rewardID");
                                        WillDevWebAPI.ADMOB_NATIVE = object.getString("nativeID");
                                        WillDevWebAPI.ADS_TYPE = object.getString("adType");
                                    }
                                    // AdMob App ID dynamic setting removed
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } catch (IOException e) {
                                Log.v("willdev",e.toString());
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            },1000);

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }

        }.start();

    }
}
