/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author Administrator
 */
public class DateConverter {
    public static String dateConverter(String dt)
    {
        if(dt==null)
            return "N/A";
        dt=dt.substring(0,dt.indexOf(" "));
        String ndt=new String();
        String s[]=dt.split("-");
        ndt+=s[2];
        ndt+=("/"+s[1]);
        ndt+=("/"+s[0]);
        return ndt;
    }
    public static String dateConverter1(String dt)
    {
        //01/11/2011
        //YYYY-MM-DD
        String ndt=new String();
        String s[]=dt.split("/");
        ndt+=s[2].trim();
        ndt+=("-"+s[1].trim());
        ndt+=("-"+s[0].trim());
        return ndt;
    }
    public static String dateConverter2(String dt)
    {
        if(dt==null)
            return "N/A";
        //dt=dt.substring(0,dt.indexOf(" "));
        String ndt=new String();
        String s[]=dt.split("/");
        ndt+=s[1];
        ndt+=("/"+s[0]);
        ndt+=("/"+s[2]);
        return ndt;
    }

    public static String dateConverter3(String dt)
    {
        if(dt==null)
            return "N/A";
        String ndt=new String();
        String s[]=dt.split("-");
        ndt+=s[2];
        ndt+=("/"+s[1]);
        ndt+=("/"+s[0]);
        return ndt;
    }
}
