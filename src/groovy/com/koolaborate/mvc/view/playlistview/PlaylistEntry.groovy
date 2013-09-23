package com.koolaborate.mvc.view.playlistview

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Composite
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.RenderingHints
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
//import javax.swing.Icon;
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingUtilities

/***********************************************************************************
 * PlaylistEntry                                                                   *
 ***********************************************************************************
 * This class represents an entry in the playlist.                                 *
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
public class PlaylistEntry extends JPanel{
	private static final long serialVersionUID = -4772418916345558526L
	boolean mouseOver = false
	Color playListEntryColor, activeBg
	float activeAlpha = 1.0f
	
	Playlist playlist
	
	boolean playing, paused = false
	String title, path, duration = ""
	int songId = 0
	
	JLabel imgLabel, titleLabel, durationLabel
	
	Font normalFont     = new Font("Calibri", Font.PLAIN, 12)
	Font playingFont    = new Font("Calibri", Font.BOLD, 12)
	Font unPlayableFont = new Font("Calibri", Font.ITALIC, 12)
	Color unplayableColor = Color.GRAY
	

	/**
	 * Constructor.
	 * 
	 * @param list the playlist
	 * @param path the complete path of the song entry
	 * @param duration the duration of the song as a String
	 * @param songId the id of the song
	 * @param title the song title
	 * @param borderColor the color of the selection border
	 * @param backgrColor the color of the selection background
	 * @param activeAlpha the alpha value for the selection
	 */
	public PlaylistEntry(Playlist list, String path, String duration, int songId, String title, Color borderColor, Color backgrColor, float activeAlpha){
		this.playlist = list
		this.title = title
		this.path = path
		this.duration = duration
		this.songId = songId
		this.playListEntryColor = borderColor
		this.activeBg = backgrColor
		this.activeAlpha = activeAlpha
		
		setOpaque(false)
		initGUI()
		
		addMouseListener([
			mouseEntered: {
				setMouseOver(true)
				repaint()
			},
			
			mouseExited: {
				setMouseOver(false)
				repaint()
			},
			
			mouseClicked: { mouseEvent ->
				MouseEvent e = mouseEvent
				PlaylistEntry selected = getThisInstance()
				
				playlist.setSelectedEntry(selected)
				
				// play selected song at double click
				if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2)
				{
					playlist.getWindow().getPlayerPanel().playSong()
				}
				// right click invokes a popup menu
				else if(SwingUtilities.isRightMouseButton(e))
				{
					playlist.showPopup(selected, e.getPoint().x, e.getPoint().y)
				}
			}
		] as MouseAdapter)
	}
	
	@Override
	protected void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		
		if(mouseOver){
			// transparency according to set value
			Composite oldComposite = g2.getComposite()
			g2.setComposite(AlphaComposite.SrcOver.derive(activeAlpha))
			g2.setColor(activeBg)
			g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5)
			g2.setColor(playListEntryColor)
			g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 5, 5)
			// afterwards set alpha value back to 1.0
			g2.setComposite(oldComposite)
		}
	}
	
	
	/**
	 * @return a reference to the instance of the class
	 */
	private PlaylistEntry getThisInstance(){
		return this
	}
	
	
	/**
	 * Initialitzes the GUI elements.
	 */
	private void initGUI(){
		setLayout(new GridBagLayout())
		
		GridBagConstraints gbc = new GridBagConstraints()
		gbc.anchor = GridBagConstraints.LINE_START
		gbc.fill = GridBagConstraints.NONE
		gbc.gridx = 0
		gbc.gridy = 0
		gbc.insets = new Insets(2, 4, 2, 4)
		
		imgLabel = new JLabel()
		imgLabel.setPreferredSize(new Dimension(16, 16))
		add(imgLabel, gbc)
		
		gbc.gridx = 1
		gbc.fill = GridBagConstraints.HORIZONTAL
		gbc.weightx = 1.0f
		titleLabel = new JLabel(title)
		titleLabel.setFont(normalFont)
		add(titleLabel, gbc)
		
		gbc.gridx = 2
		gbc.fill = GridBagConstraints.NONE
		gbc.weightx = 0.0f
		durationLabel = new JLabel(duration)
		durationLabel.setFont(normalFont)
		add(durationLabel, gbc)
	}

	/**
	 * Sets the song to be currently playing or not. An icon is set to mark the currently played song.
	 * 
	 * @param playing <code>true</code> to set the song to be playing, <code>false</code> otherwise
	 */
	public void setPlaying(boolean playing){
		this.playing = playing
		if(playing){
//			imgLabel.setIcon(playIco);
			imgLabel.setIcon(new ImageIcon(PlaylistEntry.class.getResource("/images/playlist_play.png")))
			titleLabel.setFont(playingFont)
			durationLabel.setFont(playingFont)
		} else {
			imgLabel.setIcon(null)
			titleLabel.setFont(normalFont)
			durationLabel.setFont(normalFont)
		}
		
		imgLabel.repaint()
		titleLabel.repaint()
		durationLabel.repaint()
	}
	
	
	/**
	 * Sets the song to be currently paused or not. An icon is set to mark the currently pausing song.
	 * 
	 * @param paused <code>true</code> to set the song to be paused, <code>false</code> otherwise
	 */
	public void setPaused(boolean paused){
		this.paused = paused
		if(paused){
//			imgLabel.setIcon(pauseIco);
			imgLabel.setIcon(new ImageIcon(PlaylistEntry.class.getResource("/images/playlist_paused.png")))
		} else {
			imgLabel.setIcon(null)
		}
		imgLabel.repaint()
	}

	/**
	 * Sets the entry to be playable or not. Some mp3 files cannot be played so they get marked.
	 * 
	 * @param playable <code>true</code> if it can be played, <code>false</code> otherwise
	 */
	public void setPlayable(boolean playable){
		if(!playable) {
			imgLabel.setIcon(null)
			
			titleLabel.setFont(unPlayableFont)
			titleLabel.setForeground(unplayableColor)
			
			durationLabel.setFont(unPlayableFont)
			durationLabel.setForeground(unplayableColor)
			
			imgLabel.repaint()
			titleLabel.repaint()
			durationLabel.repaint()
		}
	}
}