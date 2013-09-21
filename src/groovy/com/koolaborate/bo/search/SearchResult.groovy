package com.koolaborate.bo.search;

/***********************************************************************************
 * SearchResult                                                                    *
 ***********************************************************************************
 * Class represents one search result (song) to a given query.                     *
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
public class SearchResult
{
	private String songTitle, artist, albumTitle;
	private String albumPath;
	private int songId, albumId = -1;
	
	/**
	 * Constructor.
	 */
	public SearchResult(){}

	// getter and setter
	
	public int getAlbumId()
	{
		return albumId;
	}

	public void setAlbumId(int albumId)
	{
		this.albumId = albumId;
	}

	public String getAlbumPath()
	{
		return albumPath;
	}

	public void setAlbumPath(String albumPath)
	{
		this.albumPath = albumPath;
	}
	
	public int getSongId()
	{
		return songId;
	}

	public void setSongId(int songId)
	{
		this.songId = songId;
	}

	public void setAlbumTitle(String albumTitle)
	{
		this.albumTitle = albumTitle;
	}

	public void setArtist(String artist)
	{
		this.artist = artist;
	}

	public void setSongTitle(String songTitle)
	{
		this.songTitle = songTitle;
	}

	public String getSongTitle()
	{
		return songTitle;
	}

	public String getArtist()
	{
		return artist;
	}

	public String getAlbumTitle()
	{
		return albumTitle;
	}
}