package app.mywork.testuizangle;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class ClassPage extends ActionBarActivity implements View.OnClickListener {

    //declare all layouts
    LinearLayout.LayoutParams assignmentNameParams;
    LinearLayout.LayoutParams assignmentModifiersParams;
    LinearLayout.LayoutParams assignmentGradeParams;
    LinearLayout.LayoutParams eachAssignmentParams;
    LinearLayout.LayoutParams eachWholeAssignmentParams;
    LinearLayout.LayoutParams noAssignmentParams;
    LinearLayout.LayoutParams eachAssignmentExtraParams;
    ClipboardManager clipboard;

    //declare data for the class
    ClassData cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_page);

        //set up clipboard
        clipboard = (ClipboardManager)getSystemService(Context.CLIPBOARD_SERVICE);
        //get data from what sent it here
        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        //deserialize the data
        cd = (ClassData)b.getSerializable("DataNeeded");
        /*cd = new ClassData("AP LANG", "E");
        cd.addAssignment(new Assignment("03/20/2015", "03/20/2015","Unit 16 Quiz", 30, 30, "", false, false, ""));
        cd.addAssignment(new Assignment("03/21/2015", "03/21/2015","Unit 17 Quiz", 40, 40, "", false, false, ""));
        cd.addAssignment(new Assignment("03/21/2015", "03/21/2015","Unit 17 Quiz", 40, 40, "", false, false, ""));*/

        //set up name and color of actionbar
        getSupportActionBar().setTitle(cd.getClassName());
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(cd.getColor()));

        assignLayouts();
        addData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_class_page, menu);
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

    public void addData() {
        //get the textviews containing the grade and name of the class
        TextView mainGrade = (TextView)findViewById(R.id.mainGrade);
        //TextView className = (TextView)findViewById(R.id.className);
        mainGrade.setText(cd.getClassGrade());

        //no longer needed, now says "Assignments" here
        //className.setText(cd.getClassName());

        //get header and set its color
        RelativeLayout header = (RelativeLayout)findViewById(R.id.wholeHeader);
        header.setBackgroundColor(cd.getColor(this));

        LinearLayout assignmentLinearLayout = (LinearLayout) findViewById(R.id.assignmentsArea);
        int numAssignmentsToShow = cd.getNumberOfAssignments();

        //if there are any assignments to show
        if (numAssignmentsToShow != 0) {
            for (int i = 0; i < numAssignmentsToShow; i++) {
                //add assignment name, w=match_parent, h=match_parent, left-justify
                TextView assignmentName = new TextView(getBaseContext());
                assignmentName.setText(cd.getAssignment(i).getAssignmentName());
                assignmentName.setTextColor(Color.rgb(0, 0, 0));
                assignmentName.setAlpha(0.70f);
                assignmentName.setSingleLine(true);
                assignmentName.setEllipsize(TextUtils.TruncateAt.END);
                assignmentName.setPadding(0, dpToPixel(8), 0, dpToPixel(8));
                assignmentName.setOnLongClickListener(new LongClick());


                //add modifiers
                TextView assignmentModifiers = new TextView(getBaseContext());
                assignmentModifiers.setText(cd.getAssignment(i).getModifiers());
                assignmentModifiers.setTextColor(Color.rgb(0, 0, 0));
                assignmentModifiers.setAlpha(0.70f);
                assignmentModifiers.setSingleLine(true);
                assignmentModifiers.setPadding(0, dpToPixel(8), 0, dpToPixel(8));
                assignmentModifiers.setTypeface(Typeface.DEFAULT_BOLD);

                //where assignments are placed
                //add assignment grade, w=match_parent, h=match_parent, right-justify
                LinearLayout eachAssignmentLinearLayout = new LinearLayout(getBaseContext());
                //next three lines are new - 4/29/15
                LinearLayout eachWholeAssignmentLinearLayout = new LinearLayout(getBaseContext());
                eachWholeAssignmentLinearLayout.setOrientation(LinearLayout.VERTICAL);
                eachWholeAssignmentLinearLayout.setOnClickListener(this);
                eachWholeAssignmentLinearLayout.setTag(i);

                TextView assignmentGrade = new TextView(getBaseContext());

                assignmentGrade.setText(cd.getAssignment(i).getPointsEarned() + " /" + cd.getAssignment(i).getPointsPossible());

                assignmentGrade.setTextColor(Color.rgb(0, 0, 0));
                assignmentGrade.setAlpha(0.70f);
                assignmentGrade.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
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

                TextView restOfData = new TextView(this);
                restOfData.setText(cd.getAssignment(i).getAllExtras());
                restOfData.setAlpha(1.0f);
                restOfData.setVisibility(View.GONE);
                restOfData.setSingleLine(false);

                //add views in right order

                eachAssignmentLinearLayout.addView(assignmentName, assignmentNameParams);
                eachAssignmentLinearLayout.addView(assignmentModifiers, assignmentGradeParams);
                eachAssignmentLinearLayout.addView(assignmentGrade, assignmentGradeParams);

                eachWholeAssignmentLinearLayout.addView(eachAssignmentLinearLayout, eachAssignmentParams);
                eachWholeAssignmentLinearLayout.addView(restOfData, eachAssignmentExtraParams);
                assignmentLinearLayout.addView(eachWholeAssignmentLinearLayout, eachWholeAssignmentParams);

            }

        } else    //if now assignments to show
        {
            //add text to the center
            LinearLayout eachAssignmentLinearLayout = new LinearLayout(getBaseContext());
            TextView noAssignments = new TextView(getBaseContext());
            noAssignments.setText("No assignments to show.");
            noAssignments.setTextColor(Color.rgb(0, 0, 0));
            noAssignments.setAlpha(0.80f);
            noAssignments.setGravity(Gravity.CENTER);

            eachAssignmentLinearLayout.addView(noAssignments, noAssignmentParams);
            assignmentLinearLayout.addView(eachAssignmentLinearLayout, noAssignmentParams);
        }

        RelativeLayout classDataHeader = (RelativeLayout)findViewById(R.id.classDataLabel);
        classDataHeader.setBackgroundColor(cd.getColor(this));

        TextView teacher = (TextView)findViewById(R.id.teacher);
        TextView email = (TextView)findViewById(R.id.email);
        TextView website = (TextView)findViewById(R.id.website);

        teacher.setText(teacher.getText() + cd.getTeacher());
        email.setText(email.getText() + cd.getEmail());
        website.setText(website.getText() + cd.getWebsite());
    }

    public void assignLayouts()
    {
        assignmentNameParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        assignmentModifiersParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0f);
        assignmentGradeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 0f);

        noAssignmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        eachWholeAssignmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        eachAssignmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        eachAssignmentExtraParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        eachAssignmentExtraParams.setMargins(dpToPixel(8), 0, 0, dpToPixel(8));

        noAssignmentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        noAssignmentParams.setMargins(dpToPixel(0), dpToPixel(8), dpToPixel(0), dpToPixel(8));
    }

    @Override
    public void onClick(View view) {
        //if is new - 4/29/15
        if (view.getTag() != null) {
            TextView extras = (TextView)((LinearLayout)view).getChildAt(1);
            if (extras.getVisibility() == View.VISIBLE) {
                extras.setVisibility(View.GONE);
            } else {
                extras.setVisibility(View.VISIBLE);
            }
            //((TextView)(((LinearLayout)(((LinearLayout)view).getChildAt(0))).getChildAt(2))).setText(cd.getAssignment((int)view.getTag()).getPercentage());
        } else {
            //switch grade with percentage or vice versa
            TextView v = (TextView) view;
            String buffer = v.getHint().toString();
            v.setHint(v.getText());
            v.setText(buffer);
        }
    }

    public int dpToPixel(int dp)
    {
        //d = pixel density
        float d = getBaseContext().getResources().getDisplayMetrics().density;
        //convert dp to p
        int margin = (int) (dp * d);
        return margin;
    }

    public class LongClick implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            TextView tv = (TextView)view;
            String data = tv.getText().toString();
            ClipData clip = ClipData.newPlainText("Assignment Name", data);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), "Moved text to clipboard.", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
