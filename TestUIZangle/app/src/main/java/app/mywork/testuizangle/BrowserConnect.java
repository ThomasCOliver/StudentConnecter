package app.mywork.testuizangle;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class BrowserConnect extends ActionBarActivity {

    WebView wv;
    int currentLevel;
    String pin, password;
    ProgressBar pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_connect);
        currentLevel = -1;
        String[] credentials = StorageIO.readCredentials(this);
        wv = (WebView)findViewById(R.id.webBrowser);
        pBar = (ProgressBar)findViewById(R.id.pbar);
        enableJavascript();
        wv.getSettings().setBuiltInZoomControls(true);
        wv.getSettings().setDisplayZoomControls(false);
        pin = credentials[0];
        password = credentials[1];
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Seems like there was an error. Try again soon.", Toast.LENGTH_LONG).show();
            }

            //does this method after every complete load
            @Override
            public void onPageFinished(WebView view, String url)
            {
                try {
                    switch (currentLevel) {
                        case 0:
                            //put in pin and password
                            String javascript = "javascript:document.getElementById('Pin').value = '" + pin +  "';document.getElementById('Password').value = '" + password + "';document.getElementById('LoginButton').click();";
                            view.loadUrl(javascript);
                            currentLevel++;
                            break;
                        case 1:
                            //if the right credentials are put in, click on the person
                            String newJS = "javascript:document.getElementById('stuBannerTable').childNodes[1].childNodes[6].click();";
                            view.loadUrl(newJS);
                            currentLevel++;
                            break;
                        default:
                            currentLevel++;
                            break;

                    }

                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_LONG).show();
                }
                System.out.println(currentLevel);
            }
        });
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                pBar.setProgress(progress);
                if (progress == 100) {
                    pBar.setVisibility(View.GONE);
                } else {
                    pBar.setVisibility(View.VISIBLE);
                }
            }
        });

        currentLevel = 0;
        wv.loadUrl("http://sis.resa.net/StudentPortal/default.aspx?id=47010");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_browser_connect, menu);
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

    public void enableJavascript() {
        wv.clearCache(true);
        wv.clearHistory();
        wv.getSettings().setJavaScriptEnabled(true);
        wv.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wv.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/39.0");
    }
}
