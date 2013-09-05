package com.koolaborate.util;

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
public class StringUtils {
	
	private static StringUtils instance;
	
	def static synchronized StringUtils getInstance(){
		if(null == instance){
			instance = new StringUtils();
		}
		
		return instance;
	}
	
	/**
	 * Returns the first character of a String (as a String).
	 * 
	 * @param s the String
	 * @return the first character of the String (as a String)
	 */
	def String getFirstChar(String string) {
		if (org.apache.commons.lang3.StringUtils.isEmpty(string)) return null
		
		String firstChar = string.substring(0, 1);
		ArrayList<String> numberList = new ArrayList<String>();
		String[] numbers = [
			"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
		]
		for(String number: numbers) {
			numberList.add(number);
		}

		// if the read character is a number, return '#'
		if(numberList.contains(firstChar)) return "#";

		return firstChar;
	}
}