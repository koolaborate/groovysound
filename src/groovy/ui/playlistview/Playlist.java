package ui.playlistview;

import helper.FileHelper;
import helper.LocaleMessage;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import org.apache.commons.lang3.StringUtils;

import db.Database;
import types.Song;
import ui.decorations.Decorator;
import ui.dialogs.DeleteDialog;
import ui.editid3tag.EditId3TagFrame;
import ui.mainwindow.MainWindow;
import ui.songinfo.SongInfoFrame;

/***********************************************************************************
 * Playlist *
 *********************************************************************************** 
 * The playlist containing PlaylistEntries (a list of songs of the current
 * album). *
 *********************************************************************************** 
 * (c) Impressive Artworx, 2k8 *
 * 
 * @author Manuel Kaess *
 * @version 1.01 *
 *********************************************************************************** 
 *          This file is part of VibrantPlayer. * * VibrantPlayer is free
 *          software: you can redistribute it and/or modify * it under the terms
 *          of the Lesser GNU General Public License as published by * the Free
 *          Software Foundation, either version 3 of the License, or * (at your
 *          option) any later version. * * VibrantPlayer is distributed in the
 *          hope that it will be useful, * but WITHOUT ANY WARRANTY; without
 *          even the implied warranty of * MERCHANTABILITY or FITNESS FOR A
 *          PARTICULAR PURPOSE. See the Lesser * GNU General Public License for
 *          more details. * * You should have received a copy of the Lesser GNU
 *          General Public License * along with VibrantPlayer. If not, see
 *          <http://www.gnu.org/licenses/>. *
 ***********************************************************************************/
public class Playlist extends JPanel{
	private ArrayList<PlaylistEntry> entries;
	private PlaylistEntry selectedEntry;

	private String albumPath;
	private MainWindow window;

	/**
	 * Constructor.
	 */
	public Playlist(MainWindow w){
		this.window = w;
		entries = new ArrayList<PlaylistEntry>();

		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	}

	/**
	 * @return this instance
	 */
	public Playlist getPlaylist(){
		return this;
	}

