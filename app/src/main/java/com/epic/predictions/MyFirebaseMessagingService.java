package com.epic.predictions;

/**
 * Created by BRIAN on 22-12-2017.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "awesome";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Log.d("awesome","image:"+remoteMessage.getData().get("image"));

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),remoteMessage.getData().get("image"));
    }

    //This method is only generating push notification
    private void sendNotification(String title, String messageBody,String imageUrl) {
        Intent intent = new Intent(this, welcomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        //new sendNotification(this).execute(title,messageBody,imageUrl);
    }

    private class sendNotification extends AsyncTask<String, Void, Bitmap> {

        Context ctx;
        String message;
        String title;

        public sendNotification(Context context) {
            super();
            this.ctx = context;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            Log.d("awesome","Got params: "+params[0]+","+params[1]+","+params[2]);
            InputStream in;
            message = params[1];
            title=params[0];
            try {

                URL url = new URL(params[2]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                Log.d("awesome","Got bitmap:"+myBitmap.getHeight()+"*"+myBitmap.getWidth());
                return myBitmap;




            } catch (MalformedURLException e) {
                Log.d("awesome","Malformed url exception");
            } catch (IOException e) {
                Log.d("awesome","IO exception");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            try {
                NotificationManager notificationManager = (NotificationManager) ctx
                        .getSystemService(Context.NOTIFICATION_SERVICE);

//                Intent intent = new Intent(ctx, MainActivity.class);
//                intent.putExtra("isFromBadge", false);


//                Notification notification = new Notification.Builder(ctx)
//                        .setContentTitle(
//                                ctx.getResources().getString(R.string.app_name))
//                        .setContentText(message)
//                        .setSmallIcon(R.drawable.icon)
//                        .setLargeIcon(result).build();
//
//                // hide the notification after its selected
//                notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//                notificationManager.notify(1, notification);

                Intent notificationIntent = new Intent(ctx, MainActivity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                PendingIntent intent =
                        PendingIntent.getActivity(ctx, 0,
                                notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new NotificationCompat.Builder(ctx)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setContentIntent(intent)
                        .setSmallIcon(R.drawable.icon)
//                        .setStyle(new NotificationCompat.BigPictureStyle()
//                                .bigPicture(result).setSummaryText(message))
                        .setLargeIcon(result)
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.maxresdefault))
                                .bigLargeIcon(null))
                        .build();

                notification.flags |= Notification.FLAG_AUTO_CANCEL;

// Play default notification sound
                notification.defaults |= Notification.DEFAULT_SOUND;
                notification.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(0, notification);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

