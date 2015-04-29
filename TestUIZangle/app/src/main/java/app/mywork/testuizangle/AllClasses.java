package app.mywork.testuizangle;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class AllClasses extends ActionBarActivity implements View.OnClickListener
{
    //declare all global layouts
    LinearLayout linLay;
    LinearLayout.LayoutParams cardViewLayoutParams;
    LinearLayout.LayoutParams mainLinearLayoutParams;
    LinearLayout.LayoutParams headerLayoutParams;
    LinearLayout.LayoutParams mainGradeParams;
    LinearLayout.LayoutParams classNameParams;
    LinearLayout.LayoutParams linearAssignmentParams;
    LinearLayout.LayoutParams eachAssignmentParams;
    LinearLayout.LayoutParams assignmentNameParams;
    LinearLayout.LayoutParams assignmentModifiersParams;
    LinearLayout.LayoutParams assignmentGradeParams;
    LinearLayout.LayoutParams noAssignmentParams;
    SwipeRefreshLayout swipeLayout;
    //declare data lists
    List<ClassData> classDataList;
    List<Integer> cardIdList;


    public int currentLevel;
    WebView wv;
    String pin, password;
    WebResourceResponse wrr;
    String s;
    AssetManager assetMgr;
    ResponseReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_classes);
        getSupportActionBar().setTitle("Assignments");
        //dark indigo action bar
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A237E")));

        assignLayouts();

        //get rid of left arrow

        IntentFilter filter = new IntentFilter(ResponseReceiver.HTML_FINISHED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);


        //get objects
        //wv = new WebView(getBaseContext());

        //set up webview
        //enableHTML5AppCache();

        //get password and pin
        Intent intent = getIntent();
        pin = intent.getStringExtra("Pin");
        password = intent.getStringExtra("Password");

        //initialize
        classDataList = new ArrayList<ClassData>();
        cardIdList = new ArrayList<Integer>();

        //set up swipe refresh
        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        //on refresh, start getting data
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataFromInternet(null);
            }


        });

        //override some methods for webview
