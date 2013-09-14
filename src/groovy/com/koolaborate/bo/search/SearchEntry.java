package com.koolaborate.bo.search;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;

import com.koolaborate.model.CurrentSongInfo;
import com.koolaborate.mvc.view.albuminfo.AlbumInfoFrame;
import com.koolaborate.mvc.view.artistinfo.ArtistInfoFrame;
import com.koolaborate.mvc.view.editid3tag.EditId3TagFrame;
import com.koolaborate.mvc.view.mainwindow.CenterPanel;
import com.koolaborate.mvc.view.mainwindow.MainWindow;
import com.koolaborate.mvc.view.mainwindow.MainWindow.NAVIGATION;
import com.koolaborate.mvc.view.navigation.SubNavButton;
import com.koolaborate.mvc.view.playlistview.CoverPanel;
import com.koolaborate.mvc.view.playlistview.Playlist;
import com.koolaborate.mvc.view.playlistview.PlaylistEntry;
import com.koolaborate.mvc.view.playlistview.PlaylistPanel;
import com.koolaborate.util.LocaleMessage;

/***********************************************************************************
 * SearchEntry                                                                     *
 ***********************************************************************************
 * Every search result is visualized on a JPanel.                                  *
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
public class SearchEntry extends JPanel
{
	private static final long serialVersionUID = -3415668077276831303L;
	private boolean mouseOver = false;
	private Color border = new Color(216, 240, 250);
	private Color activeBg = new Color(243, 249, 253);
	private Font normalFont = new Font("Calibri", Font.PLAIN, 12);
	
	private JLabel aristLabel, albumLabel, songLabel;
	private MainWindow window;
	private SearchFrame frame;
	
	private SearchResult res;
	
	
	/**
	 * Constructor.
	 * 
	 * @param w reference to the main window
	 * @param result the search result object
	 * @param frame the search frame reference
	 */
	public SearchEntry(MainWindow w, SearchResult result, SearchFrame f)
	{
		this.window = w;
		this.res = result;
		this.frame = f;
		
		setOpaque(false);
		initGUI();
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				setMouseOver(true);
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent arg0)
			{
				setCursor(Cursor.getDefaultCursor());
				setMouseOver(false);
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				// hide search window
				frame.dispose();
				
				final String artistName = res.getArtist();
				CenterPanel centerPanel = window.getCenterPanel();
				centerPanel.setCurrentView(NAVIGATION.PLAYLIST, true);
				PlaylistPanel playlist = centerPanel.getPlaylist();
				playlist.setAlbumFolder(new File(res.getAlbumPath()));
				playlist.setAlbumId(res.getAlbumId());
				playlist.refreshSongList();
				CurrentSongInfo songInfo = window.getSongInfo();
				songInfo.setAlbumPath(res.getAlbumPath());
				songInfo.setAlbumId(res.getAlbumId());
				CoverPanel cover = centerPanel.getCoverPanel();
				cover.setAlbumTitle(res.getAlbumTitle());
				cover.setArtistName(artistName);
				centerPanel.updateCover(songInfo);
				setCursor(Cursor.getDefaultCursor());
				
				JPanel playlistSubNavPanel = centerPanel.getNavigationPanel().getCurrentSubNavigationPanel(NAVIGATION.PLAYLIST);
				playlistSubNavPanel.removeAll();
				// add a button 'Artist information' to the subnavigation if the artist
				// name is given
				if(!StringUtils.isEmpty(artistName))
				{
					SubNavButton artistInfo = new SubNavButton();
					artistInfo.setText(LocaleMessage.getString("nav.artistinfo"));
					BufferedImage artistInfoIco = null;
					try
					{
						artistInfoIco = ImageIO.read(getClass().getResource("/images/artist.png"));
					}
					catch(IOException e1)
					{
						e1.printStackTrace();
					}
					artistInfo.setIcon(artistInfoIco);
					artistInfo.addMouseListener(new MouseAdapter(){
						@Override
						public void mouseClicked(MouseEvent e)
						{
							new ArtistInfoFrame(window, artistName);
						}
					});
					playlistSubNavPanel.add(artistInfo);
				}
				
				// general album information (with the ability to edit)
				SubNavButton albumInfo = new SubNavButton();
				albumInfo.setText(LocaleMessage.getString("nav.albuminfo"));
				BufferedImage albumInfoIco = null;
				try
				{
					albumInfoIco = ImageIO.read(getClass().getResource("/images/cover_small.jpg"));
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
				albumInfo.setIcon(albumInfoIco);
				albumInfo.addMouseListener(new MouseAdapter(){
					@Override
					public void mouseClicked(MouseEvent e)
					{
						new AlbumInfoFrame(window, res.getAlbumId());
					}
				});
				playlistSubNavPanel.add(albumInfo);
				
				// edit id3 tags of the songs
				SubNavButton editId3Tags = new SubNavButton();
				editId3Tags.setText(LocaleMessage.getString("nav.editid3"));
				BufferedImage id3TagIco = null;
				try
				{
					id3TagIco = ImageIO.read(getClass().getResource("/images/tag.png"));
				}
				catch(IOException e1)
				{
					e1.printStackTrace();
				}
				editId3Tags.setIcon(id3TagIco);
				editId3Tags.addMouseListener(new MouseAdapter(){
					@Override
					public void mouseClicked(MouseEvent e)
					{
						new EditId3TagFrame(window, res.getAlbumId(), res.getAlbumPath(), window.getDatabase().getSongFileNamesForAlbum(res.getAlbumId())); 
					}
				});
				playlistSubNavPanel.add(editId3Tags);
				
				// play selected song
				Playlist pl = centerPanel.getPlaylist().getPlaylist();
				ArrayList<PlaylistEntry> songs = pl.getEntries();
				for(PlaylistEntry song : songs)
				{
					if(song.getSongId() == res.getSongId())
					{
						pl.setSelectedEntry(song);
						window.getPlayerPanel().playSong();
						break;
					}
				}
			}
		});
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		
		if(mouseOver)
		{
			g2.setColor(activeBg);
			g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5);
			g2.setColor(border);
			g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5);
		}
	}
	
	
	/**
	 * Initialized the GUI elements.
	 */
	private void initGUI()
	{
		setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(2, 4, 2, 4);
		
		aristLabel = new JLabel(res.getArtist());
		aristLabel.setFont(normalFont);
		aristLabel.setPreferredSize(new Dimension(160, 12));
		aristLabel.setHorizontalAlignment(SwingConstants.LEFT);
		add(aristLabel, gbc);
		
		gbc.gridx = 1;
		albumLabel = new JLabel(res.getAlbumTitle());
		albumLabel.setFont(normalFont);
		albumLabel.setPreferredSize(new Dimension(160, 12));
		albumLabel.setHorizontalAlignment(SwingConstants.LEFT);
		add(albumLabel, gbc);
		
		gbc.gridx = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0f;
		songLabel = new JLabel(res.getSongTitle());
		songLabel.setHorizontalAlignment(SwingConstants.LEFT);
		songLabel.setFont(normalFont);
		add(songLabel, gbc);
		
		setBorder(new EmptyBorder(2, 4, 2, 4));
		Dimension dim = new Dimension(Integer.MAX_VALUE, 24);
		setMaximumSize(dim);
	}


	/**
	 * Sets the mouse over effect for gradient background.
	 * 
	 * @param mouseOver <code>true</code> for gradient background, otherwise: <code>false</code>
	 */
	public void setMouseOver(boolean mouseOver)
	{
		this.mouseOver = mouseOver;
	}
}