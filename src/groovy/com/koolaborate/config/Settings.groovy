package com.koolaborate.config

import java.awt.Point
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.util.MissingResourceException
import java.util.Properties
import java.util.ResourceBundle

import com.koolaborate.mvc.view.dialogs.VistaDialog

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
public class Settings {
	/** file path to the settings file */
	public static String SETTINGS_PATH = System.getProperty("user.dir") +  File.separator + "settings.ini"
	
	// the settings that are being tracked
	int mainWindowX, mainWindowY = -1
	String lastFolder = System.getProperty("user.dir")
	float volume = 0.5f
	float balance = 0.0f
	
	// set 1.0 to default
	String version = "1.0"

	boolean hardwareAccellerated
	boolean checkForUpdatesAtStart
	
	/**
	 * Loads the settings from the specified file.
	 * @see SETTINGS_PATH
	 */
	
	// TODO refactor, split file and settings logic
	def void loadSettings(){
		Properties properties = getProperties()
		
		// read the properties
		mainWindowX = Integer.parseInt(properties.getProperty("mainwindow_x", "-1"))
		mainWindowY = Integer.parseInt(properties.getProperty("mainwindow_y", "-1"))
		lastFolder  = properties.getProperty("lastfolder", System.getProperty("user.dir"))
		volume = Float.parseFloat(properties.getProperty("volume", "0.5"))
		balance = Float.parseFloat(properties.getProperty("balance", "0.0"))
		version = properties.getProperty("version", "1.0")
		hardwareAccellerated = Boolean.parseBoolean(properties.getProperty("opengl", "false"))
		checkForUpdatesAtStart = Boolean.parseBoolean(properties.getProperty("checkupdates", "true"))
	}
	
	protected Properties getProperties(){
		Properties properties = new Properties()
		try{
			FileInputStream fis = new FileInputStream(SETTINGS_PATH)
			properties.load(fis)
			fis.close()
		}catch(Exception e){
			handleLoadError(properties)
		}
		
		return properties
	}

	protected void handleLoadError(Properties properties) {
		// if the file could not be found, it has to be created
		File file = new File(SETTINGS_PATH)
		try{
			file.createNewFile()
		} catch(IOException e2) {
			ResourceBundle lang = null
			try{
				lang = ResourceBundle.getBundle("resources.maintexts")
			} catch(MissingResourceException e3){
				e3.printStackTrace()
			}

			// this is a severe error! the settings file could not be created, this means that the user cannot write any files
			// or store anything in the current directory
			VistaDialog.showDialog(lang.getString("error.3"), lang.getString("error.4"), lang.getString("error.5"), VistaDialog.ERROR_MESSAGE)

			// additionally, print error to console
			e2.printStackTrace()

			// give up
			System.exit(-1)
		}
		// save the settings for the first time
		save()
		properties.load(new FileInputStream(file))
	}

	/**
	 * @return the current location of the main window
	 */
	def Point getMainWindowLocation(){
		if(mainWindowX > 0 && mainWindowY > 0) return new Point(mainWindowX, mainWindowY)
		return null
	}
	
	/**
	 * Moves the main window to the position where it has last been.
	 * 
	 * @param p the point that indivates the top left point of the window
	 */
	def void setMainWindowLocation(Point p){
		if(null == p) return
		this.mainWindowX = p.x
		this.mainWindowY = p.y
	}

	/**
	 * Saves the settings to the specified file.
	 * @see SETTINGS_PATH
	 */
	def void save(){
		// save the settings to the file
	    try{
	       Properties saveProps = new Properties()
	       FileOutputStream propOutFile = new FileOutputStream(SETTINGS_PATH)
	       saveProps.setProperty("mainwindow_x", Integer.toString(mainWindowX))
	       saveProps.setProperty("mainwindow_y", Integer.toString(mainWindowY))
	       saveProps.setProperty("lastfolder", lastFolder)
	       saveProps.setProperty("balance", Float.toString(balance))
	       saveProps.setProperty("volume", Float.toString(volume))
	       saveProps.setProperty("version", version)
	       saveProps.setProperty("opengl", Boolean.toString(hardwareAccellerated))
	       saveProps.setProperty("checkupdates", Boolean.toString(checkForUpdatesAtStart))
	       
	       // store the properties in the file
	       saveProps.store(propOutFile, "VibrantPlayer ini file")
	       propOutFile.close()
	    } catch(IOException e){
	    	e.printStackTrace()
	    }
	}

	// getter and setter
	


}