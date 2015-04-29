package app.mywork.testuizangle;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class Settings extends ActionBarActivity {

    LinearLayout checkingHowOften, dataUsage, pointsLimiter, percentLimiter;
    Spinner timingsSpinner, pointsSpinner, percentSpinner;
    TextView dataCount;
    CheckBox notifications, colors, inBrowser, reload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        checkingHowOften = (LinearLayout)findViewById(R.id.checkingHowOften);
        dataUsage = (LinearLayout)findViewById(R.id.dataUsage);
        timingsSpinner = (Spinner)findViewById(R.id.timing);
        pointsSpinner = (Spinner)findViewById(R.id.pointsSpinner);
        percentSpinner = (Spinner)findViewById(R.id.percentSpinner);
        dataCount = (TextView)findViewById(R.id.dataCount);
        notifications = (CheckBox)findViewById(R.id.notifications);
        colors = (CheckBox)findViewById(R.id.colors);
        inBrowser = (CheckBox)findViewById(R.id.openZangle);
        reload = (CheckBox)findViewById(R.id.autoReloader);
        pointsLimiter = (LinearLayout)findViewById(R.id.pointLimiter);
        percentLimiter = (LinearLayout)findViewById(R.id.percentLimiter);

        if (!StorageIO.getMultiColor(this)) {
            colors.setChecked(false);
        } else {
            colors.setChecked(true);
        }
        if (!StorageIO.getShouldOpenInBrowser(this)) {
            inBrowser.setChecked(false);
        } else {
            inBrowser.setChecked(true);
        }
        if (!StorageIO.getAutoReloadOnStart(this)) {
            reload.setChecked(false);
        } else {
            reload.setChecked(true);
        }

        long period = StorageIO.getPeriod(this);
        period /= 60000;
        long minutes = (period % 60);
        String minuteString = "";
        if (minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = minutes + "";
        }

        long hours = (period / 60);
        int selectionTiming;
        switch (hours + ":" + minuteString) {
            case "0:15":
                selectionTiming = 0;
                break;
            case "0:30":
                selectionTiming = 1;
                break;
            case "1:00":
                selectionTiming = 2;
                break;
            case "2:00":
                selectionTiming = 3;
                break;
            case "3:00":
                selectionTiming = 4;
                break;
            case "6:00":
                selectionTiming = 5;
                break;
            case "12:00":
                selectionTiming = 6;
                break;
            default:
                selectionTiming = 0;
                break;
        }
        timingsSpinner.setSelection(selectionTiming, false);

        timingsSpinner.setOnItemSelectedListener(new CheckTimingsOnItemSelectedListener());

        int selectionPoints;

        switch (StorageIO.getPointLimit(this)) {
            case 0:
                selectionPoints = 0;
                break;
            case 5:
                selectionPoints = 1;
                break;
            case 10:
                selectionPoints = 2;
                break;
            case 20:
                selectionPoints = 3;
                break;
            case 50:
                selectionPoints = 4;
                break;
            default:
                selectionPoints = 0;
                break;
        }

        pointsSpinner.setSelection(selectionPoints, false);

        pointsSpinner.setOnItemSelectedListener(new CheckPointsOnItemSelectedListener());

        int selectionPercent;

        switch ((int)(StorageIO.getPercentageLimit(this) * 10)) {
            case 6:
                selectionPercent = 0;
                break;
            case 7:
                selectionPercent = 1;
                break;
            case 8:
                selectionPercent = 2;
                break;
            case 9:
                selectionPercent = 3;
                break;
            case 10:
                selectionPercent = 4;
                break;
            default:
                selectionPercent = 0;
                break;
        }

        percentSpinner.setSelection(selectionPercent, false);

        percentSpinner.setOnItemSelectedListener(new CheckPercentageOnItemSelectedListener());


        int times = (int)(1440 / period);
        dataCount.setText(String.format("%6.1f", Math.round(10 * (times * .06 * 30)) / 10.0) + " MB");

        if (!StorageIO.getAllowNotifications(this)) {
            notifications.setChecked(false);
            changeNotifications(notifications);
        }

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A237E")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void changeNotifications(View view) {
        CheckBox checkBox = (CheckBox)view;

        if (checkBox.isChecked()) {
            killAlarm();
            checkingHowOften.setAlpha(1);
            checkingHowOften.setEnabled(true);
            pointsLimiter.setAlpha(1);
            pointsLimiter.setEnabled(true);
            timingsSpinner.setEnabled(true);
            dataUsage.setAlpha(1);
            dataUsage.setEnabled(true);
            pointsSpinner.setEnabled(true);
            percentLimiter.setEnabled(true);
            percentLimiter.setAlpha(1);
            percentSpinner.setEnabled(true);

            setUpAlarm();
            StorageIO.setAllowNotifications(true, this);
        } else {
            killAlarm();
            checkingHowOften.setAlpha(0.3f);
            checkingHowOften.setEnabled(false);
            pointsLimiter.setAlpha(0.3f);
            pointsLimiter.setEnabled(false);
            timingsSpinner.setEnabled(false);
            dataUsage.setAlpha(0.3f);
            dataUsage.setEnabled(false);
            pointsSpinner.setEnabled(false);
            percentLimiter.setEnabled(false);
            percentLimiter.setAlpha(0.3f);
            percentSpinner.setEnabled(false);
            StorageIO.setAllowNotifications(false, this);
        }

    }

    public void openInZangle(View view) {
        CheckBox browserZangle = (CheckBox)view;
        if (browserZangle.isChecked()) {
            StorageIO.setShouldOpenInBrowser(true, this);
        } else {
            StorageIO.setShouldOpenInBrowser(false, this);
        }
    }

    public void changeColors(View view) {
        CheckBox changeColor = (CheckBox)view;
        if (changeColor.isChecked()) {
            StorageIO.setMultiColor(true, this);
        } else {
            StorageIO.setMultiColor(false, this);
        }
    }

    public void autoReload(View view) {
        CheckBox autoReload = (CheckBox)view;
        if (autoReload.isChecked()) {
            StorageIO.setAutoReloadOnStart(true, this);
        } else {
            StorageIO.setAutoReloadOnStart(false, this);
        }
    }

    public void killAlarm() {
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, GetDataFromZangle.class);
        i.putExtra(GetDataFromZangle.FROM_WHERE, GetDataFromZangle.FROM_ALARM);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        mgr.cancel(pi);
        System.out.println("Alarm killed");
    }

    public void setUpAlarm() {
        AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, GetDataFromZangle.class);
        i.putExtra(GetDataFromZangle.FROM_WHERE, GetDataFromZangle.FROM_ALARM);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        int period = (int)StorageIO.getPeriod(this);
        mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()/* + period*/, period, pi);
        System.out.println("Alarm set up");
    }

    public class CheckTimingsOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                killAlarm();

                switch (position) {
                    case 0:
                        StorageIO.setPeriod(15, getApplicationContext());
                        setUpAlarm();
                        break;
                    case 1:
                        StorageIO.setPeriod(30, getApplicationContext());
                        setUpAlarm();
                        break;
                    case 2:
                        StorageIO.setPeriod(60, getApplicationContext());
                        setUpAlarm();
                        break;
                    case 3:
                        StorageIO.setPeriod(120, getApplicationContext());
                        setUpAlarm();
                        break;
                    case 4:
                        StorageIO.setPeriod(180, getApplicationContext());
                        setUpAlarm();
                        break;
                    case 5:
                        StorageIO.setPeriod(360, getApplicationContext());
                        setUpAlarm();
                        break;
                    case 6:
                        StorageIO.setPeriod(720, getApplicationContext());
                        setUpAlarm();
                        break;
                    default:
                        setUpAlarm();
                        break;
                }


                long period = StorageIO.getPeriod(getApplicationContext()) / 60000;
                int times = (int) (1440 / period);
                dataCount.setText(String.format("%6.1f", Math.round(10 * (times * .06 * 30)) / 10.0) + " MB");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        }

    public class CheckPointsOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            switch (position) {
                case 0:
                    StorageIO.setPointLimit(-10, getApplicationContext());
                    break;
                case 1:
                    StorageIO.setPointLimit(5, getApplicationContext());
                    break;
                case 2:
                    StorageIO.setPointLimit(10, getApplicationContext());
                    break;
                case 3:
                    StorageIO.setPointLimit(20, getApplicationContext());
                    break;
                case 4:
                    StorageIO.setPointLimit(50, getApplicationContext());
                    break;
                default:
                    StorageIO.setPointLimit(0, getApplicationContext());
                    break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    public class CheckPercentageOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            switch (position) {
                case 0:
                    StorageIO.setPercentageLimit(0.6, getApplicationContext());
                    break;
                case 1:
                    StorageIO.setPercentageLimit(0.7, getApplicationContext());
                    break;
                case 2:
                    StorageIO.setPercentageLimit(0.8, getApplicationContext());
                    break;
                case 3:
                    StorageIO.setPercentageLimit(0.9, getApplicationContext());
                    break;
                case 4:
                    StorageIO.setPercentageLimit(1.0, getApplicationContext());
                    break;
                default:
                    StorageIO.setPercentageLimit(0.6, getApplicationContext());
                    break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }
}
