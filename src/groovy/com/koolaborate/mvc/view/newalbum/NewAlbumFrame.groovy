package com.koolaborate.mvc.view.newalbum

import java.awt.BorderLayout
import java.awt.Color
import java.awt.FlowLayout
import java.awt.GradientPaint
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.event.ActionListener
import java.awt.image.BufferedImage

import javax.imageio.ImageIO
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.SwingUtilities

import org.apache.commons.lang3.StringUtils
import org.farng.mp3.TagException
import org.jdesktop.swingx.JXBusyLabel

import com.koolaborate.config.ExtendedMP3Info
import com.koolaborate.config.MP3PropertyReader
import com.koolaborate.model.Album
import com.koolaborate.model.Song
import com.koolaborate.mvc.view.dialogs.VistaDialog
import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.util.ImageHelper
import com.koolaborate.util.LocaleMessage

/***********************************************************************************
 * NewAlbumFrame *
 *********************************************************************************** 
 * A window that is shown when the user wants to insert a new album into the *
 * database. *
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
class NewAlbumFrame extends JFrame{
	private static final long serialVersionUID = 3396798307868339914L
	JButton okButt, cancelButt, searchButt
	JTextField folderPath, albumTitle, albumArtist, albumYear
	JComboBox<String> songList
	JXBusyLabel busyLabel
	JPanel albumInfoPanel, centerPanel
	MainWindow mainWindow
	BufferedImage bufferedSearchImage

	File albumFolder
	Album a
	List<Song> songs

	def initializeGui(){
		songs = new ArrayList<Song>()
		
		SwingUtilities.invokeLater([
			run: {
				initGUI()
			}
		] as Runnable)
	}

	/**
	 * Initializes the GUI elements.
	 */
	private void initGUI(){
		try {
			bufferedSearchImage = ImageIO.read(getClass().getResource(
					"/images/searchfolder.png"))
			setIconImage(bufferedSearchImage)
		} catch(IOException e) {
			e.printStackTrace()
		}
		setDefaultCloseOperation(DISPOSE_ON_CLOSE)
		setTitle(LocaleMessage.getInstance().getString("newalbum.add_new_album"))

		setLayout(new BorderLayout())

		JPanel bgPanel = [
			paintComponent: { g ->
				Color color1 = new Color(237, 242, 249)
				Color color2 = new Color(255, 255, 255)
				Graphics2D g2d = (Graphics2D) g

				int w = getWidth()
				int h = getHeight()

				// Paint a gradient from top to bottom
				GradientPaint gp = new GradientPaint(0, 0, color1, 0, h, color2)

				g2d.setPaint(gp)
				g2d.fillRect(0, 0, w, h)
			}
		] as JPanel
	
		bgPanel.setLayout(new BorderLayout())

		// header panel
		bgPanel.add(createHeaderPanel(), BorderLayout.NORTH)

		// main panel
		centerPanel = createCenterPanel()
		bgPanel.add(centerPanel, BorderLayout.CENTER)

		add(bgPanel, BorderLayout.CENTER)

		// button panel
		add(createButtonPanel(), BorderLayout.SOUTH)

		setSize(500, 350)
		setLocationRelativeTo(null)
		setVisible(true)
	}

	/**
	 * @return creates and returns the header JPanel
	 */
	protected JPanel createHeaderPanel(){
		NewAlbumHeaderPanel newAlbumHeaderPanel = new NewAlbumHeaderPanel()
		newAlbumHeaderPanel.setBufferedSearchImage(bufferedSearchImage)
		newAlbumHeaderPanel.setFolderPath(folderPath)
		newAlbumHeaderPanel.setSearchButton(searchButt)
		newAlbumHeaderPanel.setMainWindow(mainWindow)
		newAlbumHeaderPanel.setAlbumFolder(albumFolder)
		newAlbumHeaderPanel.setBusyLabel(busyLabel)
		newAlbumHeaderPanel.setNewAlbumFrame(this)
		
		return newAlbumHeaderPanel
	}

	/**
	 * @return creates and returns the center JPanel
	 */
	private JPanel createCenterPanel(){
		NewAlbumCenterPanel newAlbumCenterPanel = 
			new NewAlbumCenterPanel(busyLabel: busyLabel, albumInfoPanel: albumInfoPanel)
			
		newAlbumCenterPanel.setAlbumTitle(albumTitle)
		newAlbumCenterPanel.setAlbumArtist(albumArtist)
		newAlbumCenterPanel.setAlbumYear(albumYear)
		newAlbumCenterPanel.setSongList(songList)
		
		return newAlbumCenterPanel
	}

	/**
	 * @return creates and returns the button JPanel
	 */
	private JPanel createButtonPanel(){
		JPanel p = new JPanel()

		okButt = new JButton(LocaleMessage.getInstance().getString("newalbum.okbutton"))
		okButt.setToolTipText(LocaleMessage.getInstance().getString("newalbum.okbutton_tooltip"))
		okButt.addActionListener([
			actionPerformed: {
				if(saveAlbumAndSongsIntoDB()) {
					dispose()
					mainWindow.getCenterPanel().refreshAlbumsView(
							mainWindow.getCenterPanel().getAlbumsPanel().getSortMode())
					SwingUtilities.invokeLater([
						run: {
							centerPanel.revalidate()
						}
					] as Runnable)
				}
			}
		] as ActionListener)
		okButt.setEnabled(false)

		cancelButt = new JButton(LocaleMessage.getInstance().getString("common.abort"))
		cancelButt.setToolTipText(LocaleMessage.getInstance().getString("common.abort_tooltip"))
		cancelButt.addActionListener([
			actionPerformed: {
				dispose()
			}
		] as ActionListener)

		p.setLayout(new FlowLayout(FlowLayout.RIGHT))
		p.add(okButt)
		p.add(cancelButt)
		return p
	}

	/**
	 * Loads the album information (title, artist and year) from within the mp3
	 * files in the directory.
	 */
	private void loadAlbumInfo(){
		// first clear the song lists
		songs.clear()
		songList.removeAllItems()

		final MP3PropertyReader mp3SongInfo = new MP3PropertyReader()

		String album = ""
		String artist = ""
		String year = ""

		String[] songfiles = albumFolder.list()
		String albumPath = albumFolder.getAbsolutePath()
		albumPath += File.separator
		int i = 1
		for(String songfile: songfiles) {
			// infos of the current song (index 0: title, 1: duration)
			String[] songInfos = new String[2]

			// read id3 tag infos from mp3 files
			if(songfile.toLowerCase().endsWith("mp3")) {
				try {
					String[] albumInfos = ExtendedMP3Info.getAlbumInfoFromSongFiles(albumPath + songfile)
					if(StringUtils.isEmpty(album)) album = albumInfos[0]
					if(StringUtils.isEmpty(artist)){
						artist = albumInfos[1]
					}
						
					if(StringUtils.isEmpty(year)) year = albumInfos[2]
					songInfos = mp3SongInfo.getSongTitleAndLength(albumPath, songfile)
				} catch(IOException e) {
					e.printStackTrace()
				} catch(TagException e) {
					e.printStackTrace()
				}
			} else {
				// only mp3 files are supported
				continue
			}

			Song s = new Song()
			s.setFileName(songfile)
			s.setTitle(songInfos[0])
			s.setDuration(songInfos[1])
			s.setArtist(artist)
			songs.add(s)
			songList.addItem(i + ": " + songInfos[0])
			i++
		}

		albumTitle.setText(album) 
		albumArtist.setText(artist)
		albumYear.setText(year)

		SwingUtilities.invokeLater([
			run: {
				busyLabel.setBusy(false)
				busyLabel.setText(LocaleMessage.getInstance().getString("newalbum.searching_done"))
				busyLabel.setToolTipText(LocaleMessage.getInstance().getString("newalbum.searching_done_tooltip"))
				centerPanel.add(albumInfoPanel, BorderLayout.CENTER)
				if(songs.size() > 0)
					okButt.setEnabled(true)
				// if no songs were fond in the directory
				else {
					VistaDialog.showDialog(
							LocaleMessage.getInstance().getString("newalbum.nosongs_dialog_title"),
							LocaleMessage.getInstance().getString("newalbum.nosongs_title"),
							LocaleMessage.getInstance().getString("newalbum.nosongs_text"),
							VistaDialog.WARNING_MESSAGE)
				}
			}
		] as Runnable)
	}

	/**
	 * Saves the album and its songs information into the database.
	 * 
	 * @return <code>true</code> if everything worked fine, <code>false</code>
	 *         if the album is already in the db
	 */
	private boolean saveAlbumAndSongsIntoDB(){
		String artist = albumArtist.getText().trim()
		String title = albumTitle.getText().trim()

		// ensure that artist and title are given
		if(StringUtils.isEmpty(artist) || StringUtils.isEmpty(title)) {
			VistaDialog.showDialog(LocaleMessage.getInstance().getString("error.6"),
					LocaleMessage.getInstance().getString("error.24"),
					LocaleMessage.getInstance().getString("error.25"),
					VistaDialog.WARNING_MESSAGE)
			return false
		}

		// check if the album is already in the database
		if(mainWindow.getDatabase().checkAlbumAlreadyInDB(artist, title)) {
			VistaDialog.showDialog(LocaleMessage.getInstance().getString("error.9"),
					LocaleMessage.getInstance().getString("error.10"),
					LocaleMessage.getInstance().getString("error.11"),
					VistaDialog.INFORMATION_MESSAGE)
			return false
		}

		// check the year (if given)
		int year = 0
		String yearString = albumYear.getText().trim()
		if(StringUtils.isEmpty(yearString)) yearString = "0"
		try {
			year = Integer.parseInt(yearString)
		} catch(NumberFormatException e) {
			VistaDialog.showDialog(LocaleMessage.getInstance().getString("error.6"),
					LocaleMessage.getInstance().getString("error.7"),
					LocaleMessage.getInstance().getString("error.8"),
					VistaDialog.INFORMATION_MESSAGE)
			return false
		}

		// create an album object
		a = new Album()
		a.setArtist(artist)
		a.setFolderPath(folderPath.getText())
		a.setTitle(title)
		a.setYear(year)
		// try to build a preview image
		BufferedImage preview = null
		File bigCover = new File(a.getFolderPath() + File.separator + "folder.jpg")
		if(bigCover.exists()) {
			ImageHelper helper = new ImageHelper()
			preview = helper.createSmallCover(bigCover)
		}
		a.setPreview(preview)

		int albumId = a.saveIntoDB(mainWindow.getDatabase())
		songs.each { song ->
			song.albumId = albumId
			song.saveIntoDB(mainWindow.getDatabase())
		}

		return true
	}

	/**
	 * @return a reference to the instance of this class
	 */
	private NewAlbumFrame getThisInstance(){
		return this 
	}
}