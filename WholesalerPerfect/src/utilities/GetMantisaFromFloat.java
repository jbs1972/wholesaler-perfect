/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author A. Ganguly
 */
public class GetMantisaFromFloat {

    public static String getMantisaFromFloat(String no)
    {
        String result="";
        if(no.indexOf('.')!=-1)
        {
            result=no.substring(0, no.indexOf("."));
        }
        else
        {
            result=no;
        }
        return result;
    }
}
