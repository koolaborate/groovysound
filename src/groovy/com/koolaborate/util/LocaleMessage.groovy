package com.koolaborate.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/***********************************************************************************
 * LocaleMessage                                                                   *
 ***********************************************************************************
 * A helper class that returns message texts for the current Locale.               *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.01                                                                   *
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
public class LocaleMessage 
{
	private LocaleMessage(){}
	private static LocaleMessage messageInstance
	
	public static synchronized LocaleMessage getInstance(){
		if(null == messageInstance) messageInstance = new LocaleMessage()
		
		return messageInstance
	}
	
	private static final String BUNDLE_NAME = "resources.maintexts";
	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
	
	/**
	 * Returns the text which corresponds to the given key for the current Locale.
	 * 
	 * @param key the key to be searched for
	 * @return a String containing the text in the current Locale
	 */
	public String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e){
			System.out.println("Ressource for key '" + key + "' is missing.");
			return "MISSING: " + key; 
		}
	}
}