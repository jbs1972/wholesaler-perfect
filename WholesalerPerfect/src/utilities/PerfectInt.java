/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author Jayanta B. Sen
 */
public class PerfectInt {
    public static String getPerfectInt(String no)
    {
        if(no.indexOf(".")==-1)
            return no;
        if(no.indexOf(".")==0)
            return "0";
        return no.substring(0, no.indexOf("."));
    }
}
