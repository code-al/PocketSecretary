package tw.edu.ncku.iim.h34035041.pocketsecretary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String getExtra = intent.getExtras().getString("alarm");

        Intent serviceIntent = new Intent(context, RingtoneService.class);

        serviceIntent.putExtra("alarm", getExtra);

        context.startService(serviceIntent);
    }
}
