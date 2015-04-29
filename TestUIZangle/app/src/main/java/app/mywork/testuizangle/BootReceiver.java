package app.mywork.testuizangle;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class BootReceiver extends BroadcastReceiver {

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (StorageIO.getAllowNotifications(context)) {
            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent i = new Intent(context, GetDataFromZangle.class);
            i.putExtra(GetDataFromZangle.FROM_WHERE, GetDataFromZangle.FROM_ALARM);
            PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
            mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), StorageIO.getPeriod(context), pi);
            System.out.println("Alarm set up");
        } else {
            System.out.println("Notifications set to off.");
        }
    }
}
