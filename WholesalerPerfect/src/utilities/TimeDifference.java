/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jayanta B.Sen
 */
public class TimeDifference {
    
    public static String timeDifference(String t1, String t2)
    {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        Date d1=null;
        try {
            d1 = df.parse("01/01/2014 "+t1);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        Date d2=null;
        try {
            d2 = df.parse("01/01/2014 "+t2);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        long d = d2.getTime() - d1.getTime();
        long hh = d / (3600 * 1000);
        long mm = (d - hh * 3600 * 1000) / (60 * 1000);
        System.out.printf("%02d:%02d\n", hh, mm);
        return hh+"hr. "+mm+"min.";
    }
    
    public static void currentTime()
    {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm a");
        String currenttime = formatter2.format(currentTime.getTime());
        System.out.println("Current date in String Format: " + currenttime);
    }
    
    public static void main(String args[])
    {
        timeDifference("8:50 AM","11:20 AM");
        currentTime();
    }
}
