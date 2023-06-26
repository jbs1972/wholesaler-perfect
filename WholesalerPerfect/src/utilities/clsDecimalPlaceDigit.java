/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author Administrator
 */
public class clsDecimalPlaceDigit {
    public static String meth(String n)
    {
        String no=null;
        if(n.indexOf(".")!=-1)
        {
            String m=n.substring(0,n.indexOf("."));
            String s=n.substring(n.indexOf(".")+1);
            if(s.length()==1)
                s+="0";
            no=m+"."+s.substring(0,2);
        }
        else
        {
            no=n+".00";
        }
        return no;
    }
    public static String meth3(String n)
    {
        String no=null;
        if(n.indexOf(".")!=-1)
        {
            String m=n.substring(0,n.indexOf("."));
            String s=n.substring(n.indexOf(".")+1);
            if(s.length()==1)
                s+="00";
            if(s.length()==2)
                s+="0";
            no=m+"."+s.substring(0,3);
        }
        else
        {
            no=n+".000";
        }
        return no;
    }
}
