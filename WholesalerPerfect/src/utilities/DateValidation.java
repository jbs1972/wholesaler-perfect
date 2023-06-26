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
 * @author Administrator
 */
public class DateValidation
{
    public static boolean isValidDate(String date)
	{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date testDate = null;
            try
            {
       		testDate = sdf.parse(date);
            }
            catch (ParseException e)
            {
      		//System.out.println("The date you provided is in an invalid date format.");
      		return false;
            }
            if (!sdf.format(testDate).equals(date))
            {
      		//System.out.println("The date that you provided is invalid.");
      		return false;
            }
            return true;
	}
}
