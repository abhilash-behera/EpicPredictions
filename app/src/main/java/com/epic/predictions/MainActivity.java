package com.epic.predictions;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.ads.AbstractAdListener;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;

import io.presage.Presage;
import io.presage.common.AdConfig;
import io.presage.common.network.models.RewardItem;
import io.presage.interstitial.PresageInterstitial;
import io.presage.interstitial.PresageInterstitialCallback;
import io.presage.interstitial.optinvideo.PresageOptinVideo;
import io.presage.interstitial.optinvideo.PresageOptinVideoCallback;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private InterstitialAd interstitialAd;
    private InterstitialAd fbRepeatingInterstitial;
    private com.google.android.gms.ads.InterstitialAd admobInterstitial;
    private PresageInterstitial presageInterstitial;
    private PresageOptinVideo presageOptinVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        super.onCreate(savedInstanceState);

        Presage.getInstance().start("273986", this);
        MobileAds.initialize(this, "ca-app-pub-1591993844409076~4073068347");


        setContentView(R.layout.activity_main);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Previous Matches");

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        navigationView.getMenu().getItem(0).setChecked(true);

        if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
        else {
            PreviousFragment previous=new PreviousFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content_main, previous, "PreviousFragment").commit();
            Handler handler=new Handler();

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFbInterstitialAdOnce(getResources().getString(R.string.fb_15_sec_interstitial));
                }
            },15000);


            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showPresageInterstitialOnce();
                }
            },45000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showFbRepeatingInterstitial(getResources().getString(R.string.fb_every_60_sec_interstitial),60000);
                }
            },60000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showAdmobInterstitialOnce(getResources().getString(R.string.admob_interstitial));

                }
            },75000);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOptInVideoOnce("8fa48cd0-a455-0136-7b70-0242ac120003");
                }
            },90000);
        }
    }

    private void showFbRepeatingInterstitial(final String adId, final int repeatInterval) {
        fbRepeatingInterstitial = new InterstitialAd(this, adId);
        fbRepeatingInterstitial.setAdListener(new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                super.onError(ad, adError);
                Log.d("awesome","Main activity fb repeating interstitial ad error: "+adError.getErrorMessage());
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showFbRepeatingInterstitial(adId,repeatInterval);
                    }
                },repeatInterval);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                super.onAdLoaded(ad);
                Log.d("awesome","Main activity fb repeating interstitial ad loaded");
                if(fbRepeatingInterstitial.isAdLoaded()){
                    fbRepeatingInterstitial.show();
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFbRepeatingInterstitial(adId,repeatInterval);
                        }
                    },repeatInterval);
                }

            }

            @Override
            public void onAdClicked(Ad ad) {
                super.onAdClicked(ad);
                Log.d("awesome","Main activity fb repeating interstitial ad clicked");
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                super.onInterstitialDisplayed(ad);
                Log.d("awesome","Main activity fb repeating interstitial ad displayed");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                Log.d("awesome","Main activity fb repeating interstitial ad dismissed");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                super.onLoggingImpression(ad);
                Log.d("awesome","Main activity fb repeating interstitial ad impression");
            }
        });
        fbRepeatingInterstitial.loadAd();
    }

    private void showPresageInterstitialOnce() {
        presageInterstitial=new PresageInterstitial(this);
        presageInterstitial.setInterstitialCallback(new PresageInterstitialCallback() {
            @Override
            public void onAdAvailable() {
                Log.d("awesome","presage interstitial available");
            }

            @Override
            public void onAdNotAvailable() {
                Log.d("awesome","presage interstitial ad not available");
            }

            @Override
            public void onAdLoaded() {
                Log.d("awesome","presage interstitial ad loaded");
                if(presageInterstitial.isLoaded()){
                    presageInterstitial.show();
                }
            }

            @Override
            public void onAdNotLoaded() {
                Log.d("awesome","presage interestitial ad not loaded");
            }

            @Override
            public void onAdDisplayed() {
                Log.d("awesome","presage interestitial ad displayed");
            }

            @Override
            public void onAdClosed() {
                Log.d("awesome","presage interstitial ad closed");
            }

            @Override
            public void onAdError(int i) {
                Log.d("awesome","presage interstitial ad error: "+i);
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showPresageInterstitialOnce();
                    }
                },45000);
            }
        });
        presageInterstitial.load();
    }

    private void showFbInterstitialAdOnce(final String interstitialAdId) {
        interstitialAd = new InterstitialAd(this, interstitialAdId);
        interstitialAd.setAdListener(new AbstractAdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                super.onError(ad, adError);
                if(adError.getErrorCode()==AdError.NO_FILL_ERROR_CODE){
                    Handler handler=new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showFbInterstitialAdOnce(interstitialAdId);
                        }
                    },30000);
                }
                Log.d("awesome","Main activity interestitial ad error: "+adError.getErrorCode()+":-"+adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                super.onAdLoaded(ad);
                try{
                    interstitialAd.show();
                }catch (Exception ignored){}

                Log.d("awesome","Main activity interestitial ad loaded: "+ad);
            }

            @Override
            public void onAdClicked(Ad ad) {
                super.onAdClicked(ad);
                Log.d("awesome","Main activity interestitial ad clicked: "+ad);
            }

            @Override
            public void onInterstitialDisplayed(Ad ad) {
                super.onInterstitialDisplayed(ad);
                Log.d("awesome","Main activity interestitial ad displayed: "+ad);
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                Log.d("awesome","Main activity interestitial ad dismissed: "+ad);
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                super.onLoggingImpression(ad);
                Log.d("awesome","Main activity interestitial ad impression: "+ad);
            }
        });
        interstitialAd.loadAd();
    }

    private void showAdmobInterstitialOnce(final String adId){
        admobInterstitial=new com.google.android.gms.ads.InterstitialAd(this);
        admobInterstitial.setAdUnitId(adId);
        AdRequest adRequest=new AdRequest.Builder().build();
        admobInterstitial.loadAd(adRequest);
        admobInterstitial.setAdListener(new AdListener(){
            @Override
            public void onAdFailedToLoad(int i) {
                super.onAdFailedToLoad(i);
                Log.d("awesome","Failed to load admob interstitial: "+i);
                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showAdmobInterstitialOnce(adId);
                    }
                },75000);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d("awesome","Admob interstitial loaded");
                admobInterstitial.show();
            }
        });
    }

    private void showOptInVideoOnce(String adId) {
        AdConfig adConfig=new AdConfig(adId);
        presageOptinVideo=new PresageOptinVideo(MainActivity.this,adConfig);
        presageOptinVideo.setOptinVideoCallback(new PresageOptinVideoCallback() {
            @Override
            public void onAdRewarded(RewardItem rewardItem) {
                Log.d("awesome","presage opt in reward received");
            }

            @Override
            public void onAdAvailable() {
                Log.d("awesome","presage opt in ad available");
            }

            @Override
            public void onAdNotAvailable() {
                Log.d("awesome","presage opt in ad not available");
            }

            @Override
            public void onAdLoaded() {
                Log.d("awesome","presage opt in ad loaded");
                if(presageOptinVideo.isLoaded()){
                    presageOptinVideo.show();
                }
            }

            @Override
            public void onAdNotLoaded() {
                Log.d("awesome","presage opt in ad not loaded");
            }

            @Override
            public void onAdDisplayed() {
                Log.d("awesome","presage opt in ad displayed");
            }

            @Override
            public void onAdClosed() {
                Log.d("awesome","presage opt in ad closed");
            }

            @Override
            public void onAdError(int i) {
                Log.d("awesome","presage opt in ad error");
            }
        });
        presageOptinVideo.load();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            //Toast.makeText(getBaseContext(), "not ready",Toast.LENGTH_LONG).show();
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "This App Has Helped me win thousands on SportPesa.\n" +
                    "It gives daily SURE BETS FOR FREE.\n" +
                    "Download it here; \n" +
                    "https://play.google.com/store/apps/details?id=com.epic.predictions\n";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Epic Predictions");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share Epic Predictions app With Friends"));

            return true;
        }else if (id == R.id.action_about) {

            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {   startActivity(new Intent(MainActivity.this,AboutActivity.class));   }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.previous_matches) {
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("PreviousFragment");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else {
                    setTitle("Previous Matches");
                    PreviousFragment Previous = new PreviousFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, Previous, "PreviousFragment").commit();
                    showFbInterstitialAdOnce(getResources().getString(R.string.fb_previous_interstitial));
                }
            }
        }

        else if (id == R.id.todays_matches) {
            Fragment fragment=getSupportFragmentManager().findFragmentByTag("TodaysFragment");
            if(fragment==null){
                if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
                else {
                    setTitle("Today's Matches");
                    TodaysFragment Todays = new TodaysFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.content_main, Todays, "TodaysFragment").commit();
                    showFbInterstitialAdOnce(getResources().getString(R.string.fb_todays_interstitial));
                }
            }
        }

        else if (id == R.id.nav_rateus) {


            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {

                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }


        }
        else if (id == R.id.nav_share) {

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBodyText = "This App Has Helped me win thousands on SportPesa.\n" +
                    "It gives daily SURE BETS FOR FREE.\n" +
                    "Download it here; \n" +
                    "https://play.google.com/store/apps/details?id=com.epic.predictions\n";
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT,"Epic Predictions");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText);
            startActivity(Intent.createChooser(sharingIntent, "Share Epic Predictions app With Friends"));


        }
        else if (id == R.id.nav_about) {
            if(!isConnected(MainActivity.this)) buildDialog(MainActivity.this).show();
            else {
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                //showFbInterstitialAdOnce("342304149587187_356787844805484"); //AboutActivity interstitial
            }

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else return false;
        } else
            return false;
    }


    public AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setIcon(R.drawable.ic_error);
        builder.setTitle("Internet Connection Lost");
        builder.setMessage("You need to have Mobile Data or wifi to access this.");

        builder.setPositiveButton("REMAIN", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
                System.exit(0);
            }
        });
        return builder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(interstitialAd!=null){
//            interstitialAd.destroy();
//        }

    }
}
