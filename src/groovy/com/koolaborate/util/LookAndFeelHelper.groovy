package com.koolaborate.util;

import java.util.ArrayList;
import java.util.List;
import javax.swing.UIManager;

/***********************************************************************************
 * LookAndFeelHelper                                                               *
 ***********************************************************************************
 * A helper class that deals with look and feel settings.                          *
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
public class LookAndFeelHelper
{
	// available look and feels
	public static final String PLAF_METAL     = "javax.swing.plaf.metal.MetalLookAndFeel";
	public static final String PLAF_WINDOWS   = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	public static final String PLAF_MOTIF     = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
	public static final String PLAF_MAC       = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
	
    /**
     * Returns the class name for the given look and feel name.
     * 
     * @param lnfName the name of the look and feel
     * @return the class name of the given look and feel
     */
    public static String getClassNameForLnF(String lnfName)
    {
    	String ret = null;
    	UIManager.LookAndFeelInfo[] ls = UIManager.getInstalledLookAndFeels();
    	for(UIManager.LookAndFeelInfo l : ls)
    	{
    		if(l.getName().equals(lnfName))
    		{
    			ret = l.getClassName();
    			break;
    		}
    	}
    	return ret;
    }
    
    
    /**
     * Returns all available look and feel names.
     * 
     * @return a list containing all look and feel names
     */
    public static List<String> getAllLookAndFeelNames()
    {
    	// build complete lnf list
		List<String> lnfs = new ArrayList<String>();
		UIManager.LookAndFeelInfo[] ls = UIManager.getInstalledLookAndFeels();
		for(UIManager.LookAndFeelInfo l : ls)
		{
			lnfs.add(l.getName());
		}
    	return lnfs;
    }
	
    
    /**
     * Returns the name of the currently set look and feel.
     * 
     * @return the name of the currently set look and feel
     */
    public static String getCurrentLookName()
    {
    	return UIManager.getLookAndFeel().getName();
    }
    
    
    /**
     * Sets the system standard look and feel.
     */
    public static void setStandardLookAndFeel()
    {
    	OSHelper.setDefaultLookAndFeel();
    }
}