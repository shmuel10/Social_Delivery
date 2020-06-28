package il.co.yedidia_shmuel.ddb_2.util;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.Nullable;

public class CheckConnectionService extends IntentService {

    private ConnectivityManager connectivityManager;
    private NetworkInfo activeNetworkInfo;

    public CheckConnectionService() {
        super("CheckConnectionService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (true){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if(activeNetworkInfo == null || !activeNetworkInfo.isConnected()){
                sendBroadcast(new Intent("no_internet_connection"));
            }
        }
    }

    @Override
    public void setIntentRedelivery(boolean enabled) {
        super.setIntentRedelivery(true);
    }
}
