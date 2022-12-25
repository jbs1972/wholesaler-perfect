/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Jayanta B. Sen
 */
public class DateDiff {

    static public int getDateDiff(String d1, String d2)
    {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date dd1 = null;
        Date dd2 = null;
        try
        {
            dd1 = dateFormat.parse(d1);
            dd2 = dateFormat.parse(d2);
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
        int days = calculateDifference(dd1,dd2);
        return days;
    }

    public static int calculateDifference(Date a, Date b)
    {
        int tempDifference = 0;
        int difference = 0;
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();

        if (a.compareTo(b) < 0)
        {
            earlier.setTime(a);
            later.setTime(b);
        }
        else
        {
            earlier.setTime(b);
            later.setTime(a);
        }

        while (earlier.get(Calendar.YEAR) != later.get(Calendar.YEAR))
        {
            tempDifference = 365 * (later.get(Calendar.YEAR) - earlier.get(Calendar.YEAR));
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        if (earlier.get(Calendar.DAY_OF_YEAR) != later.get(Calendar.DAY_OF_YEAR))
        {
            tempDifference = later.get(Calendar.DAY_OF_YEAR) - earlier.get(Calendar.DAY_OF_YEAR);
            difference += tempDifference;
            earlier.add(Calendar.DAY_OF_YEAR, tempDifference);
        }

        return difference;
    }
    
    public static void main(String args[])
    {
        System.out.println("Difference in days: "+getDateDiff("01/01/2016", "10/03/2016"));
    }
}
