package com.koolaborate.util

import java.awt.Shape
import java.awt.Window
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import org.apache.log4j.Logger

/***********************************************************************************
 * WindowShaper                                                                    *
 ***********************************************************************************
 * A helper class to change the shape of a java.awt.Window. Window shaping has     *
 * been introduced to the JDK from version 1.6_10 on and is supposed to change its *
 * package location. Therefore, this helper class uses reflection to find the      *
 * class which will only be available in JRE 1.6_10 and newer.                     *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k9                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.2                                                                    *
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
public class WindowShaper
{
	private static Logger log = Logger.getLogger(WindowShaper.class.getName())
	
	Class<?> awtUtilities = null
	Method method = null
	
	/**
	 * Constructor.
	 */
	public WindowShaper(){
		// initialize methods for window shaping using reflection
		try{
			awtUtilities = Class.forName("com.sun.awt.AWTUtilities")
			// in JDK7 the helper class methods got moved to java.awt.Window...
			if(awtUtilities == null) awtUtilities = Class.forName("java.awt.Window")
			method = awtUtilities.getMethod("setWindowShape", Window.class, Shape.class)
		} catch(Exception e){
			log.debug("Round window support disabled. Please check JRE version: " + 
					e.getMessage())
		}
	}
	
	/**
	 * Sets the given shape for the given window if the window shaping method is 
	 * available. Otherwise, no action will be performed.
	 * 
	 * @param w the window to be shaped
	 * @param s the shape for the window
	 * @return <code>true</code> if the window shape could be set successfully, 
	 *         <code>false</code> otherwise
	 */
	public boolean shapeWindow(Window w, Shape s)
	{
		// set the window shape (if possible) 
		if(method != null)
		{
			try
			{
				method.invoke(null, w, s)
				return true
			}
			catch(IllegalArgumentException e)
			{
				log.error(e.getMessage()) 
			}
			catch(IllegalAccessException e)
			{
				log.error(e.getMessage())
			}
			catch(InvocationTargetException e)
			{
				log.error(e.getMessage())
			}
		}
		return false
	}
}