package types;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.LogManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.tag.Tag;

/***********************************************************************************
 * CurrentSongInfo                                                                 *
 ***********************************************************************************
 * A class that holds information about the currently played song.                 *
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
public class CurrentSongInfo
{
	private String albumPath = "";
	private String songPath = "";
	private String artist, songTitle, albumTitle, year, coverPath, genre = ""; 
	
	private int trackNo = 0;
	private int albumId = -1;
	private long duration = (long) 0;
	private int songId = -1;
	
	private static String folderImg = "folder.jpg";
	
	/** the log4j logger */
	static Logger log = Logger.getLogger(CurrentSongInfo.class.getName());
	
	/**
	 * Constructor.
	 */
	public CurrentSongInfo()
	{
		// disable the logging from JAudioTagger
		try
		{
			LogManager.getLogManager().readConfiguration(new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "log.properties")));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the information about the current song and stores it into the fields.
	 * 
	 * @throws IOException in case the file is non-existent or not readable
	 * @throws TagException if a tag is tried to be read which doesn't exist within the song file
	 */
	public void readSongInfo() throws IOException, TagException
	{
		if(StringUtils.isEmpty(this.songPath)) return;
		
		File sourceFile = new File(this.songPath);
		AudioFile f;
		try
		{
			f = AudioFileIO.read(sourceFile);
			Tag tag = f.getTag();
			AudioHeader h = f.getAudioHeader();
			
			this.artist     = tag.getFirstArtist();
			this.songTitle  = tag.getFirstTitle();
			this.albumTitle = tag.getFirstAlbum();
			this.year 	    = tag.getFirstYear();
			this.genre      = tag.getFirstGenre();
			try
			{
				this.trackNo = Integer.parseInt(tag.getFirstTrack());
			}
			catch(NumberFormatException e)
			{
				this.trackNo = 0;
			}
			
			this.duration = h.getTrackLength();
		}
		catch(Exception e)
		{
			log.debug(e.getMessage());
			// define standard values
			this.artist     = "";
			this.songTitle  = sourceFile.getName();
			this.albumTitle = "";
			this.year 	    = "";
			this.genre      = "";
			this.trackNo    = 0;
		}
	}
	
	/**
	 * Method checks whether a cover image for the album (and thereby for the song) exists.
	 * It has to be in the same directory and be called "folder.jpg". This is the same format that the
	 * Windows(R) MediaPlayer uses.
	 */
	private void checkCoverImageExists()
	{
		if(StringUtils.isEmpty(this.albumPath)) return;
		
		File album = new File(this.albumPath);
		{
			File folderImg = new File(album.getAbsolutePath() + File.separator + CurrentSongInfo.folderImg);
			if(folderImg.exists())
			{
				this.coverPath = folderImg.getAbsolutePath();
			}
			else this.coverPath = null;
		}
	}
	
	/**
	 * Method tries to find the song title from the given file. If neither an ID3V2 nor an ID3V1 song title
	 * can be found, the filename is returned again.
	 * 
	 * @param folder the folder where the song lies in
	 * @param filename the file name of the song
	 * @return the song title if it can be found or the filename otherwise
	 * @throws IOException in case the song file could not be opened
	 * @throws TagException if an incorrect tag was given
	 */
	public String getSongTitleForFile(String folder, String filename) throws IOException, TagException
	{
		String ret = "";
		File sourceFile = new File(folder + File.separator + filename);
	    MP3File mp3file = new MP3File(sourceFile);
	    
		if(mp3file.hasID3v2Tag())
		{
			AbstractID3v2 id3v2 = mp3file.getID3v2Tag();
			if(id3v2 != null)
			{
				ret  = id3v2.getSongTitle();
				if(!StringUtils.isEmpty(ret)) return ret;
			}
		}
		else if(mp3file.hasID3v1Tag()) // mp3file.getID3v1Tag() != null
		{
			ID3v1 id3v1 = mp3file.getID3v1Tag();
			if(id3v1 != null)
			{
				ret  = id3v1.getTitle();
				if(!StringUtils.isEmpty(ret)) return ret;
			}
		}
		
		return filename;
	}
	
	// getter and setter
	
	public void setAlbumPath(String path)
	{
		this.albumPath = path;
		checkCoverImageExists();
	}
	
	public void setSongPath(String path)
	{
		this.songPath = path;
	}
	
	public String getAlbumPath()
	{
		return this.albumPath;
	}

	public String getAlbumTitle()
	{
		return albumTitle;
	}

	public String getArtist()
	{
		return artist;
	}

	public String getCoverPath()
	{
		return coverPath;
	}

	public long getDuration()
	{
		return duration;
	}

	public String getGenre()
	{
		return genre;
	}

	public String getSongTitle()
	{
		return songTitle;
	}

	public int getTrackNo()
	{
		return trackNo;
	}

	public String getYear()
	{
		return year;
	}
	
	public String getSongFilePath()
	{
		return this.songPath;
	}
	
	public String getSongFileName()
	{
		File f = new File(songPath);
		return f.getName();
	}
	
	public int getAlbumId()
	{
		return this.albumId;
	}
	
	public void setAlbumId(int id)
	{
		this.albumId = id;
	}

	public int getSongID()
	{
		return this.songId ;
	}
	
	public void setSongId(int songId)
	{
		this.songId = songId;
	}
}