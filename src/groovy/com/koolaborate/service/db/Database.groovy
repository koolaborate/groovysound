package com.koolaborate.service.db

import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.sql.Blob
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.sql.Statement
import java.util.ArrayList
import java.util.HashMap
import java.util.List
import java.util.Map
import java.util.Properties
import java.util.logging.LogManager

import javax.imageio.ImageIO
import javax.imageio.stream.ImageInputStream

import org.apache.log4j.Logger
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.Tag

import com.koolaborate.bo.search.SearchResult
import com.koolaborate.model.Album
import com.koolaborate.model.Artist
import com.koolaborate.model.Song
import com.koolaborate.mvc.view.albumview.AlbumsOverviewPanel.SORT_MODE

/***********************************************************************************
 * Database                                                                        *
 ***********************************************************************************
 * This class handles all activities that deal with the database. Whenever data    *
 * has to be inserted, deleted or retrieved, use this class.                       *
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
public class Database{
	/** the log4j logger */
	static Logger log = Logger.getLogger(Database.class.getName())
	
	String framework = "embedded"
    String driver = "org.apache.derby.jdbc.EmbeddedDriver"
    String protocol = "jdbc:derby:"
    
    int maxSize = 32672
    
    Connection connection
	
	/**
	 * Constructor.
	 */
	public Database(){
		/*
		 * The driver is installed by loading its class.
         * In an embedded environment, this will start up Derby, since it is not already running.
		 */
		try{
			Class.forName(driver).newInstance()
			
			log.debug("Database driver loaded.")
			
			connection = null
			Properties props = new Properties()
			props.put("user", "AlbumPlayer")
			props.put("password", "music")
			
			/*
			 * The connection specifies create=true to cause
			 * the database to be created. To remove the database,
			 * remove the directory derbyDB and its contents.
			 * The directory derbyDB will be created under
			 * the directory that the system property
			 * derby.system.home points to, or the current
			 * directory if derby.system.home is not set.
			 */
			connection = DriverManager.getConnection(protocol + "derbyDB;create=true", props)
			
			log.debug("Connected to database derbyDB.")
			
			connection.setAutoCommit(false)
			
			// check if the database is set up already
			checkDatabase()
		} catch(InstantiationException e){
			log.error(e.getMessage())
		} catch(IllegalAccessException e){
			log.error(e.getMessage())
		} catch(ClassNotFoundException e){
			log.error(e.getMessage())
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		
		// disable logging for JAudioTagger
		try{
			LogManager.getLogManager().readConfiguration(new FileInputStream(new File(System.getProperty("user.dir") + File.separator + "log.properties")))
		} catch(Exception e){
			log.warn(e.getMessage())
		}
	}
	
	
	/**
	 * Method checks whether the database is already setup or needs to be set up.
	 */
	private void checkDatabase(){
		ResultSet resultSet
		try{
			resultSet = connection.getMetaData().getTables("%", "%", "%", ["TABLE"])
			boolean shouldCreateTable = true
			
			while(resultSet.next()){
				if(resultSet.getString("TABLE_NAME").equalsIgnoreCase("albums")){
					shouldCreateTable = false
				}
			}
			resultSet.close()
			
			if(shouldCreateTable){
				log.info("Setting up database. Creating tables...")
				createDatabase()
			}
		} catch (SQLException e){
			log.error(e.getMessage())
		}
	}


	/**
	 * Sets up the database and creates all necessary tables.
	 */
	public void createDatabase(){
		if(connection != null){
			try{
				Statement s = connection.createStatement()
				//TYPES: date float int varchar(10)
				s.execute("CREATE TABLE albums(id int, title varchar(100), artist varchar(80), albumyear int, " + "folderpath varchar(300), preview blob(1M))")
	            log.info("Created table albums")
	            
	            s.execute("CREATE TABLE songs(id int, album_id int, duration varchar(6), title varchar(80), " + "filename varchar(80))")
	            log.info("Created table songs")
	            
	            s.execute("CREATE TABLE artists(id int, name varchar(80), text varchar(" + maxSize + "), pic blob(5M))")
	            log.info("Created table artists")
	            
	            log.info("Tables successfully created.")
	            
	            connection.commit()
	            s.close()
			} catch(SQLException e){
				log.error(e.getMessage())
			}
		}
	}
	

	/**
	 * Method closes connection to the database.
	 */
	public void shutDownConnection(){
		boolean gotSQLExc = false
		if(framework.equals("embedded")){
	        try{
            	if(connection != null) connection.close()
                DriverManager.getConnection("jdbc:derby:;shutdown=true")
            } catch(SQLException se){
                gotSQLExc = true
            }

            if(!gotSQLExc){
            	log.warn("Database did not shut down normally!")
            } else{
                log.debug("Database shut down normally.")
            }
        }
	}


	/**
	 * Method executes the given SQL String.
	 * 
	 * @param sql the String to be executed immediately
	 */
	public void executeSQL(String sql){
		Statement s
		try {
			s = connection.createStatement()
			System.out.println(sql)
			s.execute(sql)
			connection.commit()
			s.close()
		} catch (SQLException e){
			log.error(e.getMessage())
		}
	}
	
	
	/**
	 * Returns a list containing all available albums in the database.
	 * 
	 * @param sorting the desired sorting method (may be <code>null</code>
	 * @return a list containing all albums
	 */
	public List<Album> getAllAlbums(SORT_MODE sorting){
		ArrayList<Album> albums = new ArrayList<Album>()
		
		//get them from the db
		Statement s
		try{
			String sortSQL = "ORDER BY title ASC"
			if(sorting == SORT_MODE.SORT_ALBUMTITLE)    sortSQL = "ORDER BY title ASC"
			else if(sorting == SORT_MODE.SORT_ARTIST)   sortSQL = "ORDER BY artist ASC"
			else if(sorting == SORT_MODE.SORT_SHOW_ALL) sortSQL = "ORDER BY title ASC"
			
			s = connection.createStatement()
			ResultSet rs = s.executeQuery("SELECT id, title, artist, albumyear, folderpath, preview " + "FROM albums " + sortSQL)
			
			while(rs.next()){
				Album a = new Album()
				a.setArtist(rs.getString("artist"))
				a.setFolderPath(rs.getString("folderpath"))
				a.setId(rs.getInt("id"))
				a.setTitle(rs.getString("title"))
				a.setYear(rs.getInt("albumyear"))
				Blob b = rs.getBlob("preview")
				BufferedImage preview = null
				if(b != null){
					try{
						ImageInputStream iis = ImageIO.createImageInputStream(b.getBinaryStream())
						preview = ImageIO.read(iis)
					} catch (IOException e) {
						e.printStackTrace()
						preview = null
					}
				}
				a.setPreview(preview)
				albums.add(a)
			}
			rs.close()
			s.close()
		}
		catch (SQLException e) {
			e.printStackTrace()
		}
		return albums
	}
	

	/**
	 * Method is used to insert a new album into the database.
	 * 
	 * @param title the album title
	 * @param artist the artist name of the album
	 * @param year the year in which the album has been released
	 * @param folderPath the path where the album song files can be found on the hard disc
	 * @param preview a little preview image
	 * @return the newly inserted album id
	 */
	public int insertNewAlbum(String title, String artist, int year, String folderPath, BufferedImage preview){
		int newId = 0
		Statement s
		try {
			s = connection.createStatement()
			ResultSet rs = s.executeQuery("SELECT MAX(id) FROM albums")
			if(rs.next()){
				newId = rs.getInt(1)
			}
			rs.close()

			newId++
			
			PreparedStatement prep = connection.prepareStatement("INSERT INTO albums(id, title, artist, albumyear, folderpath, preview) " + "VALUES(?, ?, ?, ?, ?, ?)")
			
			prep.setInt(1, newId)
			prep.setString(2, title)
			prep.setString(3, artist)
			prep.setInt(4, year)
			prep.setString(5, folderPath)
			
			if(preview != null){
				ByteArrayOutputStream out = new ByteArrayOutputStream()
				try{
					ImageIO.write(preview, "jpeg", out)
				} catch (IOException e){
					e.printStackTrace()
				}
				byte[] buf = out.toByteArray()
				ByteArrayInputStream inStream = new ByteArrayInputStream(buf)
				
				prep.setBinaryStream(6, inStream, inStream.available())
			} else{
				prep.setBinaryStream(6, null)
			}
			
//			String sql = "INSERT INTO albums (id, title, artist, albumyear, folderpath, pic) " +
//				"VALUES("+ newId +", '"+ title +"', '"+ artist +"', "+ year +", '"+ folderPath +"')";
//			s.execute(sql);
			
			prep.execute()
			
			connection.commit()
			s.close()
			prep.close()
		}
		catch(SQLException e){
			log.error(e.getMessage())
		}
		return newId
	}


	/**
	 * Method is used to insert a new song into the database.
	 * 
	 * @param title the song title
	 * @param fileName the file name of the song file, for example "01 - Song 01.mp3"
	 * @param duration the duration of the song as a String, for example "5:13"
	 * @param albumId the corresponding album id for the song
	 */
	public void insertNewSong(String title, String fileName, String duration, int albumId)
	{
		String newTitle = title.replaceAll("'", "`")
		String newFileName = fileName.replaceAll("'", "`")
		
		int newId = 0
		Statement s
		try{
			s = connection.createStatement()
			ResultSet rs = s.executeQuery("SELECT MAX(id) FROM songs")
			if(rs.next()){
				newId = rs.getInt(1)
			}
			rs.close()

			newId++
			String sql = "INSERT INTO songs (id, album_id, title, filename, duration) " + "VALUES(" + newId + ", " + albumId + ", '" + newTitle + "', '" + newFileName + "', '" + duration + "')"
			s.execute(sql)
			connection.commit()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
	}


	/**
	 * Returns a list containing all songs for the given album.
	 * 
	 * @param albumId the album id
	 * @return a list containing all songs for the given album
	 */
	public List<Song> getSongsForAlbum(int albumId){
		List<Song> songs = new ArrayList<Song>()
		
		Statement s
		try{
			s = connection.createStatement()
			ResultSet rs = s.executeQuery("SELECT id, title, filename, duration FROM songs WHERE album_id = " + albumId + " ORDER BY id ASC")
			while(rs.next()){
				Song song = new Song()
				song.setId(rs.getInt("id"))
				song.setAlbumId(albumId)
				song.setDuration(rs.getString("duration"))
				song.setFileName(rs.getString("filename"))
				song.setTitle(rs.getString("title"))
				songs.add(song)
			}
			rs.close()
			s.close()
		}
		catch(SQLException e){
			log.error(e.getMessage())
		}
		
		return songs
	}
	
	
	/**
	 * Deletes an album and all of its songs from the database.
	 * 
	 * @param albumId the album id of the album to be deleted
	 */
	public void deleteAlbum(int albumId){
		Statement s
		try{
			s = connection.createStatement()
			s.execute("DELETE FROM songs WHERE album_id = " + albumId)
			s.execute("DELETE FROM albums WHERE id = " + albumId)
			connection.commit()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
	}


	/**
	 * Deletes the song with the specified id from the database.
	 * 
	 * @param songId the id of the song to be deleted
	 */
	public void deleteSong(int songId){
		Statement s
		try{
			s = connection.createStatement()
			s.execute("DELETE FROM songs WHERE id = " + songId)
			connection.commit()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
	}


	/**
	 * Returns the complete path to the given album.
	 * 
	 * @param albumId the album id the song belongs to
	 * @return the complete path to the album
	 */
	public String getAlbumPath(int albumId){
		Statement s
		String folder = null
		try{
			s = connection.createStatement()
			
			// then get the album path
			ResultSet rs = s.executeQuery("SELECT folderpath FROM albums WHERE id = " + albumId)
			if(rs.next()){
				folder = rs.getString("folderpath")
			}
			
			rs.close()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		
		return folder
	}
	
	
	/**
	 * Returns a hash map containg information about a given album.
	 * 
	 * @param albumId the album id
	 * @return a HashMap containing the id, title, artist, year and path of the album
	 */
	public Map<String, String> getInfosForAlbum(int albumId){
		HashMap<String, String> ret = new HashMap<String, String>()
		
		Statement s
		try{
			s = connection.createStatement()
			
			// get all album information
			ResultSet rs = s.executeQuery("SELECT title, artist, albumyear, folderpath FROM albums WHERE id = " + albumId)
			if(rs.next()){
				ret.put("id", Integer.toString(albumId))
				ret.put("title", rs.getString("title"))
				ret.put("artist", rs.getString("artist"))
				ret.put("albumyear", Integer.toString(rs.getInt("albumyear")))
				ret.put("folderpath", rs.getString("folderpath"))
			}
			
			rs.close()
			s.close()
		}
		catch(SQLException e){
			log.error(e.getMessage())
		}
		
		return ret
	}


	/**
	 * Returns the album id for the given song.
	 * 
	 * @param songId the id of the song
	 * @return the album id the song belongs to
	 */
	public int getAlbumIdForSong(String songId)
	{
		Statement s
		int albumId = -1
		try{
			s = connection.createStatement()
			
			// then get the album path
			ResultSet rs = s.executeQuery("SELECT album_id FROM songs WHERE id = " + songId)
			if(rs.next()){
				albumId = rs.getInt("album_id")
			}
			
			rs.close()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		
		return albumId
	}


	/**
	 * Returns the album specified through the ID.
	 * 
	 * @param albumId the album id
	 * @return the album with the given id
	 */
	public Album getAlbumById(int albumId){
		Album a = new Album()
		Statement s
		
		try{
			s = connection.createStatement()
			
			// get all album information
			ResultSet rs = s.executeQuery("SELECT title, artist, albumyear, folderpath, preview FROM albums WHERE id = " + albumId)
			if(rs.next()){
				a.setArtist(rs.getString("artist"))
				a.setFolderPath(rs.getString("folderpath"))
				a.setTitle(rs.getString("title"))
				a.setYear(rs.getInt("albumyear"))
				a.setId(albumId)
				Blob b = rs.getBlob("preview")
				BufferedImage preview = null
				if(b != null){
					try{
						ImageInputStream iis = ImageIO.createImageInputStream(b.getBinaryStream())
						preview = ImageIO.read(iis)
					} catch (IOException e){
						log.error(e.getMessage())
						preview = null
					}
				}
				a.setPreview(preview)
			}
			rs.close()
			s.close()
		}
		catch(SQLException e){
			log.error(e.getMessage())
		}
		
		return a
	}


	/**
	 * Updates the given album.
	 * 
	 * @param a the given album to be updated
	 */
	public void updateAlbum(Album a)
	{
		try{
			PreparedStatement prep = connection.prepareStatement("UPDATE albums SET title = ?, artist = ?, albumyear = ?, folderpath = ?, preview = ? " + "WHERE id = ?")
			
			prep.setString(1, a.getTitle())
			prep.setString(2, a.getArtist())
			prep.setInt(3, a.getYear())
			prep.setString(4, a.getFolderPath())
			if(a.getPreview() != null){
				ByteArrayOutputStream out = new ByteArrayOutputStream()
				try{
					ImageIO.write(a.getPreview(), "jpeg", out)
				} catch (IOException e){
					log.error(e.getMessage())
				}
				byte[] buf = out.toByteArray()
				ByteArrayInputStream inStream = new ByteArrayInputStream(buf)
				
				prep.setBinaryStream(5, inStream, inStream.available())
			} else{
				prep.setBinaryStream(5, null)
			}
			
			prep.setInt(6, a.getId())
			
			prep.executeUpdate()
			
			connection.commit()
			prep.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
	}
	
	
	/**
	 * Returns the artist object for an artist if he/she is in the database.
	 * Otherwise, <code>null</code> is returned.
	 * 
	 * @param name the name of the artist (will be trimmed)
	 * @return the artist or <code>null</code>
	 */
	public Artist getArtistByName(String name){
		Statement s
		try{
			s = connection.createStatement()
			
			// get all album information
			ResultSet rs = s.executeQuery("SELECT id, text, pic FROM artists WHERE name LIKE '" + name + "'")
			if(rs.next()){
				Artist a = new Artist()
				a.setName(name)
				a.setId(rs.getInt("id"))
				a.setDescription(rs.getString("text"))
				Blob b = rs.getBlob("pic")
				BufferedImage pic = null
				if(b != null){
					try{
						ImageInputStream iis = ImageIO.createImageInputStream(b.getBinaryStream())
						pic = ImageIO.read(iis)
					} catch (IOException e){
						e.printStackTrace()
						pic = null
					}
				}
				a.setPic(pic)
				return a
			}
			rs.close()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		return null
	}
	
	
	/**
	 * Inserts a new artist into the database.
	 * 
	 * @param name the name of the band or singer
	 * @param description the description text
	 * @param pic the picture of the artist
	 * @return the newly created id for the artist
	 */
	public int insertNewArtist(String name, String description, BufferedImage pic){
		int newId = 0
		
		if(description.length() > maxSize){
			description = description.substring(0, maxSize-3) + "..."
		}
		
		Statement s
		try{
			s = connection.createStatement()
			ResultSet rs = s.executeQuery("SELECT MAX(id) FROM artists")
			if(rs.next()){
				newId = rs.getInt(1)
			}
			rs.close()
			newId++
			
			PreparedStatement prep = conn.prepareStatement("INSERT INTO artists(id, name, text, pic) " + "VALUES(?, ?, ?, ?)")
			
			prep.setInt(1, newId)
			prep.setString(2, name)
			prep.setString(3, description)
			
			if(pic != null){
				ByteArrayOutputStream out = new ByteArrayOutputStream()
				try{
					ImageIO.write(pic, "jpeg", out)
				} catch (IOException e){
					e.printStackTrace()
				}
				byte[] buf = out.toByteArray()
				ByteArrayInputStream inStream = new ByteArrayInputStream(buf)
				
				prep.setBinaryStream(4, inStream, inStream.available())
			} else{
				prep.setBinaryStream(4, null)
			}
			
			prep.execute()
			
			connection.commit()
			s.close()
			prep.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		return newId
	}


	/**
	 * Updates an already saved artist.
	 * 
	 * @param a the new artist object
	 */
	public void updateArtist(Artist a){
		String description = a.getDescription()
		if(description.length() > maxSize){
			description = description.substring(0, maxSize-3) + "..."
			a.setDescription(description)
		}
		
		try{
			PreparedStatement prep = connection.prepareStatement("UPDATE artists SET name = ?, text = ?, pic = ? " + "WHERE id = ?")
			
			prep.setString(1, a.getName())
			prep.setString(2, a.getDescription())
			if(a.getPic() != null){
				ByteArrayOutputStream out = new ByteArrayOutputStream()
				try{
					ImageIO.write(a.getPic(), "jpeg", out)
				} catch (IOException e){
					log.error(e.getMessage())
				}
				byte[] buf = out.toByteArray()
				ByteArrayInputStream inStream = new ByteArrayInputStream(buf)
				
				prep.setBinaryStream(3, inStream, inStream.available())
			} else{
				prep.setBinaryStream(3, null)
			}
			prep.setInt(4, a.getId())
			
			prep.executeUpdate()
			
			connection.commit()
			prep.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
	}


	/**
	 * Returns whether or not an album is already in the database.
	 * 
	 * @param artist the name of the artist of the album
	 * @param title the album's title
	 * @return <code>true</code> if it exists alredy, <code>false</code> otherwise
	 */
	public boolean checkAlbumAlreadyInDB(String artist, String title){
		boolean ret = false
		
		Statement s
		try{
			s = connection.createStatement()
			ResultSet rs = s.executeQuery("SELECT id FROM albums WHERE title LIKE '" + title + "' AND artist LIKE '" + artist + "'")
			if(rs.next()){
				ret = true
			}
			rs.close()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
			ret = false
		}
		
		return ret
	}


	/**
	 * Updates all given songs in the database.
	 * 
	 * @param albumId the id of the album the song belongs to
	 * @param albumPath the path of the album
	 * @param songFileNames the file name to the songs being updated
	 */
	public void updateSongs(int albumId, String albumPath, List<String> songFileNames){
		for(String songFileName : songFileNames){
			String songFilePath = albumPath + File.separator + songFileName
			File file = new File(songFilePath)
			
			// maybe the song name has been modified to be able to save the name into the database
			// therefore, 's have been changed to `s#
			if(!file.exists() && songFilePath.contains("`")){
				songFilePath = songFilePath.replaceAll("`", "'")
				file = new File(songFilePath)
			}
			
			if(file.exists()){
				AudioFile f
				try{
					f = AudioFileIO.read(file)
					Tag tag = f.getTag()
					
					String newTitle = tag.getFirstTitle()
					
					// mask SQL specific commands
					newTitle = newTitle.replaceAll("'", "`")
					songFilePath = songFilePath.replaceAll("'", "`")
					
					Statement s
					try
					{
						s = connection.createStatement()
						
						// update the song
						s.executeUpdate("UPDATE songs SET title = '" + newTitle + "' WHERE filename = '" + songFileName + "'")
						connection.commit()
						s.close()
					} catch(SQLException e){
						log.error(e.getMessage())
					}
				} catch(Exception e){
					log.error(e.getMessage())
				}
			}
		}
		
	}


	/**
	 * Returns a list containing all song file paths for the given album.
	 * 
	 * @param albumId the id of the album
	 * @return a list containing all file paths of the songs
	 */
	public List<String> getSongPathListForAlbum(int albumId)
	{
		List<String> songPaths = new ArrayList<String>()
		
		Statement s
		try{
			s = connection.createStatement()
			
			// get the song paths
			
			ResultSet rs = s.executeQuery("SELECT s.filename, a.folderpath FROM songs s, albums a WHERE s.album_id = a.id AND a.id = " + albumId)
			while(rs.next()){
				String path = rs.getString("folderpath") + File.separator + rs.getString("filename")
				songPaths.add(path)
			}
			rs.close()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		return songPaths
	}


	/**
	 * Returns a list of search results for the given search request.
	 * 
	 * @param search the String to be found
	 * @return a list containing all search results
	 */
	public ArrayList<SearchResult> getSearchResults(String search)
	{
		ArrayList<SearchResult> results = new ArrayList<SearchResult>()
		
		Statement s
		try{
			s = connection.createStatement()
			
			search = search.toUpperCase()
			
			String searchSql = "SELECT a.folderpath, a.title, a.artist, s.title, s.id, a.id " +
							   "FROM songs s, albums a " +
							   "WHERE s.album_id = a.id " +
							   "AND (UPPER(a.artist) LIKE '%" + search + "%' OR UPPER(a.title) LIKE '%" + search + "%' OR UPPER(s.title) LIKE '%" + search + "%') " +
							   "ORDER BY a.artist ASC, a.title ASC, s.title ASC"
			
			ResultSet rs = s.executeQuery(searchSql)
			while(rs.next()){
				SearchResult res = new SearchResult()
				res.setAlbumPath(rs.getString(1))
				res.setAlbumTitle(rs.getString(2))
				res.setArtist(rs.getString(3))
				res.setSongTitle(rs.getString(4))
				res.setSongId(rs.getInt(5))
				res.setAlbumId(rs.getInt(6))
				results.add(res)
			}
			rs.close()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		return results
	}


	/**
	 * Returns a list containing all song file names for a given album.
	 * 
	 * @param albumId the album id the songs belong to
	 * @return a list containing all file names
	 */
	public List<String> getSongFileNamesForAlbum(int albumId){
		List<String> songPaths = new ArrayList<String>()
		
		Statement s
		try{
			s = connection.createStatement()
			
			// get the song file names
			ResultSet rs = s.executeQuery("SELECT filename FROM songs WHERE album_id = " + albumId)
			while(rs.next()){
				songPaths.add(rs.getString("filename"))
			}
			rs.close()
			s.close()
		} catch(SQLException e){
			log.error(e.getMessage())
		}
		return songPaths
	}
}