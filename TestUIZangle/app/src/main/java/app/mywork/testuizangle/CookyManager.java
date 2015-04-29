package app.mywork.testuizangle;
/**
 * Write a description of class CookieManager here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
import java.util.*;
public class CookyManager
{
    private List cookyList;
    public CookyManager()
    {
        cookyList = new ArrayList<String>();
    }
    
    public void addCooky(String s)
    {
        s = s.replace(",", ";");
        String[] allPossibleCookies = s.split("; ");
        for (String m : allPossibleCookies)
        {
            if (m.indexOf("ADRUM") != -1 || m.indexOf("RESA") != -1 || m.indexOf("SessionId") != -1)
            {
                addActualCooky(m);
            }
        }
    }
    
    public void addActualCooky(String s)
    {
        if (!cookyList.contains(s))
        {
            cookyList.add(s);
        }
    }
    
    public String getCookys()
    {
        String result = "";
        if (cookyList.size() != 0)
        {
            for (int i = 0; i < cookyList.size(); i++)
            {
                result += cookyList.get(i) + "; ";
            }
            result = result.substring(0, result.length() - 2);
        }
        return result;
    }
}
