package app.mywork.testuizangle;

import android.widget.Toast;

import java.util.*;
import java.util.Scanner;
import java.io.*;

public class ClassAnalyzer
{
    private static List<String> getElements(String html, String element)
    {
        String oldestHtml = html;
        String oldHtml = html;
        
        int end = 0;
        
        ArrayList<String> elements = new ArrayList<String>();
        
        //get all elements with that in its name
        while (html.indexOf("</" + element + ">") != -1)
        {
            end = 1 + html.indexOf("</" + element + ">");
            //get element starting with "<" + element and go through and include "</" + element + ">"
            elements.add(html.substring(html.indexOf("<" + element), element.length() + 3 + html.indexOf("</" + element + ">")));
            oldHtml = oldHtml.substring(end);
            //reset html so that the .substring does not ruin it
            html = oldHtml;
        }

        //return html to its original values
        html = oldestHtml;
        
        return elements;
    }
    
    private static String getTextContent(String html)
    {
        String textValue;
        textValue = html.substring(html.indexOf(">") + 1, html.lastIndexOf("<"));
        
        return textValue;
    }
    
    public static List<ClassData> convertHtmlToClasses(String html)
    {
        List<ClassData> classes = new ArrayList<ClassData>();
        try
        {
            //get rid of crap in html
            html = cleanUpHtml(html);
            //get every class in html
            List<String> tables = getElements(html, "table");
            //there are two extra tables at the end we don't want, full of crap
            tables.remove(tables.size() - 1);
            tables.remove(tables.size() - 1);
            

            List<String> assignments;

            //go through for each class
            for (String s : tables)
            {
                String markingPeriod = s.substring(s.indexOf("Marking Period ") + 15, s.indexOf("Marking Period ") + 16);
                //get the header
                List<String> head = getElements(s, "thead");
                //get all rows in the header
                List<String> headerRows = getElements(head.get(0), "tr");
                //get td in first row of the header
                List<String> headerData = getElements(headerRows.get(0), "td");
                //get the full name of the class
                String className = getElements(headerRows.get(0), "b").get(1);
                className = getTextContent(className);

                //get the name of the teacher in the form LastName, FirstName
                String teacherName = getElements(headerData.get(1), "a").get(0);
                teacherName = getTextContent(teacherName);
                
                //get grade for second row of the header
                List<String> gradeData = getElements(headerRows.get(1), "td");
                String grades = getClassGrade(gradeData.get(0));

                //create a class object given the grade, class name, and teacher
                ClassData c = new ClassData(className, teacherName, grades, markingPeriod);

                //get all the possible headers
                List<String> dataTableHeaders = getElements(headerRows.get(2), "th");
                List<String> dataTextHeaders = new ArrayList<String>();
                for (String s1 : dataTableHeaders) {
                    dataTextHeaders.add(getTextContent(getElements(s1, "label").get(0)));
                }

                //get the body
                List<String> body = getElements(s, "tbody");

                //get every row in the body
                assignments = getElements(body.get(0), "tr");

                //for each row in the body (represents each assignment
                for (String assignment : assignments)
                {
                    //if there are actual assignments, add them
                    if (!assignment.contains("No Assignments Available")) {
                        //get all bits of data for that assignment
                        List<String> data = getElements(assignment, "td");
                        for (int j = 0; j < data.size(); j++) {
                            //convert the datalist to just the inner text and exclude the tags
                            data.add(j, correctAccents(getTextContent(data.get(j))));
                            data.remove(j + 1);
                        }
                        //if percentages not included, add in a blank part.
                        //if (data.size() == 10) {
                        //    data.add(6, "");
                        //}
                        Assignment newAssignment = new Assignment(dataTextHeaders, data);
                        //create an assignment with all of these

                        //Assignment newAssignment = new Assignment(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6), data.get(7), data.get(8), data.get(9), data.get(10));
                        //add assignment to class
                        c.addAssignment(newAssignment);
                    }
                }

                //add the class to the total schedule
                classes.add(c);
            }

            return classes;
        }
        catch (Exception e)
        {

            return null;
        }


    }
    
    private static String getClassGrade(String data)
    {
        //because this one is weird, it has a special method
        return data.substring(data.indexOf("</b") + 4, data.indexOf("<a"));
    }

    //used before anything to get rid of random crap
    private static String cleanUpHtml(String html)
    {
        html = html.replaceAll("&nbsp;", "");
        html = html.replaceAll("\t", "");
        html = html.replaceAll("<br />", " ");
        html = html.replaceAll("&amp;", "&");
        //want to add something for the accented characters


        return html;
    }

    private static String correctAccents(String html) {
        int characterCode;
        int endingSpot = 0;
        while (html.indexOf("&#", endingSpot) != -1) {
            int nextStart = html.indexOf("&#", endingSpot) + 2;
            int nextEnd = html.indexOf(";", endingSpot);
            characterCode = Integer.parseInt(html.substring(nextStart, nextEnd));

            html = html.replace(html.substring(nextStart - 2, nextEnd + 1), Character.toString((char) characterCode));
            endingSpot = nextEnd + 1;
        }
        return html;
    }
}