	/**
	 * Shows a popup at the given location.
	 * 
	 * @param c
	 *            the parent component
	 * @param x
	 *            the x value of the location
	 * @param y
	 *            the y value of the location
	 */
	public void showPopup(Component c, int x, int y){
		JPopupMenu popmen = new JPopupMenu();

		JMenuItem playItem = new JMenuItem(
				LocaleMessage.getString("playlist.play_song"));
		playItem.setIcon(new ImageIcon(
				Playlist.class.getResource("/images/playlist_play.png")));
		playItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				window.getPlayerPanel().playSong();
			}
		});
		popmen.add(playItem);

		JMenuItem infoItem = new JMenuItem(
				LocaleMessage.getString("playlist.informations"));
		infoItem.setIcon(new ImageIcon(
				Playlist.class.getResource("/images/about.png")));
		infoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				new SongInfoFrame(window, getCurrentlySelectedSongPath());
			}
		});
		popmen.add(infoItem);

		JMenuItem id3Item = new JMenuItem(
				LocaleMessage.getString("id3.edit_tags"));
		id3Item.setIcon(new ImageIcon(
				Playlist.class.getResource("/images/tag_small.png")));
		id3Item.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				List<String> song = new ArrayList<String>();
				// song.add(getCurrentlySelectedSongPath());
				if(selectedEntry != null) song.add(selectedEntry.getPath());
				int albumId = window.getDatabase().getAlbumIdForSong(
						getCurrentlySelectedSongID());
				new EditId3TagFrame(window, albumId, albumPath, song);
			}
		});
		popmen.add(id3Item);

		popmen.addSeparator();

		JMenuItem delItem = new JMenuItem(
				LocaleMessage.getString("playlist.del_song"));
		delItem.setIcon(new ImageIcon(
				Playlist.class.getResource("/images/deletesmall.png")));
		delItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0){
				DeleteDialog delDiag = DeleteDialog.showDeleteSongDialog();
				if(delDiag.yesSelected) {
					Database db = window.getDatabase();
					int albumId = -1;

					// del the file from disk (if desired)
					if(delDiag.delFilesSelected) {
						albumId = db.getAlbumIdForSong(getCurrentlySelectedSongID());
						String path = getCurrentlySelectedSongPath();
						if(!StringUtils.isEmpty(path))
							FileHelper.removeFile(path);
					}

					// delete the song from the database
					try {
						String songId = getCurrentlySelectedSongID();
						albumId = db.getAlbumIdForSong(songId);
						db.deleteSong(Integer.parseInt(songId));
						// update the view since the song is gone
						List<Song> songs = db.getSongsForAlbum(albumId);
						clearPlaylist();
						setSongs(songs);
					} catch(NumberFormatException e) {
						e.printStackTrace();
					}
				}
			}
		});
		popmen.add(delItem);

		popmen.show(c, x, y);
	}

	/**
	 * Sets the list of songs.
	 * 
	 * @param songs
	 *            the songs to be filled into the playlist
	 */
	public void setSongs(List<Song> songs){
		entries.clear();
		for(Song s: songs) {
			Decorator d = window.getDecorator();
			PlaylistEntry entry = new PlaylistEntry(this, s.getFileName(),
					s.getDuration(), s.getId(), s.getTitle(),
					d.getSelectionColor2(), d.getSelectionColor1(),
					d.getSelectionAlpha());
			entry.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
			entries.add(entry);
			add(entry);
		}
		add(Box.createVerticalGlue());
		revalidate();
	}

	/**
	 * Clears the playlist.
	 */
	public void clearPlaylist(){
		this.removeAll(); // clear view
		entries.clear(); // clear list of songs
	}

	/**
	 * Sets the playing icon at the currently selected entry.
	 */
	public void setPlayIconAtCurrentEntry(){
		// set the last one not to be playing
		for(PlaylistEntry e: entries) {
			if(e.isPlaying())
				e.setPlaying(false);
			else if(e.isPaused()) e.setPaused(false);
		}
		if(selectedEntry != null) selectedEntry.setPlaying(true);
	}

	/**
	 * Sets the paused icon at the currently selected entry.
	 */
	public void setPausedIconAtCurrentEntry(){
		// set the last one not to be paused
		for(PlaylistEntry e: entries) {
			if(e.isPlaying())
				e.setPlaying(false);
			else if(e.isPaused()) e.setPaused(false);
		}
		if(selectedEntry != null) selectedEntry.setPaused(true);
	}

	/**
	 * Sets the album path.
	 * 
	 * @param path
	 *            the path of the current album
	 */
	public void setAlbumPath(String path){
		this.albumPath = path;
	}

	/**
	 * @return the currently selected song path
	 */
	public String getCurrentlySelectedSongPath(){
		// select the first entry if no entry is yet selected
		if(selectedEntry == null) {
			if(entries.size() > 0)
				selectedEntry = entries.get(0);
			else return null;
		}
		String path = albumPath + File.separator + selectedEntry.getPath();
		path = path.replaceAll("`", "'");
		return path;
	}

	/**
	 * @return the currently selected song id
	 */
	public String getCurrentlySelectedSongID(){
		// select the first entry if no entry is yet selected
		if(selectedEntry == null) {
			selectedEntry = entries.get(0);
		}

		String ret = null;
		if(selectedEntry != null)
			ret = Integer.toString(selectedEntry.getSongId());

		return ret;
	}

	/**
	 * Selects the next song.
	 * 
	 * @return <code>true</code> if there are more songs in the list,
	 *         <code>false</code> if it was the last song
	 */
	public boolean selectNextSong(){
		int selectIndex = entries.indexOf(selectedEntry) + 1;
		// TODO if continuous play is selected, continue with first song in the
		// list
		if(entries.size() <= selectIndex)
			return false;
		else {
			// select next row
			selectedEntry = entries.get(selectIndex);
			return true;
		}
	}

	/**
	 * Selects the previous song.
	 */
	public void selectPreviousSong(){
		int currIndex = entries.indexOf(selectedEntry);
		if(currIndex != 0) currIndex -= 1;
		// select previous row (currIndex)
		selectedEntry = entries.get(currIndex);
	}

	/**
	 * Selects the given entry.
	 * 
	 * @param entry
	 *            the entry to be selected
	 */
	public void setSelectedEntry(PlaylistEntry entry){
		this.selectedEntry = entry;
	}

	/**
	 * @return all entries of the list
	 */
	public ArrayList<PlaylistEntry> getEntries(){
		return entries;
	}

	/**
	 * @return the reference to the main window
	 */
	public MainWindow getWindow(){
		return window;
	}

	/**
	 * Sets the currently selected song as unplayable.
	 */
	public void setCurrentEntryUnplayable(){
		if(selectedEntry != null) selectedEntry.setPlayable(false);
	}

	/**
	 * Removes any icon from the currently selected entry.
	 */
	public void setNoIconAtCurrentEntry(){
		if(selectedEntry != null) {
			selectedEntry.setPlaying(false);
			selectedEntry.setPaused(false);
		}
	}
}