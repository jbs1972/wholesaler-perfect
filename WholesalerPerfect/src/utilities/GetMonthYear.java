/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.Calendar;

/**
 *
 * @author Jayanta B. Sen
 */
public class GetMonthYear {

    public static String getMonth()
    {
        String result[]={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        Calendar c = Calendar.getInstance();
        return result[c.get(Calendar.MONTH)];
    }

    public static String getYear()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        return year+"";
    }

    public static String getMonthInt(String month)
    {
        String result[]={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        int i=0;
        for(; i<result.length; i++)
        {
            if(result[i].equals(month))
                break;
        }
        return i<9?"0"+(i+1):i+1+"";
    }

    public static String getMedicineMonthYear(String dt)
    {
        // This function will return FEB-2014 if dt=01/02/2014
        String result=null;
        String months[]={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        String x[]=dt.split("/");
        result=months[Integer.parseInt(x[1])-1];
        result+=("-"+x[2]);
        return result;
    }

    public static String getMedicineMonthYearA(String dt)
    {
        // This function will return FEB-2014 if dt=01/02/2014
        String result=null;
        String months[]={"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
        String x[]=dt.split("/");
        result=months[Integer.parseInt(x[1])-1];
        result+=("-"+x[2]);
        return result;
    }

}
