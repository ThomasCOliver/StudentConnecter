package app.mywork.testuizangle;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class CheckIdentification extends IntentService {

    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/39.0";
    private static boolean isCorrect = false;
    public static String OUTPUT = "OUTPUT";
    public static String NETWORK_ERROR = "NETWORK_ERROR";
    private static String pin, password;

    public CheckIdentification() {
        super("CheckIdentification");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        pin = intent.getStringExtra("PIN");
        password = intent.getStringExtra("PASSWORD");
        try {
            sendPost();

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra(OUTPUT, isCorrect);
            broadcastIntent.putExtra(NETWORK_ERROR, false);
            //identify the send
            broadcastIntent.setAction(Login.CheckIdReceiver.CHECK_ID);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
            this.stopSelf();
        } catch (Exception e) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra(OUTPUT, false);
            broadcastIntent.putExtra(NETWORK_ERROR, true);
            //identify the send
            broadcastIntent.setAction(Login.CheckIdReceiver.CHECK_ID);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            sendBroadcast(broadcastIntent);
            this.stopSelf();
        }
    }

    // HTTP POST request
    private static void sendPost() throws Exception {

        String url = "https://sisweb.resa.net/StudentPortal/Home/Login";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        con.setRequestProperty("Host", "sisweb.resa.net");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", "*/*");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        con.setRequestProperty("X-Requested-With", "XMLHttpsRequest");
        con.setRequestProperty("Referer", "https://sisweb.resa.net/StudentPortal/");
        con.setRequestProperty("Pragma", "no-cache");
        con.setRequestProperty("Cache-Control", "no-cache");

        String urlParameters = "districtid=47010&Pin=" + pin + "&Password=" + password;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();


        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));


        Map<String, List<String>> map = con.getHeaderFields();

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (response.toString().equals("{\"msg\":\"\",\"valid\":\"1\"}"))
            isCorrect = true;
    }

}

