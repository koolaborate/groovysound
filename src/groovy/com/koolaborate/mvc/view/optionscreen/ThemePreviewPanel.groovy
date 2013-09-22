package com.koolaborate.mvc.view.optionscreen

import java.awt.Color
import java.awt.Dimension
import java.awt.image.BufferedImage

import javax.swing.BoxLayout
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.LineBorder

import com.koolaborate.util.LocaleMessage

/***********************************************************************************
 * ThemePreviewPanel                                                               *
 ***********************************************************************************
 * A panel that shows a preview image of the currently selected theme.             *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.04                                                                   *
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
public class ThemePreviewPanel extends JPanel{
	private static final long serialVersionUID = -3091117485725551187L
	JLabel imageLabel
	
	/**
	 * Constructor.
	 */
	public ThemePreviewPanel(){
		setOpaque(false)
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS))
		
		JLabel titleLabel = new JLabel(LocaleMessage.getInstance().getString("options.preview")) 
		titleLabel.setPreferredSize(new Dimension(230, 12))
		add(titleLabel)
		
		imageLabel = new JLabel()
		imageLabel.setBorder(new LineBorder(Color.BLACK))
		add(imageLabel)
	}
	
	
	/**
	 * Method to update the preview image.
	 * 
	 * @param img the new image to be shown
	 */
	public void updateImage(BufferedImage img){
		if(img != null){
			imageLabel.icon = new ImageIcon(img)
		}
	}
}