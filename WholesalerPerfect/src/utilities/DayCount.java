/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import query.Query;

/**
 *
 * @author Jayanta B. Sen
 */
public class DayCount {
    
    Query q=new Query();

    /*
     * The dayCount method counts the number of days starting from 1st Jan to the
     * given date in dt including the date itself.
     */
    public int dayCount(String dt)
    {
        int counter=0;
        // dt = dd/mm/yyyy
        String s[]=dt.split("/");
        int dd=Integer.parseInt(s[0]);
        int mm=Integer.parseInt(s[1]);
        int yyyy=Integer.parseInt(s[2]);
        counter=dd;
        switch(mm-1)
        {
            case 11: counter+=30;//Nov
            case 10: counter+=31;//Oct
            case 9: counter+=30;//Sep
            case 8: counter+=31;//Aug
            case 7: counter+=31;//Jul
            case 6: counter+=30;//Jun
            case 5: counter+=31;//May
            case 4: counter+=30;//Apr
            case 3: counter+=31;//Mar
            case 2: counter+=29;//Feb
            case 1: counter+=31;//Jan
        }
        return counter;
    }

    public String getColumnComparisonString(String dt1, String dt2,String val)
    {
        int dc1=dayCount(dt1);
        int dc2=dayCount(dt2);
        int yyyy=Integer.parseInt(dt2.substring(dt2.lastIndexOf("/")+1));
        StringBuilder sb=new StringBuilder();
        int i=dc1;
        for(; i!=dc2; i++)
        {
            if(i==60)
            {
                if(yyyy%4==0)
                    sb.append(" and d"+i+"="+val);
                else
                    continue;
            }
            if(i>366)
                i=1;
            sb.append(" and d"+i+"="+val);
        }
        sb.append(" and d"+i+"="+val);
        return sb.toString();
    }

    public String getColumnUpdateString(String dt1, String dt2,String val)
    {
        int dc1=dayCount(dt1);
        int dc2=dayCount(dt2);
        int yyyy=Integer.parseInt(dt2.substring(dt2.lastIndexOf("/")+1));
        StringBuilder sb=new StringBuilder();
        int i=dc1;
        for(; i!=dc2; i++)
        {
            if(i==60)
            {
                if(yyyy%4==0)
                    sb.append(",d"+i+"="+val);
                else
                    continue;
            }
            if(i>366)
                i=1;
            sb.append(",d"+i+"="+val);
        }
        sb.append(",d"+i+"="+val);
        return sb.toString().substring(1);
    }

    public String getSingleColumnUpdateString(String dt,String val)
    {
        int dc1=dayCount(dt);
        int yyyy=Integer.parseInt(dt.substring(dt.lastIndexOf("/")+1));
        StringBuilder sb=new StringBuilder();
        if(dc1==60)
        {
            if(yyyy%4==0)
                sb.append("d"+dc1+"="+val);
            else
                sb.append("d"+(dc1+1)+"="+val);
        }
        else
        {
            sb.append("d"+dc1+"="+val);
        }
        return sb.toString();
    }
    
    public String getColumnList(String dt1, String dt2)
    {
        int dc1=dayCount(dt1);
        int dc2=dayCount(dt2);
        int yyyy=Integer.parseInt(dt2.substring(dt2.lastIndexOf("/")+1));
        StringBuilder sb=new StringBuilder();
        int i=dc1;
        for(; i!=dc2; i++)
        {
            if(i==60)
            {
                if(yyyy%4==0)
                    sb.append(",d"+i);
                else
                    continue;
            }
            if(i>366)
                i=1;
            sb.append(",d"+i);
        }
        sb.append(",d"+i);
        return sb.toString().substring(1);
    }
    
    public ArrayList<Date> getDaysBetweenDates(Date startdate, Date enddate)
    {
        ArrayList<Date> dates = new ArrayList<Date>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(startdate);

        while (calendar.getTime().before(enddate))
        {
            Date result = calendar.getTime();
            dates.add(result);
            calendar.add(Calendar.DATE, 1);
        }
        
        Date result = calendar.getTime();
        dates.add(result);
        calendar.add(Calendar.DATE, 1);
        
        return dates;
    }
    
    public String dateCodeToDate(String startdt, String dateCode)
    {
        int startdc=dayCount(startdt);
        int idatecode=Integer.parseInt(dateCode.substring(1));
        Date startDt=q.parseDate(startdt, "dd/MM/yyyy");
        return new SimpleDateFormat("dd/MM/yyyy").format(q.addDays(startDt, idatecode-startdc));
    }
    
    public String dateToCode(String dt)
    {
        int dc=dayCount(dt);
        return "d"+dc;
    }
    
    public static void main(String args[])
    {
        System.out.println("Date Code For 01/02/2015 Is: "+new DayCount().dateToCode("01/02/2015"));
    }

}
