/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author Administrator
 */
public class Add0Padding6 {
    public static String add0Padding(int no)
    {
        String alteredNo="";
        String sno=no+"";
        switch(sno.length())
        {
            case 1:alteredNo="00000"+sno;
                break;
            case 2:alteredNo="0000"+sno;
                break;
            case 3:alteredNo="000"+sno;
                break;
            case 4:alteredNo="00"+sno;
                break;
            case 5:alteredNo="0"+sno;
                break;
            case 6:alteredNo=sno;
                break;
        }
        return alteredNo;
    }
}
