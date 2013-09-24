package com.koolaborate.converter

import java.awt.BorderLayout
import java.awt.Color

import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JProgressBar

import org.apache.commons.lang3.StringUtils

import com.koolaborate.error.ErrorMessage
import com.koolaborate.error.MessageType;
import com.koolaborate.mvc.view.dialogs.VistaDialog
import com.koolaborate.transaction.TransactionMessage;

/***********************************************************************************
 * MP3ToWAVConverter                                                               *
 ***********************************************************************************
 * A converter to convert mp3 files to wav files. These wav files can then be      *
 * burned onto a 'normal' audio CD. This way this plugin works as the opposite to  *
 * the CD ripper plugin.                                                           *
 *                                                                                 *
 * @see http://www.javazoom.net/services/forums/viewMessage.jsp?message=17047&     *
 *      thread=5611&parent=17045&forum=7                                           *
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
class MP3ToWAVConverter implements Convertable{
	String language
	File[] mp3s
	File destinationFolder
	int amountFiles = 0
	int convertedFiles = 0
	
	JDialog dialog
	JLabel currentFile
	JProgressBar progressBar
	
	
	/**
	 * Constructor.
	 * 
	 * @param srcDir the source directory
	 * @param dstDir the destination directory
	 */
	public MP3ToWAVConverter(){}
	
	def TransactionMessage init_REFACTOR(String sourceFolderLocation, String destinationFolderLocation){
		if(null == sourceFolderLocation || null == destinationFolderLocation) throw new IllegalArgumentException()
		if(StringUtils.isBlank(sourceFolderLocation) || StringUtils.isBlank(destinationFolderLocation)) throw new IllegalArgumentException()
		
		language = Locale.getDefault().getLanguage().toLowerCase()
		
		File srcFolder = new File(sourceFolderLocation)
		// check if the source folder is
		TransactionMessage transactionMessage = new TransactionMessage()  // use model instead
		def errorMessage = handleError(srcFolder, sourceFolderLocation)
		if(null != errorMessage) {
			transactionMessage.errorMessage = errorMessage
			return transactionMessage
		}
		
		// ensure that the destination folder exists
		destinationFolder = new File(destinationFolderLocation)
		if(!destinationFolder.exists())
		{
			try{
				destinationFolder.createNewFile()
			} catch (IOException e) {
				String errTitle = "Folder does not exist"
				String error = "The destination folder does not exist"
				String message = "The destination folder you selected ('{dstfolder}') does not exist and could not be created.\nConversion stopped."
				if(language.equals("de"))
				{
					errTitle = "Verzeichnis nicht vorhanden"
					error = "Das Zielverzeichnis existiert nicht"
					message = "Das angegebene Zielverzeichnis ('{dstfolder}') existiert nicht und konnte nicht erstellt werden.\nDie Umwandlung wird abgebrochen."
				}
				message = message.replace("{dstfolder}", destinationFolderLocation)
				
//				VistaDialog.showDialog(errTitle, error, message, VistaDialog.ERROR_MESSAGE)
				e.printStackTrace()
			}
		}
		
		// get all the mp3 files from the source folder
		mp3s = getMp3sList(srcFolder)
		
		return transactionMessage
	}
	
	def ErrorMessage handleError(File srcFolder, String sourceFolderLocation){
		ErrorMessage errorMessage = null
		
		if(!srcFolder.exists() || !srcFolder.isDirectory()){
			errorMessage = new ErrorMessage()
			errorMessage.errorType = MessageType.ERROR
			
			String errTitle = "Folder does not exist"
			String error = "The source folder does not exist"
			String message = "The source folder you selected ('{srcfolder}') does not exist.\nConversion stopped."
			if(language.equals("de")) {
				errTitle = "Verzeichnis nicht vorhanden"
				error = "Das Quellverzeichnis existiert nicht"
				message = "Das angegebene Quellverzeichnis ('{srcfolder}') existiert nicht.\nDie Umwandlung wird abgebrochen."
			}
			message = message.replace("{srcfolder}", sourceFolderLocation)
//			VistaDialog.showDialog(errTitle, error, message, VistaDialog.ERROR_MESSAGE)
			
			errorMessage.description = message
		}
		
		return errorMessage
	}
	
	def File[] getMp3sList(File srcFolder){
		def mp3s
		
		mp3s = srcFolder.listFiles(new FileFilter(){
			public boolean accept(File pathname)
			{
				if(pathname.getName().toLowerCase().endsWith(".mp3")) return true
				else return false
			}
		})
		
		return mp3s
	}
	
	
	
	
	
	
	
	@Override
	def void init(String sourceFolderLocation, String destinationFolderLocation){
		if(null == sourceFolderLocation || null == destinationFolderLocation) throw new IllegalArgumentException()
		if(StringUtils.isBlank(sourceFolderLocation) || StringUtils.isBlank(destinationFolderLocation)) throw new IllegalArgumentException()
		
		language = Locale.getDefault().getLanguage().toLowerCase()
		
		File srcFolder = new File(sourceFolderLocation)
		// check if the source folder is
		if(!srcFolder.exists() || !srcFolder.isDirectory())
		{
			String errTitle = "Folder does not exist"
			String error = "The source folder does not exist"
			String message = "The source folder you selected ('{srcfolder}') does not exist.\nConversion stopped."
			if(language.equals("de")) {
				errTitle = "Verzeichnis nicht vorhanden"
				error = "Das Quellverzeichnis existiert nicht"
				message = "Das angegebene Quellverzeichnis ('{srcfolder}') existiert nicht.\nDie Umwandlung wird abgebrochen."
			}
			message = message.replace("{srcfolder}", sourceFolderLocation)
			
			
			// TODO REFACTOR push to the view layer
			VistaDialog.showDialog(errTitle, error, message, VistaDialog.ERROR_MESSAGE)
			return
		}
		
		// ensure that the destination folder exists
		destinationFolder = new File(destinationFolderLocation)
		if(!destinationFolder.exists())
		{
			try{
				destinationFolder.createNewFile()
			}
			catch (IOException e)
			{
				String errTitle = "Folder does not exist"
				String error = "The destination folder does not exist"
				String message = "The destination folder you selected ('{dstfolder}') does not exist and could not be created.\nConversion stopped."
				if(language.equals("de"))
				{
					errTitle = "Verzeichnis nicht vorhanden"
					error = "Das Zielverzeichnis existiert nicht"
					message = "Das angegebene Zielverzeichnis ('{dstfolder}') existiert nicht und konnte nicht erstellt werden.\nDie Umwandlung wird abgebrochen."
				}
				message = message.replace("{dstfolder}", destinationFolderLocation)
				
				VistaDialog.showDialog(errTitle, error, message, VistaDialog.ERROR_MESSAGE)
				e.printStackTrace()
				return
			}
		}
		
		// get all the mp3 files from the source folder
		mp3s = srcFolder.listFiles(new FileFilter(){
			public boolean accept(File pathname)
			{
				if(pathname.getName().toLowerCase().endsWith(".mp3")) return true
				else return false
			}
		})
		amountFiles = mp3s.length
		dialog = new JDialog()
		dialog.setLayout(new BorderLayout())
		String progress = "Progress..."
		if("de".equals(language)) progress = "Fortschritt..."
		dialog.getContentPane().setBackground(Color.WHITE)
		dialog.setModal(false)
		dialog.setTitle(progress)
		dialog.setSize(300, 100)
		dialog.setResizable(false)
		dialog.setLocationRelativeTo(null)
	}

	
	/**
	 * Converts the mp3 files to wav files. 
	 */
	@Override
	def void convert() {
		Runnable runnable = new Mp3ToWavConverterThread()
		new Thread(runnable).start()
	}
	
	
	
	
	
	
}







