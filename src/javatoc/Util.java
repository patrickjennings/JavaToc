package javatoc;

/**
 * @author Patrick Jennings
 * 
 * The Util class holds special parsing and general
 * purpose utilities for the javatoc package.
 *
 */
public class Util {
	
	/*
	 * "sn = ascii value of the first letter of the screen name
	 * pw = ascii value of the first character of the password
	 * return 7696 * sn * pw"
	 * http://terraim.cvs.sourceforge.net/viewvc/terraim/terraim/src/toc/TOC2.txt
	 */
	public static int signOnCode(String u, String p) {
		int sn = u.charAt(0);
		int pw = p.charAt(0);
		return 7696 * sn * pw;
	}
	
	/*
	 * "Roasting is performed by first xoring each byte 
	 * in the password with the equivalent modulo byte in the roasting 
	 * string.  The result is then converted to ascii hex, and prepended 
	 * with 0x". 
	 * http://terraim.cvs.sourceforge.net/viewvc/terraim/terraim/src/toc/TOC1.txt
	 */
	public static String roastPassword(String str) {
		byte xor[] = "Tic/Toc".getBytes();
		int xorIndex = 0;
		String rtn = "0x";

		for(int i=  0; i < str.length(); i++) {
			String hex = Integer.toHexString(xor[xorIndex]^(int)str.charAt(i));
			if(hex.length() == 1)
				hex = "0" + hex;
			rtn += hex;
			xorIndex++;
			if(xorIndex == xor.length)
				xorIndex=0;
		}
		return rtn;
	}
	
	/*
	 * Normalize the input string by making all chars lower
	 * case and remove all non-alphabetical/numerical chars.
	 */
	public static String normalize(String input) {
        StringBuffer output = new StringBuffer();
        String temp = input.toLowerCase();
        for(int i = 0; i < input.length(); i++) {
            char c = temp.charAt(i);
            if((c >= '0' && c <= '9') || (c >= 'a' && c <= 'z')) {
                output.append(c);
            }
        }
        
        return output.toString();
    }
	
	/*
	 * Can be used to encode a new message.
	 * 
	 * Encode the string with the following:
	 * Change all newlines to <br>.
	 * Place '\\' before special chars: "Dollar signs, 
	 * 	curly brackets, square brackets, parentheses,
	 *  quotes, and backslashes".
	 */
	public static String encode(String str) {
		String rtn="";
		for(int i = 0; i < str.length(); i++) {
			switch(str.charAt(i)) {
				case '\r':
					rtn+="<br>";
					break;
				case '{':
				case '}':
				case '\\':
				case ':':
				case '"':
					rtn+="\\";
				default:
					rtn+=str.charAt(i);
			}
		}
		return rtn;
	}
	
	/*
	 * Returns the next element from a string with
	 * the following format: next:restOfString
	 */
	public static String nextElement(StringBuffer str) {
		String result = "";
	    int i = str.indexOf(":", 0);
	    if(i == -1) {
	    	result = str.toString();
	    	str.setLength(0);
	    }
	    else {
	    	result = str.substring(0,i).toString();
	    	String a = str.substring(i+1);
	    	str.setLength(0);
	    	str.append(a);
	    }
	    return result;
	}
}
