package com.koolaborate.mvc.view.navigation;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/***********************************************************************************
 * NavButton                                                                       *
 ***********************************************************************************
 * A navigation button for the different views.                                    *
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
class NavButton extends JComponent
{
	private static final long serialVersionUID = -3709284104725338580L;
	BufferedImage activeImg;
	BufferedImage inActiveImg;
	boolean mouseOver = false;
	boolean active = false;
	
	/**
	 * Constructor.
	 */
	public NavButton()
	{
        setPreferredSize(new Dimension(40, 36));
        
        addMouseListener([
        	mouseEntered: {
        		mouseOver = true;
        		repaint();
        	},
        	
        	mouseExited: {
        		mouseOver = false;
        		repaint();
        	},
        	
        	mouseClicked: {
        		active = !active;
        		repaint();
        	}
        ] as MouseAdapter);
	}
	
}