package com.koolaborate.converter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.koolaborate.mvc.view.dialogs.VistaDialog;
import com.koolaborate.util.FileHelper;

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
public class MP3ToWAVConverter
{
	private String lang = "en";
	private File[] mp3s;
	private File dstFolder;
	private int amountFiles = 0;
	private int convertedFiles = 0;
	
	private JDialog dialog;
	private JLabel currentFile;
	private JProgressBar progressBar;
	
	
	/**
	 * Constructor.
	 * 
	 * @param srcDir the source directory
	 * @param dstDir the destination directory
	 */
	public MP3ToWAVConverter(String srcDir, String dstDir)
	{
		lang = Locale.getDefault().getLanguage().toLowerCase();
		
		File srcFolder = new File(srcDir);
		// check if the source folder is
		if(!srcFolder.exists() || !srcFolder.isDirectory())
		{
			String errTitle = "Folder does not exist";
			String error = "The source folder does not exist";
			String message = "The source folder you selected ('{srcfolder}') does not exist.\nConversion stopped.";
			if(lang.equals("de"))
			{
				errTitle = "Verzeichnis nicht vorhanden";
				error = "Das Quellverzeichnis existiert nicht";
				message = "Das angegebene Quellverzeichnis ('{srcfolder}') existiert nicht.\nDie Umwandlung wird abgebrochen.";
			}
			message = message.replace("{srcfolder}", srcDir);
			VistaDialog.showDialog(errTitle, error, message, VistaDialog.ERROR_MESSAGE);
			return;
		}
		
		// ensure that the destination folder exists
		dstFolder = new File(dstDir);
		if(!dstFolder.exists())
		{
			try
			{
				dstFolder.createNewFile();
			}
			catch (IOException e)
			{
				String errTitle = "Folder does not exist";
				String error = "The destination folder does not exist";
				String message = "The destination folder you selected ('{dstfolder}') does not exist and could not be created.\nConversion stopped.";
				if(lang.equals("de"))
				{
					errTitle = "Verzeichnis nicht vorhanden";
					error = "Das Zielverzeichnis existiert nicht";
					message = "Das angegebene Zielverzeichnis ('{dstfolder}') existiert nicht und konnte nicht erstellt werden.\nDie Umwandlung wird abgebrochen.";
				}
				message = message.replace("{dstfolder}", dstDir);
				
				VistaDialog.showDialog(errTitle, error, message, VistaDialog.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			}
		}
		
		// get all the mp3 files from the source folder
		mp3s = srcFolder.listFiles(new FileFilter(){
			public boolean accept(File pathname)
			{
				if(pathname.getName().toLowerCase().endsWith(".mp3")) return true;
				else return false;
			}
		});
		amountFiles = mp3s.length;
		dialog = new JDialog();
		dialog.setLayout(new BorderLayout());
		String progress = "Progress...";
		if(lang.equals("de")) progress = "Fortschritt...";
		dialog.getContentPane().setBackground(Color.WHITE);
		dialog.setModal(false);
		dialog.setTitle(progress);
		dialog.setSize(300, 100);
		dialog.setResizable(false);
		dialog.setLocationRelativeTo(null);
	}

	
	/**
	 * Converts the mp3 files to wav files. 
	 */
	public void convertFiles()
	{
		new Thread(new Runnable(){
			public void run()
			{
				// show a status bar dialog
				SwingUtilities.invokeLater(new Runnable(){
					public void run()
					{
						currentFile = new JLabel("");
						currentFile.setBorder(new EmptyBorder(10, 10, 10, 10));
						dialog.add(currentFile, BorderLayout.CENTER);
						
						progressBar = new JProgressBar(0, amountFiles);
						dialog.add(progressBar, BorderLayout.SOUTH);
						dialog.setVisible(true);
					}
				});
						
				// collect all errors
				List<String> errors = new ArrayList<String>();
				convertedFiles = 0;
				
				String dstFolderPath = dstFolder.getAbsolutePath();
				if(!dstFolderPath.endsWith(File.separator)) dstFolderPath += File.separator;
				
				// convert all the files in the folder
				for(File mp3 : mp3s)
				{
					String mp3Path = mp3.getAbsolutePath();
					final String mp3Name = mp3.getName();
					String wavFileName = dstFolderPath + mp3Name.substring(0, mp3Name.length()-".mp3".length()); // cut the '.mp3' file extension
					wavFileName += ".wav";
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							currentFile.setText(mp3Name);
							currentFile.repaint();
						}
					});
					
					try
					{
						System.out.println("--- Start converting " + mp3Path + " ---"); 
						File outFile = new File(wavFileName);
						AudioFileFormat aff = AudioSystem.getAudioFileFormat(mp3);
						System.out.println("Audio Type : " + aff.getType()); 
						AudioInputStream in = AudioSystem.getAudioInputStream(mp3);
						AudioInputStream din = null;
						if(in != null)
						{
							AudioFormat baseFormat = in.getFormat();
							System.out.println("Source Format : " + baseFormat.toString()); 
							AudioFormat decodedFormat = new AudioFormat(
									AudioFormat.Encoding.PCM_SIGNED,
									baseFormat.getSampleRate(),
									16,
									baseFormat.getChannels(),
									baseFormat.getChannels() * 2,
									baseFormat.getSampleRate(),
									false);
							System.out.println("Target Format : " + decodedFormat.toString()); 
							din = AudioSystem.getAudioInputStream(decodedFormat, in);
							wavconvert(din, outFile);
							in.close();
							System.out.println("--- Stop converting" + mp3Path + " ---"); 
						}
					}
					catch (Exception e)
					{
						String err = "Error converting '{mp3Path}'.\nProblem: ";
						if(lang.equals("de"))
						{
							err = "Fehler beim Konvertieren von '{mp3Path}'.\nProblembeschreibung: ";
						}
						err = err.replace("{mp3Path}", mp3Path);
						errors.add(err + e.getMessage());
					}
					
					convertedFiles++;
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							progressBar.setValue(convertedFiles);
							progressBar.repaint();
						}
					});
				}
				
				dialog.dispose();
				
				// if there was an error
				if(errors.size() > 0)
				{
					String errTitle = "Errors occured";
					String error = "Errors occured trying to convert";
					if(lang.equals("de"))
					{
						errTitle = "Fehler aufgetreten";
						error = "Bei der Umwandlung traten Fehler auf";
					}
					VistaDialog.showDialog(errTitle, error, errors, VistaDialog.ERROR_MESSAGE);
				}
				else
				{
					String succTitle = "Conversion complete";
					String success = "Conversion successfully completed";
					String message = "The conversion has been successfully completed. The WAV files can be found under '{dstDir}' and burned onto an audio CD.";
					if(lang.equals("de"))
					{
						succTitle = "Konvertierung erfolgreich";
						success = "Die Konvertierung wurde erfolgreich abgeschlossen";
						message = "Die Konvertierung wurde erfolgreich abgeschlossen. Die WAV-Dateien befinden sich im Ordner '{dstDir}' und können nun auf eine Audio-CD gebrannt werden.";
					}
					message = message.replace("{dstDir}", dstFolder.getAbsolutePath());
					VistaDialog.showDialog(succTitle, success, message, VistaDialog.INFORMATION_MESSAGE);
				}
			}
		}).start();
	}
	
	
	/**
	 * Converts a mp3 file to a wav file.
	 * 
	 * @param din the audio input stream
	 * @param outFile the destination file object
	 * @throws IOException in case an exception trying to write or read a file occurred
	 * @throws LineUnavailableException
	 */
	private void wavconvert(AudioInputStream din, File outFile) throws IOException, 
		LineUnavailableException
	{
		AudioFileFormat.Type targetType = AudioFileFormat.Type.WAVE;
		AudioSystem.write(din, targetType, outFile);
	}
}