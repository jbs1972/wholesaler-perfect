package utilities;

public class ProperCase {
    public static String toDisplayCase(String s) {

        final String ACTIONABLE_DELIMITERS = " '-/"; // these cause the character following
                                                     // to be capitalized

        StringBuilder sb = new StringBuilder();
        boolean capNext = true;

        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }
    
    public static void main( String args[])
    {
        System.out.println(toDisplayCase("a string"));
        System.out.println(toDisplayCase("maRTin o'maLLEY"));
        System.out.println(toDisplayCase("john wilkes-booth"));
        System.out.println(toDisplayCase("YET ANOTHER STRING"));
    }
}
