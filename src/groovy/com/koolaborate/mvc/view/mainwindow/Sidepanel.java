package com.koolaborate.mvc.view.mainwindow;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.JPanel;
import javax.swing.border.Border;

/***********************************************************************************
 * Sidepanel                                                                       *
 ***********************************************************************************
 * A JPanel with a gradient background that is used for the left and right side of *
 * the main window.                                                                *
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
public class Sidepanel extends JPanel
{
	private boolean leftBorder = true;
	
	/**
	 * Constructor.
	 */
	public Sidepanel()
	{
		setOpaque(true);
		setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
		setBackground(new Color(99, 108, 135));
		setBorder(new Border(){
			public Insets getBorderInsets(Component c)
			{
				return new Insets(0, 0, 0, 0);
			}
			public boolean isBorderOpaque()
			{
				return false;
			}
			public void paintBorder(Component c, Graphics g, int arg2, int arg3, int arg4, int arg5)
			{
				g.setColor(Color.BLACK);
				if(leftBorder) g.drawLine(9, 0, 9, getHeight());
				else g.drawLine(0, 0, 0, getHeight());
			}
		});
	}
	
	/**
	 * Sets the panel to be the left border or the right border.
	 * 
	 * @param leftBorder <code>true</code> if it shall be the left panel, <code>false</code> if it shall be the right one
	 */
	public void setLeftBorder(boolean leftBorder)
	{
		this.leftBorder = leftBorder;
	}
}