package mobi.allshoppings.tools;

import java.io.Serializable;
import java.util.logging.Logger;

public class Hash implements Serializable {

	private static final long serialVersionUID = 5341577123238608087L;
	private static final Logger log = Logger.getLogger(Hash.class.getName());
	
	/**
	 * Java code to generate 8 or 9 digit alphanumeric code
	 * 
	 * @return
	 */
	public static String generateAuthCode() {
		return generateAuthCode(10);
	}
	
	/**
	 * Java code to generate 8 or 9 digit alphanumeric code
	 * 
	 * @return
	 */
	public static String generateAuthCode(int length) {
		// getting the current time in nanoseconds
		long decimalNumber = System.nanoTime();
		log.fine("current time in nanoseconds: " + decimalNumber);

		// To convert time stamp to alphanumeric code.
		// We need to convert base10(decimal) to base36
		String strBaseDigits = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String strTempVal = "";
		int mod = 0;
		// String concat is costly, instead we could have use stringbuffer or
		// stringbuilder
		// but here it wont make much difference.
		while (decimalNumber != 0) {
			mod = (int) (decimalNumber % 36);
			strTempVal = strBaseDigits.substring(mod, mod + 1) + strTempVal;
			decimalNumber = decimalNumber / 36;
		}

		if ( length < 1 || length > strTempVal.length() ) length = strTempVal.length();

		log.fine("alphanumeric code generated from TimeStamp : " + strTempVal);
		return strTempVal.substring(strTempVal.length() - length);
	}

}