//        wv.setWebViewClient( new WebViewClient()
//        {
//            //before request sent to internet, hopefully doesn't completely send it and just gets a cached version
//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request)
//            {
//                if ((request.getUrl().toString()).indexOf(".css") != -1)      //stylings
//                {
//                    WebResourceResponse wrr = new WebResourceResponse("text/css", "UTF-8", new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
//                    return wrr;
//                }
//                else if ((request.getUrl().toString()).indexOf(".gif") != -1) //loading animation
//                {
//                    WebResourceResponse wrr = new WebResourceResponse("image/gif", "UTF-8", new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
//                    return wrr;
//
//                }
//                else if ((request.getUrl().toString()).indexOf(".png") != -1) //student connect logo
//                {
//                    WebResourceResponse wrr = new WebResourceResponse("image/png", "UTF-8", new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
//                    return wrr;
//
//                }
//                /*else if ((request.getUrl().toString()).indexOf(".jpg") != -1 && currentLevel < 2) //student logo, causes some weird error
//                {
//                    WebResourceResponse wrr = new WebResourceResponse("image/jpeg", "UTF-8", new ByteArrayInputStream("".getBytes(StandardCharsets.UTF_8)));
//                    return wrr;
//
//                }*/
//                /*else if ((request.getUrl().toString()).indexOf(".js") != -1)  //javascript, not working for because it is having an issue loading javascript (goes into app/src/main/res/assets)
//                {
//                    int found = (request.getUrl().toString()).indexOf(".js") + 3;
//                    String pathToGo = request.getUrl().toString().substring(46, found);
//
//                    try {
//                        WebResourceResponse wrr = new WebResourceResponse("application/javascript", "UTF-8", assetMgr.open(pathToGo));
//                        return wrr;
//                    }
//                    catch (Exception e)
//                    {
//                        return null;
//
//                    }
//
//                }*/
//
//                return null;
//            }
//
//
//            @Override
//            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                Toast.makeText(getApplicationContext(), "Seems like there was an error. Try again soon.", Toast.LENGTH_LONG).show();
//            }
//
//            //does this method after every complete load
//            @Override
//            public void onPageFinished(WebView view, String url)
//            {
//                try {
//                    switch (currentLevel) {
//                        case 0:
//                            //put in pin and password
//                            String javascript = "javascript:document.getElementById('Pin').value = '" + pin +  "';document.getElementById('Password').value = '" + password + "';document.getElementById('LoginButton').click();";
//                            wv.loadUrl(javascript);
//                            currentLevel++;
//                            break;
//                        case 1:
//                            //if the right credentials are put in, click on the person
//
//                            String newJS = "javascript:document.getElementById('stuBannerTable').childNodes[1].childNodes[6].click();";
//                            wv.loadUrl(newJS);
//                            currentLevel++;
//
//                            break;
//                        case 2:
//                            //create a new thread to execute HttpRequest from
//                            ExecutorService service =  Executors.newSingleThreadExecutor();
//
//                            GetFullAssignments assignmentGetter = new GetFullAssignments();
//                            //start task to get Assignment responses
//                            Future<String> assignmentHTML = service.submit(assignmentGetter);
//                            //give process 10 seconds to get a response, otherwise give an error
//                            String result = assignmentHTML.get(10L, TimeUnit.SECONDS);
//                            if (result != null)
//                            {
//                                //clear all the cards and things from the list
//                                refreshCards(result);
//                            }
//                            else
//                            {
//                                //something took too long
//                                Toast.makeText(getApplicationContext(), "Took too long", Toast.LENGTH_LONG).show();
//                                swipeLayout.setRefreshing(false);
//                            }
//                            currentLevel++;
//                            break;
//
//                    }
//
//                }
//                catch (Exception e)
//                {
//                    swipeLayout.setRefreshing(false);
//                    Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_LONG).show();
//                }
//
//            }
//
//        });

        //if a file exists, get it
        File f = new File(getFilesDir() + "/classData.enc");
        if (f.exists())
        {
            try {
                //read data
                classDataList = StorageIO.readClassData(this);//readClassData();
                refreshCards();
                //set all assignments firstTime = false
                for (int i = 0; i < classDataList.size(); i++) {
                    for (int j = 0; j < classDataList.get(i).getNumberOfAssignments(); j++) {
                        classDataList.get(i).getAssignment(j).setFirstTime(false);
                    }
                }
                StorageIO.saveClassData(classDataList, this);
                //should reload
                if (intent.hasExtra("Reload") && intent.getBooleanExtra("Reload", false)) {
                    getDataFromInternet(null);
                }
            }
            catch (Exception e)
            {
                getDataFromInternet(null);
            }
        }
        else
        {
            getDataFromInternet(null);
        }

        //get rid of left-arrow
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_classes, menu);
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

    @Override
    public void onClick(View view) {
        //get id of the thing that was clicked
        int id = view.getId();
        boolean found = false;
        int index = -1;
        //find the classData that is in the card clicked

        for (index = 0; index < cardIdList.size(); index++) {
            if (cardIdList.get(index) == id) {
                found = true;
                break;
            }
//            while (!found) {
//                index++;
//                if (cardIdList.get(index) == id) {
//                    found = true;
//                }
//            }
        }

        //if it is not a card
        if (found == false) {
            //switch grade with percentage or vice versa
            TextView v = (TextView)view;
            String buffer = v.getHint().toString();
            v.setHint(v.getText());
            v.setText(buffer);
        } else {
            //tells page to go to ClassPage.java
            Intent intent = new Intent(this, ClassPage.class);
            //allow the classData to be sent, not working
            Bundle b = new Bundle();
            b.putSerializable("DataNeeded", classDataList.get(index));
            intent.putExtras(b);

            //and now go to it
            startActivity(intent);
        }
    }

    public int dpToPixel(int dp) {
        //d = pixel density
        float d = getBaseContext().getResources().getDisplayMetrics().density;
        //convert dp to p
        int margin = (int) (dp * d);
        return margin;
    }

    public int addCard(ClassData cd)
    {

        //add new card and a new id
        CardView cv1 = new CardView(getBaseContext());
        cv1.setId(cv1.generateViewId());
        //set radius of card and allow it to be clicked
        cv1.setRadius(dpToPixel(2));
        cv1.setClickable(true);
        //think I need this for shadows
        cv1.setUseCompatPadding(true);


        //add a linear layout in card
        LinearLayout mainLinearLayout = new LinearLayout(getBaseContext());
        mainLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //add the relative layout for heading
        LinearLayout headerLinearLayout = new LinearLayout(getBaseContext());
        headerLinearLayout.setPadding(dpToPixel(16), dpToPixel(16), dpToPixel(16), dpToPixel(16));
        //green
        headerLinearLayout.setBackgroundColor(cd.getColor(this));

        //add classname
        TextView className = new TextView(getBaseContext());
        className.setGravity(Gravity.CENTER_VERTICAL);
        className.setText(cd.getClassName());
        className.setTextColor(Color.rgb(0, 0, 0));
        //set text to 24sp
        className.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        className.setAlpha(0.50f);
        className.setSingleLine(true);
        className.setEllipsize(TextUtils.TruncateAt.END);

        //add grade
        TextView grade = new TextView(getBaseContext());
        grade.setGravity(Gravity.CENTER_VERTICAL);
        grade.setGravity(Gravity.END);
        grade.setText(cd.getClassGrade());
        grade.setTextColor(Color.rgb(0, 0, 0));
        grade.setAlpha(0.50f);
        //set text to 24sp
        grade.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        grade.setSingleLine(true);


        //assignments area
        LinearLayout assignmentLinearLayout = new LinearLayout(getBaseContext());
        assignmentLinearLayout.setPadding(dpToPixel(16), dpToPixel(8), dpToPixel(16), dpToPixel(8));
        assignmentLinearLayout.setOrientation(LinearLayout.VERTICAL);

        //determine the number of assignments to show
        int numAssignmentsToShow = 0;

        numAssignmentsToShow = cd.getNumberOfAssignments();
        if (numAssignmentsToShow > 3)
        {
            numAssignmentsToShow = 3;
        }


        //if there are any assignments to show
        if (numAssignmentsToShow != 0)
        {
            for (int i = 0; i < numAssignmentsToShow; i++)
            {
                //add assignment name, w=match_parent, h=match_parent, left-justify
                TextView assignmentName = new TextView(getBaseContext());
                assignmentName.setText(cd.getAssignment(i).getAssignmentName());
                assignmentName.setTextColor(Color.rgb(0, 0, 0));
                assignmentName.setAlpha(0.50f);
                assignmentName.setSingleLine(true);
                assignmentName.setEllipsize(TextUtils.TruncateAt.END);
                assignmentName.setPadding(0, dpToPixel(8), 0, dpToPixel(8));


                //add modifiers
                TextView assignmentModifiers = new TextView(getBaseContext());
                assignmentModifiers.setText(cd.getAssignment(i).getModifiers());
                assignmentModifiers.setTextColor(Color.rgb(0, 0, 0));
                assignmentModifiers.setAlpha(0.50f);
                assignmentModifiers.setSingleLine(true);
                assignmentModifiers.setPadding(0, dpToPixel(8), 0, dpToPixel(8));
                assignmentModifiers.setTypeface(Typeface.DEFAULT_BOLD);

                //where assignments are placed
                //add assignment grade, w=match_parent, h=match_parent, right-justify
                LinearLayout eachAssignmentLinearLayout = new LinearLayout(getBaseContext());
                TextView assignmentGrade = new TextView(getBaseContext());

                assignmentGrade.setText(cd.getAssignment(i).getPointsEarned() + " /" + cd.getAssignment(i).getPointsPossible());


                assignmentGrade.setTextColor(Color.rgb(0, 0, 0));
                assignmentGrade.setAlpha(0.50f);
                assignmentGrade.setGravity(Gravity.END);
                assignmentGrade.setSingleLine(true);
                assignmentGrade.setEllipsize(TextUtils.TruncateAt.END);
                assignmentGrade.setHint(cd.getAssignment(i).getPercentage());
                assignmentGrade.setOnClickListener(this);
                //got rid of margins for the layout, made this bigger to make it easier to click
                assignmentGrade.setPadding(0, dpToPixel(8), 0, dpToPixel(8));
                assignmentGrade.setTypeface(Typeface.MONOSPACE);

                if (cd.getAssignment(i).getPointsEarned().equals(String.format("%6.1s", "")) && cd.getAssignment(i).getModifiers().equals("  ")) {
                    //no grade in and no modifiers, make it red
                    assignmentGrade.setTextColor(Color.rgb(255, 0, 0));
                    assignmentName.setTextColor(Color.rgb(255, 0, 0));
                }

                //add views in right order

                eachAssignmentLinearLayout.addView(assignmentName, assignmentNameParams);
                eachAssignmentLinearLayout.addView(assignmentModifiers, assignmentGradeParams);
                eachAssignmentLinearLayout.addView(assignmentGrade, assignmentGradeParams);

                assignmentLinearLayout.addView(eachAssignmentLinearLayout, eachAssignmentParams);
            }

        }
        else    //if now assignments to show
        {
            //add text to the center
            LinearLayout eachAssignmentLinearLayout = new LinearLayout(getBaseContext());
            TextView noAssignments = new TextView(getBaseContext());
            noAssignments.setText("No assignments to show.");
            noAssignments.setTextColor(Color.rgb(0, 0, 0));
            noAssignments.setAlpha(0.50f);
            noAssignments.setGravity(Gravity.CENTER);

            eachAssignmentLinearLayout.addView(noAssignments, noAssignmentParams);
            assignmentLinearLayout.addView(eachAssignmentLinearLayout, noAssignmentParams);
        }

        assignmentLinearLayout.setDividerDrawable(this.getResources().getDrawable(R.drawable.mydivider));
        assignmentLinearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        //put them together in the right order and with the right layout
        headerLinearLayout.addView(className, classNameParams);
        headerLinearLayout.addView(grade, mainGradeParams);

        mainLinearLayout.addView(headerLinearLayout, headerLayoutParams);
        mainLinearLayout.addView(assignmentLinearLayout, linearAssignmentParams);
        cv1.addView(mainLinearLayout, mainLinearLayoutParams);
        linLay.addView(cv1, cardViewLayoutParams);
        //do the event for onclick
        cv1.setOnClickListener(this);

        //return id to keep in a list
        return cv1.getId();
    }

    public void assignLayouts()
    {
        //get right layout params
        cardViewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardViewLayoutParams.setMargins(dpToPixel(0), dpToPixel(4), dpToPixel(0), dpToPixel(4));

        mainLinearLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        headerLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);



        classNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);

        mainGradeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0f);


        linearAssignmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        eachAssignmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //eachAssignmentParams.setMargins(dpToPixel(0), dpToPixel(8), dpToPixel(0), dpToPixel(8));

        assignmentNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        assignmentModifiersParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f);
        assignmentGradeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0f);

        noAssignmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        noAssignmentParams.setMargins(dpToPixel(0), dpToPixel(8), dpToPixel(0), dpToPixel(8));

        //get right layout
        linLay = (LinearLayout) findViewById(R.id.linLay);
    }

    public void clearCards()
    {
        //clear all data from cards
        if (classDataList != null) {
            classDataList.clear();
        }
        if (cardIdList != null) {
            cardIdList.clear();
        }
        //remove cards
        if (linLay.getChildCount() != 0) {
            linLay.removeAllViews();
        }
    }

    //if data is in form of html
    public void refreshCards(String html) throws Exception
    {
        //clear cards
        clearCards();

        //make a class list
        classDataList = ClassAnalyzer.convertHtmlToClasses(html);
        //StorageIO.saveClassData(classDataList, this);
        //saveClassData();

        //add new cards
        refreshCards();
    }

    //if all data is already in cards
    public void refreshCards() throws Exception
    {
        //add new cards
        for (ClassData cd : classDataList)
        {
            cardIdList.add(addCard(cd));
        }

        //show it is done refreshing
        swipeLayout.setRefreshing(false);
    }

