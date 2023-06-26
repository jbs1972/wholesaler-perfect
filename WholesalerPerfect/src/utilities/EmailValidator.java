/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Jayanta B. Sen
 */

public class EmailValidator {

	static private Matcher matcher;
	private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	/**
	 * Validate hex with regular expression
	 * @return true valid hex, false invalid hex
	 */
	public static boolean validate(final String hex)
        {
            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            matcher = pattern.matcher(hex);
            return matcher.matches();
	}
}
