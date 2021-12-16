package com.example.welfareapp;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("FCM Log", "Refreshed token: " + s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null){
            // foreground
            sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
        }
        else if(remoteMessage.getData().size()>0){
            // background
            sendNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"));
        }
    }

    // Notification Send handler
    private void sendNotification(String messageBody, String messageTitle) {
        Log.d("FCM Log", "알림 메시지: " + messageBody);

        /* 알림의 탭 작업 설정 */
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelID = "Channel ID";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        /* 알림 기능 만들기 */
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setFullScreenIntent(pendingIntent, true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        /* 새로운 인텐트로 앱 열기 */
        //Intent newIntent = new Intent(this, MainActivity.class);
        //newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity( newIntent );

        /* 채널 만들기 */
        /* Android 8.0 이상에서 알림을 게시하려면 알림을 만들어야 함 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Channel Name";
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}