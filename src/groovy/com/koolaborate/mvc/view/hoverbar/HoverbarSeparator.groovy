package com.koolaborate.mvc.view.hoverbar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JComponent;

/***********************************************************************************
 * HoverbarSeparator                                                               *
 ***********************************************************************************
 * A separator to seperate different elements in a hover bar.                      *
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
class HoverbarSeparator extends JComponent{
	private static final long serialVersionUID = -3427651944716569302L;
	int width, height;
	Color c = new Color(75, 89, 105); // Color.BLACK
	
	public HoverbarSeparator(int width, int height)
	{
		this.width = width;
		this.height = height;
		setPreferredSize(new Dimension(width, height));
	}
	
	@Override
	protected void paintComponent(Graphics g) 
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(c);
		g2.drawLine(width/2, 3, width/2, height-3);
//		g2.setColor(Color.GRAY.darker());
//		g2.drawLine(width/2 + 1, 3, width/2 + 1, height-3);
	}
}