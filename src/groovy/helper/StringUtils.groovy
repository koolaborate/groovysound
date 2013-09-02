package helper;

import java.util.ArrayList;

/***********************************************************************************
 * StringUtils                                                                     *
 ***********************************************************************************
 * A helper class that deals with Strings.                                         *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.0                                                                    *
 ***********************************************************************************
 * This file is part of VibrantPlayer.                                             *
 *                                                                                 *
 *  VibrantPlayer is free software: you can redistribute it and/or modify          *
 *  it under the terms of the Lesser GNU General Public License as published by    *
 *  the Free Software Foundation, either version 3 of the License, or              *
 *  (at your option) any later version.                                            *
 *                                                                                 *
 *  VibrantPlayer is distributed in the hope that it will be useful,               *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser            *
 *  GNU General Public License for more details.                                   *
 *                                                                                 *
 *  You should have received a copy of the Lesser GNU General Public License       *
 *  along with VibrantPlayer. If not, see <http://www.gnu.org/licenses/>.          *
 ***********************************************************************************/
public class StringUtils
{
	/**
	 * Returns whether a String is <code>Null</code> or equals "".
	 * 
	 * @param s the String to be tested
	 * @return <code>true</code> in case the String is <code>Null</code> or empty, <code>false</code> otherwise
	 */
	public static boolean isNullOrEmpty(String s)
	{
		return(s == null || s.trim().equals(""));
	}

	
	/**
	 * Returns whether or not the given eMail address is valid.
	 * 
	 * @param mailAddress the eMail address to test
	 * @return <code>true</code> if the address is valid, <code>false</code> otherwise
	 */
	public static boolean isValidMailAddress(String mailAddress)
	{
		boolean ret = true;
		
		int i = mailAddress.indexOf("@");
		int j = mailAddress.indexOf(".", i);
		
		if (i == 0) // amount of characters before the @
		{
			ret = false;
		}
		
		if (j == -1) // there may be no point before the @ symbol
		{
			ret = false;
		}
		
		if (j == -1 || j == (mailAddress.length()-1)) // the point may not be the last character
		{
			ret = false;
		}
		
		return ret; 
	}
	
	
	/**
	 * Returns whether or not the given phone number is valid.
	 * 
	 * @param number the number to test
	 * @return <code>true</code> if the number is valid, <code>false</code> otherwise
	 */
	public static boolean isValidPhoneNumber(String number)
	{
		ArrayList<Character> allowedNumbers = new ArrayList<Character>();
		char[] numbers = ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', ' ', '/', '-'];
		for(char c: numbers) allowedNumbers.add(c);
		
		char[] testArray = number.toCharArray();
		// test every character
		for(char c: testArray)
		{
			// if the read character is not in the list of allowed characters
			if(!allowedNumbers.contains(c)) return false;
		}
		return true;
	}
	
	
	/**
	 * Returns the first character of a String (as a String).
	 * 
	 * @param s the String
	 * @return the first character of the String (as a String)
	 */
	public static String getFirstChar(String s)
	{
		String ret = s.substring(0, 1);
		ArrayList<String> nums = new ArrayList<String>();
		String[] numbers = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9"]
		for(String n: numbers) nums.add(n);
		
		// if the read character is a number, return '#'
		if(nums.contains(ret)) return "#";
		
		return ret;
	}
}