package ui.playlistview;

import java.awt.Dimension;
import java.io.File;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import types.Song;
import ui.mainwindow.MainWindow;

/***********************************************************************************
 * PlaylistPanel                                                                   *
 ***********************************************************************************
 * A panel that contains the playlist.                                             *
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
public class PlaylistPanel extends JPanel
{
	private Playlist playlist;
	private File albumFolder;
	private MainWindow window;
	private int albumId = -1;
	
	/**
	 * Constructor.
	 */
	public PlaylistPanel(MainWindow w)
	{
		this.window = w;
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		setPreferredSize(new Dimension(300, Integer.MAX_VALUE));
		
		playlist = new Playlist(window);

		EmptyBorder empty = new EmptyBorder(0, 0, 0, 0);
		JScrollPane scroll = new JScrollPane(playlist);
		scroll.setOpaque(false);
		scroll.setBorder(empty);
		scroll.getViewport().setOpaque(false);
		scroll.setViewportBorder(empty);
		add(scroll);
	}
	
	
	/**
	 * Sets the album folder.
	 * 
	 * @param folder the folder of the album to be set
	 */
	public void setAlbumFolder(File folder)
	{
		playlist.clearPlaylist();
		this.albumFolder = folder;
		playlist.setAlbumPath(albumFolder.getAbsolutePath());
	}
	
	
	/**
	 * Refreshes the song list in the playlist.
	 */
	public void refreshSongList()
	{
		playlist.clearPlaylist();
		List<Song> songs = window.getDatabase().getSongsForAlbum(albumId);
		playlist.setSongs(songs);
	}

	
	/**
	 * @return the currently selected song path
	 */
	public String getCurrentlySelectedSong()
	{
		return this.playlist.getCurrentlySelectedSongPath();
	}
	
	
	/**
	 * @return the album id of the currently selected song
	 */
	public int getCurrentlySelectedSongAlbumId()
	{
		int ret = -1;
		try
		{
			ret = window.getDatabase().getAlbumIdForSong(this.playlist.getCurrentlySelectedSongID());
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	
	/**
	 * Selects the next song in the playlist.
	 * 
	 * @return <code>true</code> if there is another song in the playlist, <code>false</code> otherwise.
	 */
	public boolean selectNextSong()
	{
		return playlist.selectNextSong();
	}

	
	/**
	 * Selects the previous song.
	 */
	public void selectPreviousSong()
	{
		playlist.selectPreviousSong();
	}

	
	/**
	 * @return the playlist
	 */
	public Playlist getPlaylist()
	{
		return this.playlist;
	}

	
	/**
	 * Sets the album id of the current album.
	 * 
	 * @param albumId the album id to be set
	 */
	public void setAlbumId(int albumId)
	{
		this.albumId = albumId;
	}

	
	/**
	 * @return the id of the currently selected song
	 */
	public int getCurrentlySelectedSongID()
	{
		return Integer.parseInt(this.playlist.getCurrentlySelectedSongID());
	}
}