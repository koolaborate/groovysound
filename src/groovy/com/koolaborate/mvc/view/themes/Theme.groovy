package com.koolaborate.mvc.view.themes;

import java.awt.image.BufferedImage;

/***********************************************************************************
 * Theme                                                                           *
 ***********************************************************************************
 * An interface for all themes. A theme must have a name, description and preview  *
 * image.                                                                          *
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
public interface Theme
{
	/**
	 * @return the name of the theme
	 */
	public String getName();
	
	/**
	 * @return a description text of the theme
	 */
	public String getDescription();
	
	/**
	 * @return a preview image of the theme
	 */
	public BufferedImage getPreviewImage();
	
	/**
	 * Sets all panels and colors that the theme defines. 
	 */
	public void setThemeSettings();
}