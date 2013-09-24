package com.koolaborate.mvc.view.navigation;

import java.awt.BorderLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;

/***********************************************************************************
 * Separator                                                                       *
 ***********************************************************************************
 * A separator to separate the navigation buttons from the sub navigation buttons. *
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
class Separator extends JComponent implements Cloneable 
{
	private static final long serialVersionUID = -1799382510955618434L;
	JLabel content;
	
	/**
	 * Constructor.
	 */
	public Separator(){
		setOpaque(false);
		setLayout(new BorderLayout());
	}
	
	public void setContent(JLabel content){
		this.content = content;
		removeAll();
		add(content, BorderLayout.CENTER);
	}
	
	@Override
	public JComponent clone() {
		JComponent ret = null;
		try{
			ret = (JComponent) super.clone();
		}catch(CloneNotSupportedException e){
			e.printStackTrace();
		}
		return ret;
	}
}