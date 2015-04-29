package app.mywork.testuizangle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class CheckIdReceiver extends BroadcastReceiver {

    public static String CHECK_ID = "checkMe";

    public CheckIdReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isGood = intent.getBooleanExtra(CheckIdentification.OUTPUT, false);
        if (isGood) {
            Toast.makeText(context, "Correct input", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Bad input", Toast.LENGTH_LONG).show();
        }

    }
}
