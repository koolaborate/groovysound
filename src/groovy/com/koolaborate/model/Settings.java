package com.koolaborate.model;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.util.FileHelper;

/***********************************************************************************
 * Settings                                                                        *
 ***********************************************************************************
 * The settings structure is used for saving application settings like whether or  *
 * not to use OpenGL, the current window positions, the last set volume etc.       *
 * All these settings are stored within this structure and when the program is     *
 * shut down, the settings are stored into a file from which they are retrieved    *
 * during the next program start.                                                  *
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
public class Settings
{
	/** file path to the settings file */
	private static String SETTINGS_PATH = System.getProperty("user.dir") + 
		File.separator + "settings.ini";
	
	// the settings that are being tracked
	private int mainWindowX, mainWindowY = -1;
	private String lastFolder = System.getProperty("user.dir");
	private float volume = 0.5f;
	private float balance = 0.0f;
	
	// set 1.0 to default
	private String version = "1.0";

	private boolean hardwareAccellerated;
	private boolean checkForUpdatesAtStart;
	
	/**
	 * Loads the settings from the specified file.
	 * @see SETTINGS_PATH
	 */
	public void loadSettings()
	{
		Properties getprops = new Properties();
		try
		{
			FileInputStream fis = new FileInputStream(SETTINGS_PATH);
			getprops.load(fis);
			fis.close();
		} 
		catch(IOException e)
		{
			// if the file could not be found, it has to be created
			try
			{
				FileHelper.getInstance().createFile(SETTINGS_PATH);
			}
			catch(IOException e2)
			{
				ResourceBundle lang = null;
				try 
			    { 
			    	lang = ResourceBundle.getBundle("resources.maintexts"); 
			    } 
			    catch(MissingResourceException e3) 
			    { 
			    	e3.printStackTrace(); 
			    } 
				
				// this is a severe error! the settings file could not be created, this means that the user cannot write any files
				// or store anything in the current directory
				VistaDialog.showDialog(lang.getString("error.3"), lang.getString("error.4"), 
						lang.getString("error.5"), VistaDialog.ERROR_MESSAGE);
				
				// additionally, print error to console
				e2.printStackTrace();
				
				// give up
				System.exit(-1);
			}
			// save the settings for the first time
			save();
			return;
		}
		
		// read the properties
		mainWindowX = Integer.parseInt(getprops.getProperty("mainwindow_x", "-1"));
		mainWindowY = Integer.parseInt(getprops.getProperty("mainwindow_y", "-1"));
		lastFolder  = getprops.getProperty("lastfolder", System.getProperty("user.dir"));
		volume = Float.parseFloat(getprops.getProperty("volume", "0.5"));
		balance = Float.parseFloat(getprops.getProperty("balance", "0.0"));
		version = getprops.getProperty("version", "1.0");
		hardwareAccellerated = Boolean.parseBoolean(getprops.getProperty("opengl", "false"));
		checkForUpdatesAtStart = Boolean.parseBoolean(getprops.getProperty("checkupdates", "true"));
	}

	/**
	 * @return the current location of the main window
	 */
	public Point getMainWindowLocation()
	{
		if(mainWindowX > 0 && mainWindowY > 0) return new Point(mainWindowX, mainWindowY);
		return null;
	}
	
	/**
	 * Moves the main window to the position where it has last been.
	 * 
	 * @param p the point that indivates the top left point of the window
	 */
	public void setMainWindowLocation(Point p)
	{
		this.mainWindowX = p.x;
		this.mainWindowY = p.y;
	}

	/**
	 * Saves the settings to the specified file.
	 * @see SETTINGS_PATH
	 */
	public void save()
	{
		// save the settings to the file
	    try
	    {
	       Properties saveProps = new Properties();
	       FileOutputStream propOutFile = new FileOutputStream(SETTINGS_PATH);
	       saveProps.setProperty("mainwindow_x", Integer.toString(mainWindowX));
	       saveProps.setProperty("mainwindow_y", Integer.toString(mainWindowY));
	       saveProps.setProperty("lastfolder", lastFolder);
	       saveProps.setProperty("balance", Float.toString(balance));
	       saveProps.setProperty("volume", Float.toString(volume));
	       saveProps.setProperty("version", version);
	       saveProps.setProperty("opengl", Boolean.toString(hardwareAccellerated));
	       saveProps.setProperty("checkupdates", Boolean.toString(checkForUpdatesAtStart));
	       
	       // store the properties in the file
	       saveProps.store(propOutFile, "VibrantPlayer ini file");
	       propOutFile.close();
	    }
	    catch(IOException e)
	    {
	    	e.printStackTrace();
	    }
	}

	// getter and setter
	
	public float getBalance()
	{
		return this.balance;
	}

	public float getVolume()
	{
		return this.volume;
	}

	public void setVolume(float volume)
	{
		this.volume = volume;
	}

	public void setBalance(float balance)
	{
		this.balance = balance;
	}

	public String getLastFolder()
	{
		return lastFolder;
	}

	public void setLastFolder(String lastFolder)
	{
		this.lastFolder = lastFolder;
	}

	public String getVersion()
	{
		return version;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	public boolean isHardwareAccellerated()
	{
		return hardwareAccellerated;
	}

	public void setHardwareAccellerated(boolean hardwareAccellerated)
	{
		this.hardwareAccellerated = hardwareAccellerated;
	}

	public boolean isCheckForUpdatesAtStart()
	{
		return checkForUpdatesAtStart;
	}
	
	public void setCheckForUpdatesAtStart(boolean checkForUpdatesAtStart)
	{
		this.checkForUpdatesAtStart = checkForUpdatesAtStart;
	}
}