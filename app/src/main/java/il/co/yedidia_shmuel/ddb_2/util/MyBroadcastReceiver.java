package il.co.yedidia_shmuel.ddb_2.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import il.co.yedidia_shmuel.ddb_2.R;


public class MyBroadcastReceiver extends BroadcastReceiver {
    private String CHANNEL_ID = "BroadcastReceiver";
    private static int packageCount = 0;

    private  NotificationCompat.Builder builder;
    private NotificationManagerCompat notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() != null){
            switch (intent.getAction()){
                case "new_package_service":
                    builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_alert_24dp)
                            .setContentTitle("DDB! new package")
                            .setContentText("You receive new package")
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_MAX);
                    notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(packageCount++, builder.build());
                    break;
                case"no_internet_connection":
                    builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_alert_24dp)
                            .setContentTitle("DDB! no internet connection")
                            .setContentText("Check your connection")
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setPriority(NotificationCompat.PRIORITY_MAX);
                    notificationManager = NotificationManagerCompat.from(context);
                    notificationManager.notify(1234, builder.build());
                    break;
            }
        }
    }
}
