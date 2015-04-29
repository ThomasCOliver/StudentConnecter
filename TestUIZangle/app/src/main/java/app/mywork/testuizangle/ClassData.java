package app.mywork.testuizangle;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.plus.model.people.Person;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 3/27/2015.
 */
public class ClassData implements Serializable
{
    private List<Assignment> assignmentList;
    private String className, classGrade, teacher, markingPeriod;
    private double classNumberGrade;

    public ClassData(String className, String teacher, String classGrade, String markingPeriod)
    {
        //if it starts with something like "B AP Lang and Comp", get rid fo "B "
        if (className.indexOf(" ") == 1)
        {
            className = className.substring(2);
        }

        //if class include a class code (617970), get rid of it
        if (className.contains(" ("))
        {
            this.className = className.substring(0, className.indexOf(" ("));
        }
        else {
            this.className = className;
        }

        if (classGrade.equalsIgnoreCase("Not Available"))
        {
            this.classGrade = "N/A";
            classNumberGrade = -1;
        }
        else
        {
            //if class has a numerical grade
            if (classGrade.contains("(")) {
                //get number between ( and % in (97.3%)
                this.classNumberGrade = Double.parseDouble(classGrade.substring(classGrade.indexOf("(") + 1, classGrade.indexOf("%")));
                this.classGrade = classGrade.substring(0, classGrade.indexOf(" "));
            } else {
                this.classGrade = classGrade;
                classNumberGrade = -1;
            }
        }

        this.teacher = teacher;
        this.markingPeriod = markingPeriod;

        //initialize
        assignmentList = new ArrayList<Assignment>();
    }

    //mutator methods
    public void addAssignment(Assignment a)
    {
        assignmentList.add(a);
    }

    //accessor methods
    public Assignment getAssignment(int position)
    {
        return assignmentList.get(position);
    }

    public String getClassName()
    {
        return className;
    }

    public String getClassGrade()
    {
        //if a percentage is given by the class, use it
        if (classNumberGrade != -1)
        {
            return classGrade + " (" + classNumberGrade + "%)";
        }
        else    //otherwise, just give back the letter
        {
            return classGrade;
        }
    }

    //return just letter grade
    public String justGetLetter()
    {
        return classGrade;
    }

    //return just number grade
    public double getClassNumberGrade()
    {
        //if there is a number grade
        if (classNumberGrade != -1)
        {
            return classNumberGrade;
        }
        else    //if no number grade is in, return -1
        {
            return -1;
        }
    }

    //get color based on grade
    public int getColor(Context context)
    {
        if (!StorageIO.getMultiColor(context) || justGetLetter().equals("N/A")) {
            return Color.parseColor("#3fb5a3");
        }
        double predictedGrade = 0;
        switch (justGetLetter()) {
            case "A":
                predictedGrade = 96.25;
                break;
            case "A-":
                predictedGrade = 91;
                break;
            case "B+":
                predictedGrade = 88;
                break;
            case "B":
                predictedGrade = 84.5;
                break;
            case "B-":
                predictedGrade = 81;
                break;
            case "C+":
                predictedGrade = 78;
                break;
            case "C":
                predictedGrade = 74.5;
                break;
            case "C-":
                predictedGrade = 71;
                break;
            case "D+":
                predictedGrade = 68;
                break;
            case "D":
                predictedGrade = 64.5;
                break;
            case "D-":
                predictedGrade = 61;
                break;
            case "E":
                predictedGrade = 60;
                break;
            default:
                predictedGrade = -1;
        }


        return getColor(predictedGrade);
    }

    //mix colors
    private int getColor(double expectedGrade)
    {
        int red1;
        int green1;
        int blue1;
        int red2;
        int green2;
        int blue2;

        int resultRed;
        int resultGreen;
        int resultBlue;

        //see which colors to combine
        if (expectedGrade >= 75)
        {
            //green
            red1 = 76;
            green1 = 175;
            blue1 = 80;

            //yellow
            red2 = 255;
            green2 = 235;
            blue2 = 59;
            expectedGrade -= 75;
        }
        else
        {
            //yellow
            red1 = 255;
            green1 = 235;
            blue1 = 59;

            //red
            red2 = 244;
            green2 = 67;
            blue2 = 54;

            expectedGrade -= 60;
        }

        //get right proportion
        expectedGrade *= .04;
        //average colors
        resultRed = (int)(expectedGrade * red1 + (1 - expectedGrade) * red2);
        resultGreen = (int)(expectedGrade * green1 + (1 - expectedGrade) * green2);
        resultBlue = (int)(expectedGrade * blue1 + (1 - expectedGrade) * blue2);

        //get the color and return it
        return Color.rgb(resultRed, resultGreen, resultBlue);
    }

