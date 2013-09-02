package mp3;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

/***********************************************************************************
 * ExtendedMP3Info                                                                 *
 ***********************************************************************************
 * This class works as a backup solution for the JAudioTagger library since the    *
 * results given by the farng library are more reliable.                           *
 * The information is used to determine the name, artist and year of a new album.  *
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
public class ExtendedMP3Info
{
	/**
	 * Test method to show some header fields of MP3 files.
	 * 
	 * @param filename the file path of the mp3 song file
	 * @throws IOException if the file could not be read
	 * @throws TagException if a tag is unavailable
	 */
	@Deprecated
	public static void showMP3Info(String filename) throws IOException, TagException
	{
		JFrame infoFrame = new JFrame("MP3 Informationen");
		
		File sourceFile = new File(filename);
	    MP3File mp3file = new MP3File(sourceFile);
		
		StringBuilder sb = new StringBuilder();
		
		if(mp3file.hasFilenameTag()) 
		{
			sb.append("------BASIC INFO----------------------");
			sb.append("\nFilename-Tag: " + mp3file.getFilenameTag());
			sb.append("\nFrequenz: " + mp3file.getFrequency() + " Hz");
			sb.append("\nBitrate: " + mp3file.getBitRate());
		}
		if(mp3file.hasID3v1Tag()) // mp3file.getID3v1Tag() != null
		{
			ID3v1 id3v1 = mp3file.getID3v1Tag();
			if(id3v1 != null)
			{
				sb.append("\n------MP3 ID3 VERSION 1 INFO------------------");
				sb.append("\nInterpret:    " + id3v1.getArtist());
				sb.append("\nGenre:  " +  id3v1.getGenre());
				sb.append("\nAlbum:     " + id3v1.getAlbum());
				sb.append("\nTitel:     " + id3v1.getTitle());
				sb.append("\nAlbumtitel:     " + id3v1.getAlbumTitle());
				sb.append("\nJahr:      " + id3v1.getYear());
			}
		}
		if(mp3file.hasID3v2Tag())
		{
			AbstractID3v2 id3v2 = mp3file.getID3v2Tag();
			if(id3v2 != null)
			{
				sb.append("\n------MP3 ID3 VERSION 2 INFO------------------");
				sb.append("\nInterpret:    " + id3v2.getLeadArtist());
				sb.append("\nTrack#:    " + id3v2.getTrackNumberOnAlbum());
				sb.append("\nGenre:  " +  id3v2.getSongGenre());
				sb.append("\nTitel:     " + id3v2.getSongTitle());
				sb.append("\nAlbumtitel:     " + id3v2.getAlbumTitle());
				sb.append("\nJahr:      " + id3v2.getYearReleased());
//				sb.append("\n------MP3 ID3 VERSION 2 INFO------------------");
//				sb.append("\nInterpret:    " + mp3file.getID3v2Tag().getLeadArtist());
//				sb.append("\nTrack#:    " + mp3file.getID3v2Tag().getTrackNumberOnAlbum());
//				sb.append("\nGenre:  " +  mp3file.getID3v2Tag().getSongGenre());
//				sb.append("\nTitel:     " + mp3file.getID3v2Tag().getSongTitle());
//				sb.append("\nAlbumtitel:     " + mp3file.getID3v2Tag().getAlbumTitle());
//				sb.append("\nJahr:      " + mp3file.getID3v2Tag().getYearReleased());
			}
		}
		if(mp3file.hasLyrics3Tag())
		{
			sb.append("\n------MP3 LYRICS INFO------------------");
			sb.append(mp3file.getLyrics3Tag().toString());
		}
		
		String info = sb.toString();
		
		// writing: setter und danach mp3file.save();
		
		infoFrame.add(new JTextArea(info));
		infoFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		infoFrame.pack();
		infoFrame.setLocationRelativeTo(null);
		infoFrame.setVisible(true);
	}
	
	
	/**
	 * Method returns an array containing album information for a newly inserted album.
	 * 
	 * @param filename the file path of the first song file in the album folder
	 * @return an array containing the title, artist and release year of the album (when set in the song file)
	 * @throws IOException if the song file could not be read
	 * @throws TagException if the song file does not contain the needed tags
	 */
	public static String[] getAlbumInfoFromSongFiles(String filename) throws IOException, TagException
	{
		String[] albumInfo = new String[3];
		albumInfo[0] = ""; // album title
		albumInfo[1] = ""; // album artist
		albumInfo[2] = ""; // album released year
		
		File sourceFile = new File(filename);
	    MP3File mp3file = new MP3File(sourceFile);
		
		if(mp3file.hasID3v2Tag())
		{
			AbstractID3v2 id3v2 = mp3file.getID3v2Tag();
			if(id3v2 != null)
			{
				albumInfo[0] = id3v2.getAlbumTitle();
				albumInfo[1] = id3v2.getLeadArtist();
				albumInfo[2] = id3v2.getYearReleased();
			}
		}
		else if(mp3file.hasID3v1Tag()) 
		{
			ID3v1 id3v1 = mp3file.getID3v1Tag();
			if(id3v1 != null)
			{
				albumInfo[0] = id3v1.getAlbumTitle();
				albumInfo[1] = id3v1.getArtist();
				albumInfo[2] = id3v1.getYear();
			}
		}
		return albumInfo;
	}
}