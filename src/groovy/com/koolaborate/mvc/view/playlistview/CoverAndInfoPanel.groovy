package com.koolaborate.mvc.view.playlistview

import java.awt.Dimension
import javax.swing.JPanel

/***********************************************************************************
 * CoverAndInfoPanel                                                               *
 ***********************************************************************************
 * A panel that contains the cover panel with an cover image and a panel           *
 * containing textual information about the currently played song as well as a     *
 * progress slider.                                                                *
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
public class CoverAndInfoPanel extends JPanel{
	private static final long serialVersionUID = -9201729498791490290L
	CoverPanel coverPanel
	SongInfoPanel songInfoPanel
	TimeElapsedPanel timeElapsedPanel
	
	
	/**
	 * Constructor.
	 */
	public CoverAndInfoPanel(CoverPanel coverPanel, SongInfoPanel songInfoPanel, TimeElapsedPanel timeElapsedPanel){
		this.coverPanel = coverPanel
		this.songInfoPanel = songInfoPanel
		this.timeElapsedPanel = timeElapsedPanel
		setOpaque(false)
		setLayout(null)
		setPreferredSize(new Dimension(coverPanel.getPreferredSize().width, coverPanel.getPreferredSize().height + 90))
		
		this.coverPanel.setBounds(0, 0, coverPanel.getPreferredSize().width, coverPanel.getPreferredSize().height)
		this.songInfoPanel.setBounds(20, (int)(coverPanel.getPreferredSize().height/1.5 + 10), 
				coverPanel.getPreferredSize().width - 20, songInfoPanel.getPreferredSize().height + 50)
		this.timeElapsedPanel.setBounds(10, (int)(coverPanel.getPreferredSize().height/1.5 + 50), 
				coverPanel.getPreferredSize().width - 10, songInfoPanel.getPreferredSize().height + 90)
		
		add(this.coverPanel)
		add(this.songInfoPanel)
		add(this.timeElapsedPanel)
	}
}