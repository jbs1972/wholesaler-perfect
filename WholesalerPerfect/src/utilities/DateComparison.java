/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jayanta B. Sen
 */
public class DateComparison {

    public boolean isTodtGreaterThanFromdt(String fromdt,String todt)
    {
        boolean flag=false;
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = sdf.parse(fromdt);
            Date date2 = sdf.parse(todt);
            if(date1.after(date2)){
                flag=false;
            }
            if(date1.before(date2)){
                flag=true;
            }
            if(date1.equals(date2)){
                flag=true;
            }
        }catch(ParseException ex){
            ex.printStackTrace();
        }
        return flag;
    }

}
