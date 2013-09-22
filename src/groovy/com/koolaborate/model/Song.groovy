package com.koolaborate.model;

import com.koolaborate.service.db.Database;

/***********************************************************************************
 * Song                                                                            *
 ***********************************************************************************
 * Class represents one song.                                                      *
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
public class Song
{
	String title, artist, fileName, duration;
	int id, albumId;
	
	public Song(){}

	/**
	 * Method to save the song into the database.
	 * 
	 * @param db the database instance
	 */
	public void saveIntoDB(Database db){
		db.insertNewSong(this.title, this.fileName, this.duration, this.albumId);
	}
}