//    public void savePasswordInfo() throws Exception
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
//    public void saveClassData() throws Exception
//    {
//        FileOutputStream fileOut = openFileOutput("classData.enc", Context.MODE_PRIVATE);
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream out = new ObjectOutputStream(bos);
//        try {
//            //write object to byte array output stream
//            out.writeObject(classDataList);
//            //get the data back
//            byte[] classData = bos.toByteArray();
//            //encrypt it
//            byte[] encryptedClassData = CryptUtil.encrypt(classData, "");
//            //write it to a file
//            fileOut.write(encryptedClassData);
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(), "Error saving data", Toast.LENGTH_SHORT).show();
//        }
//        finally {
//            out.close();
//            bos.close();
//            fileOut.close();
//        }
//
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
//            pin = new String(decryptedPinBytes);
//            pinStream.close();
//
//            //same as above
//            File passwordFile = new File(getFilesDir().getPath() + "/" + "password.enc");
//            FileInputStream passwordStream = new FileInputStream(passwordFile);
//            byte[] passwordBytes = new byte[(int)passwordFile.length()];
//            passwordStream.read(passwordBytes);
//            byte[] decryptedPasswordBytes = CryptUtil.decrypt(passwordBytes, "");
//            password = new String(decryptedPasswordBytes);
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
//
//    public List<ClassData> readClassData() throws Exception
//    {
//        FileInputStream classesIn = null;
//        ByteArrayInputStream bis = null;
//        ObjectInputStream in = null;
//        try {
//            //same as password and pin
//            File classDataFile = new File(getFilesDir().getPath() + "/" + "classData.enc");
//            FileInputStream classDataStream = new FileInputStream(classDataFile);
//            byte[] classDataBytes = new byte[(int)classDataFile.length()];
//            classDataStream.read(classDataBytes);
//            //decrypt bytes
//            byte[] decryptedClassDataBytes = CryptUtil.decrypt(classDataBytes, "");
//            //get the decrypted bytes into an object stream
//            bis = new ByteArrayInputStream(decryptedClassDataBytes);
//            in = new ObjectInputStream(bis);
//            //decode stream into object
//            List<ClassData> cdl = (List<ClassData>)in.readObject();
//            //close everything and return the answer
//            in.close();
//            bis.close();
//            classDataStream.close();
//            return cdl;
//        }
//        catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(), "ERROR", Toast.LENGTH_SHORT).show();
//            return null;
//        }
//
//    }
//
//
//    //get browser ready and able
//
//    private void enableHTML5AppCache() {
//        /*
//        //no idea
//        wv.getSettings().setDomStorageEnabled(true);
//
//        //enable javascript
//        wv.getSettings().setJavaScriptEnabled(true);
//
//        //use a desktop site
//        wv.getSettings().setUserAgentString("Chrome/41.0.2272.89");
//
//        //allow cache
//        wv.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
//        wv.getSettings().setAppCachePath("/data/data/"+ getPackageName() +"/cache");
//        wv.getSettings().setAllowFileAccess(true);
//        wv.getSettings().setAppCacheEnabled(true);
//        */
//    }


    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void getDataFromInternet(MenuItem item)
    {

        Intent intent = new Intent(this, GetDataFromZangle.class);
        intent.putExtra(GetDataFromZangle.FROM_WHERE, GetDataFromZangle.FROM_ALLCLASSES);
        startService(intent);

        //show that it is refreshing
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });


        //currentLevel = 0;
        //wv.loadUrl("http://sis.resa.net/StudentPortal/default.aspx?id=47010");
    }

    public void getDataFromLocal(MenuItem item) throws Exception
    {
        clearCards();

        classDataList = StorageIO.readClassData(this);

        refreshCards();
    }

    public void logout(MenuItem item) throws Exception
    {
        File passwordFile = new File(getFilesDir() + "/password.enc");
        File pinFile = new File(getFilesDir() + "/pin.enc");
        File classDataFile = new File(getFilesDir() + "/classData.enc");

        try {
            if (passwordFile.exists()) {
                passwordFile.delete();
            }
            if (pinFile.exists()) {
                pinFile.delete();
            }
            if (classDataFile.exists()) {
                classDataFile.delete();
            }

            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Error deleting files, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    public void clear(MenuItem item)
    {
        clearCards();
    }

    public void settings(MenuItem item) {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    public void browser(MenuItem item) {
        if (!StorageIO.getShouldOpenInBrowser(this)) {
            Intent i = new Intent(this, BrowserConnect.class);
            startActivity(i);
        } else {
            WebView wv = new WebView(getApplicationContext());
            wv.loadUrl("http://sis.resa.net/StudentPortal/default.aspx?id=47010");
        }
    }


    public class ResponseReceiver extends BroadcastReceiver {

        public static final String HTML_FINISHED = "finished";

        @Override
        public void onReceive(Context context, Intent intent) {
            String html = intent.getStringExtra(GetDataFromZangle.OUT_HTML);
            if (intent.getBooleanExtra(GetDataFromZangle.NETWORK_ERROR, false)) {
                Toast.makeText(context, "Network error", Toast.LENGTH_LONG).show();
            } else {
                try {
                    refreshCards(html);
                } catch (Exception e) {
                    System.out.print(e);
                }
            }

            swipeLayout.setRefreshing(false);

        }
    }


}


//
//class GetFullAssignments implements Callable<String>
//{
//    //called when it is submitted a task
//    @Override
//    public String call() throws Exception {
//        //set url to go to
//        URL obj = new URL("https://sisweb.resa.net/StudentPortal/Home/LoadProfileData/Assignments^true?_=1427074280246");
//
//        //initialize a connection
//        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
//
//        //get cookies for this site
//        CookieManager cookieManager = CookieManager.getInstance();
//        String cookies = cookieManager.getCookie("https://sis.resa.net");
//
//        //set up all the header information
//        con.setRequestProperty("Host", "sisweb.resa.net");
//        con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
//        con.setRequestProperty("Accept", "*/*");
//        con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
//        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//        con.setRequestProperty("Accept-Encoding", "gzip, deflate");
//        con.setRequestProperty("Cookie", cookies);
//        con.setRequestProperty("Connection", "keep-alive");
//        con.setRequestProperty("Referer", "https://sisweb.resa.net/StudentPortal/Home/PortalMainPage");
//
//        //get response
//        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        //read response into a string
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        //close reader
//        in.close();
//        //give back the html
//        return response.toString();
//    }
//}
