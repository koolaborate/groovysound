package ui.playlistview;

import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import types.CurrentSongInfo;

/***********************************************************************************
 * SongInfoPanel                                                                   *
 ***********************************************************************************
 * A JPanel to show various information about the currently played song.           *
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
public class SongInfoPanel extends JPanel
{
	private JLabel songLabel, artistLabel; 
	private CurrentSongInfo songInfo;
	
	/**
	 * Constructor.
	 */
	public SongInfoPanel()
	{
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);
		
		songLabel   = new JLabel();
		artistLabel = new JLabel();
		songLabel.setOpaque(false);
		songLabel.setPreferredSize(new Dimension(230, 12));
		songLabel.setFont(new Font("Serif", Font.BOLD, 20));
		artistLabel.setOpaque(false);
		
		add(songLabel);
		add(artistLabel);
	}
	
	/**
	 * Updates the song information.
	 * 
	 * @param _songInfo the new song information object
	 */
	public void updateSongInfo(CurrentSongInfo _songInfo)
	{
		this.songInfo = _songInfo;
		
		if(songInfo.getSongTitle() != null)
		{
			songLabel.setText(songInfo.getSongTitle());
		}
		else
		{
			songLabel.setText(songInfo.getSongFileName());
		}
		if(songInfo.getArtist() != null)
		{
			artistLabel.setText(songInfo.getArtist());
		}
		else
		{
			artistLabel.setText("");
		}
		
		artistLabel.setOpaque(false);
		
		// repaint thread safe
		SwingUtilities.invokeLater(new Runnable(){
			public void run()
			{
				repaint();
			}
		});
	}
}