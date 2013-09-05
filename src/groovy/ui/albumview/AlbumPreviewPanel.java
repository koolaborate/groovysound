package ui.albumview;

import helper.FileHelper;
import helper.LocaleMessage;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import controller.PlaybackController;
import controller.PlaybackController.STATE;
import types.Album;
import types.CurrentSongInfo;
import types.Song;
import ui.albuminfo.AlbumInfoFrame;
import ui.artistinfo.ArtistInfoFrame;
import ui.dialogs.DeleteDialog;
import ui.editid3tag.EditId3TagFrame;
import ui.mainwindow.CenterPanel;
import ui.mainwindow.MainWindow;
import ui.mainwindow.MainWindow.NAVIGATION;
import ui.navigation.SubNavButton;
import ui.playlistview.CoverPanel;
import ui.playlistview.PlaylistPanel;

/***********************************************************************************
 * AlbumPreviewPanel                                                               *
 ***********************************************************************************
 * A JPanel containing a little cover image and the title and artist of an album.  *
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
public class AlbumPreviewPanel extends JPanel
{
	private BufferedImage preview;
	private JLabel previewImgLabel, titleLabel, artistLabel;
	private CenterPanel centerPanel;
	private CurrentSongInfo songInfo;
	private MainWindow window;
	
	private Album album;
	private String albumFolder;
	private int albumId;
	private boolean active = false;
	
	/** the log4j logger */
	static Logger log = Logger.getLogger(AlbumPreviewPanel.class.getName());
	
	/**
	 * Constructor.
	 */
	public AlbumPreviewPanel(CenterPanel panel, CurrentSongInfo info)
	{
		this.songInfo = info;
		this.centerPanel = panel;
		this.window = panel.getMainWindow();
		
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setPreferredSize(new Dimension(102, 108));
		
		try
		{
			preview = ImageIO.read(getClass().getResource("/images/emptycover.jpg"));
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
		
		previewImgLabel = new JLabel(new ImageIcon(preview));
		previewImgLabel.setBorder(new EmptyBorder(2, 2, 0, 0));
		titleLabel = new JLabel("");  
		titleLabel.setFont(new Font("Serif", Font.PLAIN, 12));
		titleLabel.setBorder(new EmptyBorder(0, 2, 0, 0));
		artistLabel = new JLabel(""); 
		artistLabel.setFont(new Font("Serif", Font.PLAIN, 10));
		artistLabel.setBorder(new EmptyBorder(0, 2, 0, 0));
		
		add(previewImgLabel);
		add(titleLabel);
		add(artistLabel);
		
		addMouseListener(new MouseAdapter(){
			@Override
			public void mouseEntered(MouseEvent arg0)
			{
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setActive(true);
				repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent arg0)
			{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setActive(false);
				repaint();
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if(SwingUtilities.isLeftMouseButton(e))
				{
					showPlaylistofSelectedAlbum();
				}
				else if(SwingUtilities.isRightMouseButton(e))
				{
					// show a popup menu
					showPopup(previewImgLabel, e.getPoint().x, e.getPoint().y);
				}
			}
		});
	}
	
	/**
	 * Shows a popup window at the given location.
	 * 
	 * @param c the parent component
	 * @param x the x value of the location
	 * @param y the y value of the location
	 */
	private void showPopup(Component c, int x, int y)
	{
		JPopupMenu popmen = new JPopupMenu(); 
		
		JMenuItem playItem = new JMenuItem(LocaleMessage.getString("album.play"));
		playItem.setIcon(new ImageIcon(getClass().getResource("/images/playlist_play.png")));
		playItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				showPlaylistofSelectedAlbum();
			}			
		});
		popmen.add(playItem); 
		
		JMenuItem infoItem = new JMenuItem(LocaleMessage.getString("album.information"));
		infoItem.setIcon(new ImageIcon(getClass().getResource("/images/about.png")));
		infoItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				new AlbumInfoFrame(window, albumId);
			}			
		});
		popmen.add(infoItem); 
		
		popmen.addSeparator();
		
		JMenuItem delItem = new JMenuItem(LocaleMessage.getString("album.delete"));
		delItem.setIcon(new ImageIcon(getClass().getResource("/images/deletesmall.png")));
		delItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0)
			{
				DeleteDialog delDiag = DeleteDialog.showDeleteAlbumDialog();
				if(delDiag.yesSelected)
				{
					// del the files from disk (if desired)
					if(delDiag.delFilesSelected)
					{
						log.debug("Deleting files...");
						List<Song> songs = window.getDatabase().getSongsForAlbum(albumId);
						for(Song s : songs) FileHelper.removeFile(album.getFolderPath() + File.separator + s.getFileName());	
						// delete the folder
						log.debug("Deleting folder...");
						if(!StringUtils.isEmpty(album.getFolderPath())) FileHelper.removeFile(album.getFolderPath());
						log.debug("done.");
					}
					
					// delete the album from the database
					log.debug("Deleting album with ID " + albumId + " from the database.");
					window.getDatabase().deleteAlbum(albumId);
					
					// refresh the albums view
					centerPanel.refreshAlbumsView(centerPanel.getAlbumsPanel().getSortMode());
					SwingUtilities.invokeLater(new Runnable(){
						public void run()
						{
							centerPanel.revalidate();
						}
					});
					
					// refresh the playlist view if it is the selected album
					// load empty cover if it is the active album in the playlist...
					log.debug("The currently selected album folder path is: " + window.getCurrentFolderPath());
					log.debug("The path of the album to be deleted is: " + album.getFolderPath());
					if(window.getCurrentFolderPath() != null && window.getCurrentFolderPath().equals(album.getFolderPath()))
					{
						log.debug("The folders are eyual => stop playback");
						
						PlaybackController playerPanel = window.getPlayerPanel();
						centerPanel.getPlaylist().getPlaylist().clearPlaylist();
						
						// stop playback if song is from current album
						if(window.getPlayerPanel().getCurrentState() == STATE.PLAYING)
						{
							playerPanel.fadeOut();
						}
						playerPanel.setCurrentState(STATE.ENDED);
						
						CurrentSongInfo info = new CurrentSongInfo();
						
						// then clear the playlist and update the view
						window.setCurrentSongInfo(info);
						centerPanel.updateCover(window.getSongInfo());
						centerPanel.getCoverPanel().refreshCover();
						window.updateArtist(info);
						window.setCurrentFolder(null);
					}
				}
			}			
		});
		popmen.add(delItem); 
		
		popmen.show(c, x, y); 
	}
	
	/**
	 * Switches to the playlist view of the selected album.
	 */
	private void showPlaylistofSelectedAlbum()
	{
		NAVIGATION nav = NAVIGATION.PLAYLIST;
		
		final String artistName = album.getArtist();
		centerPanel.setCurrentView(nav, true);
		
		// refresh the playlist
		PlaylistPanel playlist = centerPanel.getPlaylist();
		playlist.setAlbumFolder(new File(albumFolder));
		playlist.setAlbumId(albumId);
		playlist.refreshSongList();
		songInfo.setAlbumPath(albumFolder);
		songInfo.setAlbumId(album.getId());
		// refresh the cover image
		CoverPanel cover = centerPanel.getCoverPanel();
		cover.setAlbumTitle(album.getTitle());
		cover.setArtistName(artistName);
		cover.setAlbumFolder(albumFolder);
		centerPanel.updateCover(songInfo);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		setActive(false);
		centerPanel.getAlbumsPanel().setSelectedAlbum(getThisInstance(), album);
		window.repaintBackgroundPanel();
		
		// refresh the sub navigation buttons
		JPanel playlistSubNavPanel = centerPanel.getNavigationPanel().getCurrentSubNavigationPanel(nav);
		
		// save any previously added buttons (by plugins etc.)
		Component[] previousButtons = playlistSubNavPanel.getComponents();
		List<SubNavButton> safeButtons = new ArrayList<SubNavButton>();
		for(Component c : previousButtons)
		{
			if(c instanceof SubNavButton)
			{
				SubNavButton b = (SubNavButton) c;
				if(!b.getText().equals(LocaleMessage.getString("nav.artistinfo")) &&
						!b.getText().equals(LocaleMessage.getString("nav.albuminfo")) &&
						!b.getText().equals(LocaleMessage.getString("nav.editid3")))
				{
					safeButtons.add(b);
				}
			}
		}
		
		playlistSubNavPanel.removeAll();
		
		// add a button 'Artist information' to the subnavigation if the artist name is given
		if(!StringUtils.isEmpty(artistName))
		{
			SubNavButton artistInfo = window.getDecorator().getArtistInfoSubNavButton();
			artistInfo.setText(LocaleMessage.getString("nav.artistinfo"));
			artistInfo.setMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e)
				{
					new ArtistInfoFrame(window, artistName);
				}
			});
			playlistSubNavPanel.add(artistInfo);
		}
		
		// general album information (with the ability to edit)
		SubNavButton albumInfo = window.getDecorator().getAlbumInfoSubNavButton();
		albumInfo.setText(LocaleMessage.getString("nav.albuminfo"));
		albumInfo.setMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e)
			{
				new AlbumInfoFrame(centerPanel.getMainWindow(), albumId);
			}
		});
		playlistSubNavPanel.add(albumInfo);
		
		// edit id3 tags of the songs
		SubNavButton editId3Tags = window.getDecorator().getEditId3TagsSubNavButton();
		editId3Tags.setText(LocaleMessage.getString("nav.editid3"));
		editId3Tags.setMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e)
			{
				new EditId3TagFrame(window, albumId, albumFolder, 
						window.getDatabase().getSongFileNamesForAlbum(albumId)); 
			}
		});
		playlistSubNavPanel.add(editId3Tags);
		
		// now add the previously saved buttons
		for(SubNavButton b : safeButtons) playlistSubNavPanel.add(b);
		
		playlistSubNavPanel.repaint();
		
		// update the tray icon
		window.updateTrayIconAndText(album.getFolderPath() + File.separator + "folder.jpg", 
				album.getArtist(), album.getTitle());
	}
	
	@Override
	protected void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		if(active)
		{
			// transparency according to set value
//			g2.setComposite(AlphaComposite.SrcAtop.derive(window.getDecorator().getSelectionAlpha()));
			Composite oldComposite = g2.getComposite();
			g2.setComposite(AlphaComposite.SrcOver.derive(window.getDecorator().getSelectionAlpha()));
			g2.setColor(window.getDecorator().getSelectionColor1());
			g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5);
			g2.setColor(window.getDecorator().getSelectionColor2());
			g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5);
			// afterwards set alpha value back to 1.0
			g2.setComposite(oldComposite);
