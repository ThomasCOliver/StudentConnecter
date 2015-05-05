package app.mywork.testuizangle;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import java.util.Map;

public class HttpConnection {
    private final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/39.0";
    private String identifyingNumber;
    private CookyManager cookyManager;
    private String html;
    
    public String getHtml(String pin, String password, String district) throws Exception {
        cookyManager = new CookyManager();
        sendGet("https://sisweb.resa.net/StudentPortal/default.aspx?id=47010", "");
        sendGet("https://sisweb.resa.net/StudentPortal/", "");
        sendGet("https://sisweb.resa.net/StudentPortal/Home/LoadDistrictNews/News?_=1429013377451", "https://sisweb.resa.net/StudentPortal/");
        sendPost(pin, password, district);
        sendGet("https://sisweb.resa.net/StudentPortal/Home/PortalMainPage", "https://sisweb.resa.net/StudentPortal/");
        sendGet("https://sisweb.resa.net/StudentPortal/StudentBanner/SetStudentBanner/", "https://sisweb.resa.net/StudentPortal/Home/PortalMainPage");
        sendGet("https://sisweb.resa.net/StudentPortal/Home/PortalMainPage", "https://sisweb.resa.net/StudentPortal/Home/PortalMainPage");
        sendGet("https://sisweb.resa.net/StudentPortal/Home/LoadProfileData/SP_Print?_=1429060487558", "https://sisweb.resa.net/StudentPortal/Home/PortalMainPage");
        sendGet("https://sisweb.resa.net/StudentPortal/Home/LoadProfileData/Assignments^true?_=1429060840861", "https://sisweb.resa.net/StudentPortal/Home/PortalMainPage");
        return html;
    }

    private void sendGet(String url, String referer) throws Exception
    {
        if (url.equals("https://sisweb.resa.net/StudentPortal/StudentBanner/SetStudentBanner/"))
        {
            url += identifyingNumber;
        }

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setInstanceFollowRedirects(false);
        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("Host", "sisweb.resa.net");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
        con.setRequestProperty("Connection", "keep-alive");
        if (cookyManager.getCookys().length() != 0)
        {
            con.setRequestProperty("Cookie", cookyManager.getCookys());
        }
        if (referer.length() != 0)
        {
            con.setRequestProperty("Referer", referer);
        }

        //get data into buffered reader
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

        //put all header elements into a map
        Map<String, List<String>> map = con.getHeaderFields();

        //go through each header
        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (entry.getKey() != null)
            {
                //if a cookie
                if (entry.getKey().equals("Set-Cookie"))
                {
                    //add each cookie
                    for (String s : entry.getValue())
                    {
                        cookyManager.addCooky(s);
                    }
                }
            }
            //System.out.println("Key : " + entry.getKey() + ", Value : " + entry.getValue());
        }

        //magic I don't yet understand
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //where the identifying number is
        if (url.equals("https://sisweb.resa.net/StudentPortal/Home/PortalMainPage"))
        {
            int beginning = response.toString().indexOf("arrow_") + 6;
            int end = response.toString().indexOf("\"", beginning);
            identifyingNumber = response.toString().substring(beginning, end);
        }

        //put resulting html into the holder
        html = response.toString();

    }
    
    // HTTP POST request
    private void sendPost(String pin, String password, String district) throws Exception {

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
        if (cookyManager.getCookys().length() != 0)
        {
            con.setRequestProperty("Cookie", cookyManager.getCookys());
        }

        String urlParameters = "districtid=" + district + "&Pin=" + pin + "&Password=" + password;
        //String urlParameters = "districtid=47010&Pin20005361=&Password=olivert";
        
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                
        
        Map<String, List<String>> map = con.getHeaderFields();

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            if (entry.getKey() != null)
            {
                if (entry.getKey().equals("Set-Cookie"))
                {
                    for (String s : entry.getValue())
                    {
                        cookyManager.addCooky(s);
                    }
                }
            }
            //System.out.println("Key : " + entry.getKey() + ", Value : " + entry.getValue());
        }
        
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

    }
}