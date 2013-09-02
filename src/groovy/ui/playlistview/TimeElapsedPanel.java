package ui.playlistview;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
//import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;


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
public class TimeElapsedPanel extends JPanel
{
	private JSlider timeElapsedSlider;
//	private JLabel elapsedTimeLabel, totalTimeLabel;
//	private int songSeconds = 0;
	private long songFileSize = (long)0;
	
	/**
	 * Constructor.
	 */
	public TimeElapsedPanel(final CoverPanel coverPanel)
	{
		setOpaque(false);
		
		timeElapsedSlider = new JSlider(0, 1);
		timeElapsedSlider.setOpaque(false);
//		timeElapsedSlider.setPreferredSize(new Dimension(coverPanel.getPreferredSize().width - 100, 20));
		timeElapsedSlider.setPreferredSize(new Dimension(coverPanel.getPreferredSize().width - 30, 20));
		timeElapsedSlider.setValue(0);
		timeElapsedSlider.setEnabled(false);
		timeElapsedSlider.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e)
			{
				JSlider source = (JSlider) e.getSource();
				int intVal = source.getValue();
//				System.out.println("Soll spulen zu " + seconds + " Sekunden..."); //TODO
				coverPanel.getWindow().getPlayerPanel().seekToPosition((long)intVal);
//				int min = seconds / 60;
//				int secs = seconds % 60;
//				elapsedTimeLabel.setText(" " + min + ":" + secs);
			}
		});
		
//		elapsedTimeLabel = new JLabel(" 0:00");
//		elapsedTimeLabel.setOpaque(false);
////		elapsedTimeLabel.setPreferredSize(new Dimension(20, 20));
//		totalTimeLabel = new JLabel("/ 0:00");
//		totalTimeLabel.setOpaque(false);
////		totalTimeLabel.setPreferredSize(new Dimension(30, 20));
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		add(timeElapsedSlider);
//		add(elapsedTimeLabel);
//		add(totalTimeLabel);
	}
	
	
	public JSlider getTimeElapsedSlider()
	{
		return this.timeElapsedSlider;
	}
	
	
//	public void setTotalTimeLabelText(String duration)
//	{
//		totalTimeLabel.setText(" / " + duration);
//		resetTimeElapsed();
//	}
	
//	public void setTotalTimeLabelText(long duration)
//	{
//		System.out.println("duration: " + duration); //TODO
//		
//		long min = duration / (1000000*60);
//		long sec = duration % (1000000*60);
//		// TODO umrechnung von duration in sekunde ohne cast und dann parsen...
//		
//		String secString = sec + "";
//		if(secString.length() > 2)
//		{
//			secString = secString.substring(0, 2);
//		}
//		songSeconds = (int)(Integer.parseInt(secString) + min * 60);
//		System.out.println("MIN: " + min + ", SEC: " + secString + ", SECTOTAL: " + songSeconds);
//		String durationString = min + ":" + secString;
//		totalTimeLabel.setText(" / " + durationString);
//		resetTimeElapsed();
//	}
	
//	public void setElapsedTimeLabelText(String elapsed)
//	{
//		elapsedTimeLabel.setText(elapsed);
//	}

//	private void resetTimeElapsed()
//	{
////		elapsedTimeLabel.setText("0:00");
//		timeElapsedSlider.setMinimum(0);
//		timeElapsedSlider.setValue(0);
//		timeElapsedSlider.setEnabled(true);
//		timeElapsedSlider.repaint();
//	}

	public void setSongFileSize(long size)
	{
		this.songFileSize = size;
		timeElapsedSlider.setMaximum((int)songFileSize);
		timeElapsedSlider.setEnabled(true);
	}

	public void updateSlider(final int value)
	{
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				timeElapsedSlider.setValue(value);
			}
		});
	}
}