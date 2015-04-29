package app.mywork.testuizangle;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Thomas on 3/27/2015.
 */
public class Assignment implements Serializable {
    private String dateDue, dateAssigned, assignmentName, scoredAs, comments;
    private double pointsPossible, pointsEarned, percentage;
    private boolean extraCredit, notGraded, firstTime;

    public Assignment(List<String> keys, List<String> data) {
        for (int i = 0; i < keys.size(); i++) {
            switch (keys.get(i)) {
                case "Detail":

                    break;
                case "Date Due":
                    this.dateDue = data.get(i);
                    break;
                case "Assigned":
                    this.dateAssigned = data.get(i);
                    break;
                case "Assignment":
                    this.assignmentName = data.get(i);
                    break;
                case "Pts Possible":
                    if (!data.get(i).equals("")) {
                        this.pointsPossible = Double.parseDouble(data.get(i));
                    } else {
                        this.pointsPossible = -1;
                    }
                    break;
                case "Score":
                    if (!data.get(i).equals("")) {
                        this.pointsEarned = Double.parseDouble(data.get(i));
                    } else {
                        this.pointsEarned = -1;
                    }
                    break;
                case "Pct Score":
                    break;
                case "Scored As":
                    this.scoredAs = data.get(i);
                    break;
                case "Extra Credit":
                    if (data.get(i).indexOf("check.png") != -1) {
                        this.extraCredit = true;
                    } else {
                        this.extraCredit = false;
                    }
                    break;
                case "Not Graded":
                    if (data.get(i).indexOf("check.png") != -1) {
                        this.notGraded = true;
                    } else {
                        this.notGraded = false;
                    }
                    break;
                case "Comments":
                    this.comments = data.get(i);
                    break;
            }
            if (this.pointsPossible != -1 && this.pointsEarned != -1) {
                this.percentage = this.pointsEarned / this.pointsPossible;
            } else {
                this.percentage = -1;
            }
        }
    }

    //get everything as a string for now, in the future I can tell what makes something extraCredit, notGraded, etc.
    public Assignment(String unknown, String dateDue, String dateAssigned, String assignmentName, String pointsPossible, String pointsEarned, String percent, String scoredAs, String extraCredit, String notGraded, String comments) {
        this.dateDue = dateDue;
        this.dateAssigned = dateAssigned;
        this.assignmentName = assignmentName;
        //parse the two numbers as points earned and possible
        if (!pointsEarned.equals("")) {
            this.pointsEarned = Double.parseDouble(pointsEarned);
        } else {
            this.pointsEarned = -1;
        }
        if (!pointsPossible.equals("")) {
            this.pointsPossible = Double.parseDouble(pointsPossible);
        } else {
            this.pointsPossible = -1;
        }

        //if there is a maximum amount of points inserted
        if (this.pointsPossible != -1 && this.pointsEarned != -1) {
            this.percentage = this.pointsEarned / this.pointsPossible;
        } else {
            this.percentage = -1;
        }
        this.scoredAs = scoredAs;

        if (extraCredit.indexOf("check.png") != -1) {
            this.extraCredit = true;
        } else {
            this.extraCredit = false;
        }
        if (notGraded.indexOf("check.png") != -1) {
            this.notGraded = true;
        } else {
            this.notGraded = false;
        }
        this.comments = comments;
    }

    public String getDateDue() {
        return dateDue;
    }

    public String getDateAssigned() {
        return dateAssigned;
    }

    public String getAssignmentName() {
        return assignmentName;
    }

    public String getScoredAs() {
        return scoredAs;
    }

    public String getComments() {
        return comments;
    }

    public String getPointsPossible() {
        if (pointsPossible != -1) {

            return String.format("%4.0f", pointsPossible);


            //if (pointsPossible == (int) pointsPossible) {
            //    return (int) pointsPossible + "";
            //} else {
            //    return pointsPossible + "";
            //}
        } else {
            return " ";
        }
    }

    public double getNumberPointsPossible() {
        return pointsPossible;
    }

    public String getPointsEarned() {
        if (pointsEarned != -1) {


            if (pointsEarned == (int) pointsEarned) {
                return String.format("%6.0f", pointsEarned);
            } else {
                return String.format("%6.1f", pointsEarned);
            }

            //if (pointsEarned == (int) pointsEarned) {
            //    return (int) pointsEarned + "";
            //} else {
            //    return pointsEarned + "";
            //}
        } else {
            return String.format("%6.1s", "");
        }

    }

    public String getPercentage() {
        if (percentage != -1) {
            return String.format("%7.0f", percentage * 100) + "%";
        } else {
            return String.format("%7.0s", "") + "%";
        }
    }

    public double getNumberPercentage() {
        return percentage;
    }

    public String getExtraCredit() {
        if (extraCredit == true) {
            return "EC";
        } else {
            return "";
        }
    }

    public String getNotGraded() {
        if (notGraded == true) {
            return "NG";
        } else {
            return "";
        }
    }

    public String getModifiers() {
        if (extraCredit == true && notGraded == true) {
            return " " + getNotGraded() + ", " + getExtraCredit() + " ";
        } else {
            return " " + getNotGraded() + getExtraCredit() + " ";
        }
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public boolean getFirstTime() {
        return firstTime;
    }

    public String getAllExtras() {
        String result = "";
        result += "Date Due: " + getDateDue() + "\nScored As: " + getScoredAs() + "\nComments: " + getComments();
        return result;
    }
}
