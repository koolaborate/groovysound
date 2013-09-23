package com.koolaborate.mvc.view.playlistview

import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
//import javax.swing.JLabel;
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.SwingUtilities


/***********************************************************************************
 * TimeElapsedPanel                                                                *
 ***********************************************************************************
 * A panel with a slider that shows the progress of the song.                      *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.0                                                                    *
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
public class TimeElapsedPanel extends JPanel{
	private static final long serialVersionUID = -2079874923276877017L
	JSlider timeElapsedSlider
//	private JLabel elapsedTimeLabel, totalTimeLabel;
//	private int songSeconds = 0;
	long songFileSize = (long) 0
	
	/**
	 * Constructor.
	 */
	public TimeElapsedPanel(final CoverPanel coverPanel){
		setOpaque(false)
		
		timeElapsedSlider = new JSlider(0, 1)
		timeElapsedSlider.setOpaque(false)
//		timeElapsedSlider.setPreferredSize(new Dimension(coverPanel.getPreferredSize().width - 100, 20));
		timeElapsedSlider.setPreferredSize(new Dimension(coverPanel.getPreferredSize().width - 30, 20))
		timeElapsedSlider.setValue(0)
		timeElapsedSlider.setEnabled(false)
		timeElapsedSlider.addMouseListener([
			mouseReleased: { event ->
				MouseAdapter e = (MouseAdapter) event
				JSlider source = (JSlider) e.getSource()
				int intVal = source.getValue()
//				System.out.println("Soll spulen zu " + seconds + " Sekunden..."); //TODO
				coverPanel.mainWindow.getPlayerPanel().seekToPosition((long)intVal)
//				int min = seconds / 60;
//				int secs = seconds % 60;
//				elapsedTimeLabel.setText(" " + min + ":" + secs);
			}
		] as MouseAdapter)
		
//		elapsedTimeLabel = new JLabel(" 0:00");
//		elapsedTimeLabel.setOpaque(false);
////		elapsedTimeLabel.setPreferredSize(new Dimension(20, 20));
//		totalTimeLabel = new JLabel("/ 0:00");
//		totalTimeLabel.setOpaque(false);
////		totalTimeLabel.setPreferredSize(new Dimension(30, 20));
		
		setLayout(new FlowLayout(FlowLayout.LEFT))
		add(timeElapsedSlider)
//		add(elapsedTimeLabel);
//		add(totalTimeLabel);
	}
	
	
	public void setSongFileSize(long size){
		this.songFileSize = size
		timeElapsedSlider.setMaximum((int)songFileSize)
		timeElapsedSlider.setEnabled(true)
	}

	public void updateSlider(final int value){
		SwingUtilities.invokeLater([
			run: {
				timeElapsedSlider.setValue(value)
			}
		] as Runnable)
	}
}