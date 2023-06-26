/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jayanta B. Sen
 */
public class DateUtil {

    public static Date addDays1(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }

    public static String addDays2(String sourceDate, int days)
    {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date myDate=null;
        try {
            myDate = format.parse(sourceDate);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        myDate = DateUtil.addDays1(myDate, days);
        String dateNow = format.format(myDate);
        System.out.println("Source Date: "+sourceDate);
        System.out.println("Target Date: "+dateNow);
        return dateNow;
    }

    public static int dateDifference(String fromdt, String todt)
    {
        long diff = 0L;
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date1 = myFormat.parse(fromdt);
            Date date2 = myFormat.parse(todt);
            diff = date2.getTime() - date1.getTime();
            System.out.println ("Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return (int)TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
    
}
