package com.koolaborate.mvc.view.navigation

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities
import javax.swing.border.EmptyBorder

import com.koolaborate.bo.search.SearchFrame
import com.koolaborate.mvc.view.albumview.AlbumsOverviewPanel.SORT_MODE
import com.koolaborate.mvc.view.decorations.Decorator
import com.koolaborate.mvc.view.dialogs.AboutDialog
import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.mvc.view.mainwindow.MainWindow.NAVIGATION
import com.koolaborate.mvc.view.mainwindow.components.WindowCenterPanel;
import com.koolaborate.mvc.view.newalbum.NewAlbumFrame
import com.koolaborate.util.LocaleMessage

/***********************************************************************************
 * NavigationPanel                                                                 *
 ***********************************************************************************
 * The navigation panel in the top part of the main window.                        *
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
class NavigationPanel extends JPanel
{
	private static final long serialVersionUID = -152859065478817033L

	NavButton albumsButton, playlistButton, settingsButton
	
	MainWindow mainWindow
	WindowCenterPanel centerPanel 
	
	JPanel themedBgPanel
	JPanel subNavPanel
	JPanel albumsSubNavPanel 
	JPanel playlistSubNavPanel
	JPanel settingsSubNavPanel
	
	
	/**
	 * Constructor.
	 * 
	 * @param panel reference to the center panel
	 * @param window the main window 
	 */
	public NavigationPanel(WindowCenterPanel panel, MainWindow window)
	{
		this.centerPanel = panel
		this.mainWindow = window
		
		// load the theme specific panels and buttons
		Decorator d = window.getDecorator()
		this.themedBgPanel  = d.getNavigationBackgroundPanel()
		this.albumsButton   = d.getAlbumsViewButton()
		this.playlistButton = d.getPlaylistViewButton()
		this.settingsButton = d.getSettingsViewButton()
		
		albumsButton.setToolTipText(LocaleMessage.getInstance().getString("nav.showalbums"))
		albumsButton.addMouseListener([
			mouseClicked: {
				centerPanel.setCurrentView(NAVIGATION.ALBUMS, true)
				playlistButton.setActive(false)
				settingsButton.setActive(false)
				updateButtons()
			}
		] as MouseAdapter)
		albumsButton.setActive(true)
		
		playlistButton.setToolTipText(LocaleMessage.getInstance().getString("nav.playlist"))
		playlistButton.addMouseListener([
			mouseClicked: {
				centerPanel.setCurrentView(NAVIGATION.PLAYLIST, true)
				albumsButton.setActive(false)
				settingsButton.setActive(false)
				updateButtons()
			}
		] as MouseAdapter)
		
		settingsButton.setToolTipText(LocaleMessage.getInstance().getString("nav.options"))
		settingsButton.addMouseListener([
			mouseClicked: {
				centerPanel.setCurrentView(NAVIGATION.SETTINGS, true)
				albumsButton.setActive(false)
				playlistButton.setActive(false)
				updateButtons()
			}] as MouseAdapter)
		
		// the sub navigation (depending on the current view like album view or playlist view)
		subNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0))
		subNavPanel.setOpaque(false)
		calculateSubNavWidth(mainWindow.getSize())
		
		Box box = new Box(BoxLayout.X_AXIS)
		box.add(Box.createHorizontalStrut(10))
		box.add(albumsButton)
		box.add(Box.createHorizontalStrut(2))
		box.add(playlistButton)
		box.add(Box.createHorizontalStrut(2))
		box.add(settingsButton)
		box.add(Box.createHorizontalStrut(10))
		
		Separator s1 = new Separator()
		s1.setContent(d.getSubNavSeparatorContent())
		
		box.add(s1)
		box.add(Box.createHorizontalStrut(10))
		
		createAllSubNavigationButtons()
		setSubNavButtons(NAVIGATION.ALBUMS)

		box.add(subNavPanel)
		
		box.add(Box.createHorizontalStrut(10))
		box.add(new Separator())
		
		box.add(s1.clone())
		box.add(Box.createHorizontalStrut(10))
		
		// navigation buttons that are always visible
		JPanel commonFuntionsPanel = new JPanel()
		commonFuntionsPanel.setOpaque(false)
		commonFuntionsPanel.setPreferredSize(new Dimension(80, 36))
		commonFuntionsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 0, 0))
		
		SubNavButton searchButt = d.getSearchSubNavButton()
		searchButt.setText(LocaleMessage.getInstance().getString("search.search"))
		searchButt.addMouseListener([
			mouseClicked: {
				new SearchFrame(mainWindow)
			}
		] as MouseAdapter)
		commonFuntionsPanel.add(searchButt)
		
		SubNavButton aboutButt = d.getAboutSubNavButton()
		aboutButt.setText(LocaleMessage.getInstance().getString("common.about") + "...")
		aboutButt.addMouseListener([
			mouseClicked: {
				new AboutDialog(mainWindow)
			}
		] as MouseAdapter)
		commonFuntionsPanel.add(aboutButt)
		commonFuntionsPanel.setBorder(new EmptyBorder(0, 0, 0, 4)) // Spacing to the right
		box.add(commonFuntionsPanel)
		
		
		themedBgPanel.setLayout(new FlowLayout(FlowLayout.LEFT))
		themedBgPanel.add(box)
		setLayout(new BorderLayout())
		add(themedBgPanel)
	}
	
	
	/**
	 * Calculates the preferred size of the sub navigation panel.
	 * 
	 * @param dimension the dimension of the main window
	 */
	public void calculateSubNavWidth(Dimension dimension) {
		int width = dimension.width - 2*20 - settingsButton.getPreferredSize().width*3 - 30 - 90
		subNavPanel.setPreferredSize(new Dimension(width, 36))
		subNavPanel.revalidate()
	}


	/**
	 * Sets the sub navigation buttons according to the selection
	 * 
	 * @param nav the currently selected navigation
	 */
	private void setSubNavButtons(NAVIGATION nav) {
		subNavPanel.removeAll()
		
		if(nav == NAVIGATION.ALBUMS) 		subNavPanel.add(albumsSubNavPanel) 
		else if(nav == NAVIGATION.PLAYLIST)	subNavPanel.add(playlistSubNavPanel)
		else if(nav == NAVIGATION.SETTINGS) subNavPanel.add(settingsSubNavPanel)
		
		subNavPanel.repaint()
	}


	private void createAllSubNavigationButtons() {
		// sub navigation for the albums view
		albumsSubNavPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0))
		albumsSubNavPanel.setOpaque(false)
		
		SubNavButton addAlbum = mainWindow.getDecorator().getAddAlbumSubNavButton()
		addAlbum.setText(LocaleMessage.getInstance().getString("newalbum.add_album"))
		addAlbum.addMouseListener([
			mouseClicked: {
				new NewAlbumFrame(mainWindow: mainWindow)
			}
		] as MouseAdapter)
		albumsSubNavPanel.add(addAlbum)
		
		final String[] choices = [LocaleMessage.getInstance().getString("nav.sort_album"), 
				LocaleMessage.getInstance().getString("nav.sort_artist"), 
				LocaleMessage.getInstance().getString("nav.sort_none")]
		
		// TODO this is where the concurrent modification happens...
		JComboBox<String> sortModeBox = new JComboBox<String>(choices)
		sortModeBox.addItemListener([
			itemStateChanged: { e ->
				if(e.getStateChange() == ItemEvent.SELECTED)
				{
					String selection = (String) e.getItem()
					SORT_MODE selectedMode = null
					if(selection.equals(choices[0])) 
					{
						selectedMode = SORT_MODE.SORT_ALBUMTITLE
					}
					else if(selection.equals(choices[1]))
					{
						selectedMode = SORT_MODE.SORT_ARTIST
					}
					else if(selection.equals(choices[2]))
					{
						selectedMode = SORT_MODE.SORT_SHOW_ALL
					}
					centerPanel.getAlbumsPanel().setSortMode(selectedMode)
					centerPanel.refreshAlbumsView(selectedMode)
				}
			}
		] as ItemListener)
		JLabel sortLabel = new JLabel(LocaleMessage.getInstance().getString("nav.sorting") + ":")
		sortLabel.setForeground(mainWindow.getDecorator().getNavbarForegroundColor())
		sortLabel.setBorder(new EmptyBorder(0, 10, 0, 4))
		
		albumsSubNavPanel.add(sortLabel)
		albumsSubNavPanel.add(sortModeBox)
		
		// sub navigation for the playlist view
		playlistSubNavPanel = new JPanel()
		playlistSubNavPanel.setOpaque(false)
		playlistSubNavPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0))
		
		// sub navigation for the settings view
		settingsSubNavPanel = new JPanel()	
		settingsSubNavPanel.setOpaque(false)
		settingsSubNavPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0))
	}


	/**
	 * Updates the button states.
	 */
	public void updateButtons()
	{
		albumsButton.repaint()
		playlistButton.repaint()
		settingsButton.repaint()
	}
	
	
	/**
	 * Sets the current view.
	 * 
	 * @param nav the view to be set
	 */
	public void setCurrentView(NAVIGATION nav)
	{
		// first, set all buttons to inactive and then turn the one to active
		albumsButton.setActive(false)
		playlistButton.setActive(false)
		settingsButton.setActive(false)
		
		if(nav == NAVIGATION.ALBUMS) 		albumsButton.setActive(true)
		else if(nav == NAVIGATION.PLAYLIST)	playlistButton.setActive(true)
		else if(nav == NAVIGATION.SETTINGS) settingsButton.setActive(true)
		
		setSubNavButtons(nav)
		
		SwingUtilities.invokeLater([
			run: {
				updateButtons()
			}
		] as Runnable)
	}
	
	
	/**
	 * Returns the currently active sub navigation panel.
	 * 
	 * @param nav the currently selected navigation
	 * @return the according sub navigation panel
	 */
	public JPanel getCurrentSubNavigationPanel(NAVIGATION nav)
	{
		if(nav == NAVIGATION.ALBUMS) 		return albumsSubNavPanel
		else if(nav == NAVIGATION.PLAYLIST)	return playlistSubNavPanel
		else if(nav == NAVIGATION.SETTINGS) return settingsSubNavPanel
		else return null
	}
	
	
	/**
	 * Adds a sub navigation button to the given navigation.
	 * 
	 * @param nav the navigation menu to which the button shall be added
	 * @param b the button to be added
	 */
	public void addSubNavButton(NAVIGATION nav, SubNavButton b)
	{
		if(nav == NAVIGATION.ALBUMS)        albumsSubNavPanel.add(b)
		else if(nav == NAVIGATION.PLAYLIST)	playlistSubNavPanel.add(b)
		else if(nav == NAVIGATION.SETTINGS) settingsSubNavPanel.add(b)
	}
}