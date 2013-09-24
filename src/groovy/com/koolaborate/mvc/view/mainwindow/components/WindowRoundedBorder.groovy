package com.koolaborate.mvc.view.mainwindow.components

import java.awt.Color
import java.awt.Component
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Insets
import java.awt.RenderingHints
import javax.swing.border.AbstractBorder

/***********************************************************************************
 * RoundedBorder                                                                   *
 ***********************************************************************************
 * A rounded border for the main window.                                           *
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
class WindowRoundedBorder extends AbstractBorder{
	private static final long serialVersionUID = 8257416430750649811L
	Color color
	int radius

	@Override
	public Insets getBorderInsets(Component c){
		return new Insets(1, 1, 1, 1)
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
		Graphics2D g2d = (Graphics2D) g
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
		g2d.setColor(color)
//		g2d.drawLine(0, 0, width, 0); // top
//		g2d.drawLine(0, height-1, width, height-1); // bottom
//		g2d.drawLine(0, 0, 0, height); // left
//		g2d.drawLine(width-1, 0, width-1, height); // right
		g2d.drawRoundRect(0, 0, width-1, height-1, radius, radius)
	}
}