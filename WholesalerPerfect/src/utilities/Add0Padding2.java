/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author Administrator
 */
public class Add0Padding2 {
    
    public static String add0Padding(int no)
    {
        String alteredNo="";
        String sno=no+"";
        switch(sno.length())
        {
            case 1:alteredNo="0"+sno;
                break;
            case 2:alteredNo=sno;
                break;
        }
        return alteredNo;
    }
}
