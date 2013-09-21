package com.koolaborate.mvc.view.optionscreen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/***********************************************************************************
 * TitlePanel                                                                      *
 ***********************************************************************************
 * A title panel for each of the different categories of the settings screen.      *
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
public class TitlePanel extends JPanel
{
	private static final long serialVersionUID = -3361496455045512196L;

	/**
	 * Constructor.
	 * 
	 * @param title the title
	 * @param ico the icon for the category
	 */
	public TitlePanel(String title, ImageIcon ico)
	{
		setOpaque(false);
		setLayout(new BorderLayout());
		
		JLabel imgLabel = new JLabel(ico);
		imgLabel.setBorder(new EmptyBorder(0, 10, 0, 10));
		add(imgLabel, BorderLayout.WEST);
		
		JLabel titleLabel = new JLabel(title);
		titleLabel.setFont(new Font("Calibri", Font.BOLD, 20));
		add(titleLabel, BorderLayout.CENTER);

		setPreferredSize(new Dimension(Integer.MAX_VALUE, ico.getIconHeight() + 4));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, ico.getIconHeight() + 4));
		
		setAlignmentX(Component.LEFT_ALIGNMENT);
		setAlignmentY(Component.TOP_ALIGNMENT);
	}
}