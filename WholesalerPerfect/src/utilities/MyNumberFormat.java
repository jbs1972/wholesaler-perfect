package utilities;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import utilities.clsDecimalPlaceDigit;

public class MyNumberFormat {
    
    private static DecimalFormat df2 = new DecimalFormat("###.##");
    
    public static void main(String[] args)
    {
        System.out.println("Rs. "+rupeeFormat(-0.12));
    }
    
    public static String rupeeFormat(double amt)
    {
//        System.out.println("amt = "+amt);
//        System.out.println("df2 output = "+df2.format(amt));
//        System.out.println("clsDecimalPlaceDigit = "+clsDecimalPlaceDigit.meth(df2.format(amt)));
        amt = Double.parseDouble(clsDecimalPlaceDigit.meth(df2.format(amt)));
        String value=(int)amt+"";
        String scale = clsDecimalPlaceDigit.meth(df2.format(amt)).substring(clsDecimalPlaceDigit.meth(df2.format(amt)).indexOf("."));
        
        value=value.replace(",","");
        char lastDigit=value.charAt(value.length()-1);
        String result = "";
        int len = value.length()-1;
        int nDigits = 0;

        for (int i = len - 1; i >= 0; i--)
        {
            result = value.charAt(i) + result;
            nDigits++;
            if (((nDigits % 2) == 0) && (i > 0))
            {
                result = "," + result;
            }
        }
        String commaSeparatedMantisa = result+lastDigit;
        if (commaSeparatedMantisa.startsWith("-,"))
            commaSeparatedMantisa = "-"+commaSeparatedMantisa.substring(2);
        return (amt<0 && commaSeparatedMantisa.charAt(0) != '-')?("-"+commaSeparatedMantisa+scale):commaSeparatedMantisa+scale;
    }
}
