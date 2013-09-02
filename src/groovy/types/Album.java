package types;

import java.awt.image.BufferedImage;
import java.util.Map;
import db.Database;

/***********************************************************************************
 * Album                                                                           *
 ***********************************************************************************
 * Class represents one album.                                                     *
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
public class Album
{
	private String title, artist, folderPath;
	private int id, year;
	private BufferedImage preview;
	
	/**
	 * Constructor.
	 */
	public Album()
	{
		id = -1;
	}
	
	
	/**
	 * Method to save the album into the database.
	 * 
	 * @param db the database instance
	 * @return the newly inserted id for the album
	 */
	public int saveIntoDB(Database db)
	{
		this.id = db.insertNewAlbum(this.title, this.artist, this.year, this.folderPath, this.preview);
		return this.id;
	}
	
	
	/**
	 * Loads all values of an album from the database.
	 * 
	 * @param id the id of the album
	 * @param db the database instance
	 */
	public void loadValuesFromDB(int id, Database db)
	{
		Map<String, String> infos = db.getInfosForAlbum(id);
		this.id = id;
		this.artist = infos.get("artist");
		this.folderPath = infos.get("folderpath");
		this.title = infos.get("title");
		this.year = Integer.parseInt(infos.get("albumyear"));
	}

	// getter and setter

	public String getArtist()
	{
		return artist;
	}

	public void setArtist(String artist)
	{
		this.artist = artist;
	}

	public String getFolderPath()
	{
		return folderPath;
	}

	public void setFolderPath(String folderPath)
	{
		this.folderPath = folderPath;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public BufferedImage getPreview()
	{
		return preview;
	}

	public void setPreview(BufferedImage preview)
	{
		this.preview = preview;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public int getYear()
	{
		return year;
	}

	public void setYear(int year)
	{
		this.year = year;
	}
}