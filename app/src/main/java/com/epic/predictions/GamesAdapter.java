package com.epic.predictions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by abhilash on 4/3/18
 */

public class GamesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Game> games;
    private static final int GAME_VIEW=1;
    private static final int ADMOB_VIEW=2;
    private static final int FB_AD_VIEW=3;
    private Context context;
    private ArrayList<String> nativeAdIds;
    private int index=0;
    private String fbBannerAdId;

    public GamesAdapter(Context context,ArrayList<Game> games,ArrayList<String> fbNativeAdIds,String fbBannerId){
        this.games=games;
        this.context=context;
        this.nativeAdIds=fbNativeAdIds;
        this.fbBannerAdId=fbBannerId;
    }

    public class GameViewHolder extends RecyclerView.ViewHolder{
        public TextView txtLeague;
        public TextView txtDate;
        public TextView txtTeams;
        public TextView txtTime;
        public TextView txtPrediction;
        public TextView txtOdd;
        public TextView txtCount;
        public RelativeLayout adRelativeLayout;
        public GameViewHolder(View view){
            super(view);
            txtLeague=view.findViewById(R.id.txtLeague);
            txtDate=view.findViewById(R.id.txtDate);
            txtTeams=view.findViewById(R.id.txtTeams);
            txtTime=view.findViewById(R.id.txtTime);
            txtPrediction=view.findViewById(R.id.txtPrediction);
            txtOdd=view.findViewById(R.id.txtOdd);
            txtCount=view.findViewById(R.id.txtCount);
            adRelativeLayout =view.findViewById(R.id.adRelativeLayout);
        }
    }

    public class AdmobViewHolder extends RecyclerView.ViewHolder{
        public com.google.android.gms.ads.AdView adView;
        public AdmobViewHolder(final View itemView){
            super(itemView);
            adView=itemView.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdListener(new com.google.android.gms.ads.AdListener(){
                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Log.d("awesome","Games adapter admob banner clicked");
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Log.d("awesome","Games adapter admob banner closed");
                }

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                    Log.d("awesome","Games adapter admob banner error: "+i);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    Log.d("awesome","Games adapter admob banner loaded");
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    Log.d("awesome","Games adapter admob banner opened");
                }

                @Override
                public void onAdImpression() {
                    super.onAdImpression();
                    Log.d("awesome","Games adapter admob banner impression");
                }
            });
            adView.loadAd(adRequest);
        }
    }

    public class FbAdViewHolder extends RecyclerView.ViewHolder{
        public FbAdViewHolder(final View itemView){
            super(itemView);
            /*int random= new Random().nextInt(3);
            Log.d("awesome","Random number: "+random);*/
            if(index==3){
                index=0;
            }
            Log.d("awesome","index: "+index);
            String nativeAdId=nativeAdIds.get(index++);

            final NativeAd nativeAd=new NativeAd(context,nativeAdId);
            nativeAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    Log.d("awesomme","Games fb native ad media downloaded");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.d("awesome","Games fb native ad error:"+adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d("awesome","Games fb native ad loaded: "+ad);
                    nativeAd.unregisterView();

                    // Add the Ad view into the ad container.
                    LinearLayout nativeAdLayout=(LinearLayout)LayoutInflater.from(context).inflate(R.layout.native_ad_layout,(LinearLayout)itemView,false);
                    nativeAdLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    ((LinearLayout)itemView).addView(nativeAdLayout);


                    // Add the AdChoices icon
                    LinearLayout adChoicesContainer = (LinearLayout)nativeAdLayout.findViewById(R.id.ad_choices_container);
                    AdChoicesView adChoicesView = new AdChoicesView(context, nativeAd, true);
                    adChoicesContainer.addView(adChoicesView, 0);

                    // Create native UI using the ad metadata.
                    AdIconView nativeAdIcon = nativeAdLayout.findViewById(R.id.native_ad_icon);
                    TextView nativeAdTitle = nativeAdLayout.findViewById(R.id.native_ad_title);
                    MediaView nativeAdMedia = nativeAdLayout.findViewById(R.id.native_ad_media);
                    TextView nativeAdSocialContext = nativeAdLayout.findViewById(R.id.native_ad_social_context);
                    TextView nativeAdBody = nativeAdLayout.findViewById(R.id.native_ad_body);
                    TextView sponsoredLabel = nativeAdLayout.findViewById(R.id.sponsored_label);
                    Button nativeAdCallToAction = nativeAdLayout.findViewById(R.id.native_ad_call_to_action);

                    // Set the Text.
                    nativeAdTitle.setText(nativeAd.getAdvertiserName());
                    nativeAdBody.setText(nativeAd.getAdBodyText());
                    nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                    nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                    nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                    sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                    // Create a list of clickable views
                    List<View> clickableViews = new ArrayList<>();
                    clickableViews.add(nativeAdTitle);
                    clickableViews.add(nativeAdCallToAction);

                    // Register the Title and CTA button to listen for clicks.
                    nativeAd.registerViewForInteraction(
                            nativeAdLayout,
                            nativeAdMedia,
                            nativeAdIcon,
                            clickableViews);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.d("awesome","Games fb native ad clicked");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.d("awesome","Games fb native ad logging impression");
                }
            });
            nativeAd.loadAd();
        }
    }

    @Override
    public int getItemCount() {
        if(games.size()<2){
            return games.size();
        }else if(games.size()==2){
            return games.size()+1;
        }else{
            return games.size()+3;
        }
    }

    @Override
    public int getItemViewType(int position) {

        switch (position){
            case 1:
                return ADMOB_VIEW;
            case 3:
                return ADMOB_VIEW;
            case 5:
                return ADMOB_VIEW;
            default:
                return GAME_VIEW;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case GAME_VIEW:
                View gameView=LayoutInflater.from(parent.getContext()).inflate(R.layout.game_row_view,null);
                return new GameViewHolder(gameView);
            case FB_AD_VIEW:
                View fbAdView=LayoutInflater
                        .from(parent.getContext())
                        .inflate(R.layout.native_ad_container,null)
                        .findViewById(R.id.native_ad_container);
                return new FbAdViewHolder(fbAdView);
            case ADMOB_VIEW:
                View admobAdView=LayoutInflater
                        .from(context)
                        .inflate(R.layout.admob_ad_layout,null);
                return new AdmobViewHolder(admobAdView);
            default:
                Log.d("awesome","Default view");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof GameViewHolder)) {
            return;
        }
        if(position==2){
            position=position-1;
        }else if(position==4){
            position=position-2;
        }else if(position>=6){
            position=position-3;
        }

        if(position<games.size()){
            Game game=games.get(position);
            ((GameViewHolder) holder).txtLeague.setText(game.getLeague());
            ((GameViewHolder) holder).txtTeams.setText(game.getTeamA()+"\nvs\n"+game.getTeamB());
            try{
                Date date=new SimpleDateFormat("yyyy-mm-dd").parse(game.getDate());
                SimpleDateFormat newFormat=new SimpleDateFormat("dd-mm-yyyy");
                ((GameViewHolder) holder).txtDate.setText(newFormat.format(date));
            }catch (Exception e){
                Log.d("awesome","Error in parsing date: "+e.toString());
                ((GameViewHolder) holder).txtDate.setText(game.getDate());
            }
            ((GameViewHolder) holder).txtPrediction.setText(game.getTip());
            ((GameViewHolder) holder).txtOdd.setText(game.getOdds());
            ((GameViewHolder) holder).txtCount.setText(String.valueOf(game.getCount()));

            if(game.getStatus().equalsIgnoreCase("won")){
                ((GameViewHolder) holder).txtTime.setText(game.getTeamAscore()+" - "+game.getTeamBscore());
                ((GameViewHolder) holder).txtTime.setBackground(context.getResources().getDrawable(R.drawable.frame_background_green));
            }else if(game.getStatus().equalsIgnoreCase("lost")){
                ((GameViewHolder) holder).txtTime.setText(game.getTeamAscore()+" - "+game.getTeamBscore());
                ((GameViewHolder) holder).txtTime.setBackground(context.getResources().getDrawable(R.drawable.frame_background));
            }else if(game.getStatus().equalsIgnoreCase("upcoming")){
                ((GameViewHolder) holder).txtTime.setText(game.getTime());
                ((GameViewHolder) holder).txtTime.setBackground(context.getResources().getDrawable(R.drawable.frame_background));
            }

            final RelativeLayout adViewContainer=((GameViewHolder)holder).adRelativeLayout;
            final AdView adView=new AdView(context,fbBannerAdId,AdSize.BANNER_HEIGHT_50);
            adViewContainer.addView(adView);
            adView.setAdListener(new AdListener() {
                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.d("awesome","Games adapter fbBannerAd error:"+adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    Log.d("awesome","Games adapter fbBannerAd loaded:"+ad);
                }

                @Override
                public void onAdClicked(Ad ad) {
                    Log.d("awesome","Games adapter fbBannerAd clicked");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    Log.d("awesome","Games adapter fbBannerAd impression");
                }
            });
            adView.loadAd();

//            final AdView adView=new AdView(context);
//            adView.setAdSize(AdSize.SMART_BANNER);

            /*String fragment_tag=((MainActivity)context).getSupportFragmentManager().getFragments().get(0).getTag();
            Log.d("awesome","fragment_tag="+fragment_tag);
            if(fragment_tag.equalsIgnoreCase("PreviousFragment")){
                Log.d("awesome","Setting ad id for PreviousFragment");
                adView.setAdUnitId(context.getResources().getString(R.string.admob_previous));
            }
            else if(fragment_tag.equalsIgnoreCase("TodaysFragment")){
                adView.setAdUnitId(context.getResources().getString(R.string.admob_todays));
            }*/

//            adView.setAdUnitId(context.getResources().getString(R.string.sep_banner_admob));
//
//            ((GameViewHolder) holder).adRelativeLayout.addView(adView);
//
//            AdRequest adRequest = new AdRequest.Builder().addTestDevice("493C01A0BB55FCED482A594F8E4023E0").build();
//            adView.loadAd(adRequest);
//
//            adView.setAdListener(new com.google.android.gms.ads.AdListener(){
//                @Override
//                public void onAdLoaded() {
//                    super.onAdLoaded();
//                    Log.d("awesome","Smart banner loaded");
//                    Log.d("awesome","Relative layout :"+((GameViewHolder) holder).adRelativeLayout.getLayoutParams().width+"*"+((GameViewHolder) holder).adRelativeLayout.getLayoutParams().height);
//                }
//
//                @Override
//                public void onAdFailedToLoad(int i) {
//                    super.onAdFailedToLoad(i);
//                    Log.d("awesome","Error in loading smart banners: "+i);
//                }
//            });

        }else{
            Log.d("awesome","index out of bounds");
        }
    }
}
