package helper;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/***********************************************************************************
 * OSHelper                                                                        *
 ***********************************************************************************
 * A helper class that deals with various operating system conform issues.         *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.1                                                                    *
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
public class OSHelper
{
	/** whether it is a Windows(R) operating system or not */
	public static final boolean WINDOWS_OS = System.getProperty("os.name").toLowerCase().startsWith("windows");


	/**
	 * Sets the look and feel that is the standard for the operating system
	 */
	public static void setDefaultLookAndFeel()
	{
		// set the system look and feel (lnf)
	    try 
	    {
	    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
	}
	
	
	/**
	 * Sets the look and feel that is given.
	 * 
	 * @param lnfName the name of the look and feel
	 */
	public static void setLookAndFeel(String lnfName)
	{
		// set the given look and feel (lnf)
	    try 
	    {
	    	String className = LookAndFeelHelper.getClassNameForLnF(lnfName);
	    	UIManager.setLookAndFeel(className);
	    }
	    catch(Exception e)
	    {
	    	setDefaultLookAndFeel();
	    	e.printStackTrace();
	    }
	}
	
	
	/**
	 * Returns whether or not the current OS is Vista style enabled.
	 * 
	 * @return <code>true</code> if Vista style is enabled, <code>false</code> otherwise
	 */
	public static boolean isVistaStyleEnabled()
	{
		if(WINDOWS_OS && UIManager.getLookAndFeel().getName().toLowerCase().equals("windows"))
		{
			return true;
		}
		return false;
	}
	
	
	/**
	 * Returns the Java version which is running on the current system.
	 * 
	 * @return the Java version
	 */
	public static float getJavaVersion()
	{
		float ret = 1.0f;
		String sysString = System.getProperty("java.version");
		String version = sysString.substring(0, 3); // for example "1.6"
		try
		{
			// just in case the Float wrapper class could not parse the version String
			ret = Float.parseFloat(version);
		}
		catch(Exception e){}
		return ret;
	}


	/**
	 * Returns the default Look and Feel name for the system.
	 * 
	 * @return the default Look and Feel name
	 */
	public static String getDefaultLnFName()
	{
		String className = UIManager.getSystemLookAndFeelClassName();
		LookAndFeelInfo[] installedLookAndFeels = UIManager.getInstalledLookAndFeels();
		for(LookAndFeelInfo lnf : installedLookAndFeels)
		{
			if(lnf.getClassName().equals(className)) return lnf.getName();
		}
		return "";
	}
}