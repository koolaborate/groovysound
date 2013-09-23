package com.koolaborate.mvc.view.mainwindow;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import com.koolaborate.model.CurrentSongInfo;
import com.koolaborate.mvc.controller.PlaybackController;
import com.koolaborate.mvc.view.albumview.AlbumsOverviewPanel;
import com.koolaborate.mvc.view.albumview.AlbumsOverviewPanel.SORT_MODE;
import com.koolaborate.mvc.view.mainwindow.MainWindow.NAVIGATION
import com.koolaborate.mvc.view.navigation.NavigationPanel;
import com.koolaborate.mvc.view.optionscreen.OptionScreen;
import com.koolaborate.mvc.view.playlistview.CoverAndInfoPanel;
import com.koolaborate.mvc.view.playlistview.CoverPanel;
import com.koolaborate.mvc.view.playlistview.PlaylistPanel;
import com.koolaborate.mvc.view.playlistview.SongInfoPanel;
import com.koolaborate.mvc.view.playlistview.TimeElapsedPanel;
import com.koolaborate.service.db.Database;


/***********************************************************************************
 * CenterPanel                                                                     *
 ***********************************************************************************
 * The main panel in the center of the main window. This panel can show various    *
 * views.                                                                          *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.01                                                                   *
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
class CenterPanel extends JPanel{
	private static final long serialVersionUID = -1865306352685281295L;
	NavigationPanel navigationPanel;
	PlaybackController playerControls;
	PlaylistPanel playlistPanel;
	CoverAndInfoPanel coverAndInfoPanel; 
	CoverPanel coverPanel;
	SongInfoPanel infoPanel;
	TimeElapsedPanel timeElapsedPanel;
	
	/** holds all the contents of the center panel */
	JPanel contentPanel;
	
	JPanel mainPanel;
	MainWindow mainWindow;
	Database db;
	
	JScrollPane scroll;
	AlbumsOverviewPanel albumsPanel;
	
	
	/**
	 * Constructor.
	 * 
	 * @param window the main window
	 */
	public initializeMainWindow(MainWindow window)
	{
		this.mainWindow = window;
		this.db = window.getDatabase();
		
		setLayout(new BorderLayout());
		
		this.contentPanel = window.getDecorator().getMainBackgroundPanel();
		
		contentPanel.setLayout(new BorderLayout());
		
		// playlist panel
		playlistPanel = new PlaylistPanel(window);

		// navigation
		navigationPanel = new NavigationPanel(this, window);
		contentPanel.add(navigationPanel, BorderLayout.NORTH);
		
		// side panels (no function) 
		contentPanel.add(window.getDecorator().getLeftSidePanel(), BorderLayout.WEST);
		contentPanel.add(window.getDecorator().getRightSidePanel(), BorderLayout.EAST);
		
		// the main panel in the center
		mainPanel = new JPanel();
		mainPanel.setPreferredSize(new Dimension(300, 300));
		mainPanel.setOpaque(false);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.setName("DROP");
		
		createPanels();
		
		contentPanel.add(mainPanel, BorderLayout.CENTER);
		
		// player controls (play button, stop button, etc.)
		JPanel controlsPanel = window.getDecorator().getPlayerControlsPanel();
		playerControls = new PlaybackController(this, playlistPanel, window);
		controlsPanel.add(window.getDecorator().getPlaybackControlsPanel());
		contentPanel.add(controlsPanel, BorderLayout.SOUTH);
		
		add(contentPanel, BorderLayout.CENTER);
	}
	
	
	/**
	 * Sets the current view.
	 * 
	 * @param nav the navigation element which shall be selected
	 * @param repaintRequest if the frame shall be repainted afterwards
	 */
	public void setCurrentView(NAVIGATION nav, boolean repaintRequest)
	{
		navigationPanel.setCurrentView(nav);
		mainWindow.setCurrentNavigation(nav);
		// clear the main panel
		mainPanel.removeAll();
		
		// playlist view
		if(nav == NAVIGATION.PLAYLIST)
		{
			mainPanel.add(coverAndInfoPanel, BorderLayout.WEST);
			mainPanel.add(playlistPanel, BorderLayout.CENTER);
		}
		// settings view
		else if(nav == NAVIGATION.SETTINGS)
		{
			mainPanel.add(new OptionScreen(mainWindow));
		}
		// otherwise: show albums view
		else
		{
			mainPanel.add(scroll);
		}

		if(repaintRequest)
		{
			mainPanel.repaint();
			mainPanel.revalidate();
		}
	}
	
	
	/**
	 * Creates the different panels within the playlist view.
	 */
	private void createPanels()
	{
		// cover panel
		coverPanel = new CoverPanel(mainWindow);
		
		// song info panel
		infoPanel = new SongInfoPanel();
		
		// elapsed time panel
		timeElapsedPanel = new TimeElapsedPanel(coverPanel);
		
		coverAndInfoPanel = new CoverAndInfoPanel(coverPanel, infoPanel, timeElapsedPanel);
		coverAndInfoPanel.setName("COVER");
		
		albumsPanel = new AlbumsOverviewPanel();
		albumsPanel.refreshAlbums(this, db.getAllAlbums(null));
		
		scroll = new JScrollPane(albumsPanel);
		EmptyBorder empty = new EmptyBorder(0, 0, 0, 0);
		scroll.setOpaque(false);
		scroll.setBorder(empty);
		scroll.getViewport().setOpaque(false);
		scroll.setViewportBorder(empty);
		scroll.getVerticalScrollBar().setUnitIncrement(10); // increase vertical scrolling when clicking on the arrow or using the mouse wheel
		
		// default is the albums view
		mainPanel.add(scroll);
	}
	
	
	/**
	 * Refreshed the albums view.
	 * 
	 * @param sorting the currently selected sort mode
	 */
	public void refreshAlbumsView(SORT_MODE sorting)
	{
		if(mainWindow.getCurrentNavigation() == NAVIGATION.ALBUMS)
		{
			mainPanel.remove(scroll);
		}
		
		albumsPanel.refreshAlbums(this, db.getAllAlbums(sorting));
		scroll = new JScrollPane(albumsPanel);
		EmptyBorder empty = new EmptyBorder(0, 0, 0, 0);
		scroll.setOpaque(false);
		scroll.setBorder(empty);
		scroll.getViewport().setOpaque(false);
		scroll.setViewportBorder(empty);
		
		if(mainWindow.getCurrentNavigation() == NAVIGATION.ALBUMS)
		{
			mainPanel.add(scroll);
			scroll.repaint();
			scroll.revalidate();
		}
		
		SwingUtilities.invokeLater([
			run: {
				revalidate();
			}
		] as Runnable);
	}
	
	
	/**
	 * @return the player panel with the player controls
	 */
	public PlaybackController getPlayerPanel()
	{
		return this.playerControls;
	}

	
	/**
	 * Updates the cover image.
	 * 
	 * @param songInfo the current song info to retrieve the path of the cover image
	 */
	public void updateCover(CurrentSongInfo songInfo)
	{
		updateCoverInCase(songInfo, false);
	}
	
	
	/**
	 * Updates the cover image (even if the playlist view is not the current view if desired).
	 * 
	 * @param songInfo the current song info to retrieve the path of the cover image
	 * @param alsoIfNotPlaylistView also update the cover image if the current view is not the playlist view?
	 */
	public void updateCoverInCase(CurrentSongInfo songInfo, boolean alsoIfNotPlaylistView)
	{
		boolean update = (mainWindow.getCurrentNavigation() == NAVIGATION.PLAYLIST);
		if(alsoIfNotPlaylistView) update = true;
		
		// update of the cover image
		coverPanel.setCoverPath(songInfo.coverPath);
		if(update)
		{
			coverPanel.refreshCover();
			SwingUtilities.invokeLater([
				run: {
					repaint();
					revalidate();
				}
			] as Runnable);
		}
	}

	/**
	 * @return the albums overview panel
	 */
	public AlbumsOverviewPanel getAlbumsPanel()
	{
		return albumsPanel;
	}
	
	
	/**
	 * @return the navigation panel
	 */
	public NavigationPanel getNavigationPanel() 
	{
		return navigationPanel;
	}
}