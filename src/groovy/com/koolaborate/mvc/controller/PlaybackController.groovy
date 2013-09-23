package com.koolaborate.mvc.controller

import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.image.BufferedImage
import java.io.File
import java.io.FileInputStream
import java.util.Map

import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.swing.JLabel
import javax.swing.JSlider
import javax.swing.SwingUtilities
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

import javazoom.jl.decoder.Bitstream
import javazoom.jlgui.basicplayer.BasicController
import javazoom.jlgui.basicplayer.BasicPlayer
import javazoom.jlgui.basicplayer.BasicPlayerEvent
import javazoom.jlgui.basicplayer.BasicPlayerException
import javazoom.jlgui.basicplayer.BasicPlayerListener
import mp3.CoverHelper

import org.apache.commons.lang3.StringUtils
import org.apache.log4j.Logger

import com.koolaborate.model.Album
import com.koolaborate.model.CurrentSongInfo
import com.koolaborate.model.Settings
import com.koolaborate.mvc.view.decorations.Decorator
import com.koolaborate.mvc.view.dialogs.VistaDialog
import com.koolaborate.mvc.view.mainwindow.CenterPanel
import com.koolaborate.mvc.view.mainwindow.MainWindow
import com.koolaborate.mvc.view.playercontrols.NextButton
import com.koolaborate.mvc.view.playercontrols.PlayButton
import com.koolaborate.mvc.view.playercontrols.PreviousButton
import com.koolaborate.mvc.view.playercontrols.StopButton
import com.koolaborate.mvc.view.playlistview.PlaylistPanel
import com.koolaborate.service.db.Database
import com.koolaborate.util.FileHelper
import com.koolaborate.util.ImageHelper
import com.koolaborate.util.LocaleMessage

