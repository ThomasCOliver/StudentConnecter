package app.mywork.testuizangle;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.io.File;
import java.util.List;

public class GetDataFromZangle extends IntentService {

    int currentLevel;
    public static final String OUT_HTML = "OUT";
    public static final String NETWORK_ERROR = "error";
    public static final String FROM_WHERE = "whereFrom";
    public static final String FROM_ALARM = "alarm";
    public static final String FROM_ALLCLASSES = "whereFrom";

    public GetDataFromZangle() {
        super("GetDataFromZangle");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        System.out.println("I ran");

        File f1 = new File(getFilesDir() + "/password.enc");
        File f2 = new File(getFilesDir() + "/pin.enc");

        //if there is no password, quit
        if (!f1.exists() ||!f2.exists()) {
            System.out.println("Not logged in");
            this.stopSelf();
        }
        else {

            HttpConnection connection = new HttpConnection();
            try {
                //get credentials and then get HTML from it
                String[] credentials = StorageIO.readCredentials(this);
                String response = connection.getHtml(credentials[0], credentials[1]);


                File f = new File(getFilesDir().getPath() + "/" + "classData.enc");
                List<ClassData> newCdl = ClassAnalyzer.convertHtmlToClasses(response);

                //if there is a reason to compare and there is something to compare to
                //if (f.exists() && intent.getStringExtra(FROM_WHERE).equals(FROM_ALARM)) {
                if (f.exists() && intent.getStringExtra(FROM_WHERE).equals(FROM_ALARM)) {
                    //get old data to compare to
                    List<ClassData> cdl = StorageIO.readClassData(this);
                    System.out.println("Comparing...");

                    //if there has been a change and the request came from the alarm
                    List<ClassData> classesToNotify = ClassData.toNotify(cdl, newCdl, this);
                    if (classesToNotify != null) {
                        System.out.println("Change and came from alarm");
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle("Teacher Updated Grade").setContentText("An assignment has been added or deleted.").setSmallIcon(R.drawable.ic_launcher);

                        // Creates an explicit intent for an Activity in your app
                        Intent resultIntent = new Intent(this, Login.class);

                        // The stack builder object will contain an artificial back stack for the
                        // started Activity.
                        // This ensures that navigating backward from the Activity leads out of
                        // your application to the Home screen.
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                        // Adds the back stack for the Intent (but not the Intent itself)
                        stackBuilder.addParentStack(Login.class);
                        // Adds the Intent that starts the Activity to the top of the stack
                        stackBuilder.addNextIntent(resultIntent);
                        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);

                        // Sets an ID for the notification
                        int mNotificationId = 1415926535;
                        // Gets an instance of the NotificationManager service
                        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        // Builds the notification and issues it.
                        mNotifyMgr.notify(mNotificationId, mBuilder.build());
                    }
                } else if (intent.getStringExtra(FROM_WHERE).equals(FROM_ALLCLASSES)) {
                    //set all assignments firstTime = false
                    for (int i = 0; i < newCdl.size(); i++) {
                        for (int j = 0; j < newCdl.get(i).getNumberOfAssignments(); j++) {
                            newCdl.get(i).getAssignment(j).setFirstTime(false);
                        }
                    }
                    //need to send back the info
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.putExtra(OUT_HTML, response);
                    broadcastIntent.putExtra(NETWORK_ERROR, false);
                    broadcastIntent.setAction(AllClasses.ResponseReceiver.HTML_FINISHED);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    sendBroadcast(broadcastIntent);
                }

                //save data
                StorageIO.saveClassData(newCdl, this);

                this.stopSelf();
            } catch (Exception e) {
                //likely a network error
                System.out.println(e);
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra(OUT_HTML, "");
                broadcastIntent.putExtra(NETWORK_ERROR, true);
                broadcastIntent.setAction(AllClasses.ResponseReceiver.HTML_FINISHED);
                broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                sendBroadcast(broadcastIntent);

                this.stopSelf();
            }
        }
    }
}