    public int getNumberOfAssignments()
    {
        return assignmentList.size();
    }

    public String getTeacher()
    {
        return teacher;
    }

    public String getEmail()
    {
        String[] parts = teacher.split(", ");
        if (parts[0].length() > 6)
        {
            parts[0] = parts[0].substring(0, 6);
        }

        return parts[0] + parts[1].substring(0, 1) + "@brightonk12.com";
    }

    public String getWebsite()
    {
        String[] parts = teacher.split(", ");
        return "www.brightonk12.com/webpages/" + parts[1].substring(0, 1) + parts[0] + "/";
    }

    public static List<ClassData> toNotify(List<ClassData> oldCdl, List<ClassData> newCdl, Context context) {
        boolean allClassesExist = true;

        List<ClassData> notifyCdl = new ArrayList<ClassData>();

        for (int i = 0; i < oldCdl.size(); i++) {

            if (!oldCdl.get(i).getClassName().equals(newCdl.get(i).getClassName())) {

                allClassesExist = false;
                break;

            }
        }

        if (allClassesExist) {

            //can find assignment now

            for (int i = 0; i < oldCdl.size(); i++) {

                for (int j = 0; j < newCdl.get(i).getNumberOfAssignments(); j++) {

                    Assignment matchingAssignmentInOld = findAssignment(newCdl.get(i).getAssignment(j), oldCdl.get(i));

                    if (matchingAssignmentInOld != null) {

                        //assignment found
                        if (!matchingAssignmentInOld.getFirstTime()) {
                            newCdl.get(i).getAssignment(j).setFirstTime(false);
                            //only update if score changed
                            if (!matchingAssignmentInOld.getPointsEarned().equals(newCdl.get(i).getAssignment(j).getPointsEarned())) {
                                //notify
                                notifyCdl = addClassToNotify(notifyCdl, newCdl.get(i));
                            } else {
                                //do nothing
                            }

                        } else {
                            //goto threshold part
                            newCdl.get(i).getAssignment(j).setFirstTime(false);
                            if (threshold(newCdl.get(i).getAssignment(j), context)) {
                                notifyCdl = addClassToNotify(notifyCdl, newCdl.get(i));
                            } else {
                                //do nothing
                            }
                        }

                    } else {

                        //assignment not found
                        //means that there is a value in points earned
                        if (!newCdl.get(i).getAssignment(j).getPointsEarned().equals(String.format("%6.1s", ""))) {

                            //goto threshold part
                            if (threshold(newCdl.get(i).getAssignment(j), context)) {
                                notifyCdl = addClassToNotify(notifyCdl, newCdl.get(i));
                            } else {
                                //do nothing
                            }

                        } else {
                            //there is no value in pointsEarned part
                            newCdl.get(i).getAssignment(j).setFirstTime(true);

                        }

                    }

                }

            }

            if (notifyCdl.equals(new ArrayList<ClassData>())) {
                //do not notify
                return null;
            } else {
                return notifyCdl;
            }

        }

        else {

            //set all assignments firstTime = false
            for (int i = 0; i < newCdl.size(); i++) {
                for (int j = 0; j < newCdl.get(i).getNumberOfAssignments(); j++) {
                    newCdl.get(i).getAssignment(j).setFirstTime(false);
                }
            }
            return null;
        }
    }

    private static Assignment findAssignment(Assignment a, ClassData oldCd) {

        for (int i = 0; i < oldCd.getNumberOfAssignments(); i++) {

            if (a.getAssignmentName().equals(oldCd.getAssignment(i).getAssignmentName())) {

                return oldCd.getAssignment(i);

            }

        }

        return null;

    }

    private static boolean threshold(Assignment a, Context context) {
        a.setFirstTime(false);
        if (a.getNumberPointsPossible() >= StorageIO.getPointLimit(context) || a.getNumberPercentage() <= StorageIO.getPercentageLimit(context)) {
            return true;
        }
        return false;
    }

    private static List<ClassData> addClassToNotify(List<ClassData> cdl, ClassData cd) {
        for (ClassData cdlData : cdl) {
            if (cdlData.getClassName().equals(cd.getClassName())) {
                return cdl;
            }
        }
        cdl.add(cd);
        return cdl;
    }

        @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        ClassData other = (ClassData) obj;
        if (other.getClassGrade().equals(getClassGrade()) && other.getNumberOfAssignments() == getNumberOfAssignments()) {
            result = true;
        }

        return result;
    }
}
