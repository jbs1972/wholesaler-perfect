package utilities;
import java.math.*;
public class RoundOff {
    public static String roundOff01(String ori)
    {
        // Info
        /* 1.01 to 1.49 => 1.00
         * 1.50 to 1.99 => 2.00
         */
        String newVal=null;
        String mantisa=ori.substring(0,ori.indexOf(".")).trim();
        String scale=ori.substring(ori.indexOf(".")+1).trim();
        if(scale.length()==1)
            scale+="0";
        if(scale.length()>2)
            scale=scale.substring(0, 2);
        try
        {
            int scl=Integer.parseInt(scale);
            if(scl>=1 && scl<=49)
            {
                scale="00";
            }
            else
            {
                if(scl>=50 && scl<=99)
                {
                    scale="00";
                    int mts=Integer.parseInt(mantisa);
                    mts++;
                    mantisa=mts+"";
                }
            }
        }
        catch(NumberFormatException e)
        {
            System.out.println("Class RoundOff :: "+e.getMessage());
            return "0.00";
        }
        newVal=mantisa+"."+scale;
        return newVal;
    }

    public static String roundOff02(String ori)
    {
        // Info : Just to round off to 2 digits after decimal
        String newVal=null;
        String mantisa=ori.substring(0,ori.indexOf(".")).trim();
        String scale=ori.substring(ori.indexOf(".")+1).trim();
        if(scale.length()==1)
            scale+="0";
        if(scale.length()>2)
            scale=scale.substring(0, 2);
        newVal=mantisa+"."+scale;
        return newVal;
    }

    public static float roundOff03(float Rval, int Rpl)
    {
        float p = (float)Math.pow(10,Rpl);
        Rval = Rval * p;
        float tmp = Math.round(Rval);
        return (float)tmp/p;
    }
    
    public static double roundToAnyDecimalPlace(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
    
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static void main(String args[]) {
        double ori = 458.6298;
        double result = roundToAnyDecimalPlace(ori, 2);
        System.out.println("Original= "+ori+" Result= "+result);
    }
}
