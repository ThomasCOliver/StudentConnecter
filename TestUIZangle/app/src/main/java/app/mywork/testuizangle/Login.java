package app.mywork.testuizangle;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class Login extends ActionBarActivity {

    EditText pinText, passwordText;
    CheckIdReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A237E")));

        File f1 = new File(getFilesDir() + "/hasOpened.txt");

        if (!f1.exists()) {

            try {
                //make the file so that it does not do this again
                f1.createNewFile();
                //first time opened, kill any leftovers and start up service
                AlarmManager mgr = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(this, GetDataFromZangle.class);
                i.putExtra(GetDataFromZangle.FROM_WHERE, GetDataFromZangle.FROM_ALARM);
                PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
                mgr.cancel(pi);
                System.out.println("Alarm killed");
                System.out.println("Alarm set up");
                mgr.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), StorageIO.getPeriod(this), pi);
            } catch (Exception e) {
                Toast.makeText(this, "Error with service.", Toast.LENGTH_LONG).show();
            }

        }

        //cancel my notification
        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(1415926535);

        //get rid of left-arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        try {
            File f = new File(getFilesDir().getPath() + "/" + "password.enc");
            if (f.exists()) {
                String[] credentials = StorageIO.readCredentials(this);
                //go to next page
                //tells page to go to AllClasses.java
                Intent intent = new Intent(this, AllClasses.class);
                intent.putExtra("Pin", credentials[0]);
                intent.putExtra("Password", credentials[1]);
                intent.putExtra("Reload", StorageIO.getAutoReloadOnStart(this));
                //and now go to it
                startActivity(intent);
            } else {
                pinText = (EditText) findViewById(R.id.pin);
                passwordText = (EditText) findViewById(R.id.password);

                IntentFilter filter;
                filter = new IntentFilter(CheckIdReceiver.CHECK_ID);
                filter.addCategory(Intent.CATEGORY_DEFAULT);

                receiver = new CheckIdReceiver();
                registerReceiver(receiver, filter);

                passwordText.setOnKeyListener(new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        // If the event is a key-down event on the "enter" button
                        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                            login(null);
                        }
                        return false;
                    }
                });

            }
        }
        catch (Exception e)
        {
            pinText = (EditText) findViewById(R.id.pin);
            passwordText = (EditText) findViewById(R.id.password);

            IntentFilter filter;
            filter = new IntentFilter(CheckIdReceiver.CHECK_ID);
            filter.addCategory(Intent.CATEGORY_DEFAULT);

            receiver = new CheckIdReceiver();
            registerReceiver(receiver, filter);

            passwordText.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        login(null);
                    }
                    return false;
                }
            });
        }



    }

    @Override
    public void onDestroy() {
        try {
            this.unregisterReceiver(receiver);
        } catch (Exception e) {

        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_login, menu);
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


    public void login(View view)
    {
        Intent i = new Intent(this, CheckIdentification.class);
        i.putExtra("PIN", pinText.getText().toString());
        i.putExtra("PASSWORD", passwordText.getText().toString());
        i.putExtra("DISTRICT", "47010");
        startService(i);
//        try {
//            if (pinText.getText().toString().length() == 8 && passwordText.getText().toString().length() != 0) {
//                //put in string, save them
//                savePasswordInfo(pinText.getText().toString(), passwordText.getText().toString());
//
//                //go to next page
//                //tells page to go to AllClasses.java
//                Intent intent = new Intent(this, AllClasses.class);
//                intent.putExtra("Pin", pinText.getText().toString());
//                intent.putExtra("Password", passwordText.getText().toString());
//                //and now go to it
//                startActivity(intent);
//
//            } else {
//                Toast.makeText(getApplicationContext(), "Incorrect format of credentials", Toast.LENGTH_SHORT).show();
//            }
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(), "Error saving data or sending it to next page.", Toast.LENGTH_SHORT).show();
//        }

    }

//    public void savePasswordInfo(String pin, String password) throws Exception
//    {
//        byte[] pinBytes = CryptUtil.encrypt(pin.getBytes(), "");
//        byte[] passwordBytes = CryptUtil.encrypt(password.getBytes(), "");
//
//        FileOutputStream pinFile = openFileOutput("pin.enc", Context.MODE_PRIVATE);
//        try {
//            pinFile.write(pinBytes);
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(), "Error saving data", Toast.LENGTH_SHORT).show();
//        }
//        finally {
//            pinFile.close();
//        }
//
//        FileOutputStream passwordFile = openFileOutput("password.enc", Context.MODE_PRIVATE);
//        try {
//            passwordFile.write(passwordBytes);
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(), "Error saving data", Toast.LENGTH_SHORT).show();
//        }
//        finally {
//            passwordFile.close();
//        }
//
//    }
//
//    public String[] readPasswordInfo() throws Exception
//    {
//        try {
//            //get file
//            File pinFile = new File(getFilesDir().getPath() + "/" + "pin.enc");
//            //get stream from file
//            FileInputStream pinStream = new FileInputStream(pinFile);
//            //get bytes from stream
//            byte[] pinBytes = new byte[(int)pinFile.length()];
//            pinStream.read(pinBytes);
//            //decrypt bytes
//            byte[] decryptedPinBytes = CryptUtil.decrypt(pinBytes, "");
//            //convert bytes to string, close stream
//            String pin = new String(decryptedPinBytes);
//            pinStream.close();
//
//            //same as above
//            File passwordFile = new File(getFilesDir().getPath() + "/" + "password.enc");
//            FileInputStream passwordStream = new FileInputStream(passwordFile);
//            byte[] passwordBytes = new byte[(int)passwordFile.length()];
//            passwordStream.read(passwordBytes);
//            byte[] decryptedPasswordBytes = CryptUtil.decrypt(passwordBytes, "");
//            String password = new String(decryptedPasswordBytes);
//            passwordStream.close();
//
//            //put the pin and password into an array to send back
//            String[] results = {pin, password};
//
//            return results;
//        }
//        catch (Exception e)
//        {
//
//        }
//        return null;
//    }

    public class CheckIdReceiver extends BroadcastReceiver {

        public final static String CHECK_ID = "checkMe";

        @Override
        public void onReceive(Context context, Intent intent) {

            boolean isGood = intent.getBooleanExtra(CheckIdentification.OUTPUT, false);
            boolean isNetworkError = intent.getBooleanExtra(CheckIdentification.NETWORK_ERROR, false);

            if (isGood) {

                //put in string, save them
                try {
                    StorageIO.saveCredentials(pinText.getText().toString(), passwordText.getText().toString(), getApplicationContext());
                    StorageIO.setDistrict("47010", getApplicationContext());
                    //savePasswordInfo(pinText.getText().toString(), passwordText.getText().toString());
                } catch (Exception e) {
                }

                //go to next page
                //tells page to go to AllClasses.java
                Intent newIntent = new Intent(getApplicationContext(), AllClasses.class);
                newIntent.putExtra("Pin", pinText.getText().toString());
                newIntent.putExtra("Password", passwordText.getText().toString());
                //and now go to it
                startActivity(newIntent);

            } else {
                if (isNetworkError) {
                    Toast.makeText(context, "Networking error. Please try again.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(context, "Incorrect pin or password. Please try again.", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

}