/***********************************************************************************
 * PlaybackController *
 *********************************************************************************** 
 * This class works as the controller in the model-view-controller scheme. This
 * * controller is used for playing songs, stopping them and pausing them. *
 *********************************************************************************** 
 * (c) Impressive Artworx, 2k8 *
 * 
 * @author Manuel Kaess *
 * @version 1.1 *
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
// public class PlaybackController extends JPanel implements BasicPlayerListener
public class PlaybackController implements BasicPlayerListener{
	/** the log4j logger */
	static Logger log = Logger.getLogger(PlaybackController.class.getName())

	/** the volume slider */
	JSlider volumeSlider

	// button elements
	PreviousButton prevButt
	PlayButton playButt
	StopButton stopButt
	NextButton nextButt

	/** the player to playback sound files */
	BasicPlayer player
	/** control unit to set volume, balance etc. */
	BasicController control

	public enum STATE{
		PLAYING, PAUSED, ENDED
	}

	STATE currentState = STATE.ENDED

	String filename = ""

	Settings settings

	CurrentSongInfo songInfo
	MainWindow mainWindow
	PlaylistPanel playListPanel
	CenterPanel centerPanel

	/**
	 * Constructor.
	 * 
	 * @param cPanel
	 *            the reference to the center panel
	 * @param playlist
	 *            the reference to the song playlist
	 * @param window
	 *            the reference to the main window
	 */
	public PlaybackController(CenterPanel cPanel, PlaylistPanel playlist, MainWindow window){
		this.centerPanel = cPanel
		this.playListPanel = playlist
		this.mainWindow = window
		this.songInfo = window.getSongInfo()
		this.settings = window.getSettings()

		Decorator d = window.getDecorator()
		// load the GUI elements from the decorator
		prevButt = d.getPrevButt()
		playButt = d.getPlayButt()
		stopButt = d.getStopButt()
		nextButt = d.getNextButt()
		volumeSlider = d.getVolumeSlider()
		JLabel softLabel = d.getLoudVolumeLabel()
		JLabel loudLabel = d.getSoftVolumeLabel()
		d.getPlaybackControlsPanel()

		// init the player for audio playback
		player = new BasicPlayer()
		control = (BasicController) player
		player.addBasicPlayerListener(this)

		// add the buttons...

		// previous song button
		prevButt.addMouseListener([
			mouseClicked: { mouseEvent ->
				playButt.setActive(false)
				if(currentState == STATE.PLAYING) {
					fadeOut()
				}
				playPreviousSong()
			}
		] as MouseAdapter)

		// play button
		playButt.addMouseListener([
			mouseClicked: {
				if(currentState == STATE.ENDED) {
					playSong()
				} else if(currentState == STATE.PLAYING) {
					pauseSong()
				} else if(currentState == STATE.PAUSED) {
					resumeSong()
				}
			}
		] as MouseAdapter)

		// stop button
		stopButt.addMouseListener([
			mouseClicked: {
				fadeOut()
			}
		] as MouseAdapter)

		// next song button
		nextButt.addMouseListener([
			mouseClicked: { e ->
				playButt.setActive(false)
				// first stop playback if running
				if(currentState == STATE.PLAYING) {
					fadeOut()
				}
				playNextSong()
			}
		] as MouseAdapter)

		// volume slider
		volumeSlider.setOpaque(false)

		// loudness labels
		softLabel.addMouseListener([
			mouseClicked: {
				volumeSlider.setValue(volumeSlider.getMinimum())
			}
		] as MouseAdapter)

		volumeSlider.setMinimum(0)
		volumeSlider.setMaximum(10)
		int volumeInt = (int) (settings.getVolume() * 10)
		volumeSlider.setValue(volumeInt)
		volumeSlider.addChangeListener([
			stateChanged: { e ->
				JSlider source = (JSlider) e.getSource()
				float vol = source.getValue() * 0.1f
				setPlayerVolume(vol)
			}
		] as ChangeListener)

		loudLabel.addMouseListener([
			mouseClicked: { 
				volumeSlider.setValue(volumeSlider.getMaximum())
			}
		] as MouseAdapter)
	}

	/**
	 * Resumes the currently paused song.
	 */
	public void resumeSong(){
		try {
			control.resume()
			currentState = STATE.PLAYING
			playButt.setPressed(true)
			playListPanel.getPlaylist().setPlayIconAtCurrentEntry()
		} catch(BasicPlayerException e) {
			log.warn("Unable to resume song: " + e.getMessage())
		}
	}

	/**
	 * Pauses the current song.
	 */
	public void pauseSong(){
		try {
			control.pause()
			currentState = STATE.PAUSED
			playButt.setPressed(false)
			playListPanel.getPlaylist().setPausedIconAtCurrentEntry()
		} catch(BasicPlayerException e) {
			log.warn("Unable to pause song: " + e.getMessage())
		}
	}

	/**
	 * Adjust the volume and the balance to the saved settings.
	 */
	private void adjustVolumeAndBalance(){
		try {
			// Set volume (0 to 1.0).
			control.setGain(settings.volume)
			// Set pan (balance) (-1.0 to 1.0).
			control.setPan(settings.balance)
		} catch(BasicPlayerException e) {
			log.warn("Unable to adjust volume and balance: " + e.getMessage())
		}
	}

	/**
	 * Plays the song given through playListPanel.getCurrentlySelectedSong().
	 */
	public void playSong(){
		try {
			// first end currently running song
			if(currentState == STATE.PLAYING || currentState == STATE.PAUSED)
				control.pause()

			filename = playListPanel.getCurrentlySelectedSong()

			// if there is no song to be played
			if(StringUtils.isEmpty(filename)) {
				// the play button has to be deselected again
				playButt.setActive(false)
				playButt.setPressed(false)
				return
			}

			File f = new File(filename)
			if(!f.exists()) {
				// in case the file doesn't exist any more, the user has to be
				// asked whether or not
				// to remove the song from the list. If the whole album cannot
				// be found any more,
				// the user is asked to delete the album
				handleSongNoLongerAvailable()
				return
			}

			control.open(f)

			// has to be in a thread, otherwise no playback und XP!
			// new Thread(new Runnable(){
			// public void run()
			// {
			// adjustVolumeAndBalance();
			// }
			// }).start();

			control.play()

			// HOTFIX: adjust the volume once again since it did not work under
			// Vista in
			// the first place the volume was always at max when a new song was
			// started...
			adjustVolumeAndBalance()

			currentState = STATE.PLAYING
			playButt.setPressed(true)

			// read and display song info
			songInfo.songPath = filename
			songInfo.songId = playListPanel.getCurrentlySelectedSongID()
			playListPanel.getPlaylist().setPlayIconAtCurrentEntry()

			try {
				songInfo.readSongInfo()
				mainWindow.updateArtist(songInfo)
			} catch(Exception e) {
				// the artist information and song name could not be read
				log.debug(e.getMessage())
			}
		} catch(BasicPlayerException e) {
			// TODO catch this cause in a more elegant way!
			if(e.getMessage().equals(
					"java.io.IOException: Resetting to invalid mark")) {
				// this exception is thrown if the mp3 file contains an embedded
				// cover
				// image. Save the image if there does not yet exist one
				// if return value is true: refresh cover image
				if(CoverHelper.saveEmbeddedCover(filename)) {
					// causes the cover path in songInfo to be determined
					songInfo.setAlbumPath(songInfo.getAlbumPath())
					mainWindow.updateCover(songInfo)
					// update album in album overview as well!
					String big = songInfo.getAlbumPath() + File.separator + "folder.jpg"
					BufferedImage preview = null
					File bigCover = new File(big)
					if(bigCover.exists()) {
						ImageHelper helper = new ImageHelper()
						preview = helper.createSmallCover(bigCover)
					}

					Database db = mainWindow.getDatabase()
					Album a = db.getAlbumById(songInfo.getAlbumId())
					a.setPreview(preview)
					db.updateAlbum(a)

					// refresh the albumsview since the thumbnail might have
					// changed
					centerPanel.getAlbumsPanel().refreshSelectedAlbum(a)
				}

				// now skip the cover image and playback the file
				// solution was found here:
				// http://www.javazoom.net/services/forums/viewMessage.jsp?message=17335&thread=5711&parent=-1&forum=7

				// Instead of reading the file directly, use a FileInputStream
				// and
				// BitStream to seek to the beginning of the audio data
				try {
					FileInputStream f_in = new FileInputStream(filename)
					Bitstream m = new Bitstream(f_in)
					long start = m.header_pos()

					// need to open the stream again
					try {
						m.close()
					} catch(Exception ex) {
						ex.printStackTrace()
					}
					f_in = new FileInputStream(filename)

					// skip the header
					f_in.skip(start)

					AudioInputStream instream = AudioSystem.getAudioInputStream(f_in)
					control.open(instream)
					control.play()

					// HOTFIX: adjust the volume once again since it did not
					// work under Vista in
					// the first place the volume was always at max when a new
					// song was started...
					adjustVolumeAndBalance()

					currentState = STATE.PLAYING
					playButt.setPressed(true)

					// read and display song info
					songInfo.setSongPath(filename)
					songInfo.setSongId(playListPanel
							.getCurrentlySelectedSongID())
					playListPanel.getPlaylist().setPlayIconAtCurrentEntry()

					songInfo.readSongInfo()
					mainWindow.updateArtist(songInfo)
				} catch(Exception e1) {
					log.debug(e1.getMessage())
				}
			} else {
				VistaDialog.showDialog(LocaleMessage.getInstance().getString("error.1"),
						LocaleMessage.getInstance().getString("error.22"),
						LocaleMessage.getInstance().getString("error.23"),
						VistaDialog.WARNING_MESSAGE)
				playListPanel.getPlaylist().setCurrentEntryUnplayable()
				playListPanel.getPlaylist().setNoIconAtCurrentEntry()
				log.debug(e.getMessage())
			}
		}
	}

	/**
	 * Handles the case if a song file is no longer existent under the given
	 * path.
	 */
	private void handleSongNoLongerAvailable(){
		// check if the album folder is available
		log.debug("File " + filename + " not found!")
		log.debug("Current folder: " + mainWindow.getCurrentFolderPath())

		mainWindow.getPlaylist().getPlaylist().setCurrentEntryUnplayable()

		String path = mainWindow.getCurrentFolderPath()
		if(StringUtils.isNotEmpty(path)) {
			File folder = new File(path)
			if(!folder.exists()) {
				VistaDialog delAlbum = VistaDialog.showConfirmationDialog(
						LocaleMessage.getInstance().getString("error.15"),
						LocaleMessage.getInstance().getString("error.16"),
						LocaleMessage.getInstance().getString("error.17") + " '" + " " + path + " ' " + LocaleMessage.getInstance().getString("error.20") + "\n\n" + LocaleMessage.getInstance().getString("error.18"))
				if(delAlbum.yesSelected) {
					int albumId = mainWindow.getPlaylist().getCurrentlySelectedSongAlbumId()
					log.debug("Deleting album with id " + albumId)
					if(albumId != -1)
						mainWindow.getDatabase().deleteAlbum(albumId)

					// refresh the albums view
					centerPanel.refreshAlbumsView(centerPanel.getAlbumsPanel().getSortMode())
					SwingUtilities.invokeLater([
						run: {
							centerPanel.revalidate()
						}
					] as Runnable)

					// refresh the playlist view (load empty cover since it was
					// the active
					// album in the playlist)...
					centerPanel.playlistPanel.getPlaylist().clearPlaylist()

					// stop playback if song is from current album
					if(currentState == STATE.PLAYING) {
						fadeOut()
					}
					currentState = STATE.ENDED

					CurrentSongInfo info = new CurrentSongInfo()

					// then clear the playlist and update the view
					mainWindow.setCurrentSongInfo(info)
					centerPanel.updateCover(mainWindow.getSongInfo())
					centerPanel.getCoverPanel().refreshCover()
					mainWindow.updateArtist(info)
					mainWindow.setCurrentFolder(null)
				}
			}
			// if the folder exists, only the song is missing
			else {
				VistaDialog delSong = VistaDialog.showConfirmationDialog(
						LocaleMessage.getInstance().getString("error.13"),
						LocaleMessage.getInstance().getString("error.12"),
						LocaleMessage.getInstance().getString("error.19") + " '" + filename + "' " + LocaleMessage.getInstance().getString("error.20") + LocaleMessage.getInstance().getString("error.21"))
				if(delSong.yesSelected) {
					int songId = mainWindow.getPlaylist().getCurrentlySelectedSongID()
					if(songId != -1) mainWindow.getDatabase().deleteSong(songId)
					FileHelper.getInstance().removeFile(filename)
					mainWindow.getPlaylist().refreshSongList()
					mainWindow.getPlaylist().repaint()
				}
			}
		}
	}

	/**
	 * Plays the next song in the playlist.
	 */
	public void playNextSong(){
		// selectNextSong returns true if there was another song in the list,
		// if it was the last song, then don't play it again
		if(playListPanel.selectNextSong()) {
			playSong()
			mainWindow.handleTinyWindowUpdate()
		}
	}

	/**
	 * Plays the previous song in the playlist.
	 */
	public void playPreviousSong(){
		// if it was the first song, then play it again
		playListPanel.selectPreviousSong()
		playSong()
		mainWindow.handleTinyWindowUpdate()
	}

	/**
	 * Sets the volume.
	 * 
	 * @param vol
	 *            the desired volume between 0.0f and 1.0f
	 */
	private void setPlayerVolume(float vol){
		try {
			settings.setVolume(vol)
			control.setGain(vol)
		} catch(BasicPlayerException e) {
			log.warn("Unable to set volume: " + e.getMessage())
		}
	}

	/**
	 * To softly fade out.
	 */
	public void fadeOut(){
		float vol = settings.getVolume()
		try {
			for(double volume = vol; volume >= 0.0; volume -= 0.01) {
				control.setGain(volume)
				Thread.sleep(30)
			}
			stopSong()
			control.setGain(vol)
		} catch(BasicPlayerException e) {
			log.warn("Unable to fade out: " + e.getMessage())
		} catch(InterruptedException e) {
			log.debug(e.getMessage())
		}
	}

	/**
	 * Stops the playback of the current song.
	 */
	public void stopSong(){
		try {
			control.stop()
			playButt.setPressed(false)
			playListPanel.getPlaylist().setNoIconAtCurrentEntry()
			currentState = STATE.ENDED
		} catch(BasicPlayerException e) {
			log.warn("Unable to stop song: " + e.getMessage())
		}
	}

	/**
	 * To seek within the song.
	 * 
	 * @param bytes
	 *            the amount of bytes to which the player shall seek to
	 */
	public void seekToPosition(long bytes){
		System.out.println("SEEKING TO " + bytes) // TODO
		try {
			control.seek(bytes)
		} catch(BasicPlayerException e) {
			log.debug("Cannot skip: " + e.getMessage())
		}
	}

	/**
	 * Open callback, stream is ready to play.
	 * 
	 * properties map includes audio format dependant features such as bitrate,
	 * duration, frequency, channels, number of frames, vbr flag, ...
	 * 
	 * @param stream
	 *            could be File, URL or InputStream
	 * @param properties
	 *            audio stream properties.
	 */
	public void opened(Object stream, Map properties){
		// Pay attention to properties. It's useful to get duration,
		// bitrate, channels, even tag such as ID3v2.
		log.info("opened : " + properties.toString())
	}

	/**
	 * Progress callback while playing.
	 * 
	 * This method is called severals time per seconds while playing. properties
	 * map includes audio format features such as instant bitrate, microseconds
	 * position, current frame number, ...
	 * 
	 * @param bytesread
	 *            from encoded stream.
	 * @param microseconds
	 *            elapsed (<b>reseted after a seek !</b>).
	 * @param pcmdata
	 *            PCM samples.
	 * @param properties
	 *            audio stream parameters.
	 */
	public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties){
		// Pay attention to properties. It depends on underlying JavaSound SPI
		// MP3SPI provides mp3.equalizer.
		// System.out.println("progress : " + properties.toString());

		def mp3PositionByteLong = Long.parseLong(properties.get("mp3.position.byte"))
		def mp3PositionByteInt = Integer.parseInt(properties.get("mp3.position.byte"))
		centerPanel.getTimeElapsedPanel().updateSlider(mp3PositionByteInt)
	}

	/**
	 * Notification callback for basicplayer events such as opened, eom ...
	 * 
	 * @param event
	 */
	public void stateUpdated(BasicPlayerEvent event){
		// Notification of BasicPlayer states (opened, playing, end of media,
		log.debug("stateUpdated : " + event.toString())

		int state = event.getCode()

		// if end of media, play next song
		if(state == BasicPlayerEvent.EOM) {
			playNextSong()
		} else if(state == BasicPlayerEvent.SEEKING) {
			System.out.println("SEEKING...") // TODO
		} else if(state == BasicPlayerEvent.SEEKED) {
			System.out.println("SEEKED!") // TODO
		} else if(state == BasicPlayerEvent.STOPPED) {
			// reset slider
			JSlider slider = centerPanel.getTimeElapsedPanel().getTimeElapsedSlider()
			slider.setValue(0)
			slider.repaint()
		}
	}

	/**
	 * A handle to the BasicPlayer, plugins may control the player through the
	 * controller (play, stop, ...)
	 * 
	 * @param controller
	 *            : a handle to the player
	 */
	public void setController(BasicController controller){}

	/**
	 * Sets the balance of the playback.
	 * 
	 * @param balance
	 *            a value between -1.0f (left) and 1.0f (right). 0.0f means
	 *            middle
	 */
	public void setPlayerBalance(float balance){
		try {
			settings.setBalance(balance)
			control.setPan((double) balance)
		} catch(BasicPlayerException e) {
			log.warn("Unable to set balance: " + e.getMessage())
		}
	}

	/**
	 * Sets the volume.
	 * 
	 * @param vol
	 *            the desired volume between 0.0f and 1.0f
	 */
	public void setVolumeAndUpdateSlider(float newVol){
		volumeSlider.setValue((int) (newVol * 10))
		setPlayerVolume(newVol)
	}

}