//			g2.setComposite(AlphaComposite.SrcAtop.derive(1.0f));
		}
	}
	
	/**
	 * Sets the album and sets the text fields.
	 * 
	 * @param a the album to be set
	 */
	public void setAlbum(Album a)
	{
		this.album = a;
		setAlbumData(a.getId(), a.getFolderPath(), a.getTitle(), a.getArtist(), a.getPreview());
	}
	
	/**
	 * Sets the contents of the text fields acoording to the given data values.
	 *  
	 * @param albumId the album id
	 * @param albumFolder the album folder path
	 * @param albumTitle the title of the album
	 * @param albumArtist the name of the album artist
	 * @param image the cover image of the album
	 */
	private void setAlbumData(int albumId, String albumFolder, String albumTitle,
			String albumArtist, BufferedImage image)
	{
		this.albumId = albumId;
		this.albumFolder = albumFolder;
		if(image == null) image = preview;
		previewImgLabel.setIcon(new ImageIcon(image));
		titleLabel.setText(albumTitle);
		artistLabel.setText(albumArtist);
		setToolTipText(albumArtist + " - " + albumTitle);
		repaint();
	}
	
	/**
	 * Set panel to active state.
	 * 
	 * @param active whether or not to set the panel into the active state
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	/**
	 * @return a refernce to the instance of the class
	 */
	private AlbumPreviewPanel getThisInstance()
	{
		return this;
	}
}