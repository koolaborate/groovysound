package ui.albumview;

import helper.LocaleMessage;
import helper.StringUtils;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import types.Album;
import types.CurrentSongInfo;
import ui.mainwindow.CenterPanel;
import ui.mainwindow.MainWindow;

/***********************************************************************************
 * AlbumsOverviewPanel                                                             *
 ***********************************************************************************
 * A JPanel that includes all AlbumPreviewPanels of the available albums.          *
 * @see AlbumPreviewPanel                                                          *
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
public class AlbumsOverviewPanel extends JPanel
{
	private List<Album> albums;
	private int height;
	
	private int padding = 4;
	private int spacing = 10;
	
	private Album selectedAlbum;
	private AlbumPreviewPanel selectedAlbumPanel;
	
	public enum SORT_MODE{
		SORT_ALBUMTITLE,
		SORT_ARTIST,
		SORT_SHOW_ALL
	}
	private SORT_MODE currentSortMode = SORT_MODE.SORT_ALBUMTITLE;
	
	
	/**
	 * Constructor.
	 */
	public AlbumsOverviewPanel()
	{
		setOpaque(false);
		setLayout(new FlowLayout(FlowLayout.LEFT, padding, padding));
		setBorder(new EmptyBorder(spacing, spacing, spacing, spacing));
	}
	
	
	/**
	 * Refreshes the album view and calculates the height of the scroll pane anew.
	 * 
	 * @param centerPanel the center panel in which the album preview panels lie in
	 * @param albumlist the list of all albums to be shown
	 */
	public void refreshAlbums(CenterPanel centerPanel, List<Album> albumlist)
	{
		MainWindow mainWindow = centerPanel.getMainWindow();
		CurrentSongInfo songInfo = mainWindow.getSongInfo();
		
		int availableWidth = mainWindow.getWidth() - 2 * 20;
		
		height = 0;
		removeAll(); // first clear the whole panel
		
		albums = albumlist;
		
		// show an advice if there is no album yet
		if(albums == null || albums.size() == 0)
		{
			JLabel adviceLabel = new JLabel("<HTML><i>" + LocaleMessage.getString("newalbum.empty_info") + "</i></HTML>");
			add(adviceLabel);
			
			return;
		}
		
		int albumsPerLine = 0;
		int currentAlbumsInLine = 0;
		int i = 0, rowcount = 0, labelcount = 0, labelheight = 0;
		int albumheight = 0, albumwidth = 0;

		String lastLetter = ""; // the last upper case start letter of the artist or album name
		boolean letterInserted = false;
		
		// add all album preview panels
		for(Album a : albums)
		{
			AlbumPreviewPanel albumPanel = new AlbumPreviewPanel(centerPanel, songInfo);
			albumPanel.setAlbum(a);
			
			// get the dimensions of an album preview panel (their size is always the same)
			if(albumheight == 0) albumheight = albumPanel.getPreferredSize().height;
			if(albumwidth  == 0) albumwidth  = albumPanel.getPreferredSize().width;
			if(albumsPerLine == 0) albumsPerLine = availableWidth / (albumwidth + padding);
			
			String firstLetter = "";
			// if sorting is album title...
			if(currentSortMode == SORT_MODE.SORT_ALBUMTITLE)
			{
				firstLetter = StringUtils.getFirstChar(a.getTitle()).toUpperCase();
			}
			// otherwise, it is sorted after the artist's name
			else if(currentSortMode == SORT_MODE.SORT_ARTIST)
			{
				firstLetter = StringUtils.getFirstChar(a.getArtist()).toUpperCase();
			}
				
			// if the start letter is different, a separator is inserted
			if(!firstLetter.equals(lastLetter))
			{
				SeparatorLabel lab = new SeparatorLabel(firstLetter);
				lab.setForeground(mainWindow.getDecorator().getAlbumSeparatorColor());
				// get the height of the separator label (they have always the same height)
				if(labelheight == 0) labelheight = lab.getPreferredSize().height;
				add(lab);
				labelcount++;
				rowcount++;
				lastLetter = firstLetter;
				letterInserted = true;
				currentAlbumsInLine = 0;
			}
			
			// add the album panel
			add(albumPanel);
			currentAlbumsInLine++;
			
			// one more row if more than albumsPerLine albums in one line
			if(!letterInserted && currentAlbumsInLine % albumsPerLine == 1)
			{
				rowcount++;
			}
			letterInserted = false; // reset to false
			
			i++;
		}
		
		height += labelcount * (labelheight + padding);
		height += rowcount * (albumheight + padding);
		height -= (2 * padding); // upper and lower margin isn't necessary
		height += (2 * spacing); // upper and lower border 

//		System.out.println("ANZAHL ZEILEN: " + rowcount);
//		System.out.println(" - Panelgröße: " + albumheight);
//		System.out.println(" - Panelgröße (gesamt): " + rowcount * (albumheight + padding));
//		System.out.println("ANZAHL LABELS: " + labelcount);
//		System.out.println(" - Labelgröße: " + labelheight);
//		System.out.println(" - Labelgröße (gesamt): " + labelcount * (labelheight + padding));
//		System.out.println("RÄNDER: " + 2 * spacing);
//		System.out.println("------------------------------------------");
//		System.out.println("Height: " + height);
		
		setPreferredSize(new Dimension(availableWidth, height));
	}
	
	
	/**
	 * Sets the sorting mode of the view.
	 * 
	 * @param sortmode the desired sorting mode
	 */
	public void setSortMode(SORT_MODE sortmode)
	{
		this.currentSortMode = sortmode;
	}
	
	
	/**
	 * Repaints the selected album which might be necessary if the cover image or the artist/album name has changed.
	 */
	public void refreshSelectedAlbum(Album a)
	{
		if(selectedAlbumPanel != null && selectedAlbum != null)
		{
			selectedAlbumPanel.setAlbum(a);
		}
	}


	/**
	 * Sets the selected album.
	 * 
	 * @param panel the AlbumPreviewPanel of the selected album
	 * @param album the selected album itself
	 */
	public void setSelectedAlbum(AlbumPreviewPanel panel, Album album)
	{
		this.selectedAlbumPanel = panel;
		this.selectedAlbum = album;
	}

	
	/**
	 * @return a list of all shown albums
	 */
	public List<Album> getShownAlbums()
	{
		return albums;
	}
	

	/**
	 * @return the current sorting mode
	 */
	public SORT_MODE getSortMode()
	{
		return this.currentSortMode;
	}
}