package mobi.allshoppings.tools;

/**
 * Hex Decimal conversion tool 
 * @author mhapanowicz
 *
 */
public class Hex {

	private final static char[] HEX = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};

	/**
	 * Convert an hex value to long
	 * 
	 * @param hexadecimal
	 *            The Hex String to convert
	 * @return A long representation for the input string
	 * @throws NumberFormatException
	 */
	public static long toLong(String hexadecimal) throws NumberFormatException{
		char[] chars;
		char c;
		long value;
		int i;

		if (hexadecimal == null)
			throw new IllegalArgumentException();

		chars = hexadecimal.toUpperCase().toCharArray();
		if (chars.length != 16)
			throw new RuntimeException("Incomplete hex value");

		value = 0;
		for (i = 0; i < 16; i++) {
			c = chars[i];
			if (c >= '0' && c <= '9') {
				value = ((value << 4) | (0xff & (c - '0')));
			} else if (c >= 'A' && c <= 'F') {
				value = ((value << 4) | (0xff & (c - 'A' + 10)));
			} else {
				throw new NumberFormatException("Invalid hex character: " + c);
			}
		}

		return value;
	}

	/**
	 * Creates a new hex string from a long integer
	 * 
	 * @param value
	 *            The long integer to convert
	 * @return A hex string representation for the given input
	 */
	public static String fromLong(long value) {
		char[] hexs;
		int i;
		int c;

		hexs = new char[16];
		for (i = 0; i < 16; i++) {
			c = (int)(value & 0xf);
			hexs[16-i-1] = HEX[c];
			value = value >> 4;
		}
		return new String(hexs);
	}

}