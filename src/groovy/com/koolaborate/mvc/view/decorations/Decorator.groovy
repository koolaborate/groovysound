package com.koolaborate.mvc.view.decorations;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.apache.log4j.Logger;

import com.koolaborate.mvc.view.navigation.NavButton;
import com.koolaborate.mvc.view.navigation.SubNavButton;
import com.koolaborate.mvc.view.playercontrols.NextButton;
import com.koolaborate.mvc.view.playercontrols.PlayButton;
import com.koolaborate.mvc.view.playercontrols.PreviousButton;
import com.koolaborate.mvc.view.playercontrols.StopButton;
import com.koolaborate.mvc.view.themes.DefaultTheme;

/***********************************************************************************
 * Decorator                                                                       *
 ***********************************************************************************
 * A class that includes all theme specific panels. The panels are retrieved and   *
 * used throughout the application and therefore are only defined within this      *
 * class. Therefore, the panels do only need to be set in this class in order to   *
 * achieve different looks (so called themes) for the entire application.          *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.2                                                                    *
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
public class Decorator
{
	private JPanel leftSidePanel, rightSidePanel, mainBackgroundPanel, 
		navigationBackgroundPanel, headerPanel, bottomPanel, tinyViewBgPanel;
	
	// mouse selections
	private Color selectionColor1, selectionColor2;
	private float selectionAlpha = 0.9f; // 1.0f
	
	// separators
	private Color albumSeparatorColor = new Color(0x04446e); // dark blue by default
	private JLabel subNavSeparatorContent;
	
	// the navigation buttons
	private NavButton albumsButton, playlistButton, settingsButton;
	private SubNavButton searchButt, aboutButt, addAlbumButton, editId3TagsButt, 
		albumInfoButt, artistInfoButt;

	// the playback control area
	private PreviousButton prevButt;
	private PlayButton playButt;
	private StopButton stopButt;
	private NextButton nextButt;
	private JPanel playbackControlsPanel, playerControlsPanel;
	private JSlider volumeSlider;
	private JLabel softVolumeLabel, loudVolumeLabel;

	private Color logoForegroundColor;
	private Color navBarForegroundColor;
	
	/** the log4j logger */
	static Logger log = Logger.getLogger(Decorator.class.getName());
	
	/**
	 * Constructor.
	 */
	public Decorator(){}
	
	/**
	 * @return the left side panel of the window
	 */
	public JPanel getLeftSidePanel()
	{
		// create the panel if necessary
		if(leftSidePanel == null)
		{
			leftSidePanel = DefaultTheme.createSidePanel(true);
		}
		return leftSidePanel;
	}
	
	/**
	 * @return the right side panel of the window
	 */
	public JPanel getRightSidePanel()
	{
		// create the panel if necessary
		if(rightSidePanel == null)
		{
			rightSidePanel = DefaultTheme.createSidePanel(false);
		}
		return rightSidePanel;
	}
	
	/**
	 * @return the main background panel on which the contents of all views are painted 
	 *         (except in the TinyView)
	 */
	public JPanel getMainBackgroundPanel()
	{
		// create the panel if necessary
		if(mainBackgroundPanel == null)
		{
			mainBackgroundPanel = DefaultTheme.createMainBackgroundPanel();
		}
		return mainBackgroundPanel;
	}

	/**
	 * @return the primary selection color (background)
	 */
	public Color getSelectionColor1()
	{
		// define standard color if none is specified
		if(selectionColor1 == null) selectionColor1 = new Color(243, 249, 253);
		return selectionColor1;
	}

	/**
	 * Sets the primary selection color (background).
	 * 
	 * @param selectionColor1 the color to be set
	 */
	public void setSelectionColor1(Color selectionColor1)
	{
		this.selectionColor1 = selectionColor1;
	}

	/**
	 * @return the secondary selection color (outline)
	 */
	public Color getSelectionColor2()
	{
		// define standard color if none is specified
		if(selectionColor2 == null) selectionColor2 = new Color(216, 240, 250);
		return selectionColor2;
	}

	/**
	 * Sets the secondary selection color (outline).
	 * 
	 * @param selectionColor2 the color to be set
	 */
	public void setSelectionColor2(Color selectionColor2)
	{
		this.selectionColor2 = selectionColor2;
	}

	public void setLeftSidePanel(JPanel leftSidePanel)
	{
		this.leftSidePanel = leftSidePanel;
	}

	public void setMainBackgroundPanel(JPanel mainBackgroundPanel)
	{
		this.mainBackgroundPanel = mainBackgroundPanel;
	}

	public void setRightSidePanel(JPanel rightSidePanel)
	{
		this.rightSidePanel = rightSidePanel;
	}
	
	public float getSelectionAlpha()
	{
		return selectionAlpha;
	}

	public void setSelectionAlpha(float selectionAlpha)
	{
		this.selectionAlpha = selectionAlpha;
	}

	public JPanel getNavigationBackgroundPanel()
	{
		// create the panel if necessary
		if(navigationBackgroundPanel == null)
		{
			navigationBackgroundPanel = DefaultTheme.createNavigationBackgroundPanel();
		}
		return navigationBackgroundPanel;
	}

	public void setNavigationBackgroundPanel(JPanel navigationBackgroundPanel)
	{
		this.navigationBackgroundPanel = navigationBackgroundPanel;
	}

	public NavButton getAlbumsViewButton()
	{
		// create the button first if necessary
		if(albumsButton == null) albumsButton = DefaultTheme.createNewNavButton("/images/albums.png", "/images/albums_inactive.png");
		return albumsButton;
	}

	public NavButton getPlaylistViewButton()
	{
		// create the button first if necessary
		if(playlistButton == null) playlistButton = DefaultTheme.createNewNavButton("/images/navplay.png", "/images/navplay_inactive.png");
		return playlistButton;
	}

	public NavButton getSettingsViewButton()
	{
		// create the button first if necessary
		if(settingsButton == null) settingsButton = DefaultTheme.createNewNavButton("/images/navsettings.png", "/images/navsettings_inactive.png");
		return settingsButton;
	}
	
	public SubNavButton getAboutSubNavButton()
	{
		// create the button first if necessary
		if(aboutButt == null) aboutButt = DefaultTheme.createNewSubNavButton("/images/about.png");
		return aboutButt;
	}

	public void setAboutSubNavButton(SubNavButton aboutButt)
	{
		this.aboutButt = aboutButt;
	}

	public SubNavButton getAddAlbumSubNavButton()
	{
		// create the button first if necessary
		if(addAlbumButton == null) addAlbumButton = DefaultTheme.createNewSubNavButton("/images/add_album.png");
		return addAlbumButton;
	}

	public void setAddAlbumSubNavButton(SubNavButton addAlbum)
	{
		this.addAlbumButton = addAlbum;
	}

	public SubNavButton getSearchSubNavButton()
	{
		// create the button first if necessary
		if(searchButt == null) searchButt = DefaultTheme.createNewSubNavButton("/images/search.png");
		return searchButt;
	}

	public void setSearchSubNavButton(SubNavButton searchButt)
	{
		this.searchButt = searchButt;
	}

	public SubNavButton getArtistInfoSubNavButton()
	{
		// create the button first if necessary
		if(artistInfoButt == null) artistInfoButt = DefaultTheme.createNewSubNavButton("/images/artist.png");
		return artistInfoButt;
	}
	
	public void setArtistInfoSubNavButton(SubNavButton b)
	{
		this.artistInfoButt = b;
	}

	public SubNavButton getAlbumInfoSubNavButton()
	{
		// create the button first if necessary
		if(albumInfoButt == null) albumInfoButt = DefaultTheme.createNewSubNavButton("/images/cover_small.jpg");
		return albumInfoButt;
	}

	public void setAlbumInfoSubNavButton(SubNavButton b)
	{
		this.albumInfoButt = b;
	}
	
	public SubNavButton getEditId3TagsSubNavButton()
	{
		// create the button first if necessary
		if(editId3TagsButt == null) editId3TagsButt = DefaultTheme.createNewSubNavButton("/images/tag.png");
		return editId3TagsButt;
	}

	public void setEditId3TagsSubNavButton(SubNavButton b)
	{
		this.editId3TagsButt = b;
	}
	
	public Color getAlbumSeparatorColor()
	{
		return albumSeparatorColor;
	}

	public void setAlbumSeparatorColor(Color c)
	{
		this.albumSeparatorColor = c;
	}
	
	public JLabel getSubNavSeparatorContent()
	{
		// create the content first if necessary
		if(subNavSeparatorContent == null) subNavSeparatorContent = DefaultTheme.createSubNavSeparatorLabel();
		return subNavSeparatorContent;
	}
	
	public void setSubNavSeparatorContent(JLabel l)
	{
		this.subNavSeparatorContent = l;
	}

	public JPanel getHeaderPanel()
	{
		if(headerPanel == null) headerPanel = DefaultTheme.createHeaderPanel();
		return headerPanel;
	}

	public void setHeaderPanel(JPanel headerPanel)
	{
		this.headerPanel = headerPanel;
	}

	public JPanel getBottomPanel()
	{
		if(bottomPanel == null) bottomPanel = DefaultTheme.createBottomPanel();
		return bottomPanel;
	}

	public void setBottomPanel(JPanel bottomPanel)
	{
		this.bottomPanel = bottomPanel;
	}

	public JPanel getTinyViewBgPanel()
	{
		if(tinyViewBgPanel == null) tinyViewBgPanel = DefaultTheme.createTinyViewBgPanel();
		return tinyViewBgPanel;
	}

	public void setTinyViewBgPanel(JPanel tinyViewBgPanel)
	{
		this.tinyViewBgPanel = tinyViewBgPanel;
	}

	public NextButton getNextButt()
	{
		if(nextButt == null) nextButt = DefaultTheme.createPlaybackNextButton();
		return nextButt;
	}

	public void setNextButt(NextButton nextButt)
	{
		this.nextButt = nextButt;
	}

	public JPanel getPlaybackControlsPanel()
	{
		if(playbackControlsPanel == null) playbackControlsPanel = DefaultTheme.createPlaybackControlsPanel(getPlayButt(),
				getPrevButt(), getNextButt(), getStopButt(), getVolumeSlider(), getSoftVolumeLabel(), getLoudVolumeLabel());
		return playbackControlsPanel;
	}
	
	public JPanel getPlayerControlsPanel()
	{
		if(playerControlsPanel == null) playerControlsPanel = DefaultTheme.createPlayerControlsPanel();
		return playerControlsPanel;
	}

	public void setPlaybackControlsPanel(JPanel playbackControlsPanel)
	{
		this.playbackControlsPanel = playbackControlsPanel;
	}

	public PlayButton getPlayButt()
	{
		if(playButt == null) playButt = DefaultTheme.createPlaybackPlayButton();
		return playButt;
	}

	public void setPlayButt(PlayButton playButt)
	{
		this.playButt = playButt;
	}

	public PreviousButton getPrevButt()
	{
		if(prevButt == null) prevButt = DefaultTheme.createPlaybackPrevButton();
		return prevButt;
	}

	public void setPrevButt(PreviousButton prevButt)
	{
		this.prevButt = prevButt;
	}

	public StopButton getStopButt()
	{
		if(stopButt == null) stopButt = DefaultTheme.createPlaybackStopButton();
		return stopButt;
	}

	public void setStopButt(StopButton stopButt)
	{
		this.stopButt = stopButt;
	}

	public JSlider getVolumeSlider()
	{
		if(volumeSlider == null) volumeSlider = DefaultTheme.createVolumeSlider();
		return volumeSlider;
	}

	public void setVolumeSlider(JSlider volumeSlider)
	{
		this.volumeSlider = volumeSlider;
	}

	public JLabel getLoudVolumeLabel()
	{
		if(loudVolumeLabel == null) loudVolumeLabel = DefaultTheme.createLoudVolumeLabel();
		return loudVolumeLabel;
	}

	public JLabel getSoftVolumeLabel()
	{
		if(softVolumeLabel == null) softVolumeLabel = DefaultTheme.createSoftVolumeLabel();
		return softVolumeLabel;
	}
	
	public void setLoudVolumeLabel(JLabel loudVolumeLabel)
	{
		this.loudVolumeLabel = loudVolumeLabel;
	}

	public void setSoftVolumeLabel(JLabel softVolumeLabel)
	{
		this.softVolumeLabel = softVolumeLabel;
	}

	public void setPlayerControlsPanel(JPanel playerControlsPanel)
	{
		this.playerControlsPanel = playerControlsPanel;
	}

	public Color getLogoForegroundColor()
	{
		if(logoForegroundColor == null) logoForegroundColor = Color.WHITE;
		return logoForegroundColor;
	}

	public void setLogoForegroundColor(Color logoForegroundColor)
	{
		this.logoForegroundColor = logoForegroundColor;
	}

	public Color getNavbarForegroundColor()
	{
		if(navBarForegroundColor == null) navBarForegroundColor = Color.WHITE;
		return navBarForegroundColor;
	}

	public void setNavbarForegroundColor(Color navBarForegroundColor)
	{
		this.navBarForegroundColor = navBarForegroundColor;
	}

	public void setAlbumsButton(NavButton albumsButton)
	{
		this.albumsButton = albumsButton;
	}

	public void setPlaylistButton(NavButton playlistButton)
	{
		this.playlistButton = playlistButton;
	}

	public void setSettingsButton(NavButton settingsButton)
	{
		this.settingsButton = settingsButton;
	}
}