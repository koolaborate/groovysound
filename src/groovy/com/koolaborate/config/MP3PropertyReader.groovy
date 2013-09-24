package com.koolaborate.config

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Map
import java.util.logging.LogManager
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.UnsupportedAudioFileException
import javax.swing.JFrame
import javax.swing.JTextArea
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.audio.AudioHeader
import org.jaudiotagger.tag.Tag
import org.tritonus.share.sampled.TAudioFormat
import org.tritonus.share.sampled.file.TAudioFileFormat

/***********************************************************************************
 * MP3PropertyReader                                                               *
 ***********************************************************************************
 * A class to read id3 tags from mp3 files such as duration and title of a song.   *
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
class MP3PropertyReader{
	public MP3PropertyReader() {
		try {
			LogManager.getLogManager().readConfiguration(new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "log.properties")))
		} catch(Exception e) {
			e.printStackTrace()  
		}
	}
	
	/**
	 * Shows an informational window with some mp3 file tags.
	 * 
	 * 
	 * @param filename the name of the song file to be displayed
	 */
	@Deprecated
	def void showMP3Info(String filename){
		File file = new File(filename)
		AudioFileFormat baseFileFormat = null
		AudioFormat baseFormat = null 
		try{
			baseFileFormat = AudioSystem.getAudioFileFormat(file)
			baseFormat = baseFileFormat.getFormat()
			// TAudioFileFormat properties
			if(baseFileFormat instanceof TAudioFileFormat){
				Map properties = ((TAudioFileFormat)baseFileFormat).properties()
				
				JFrame infoFrame = new JFrame("MP3 Informationen")
				
				StringBuilder sb = new StringBuilder()
				sb.append("Interpret: " + properties.get("author"))
				sb.append("\nTitel:     " + properties.get("title"))
				long duration = (Long)properties.get("duration")
				long min = duration / (1000000*60)
				long sec = duration % (1000000*60)
				sb.append("\nLänge:     " + min + ":" + sec + " min")
				int channels = (Integer)properties.get("mp3.channels")
				String monoStereo = "mono"
				if(channels == 2) monoStereo = "stereo"
				sb.append("\nKanäle:    " + monoStereo)
				int frequency = (Integer)properties.get("mp3.frequency.hz")
				sb.append("\nFrequenz:  " + frequency + " Hz")
				sb.append("\nAlbum:     " + properties.get("album")) 
				sb.append("\nJahr:      " + properties.get("date"))
				String info = sb.toString()
				
				infoFrame.add(new JTextArea(info))
				infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE)
				infoFrame.pack()
				infoFrame.setLocationRelativeTo(null)
				infoFrame.setVisible(true)
				
			}
			
			if(baseFormat instanceof TAudioFormat){
				Map properties = ((TAudioFormat)baseFormat).properties()
				String key = "bitrate"
				Integer val = (Integer) properties.get(key)
				val /= 1000
				System.out.println("Bitrate: " + val + " kHz")
			}
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace()
		} catch (IOException e) {
			e.printStackTrace()
		}
	}
	
	
	/**
	 * Returns the title and duration of a song.
	 * @see getSongTitleAndLength
	 * 
	 * @param albumPath the folder path of the album
	 * @param filename the name of the song
	 * @return an array containing the title and duration of a song as Strings
	 */
	public String[] getSongTitleAndLength(String albumPath, String filename) {
		String songPath = albumPath + File.separator + filename
		return getSongTitleAndLength(songPath)
	}


	/**
	 * Returns the title and duration of a song.
	 * 
	 * @param songPath the path to the song file
	 * @return an array containing the title and duration of a song as Strings
	 */
	public String[] getSongTitleAndLength(String songPath) {
		File file = new File(songPath)
		
		String[] info = new String[2]
		info[0] = file.getName() // song title
		info[1] = "0:00" // duration
		
		AudioFile f
		try{
			f = AudioFileIO.read(file)
			Tag tag = f.getTag()
			AudioHeader h = f.getAudioHeader()
			
			// if the tag could not be extracted, then return the standard values
			if(tag == null) return info
			
			int length =  h.getTrackLength()
			int mins = length / 60
			int secs = length % 60
			info[0] = tag.getFirstTitle()
			String secString = Integer.toString(secs)
			if(secString.length() > 2) secString = secString.substring(0, 2)
			else if(secString.length() < 2) secString = "0" + secString
			info[1] = mins + ":" + secString
		} catch(Exception e) {
			e.printStackTrace()
		}
		
		return info
	}


	/**
	 * Returns the length of a song in milliseconds.
	 * 
	 * @param songPath the path to the song file
	 * @return the duration of the song in milliseconds
	 */
	public long getSongLength(String songPath) {
		long ret = (long)0
		File file = new File(songPath)
		AudioFileFormat baseFileFormat = null
		try {
			baseFileFormat = AudioSystem.getAudioFileFormat(file)
			// TAudioFileFormat properties
			if(baseFileFormat instanceof TAudioFileFormat) {
				Map properties = ((TAudioFileFormat)baseFileFormat).properties()
				ret = (Long)properties.get("duration")
			}
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace() 
		} catch (IOException e) {
			e.printStackTrace()
		} 
		
		return ret
	}
}