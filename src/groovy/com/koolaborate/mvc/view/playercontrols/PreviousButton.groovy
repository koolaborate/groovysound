package com.koolaborate.mvc.view.playercontrols

import java.awt.Color
import java.awt.Dimension
import java.awt.Shape
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.geom.AffineTransform
import java.awt.geom.GeneralPath
import java.awt.image.BufferedImage
import javax.swing.JComponent

/***********************************************************************************
 * PreviousButton                                                                  *
 ***********************************************************************************
 * A shaped button to skip the current song and continue with the previous one.    *
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
class PreviousButton extends JComponent{
	private static final long serialVersionUID = 5214155548576126075L
	BufferedImage activeImg
	BufferedImage inActiveImg
	boolean active = false
	boolean pressed = false
	Shape shape, base
	
	public PreviousButton(){
        Dimension dim = new Dimension(activeImg.getWidth(), activeImg.getHeight())
        setPreferredSize(dim)
        setMaximumSize(dim)
        setMinimumSize(dim)
        setSize(dim)
        setBackground(Color.BLACK)
        setAlignmentY(JComponent.TOP_ALIGNMENT)
        initShape()

        addMouseListener([
        	mouseEntered: {
        		active = true
        		repaint()
        	},
        	
        	mouseExited: {
        		active = false
        		repaint()
        	},
        	
        	mouseClicked: {
        		// TODO there could also be a shadowed version of the button when it is clicked
        		// must also be implemented in the paintComponent() method
        		pressed = !pressed
//        		repaint();
        	}
        ] as MouseAdapter)
	}
	
	
    protected void initShape(){
    	if(shape == null){
    		if(!getBounds().equals(base)){
    			base = getBounds()
    			
    			// coordinates
    			int rightEndX = 26
    			int centerY = 11
    			int startLeftX = 5
    			int endLowerY = 21
    			
    			GeneralPath myShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD)
    			// upper side
    			myShape.moveTo(startLeftX, 0)
    			myShape.lineTo(rightEndX, 0)
    			// right arc
    			float quadPointX = 20
    			float quadPointY = centerY
    			myShape.quadTo(quadPointX, quadPointY, rightEndX, endLowerY) 
    			// lower side
    			myShape.lineTo(startLeftX, endLowerY)
    			// left arc
    			quadPointX = -4
    			quadPointY = centerY
    			myShape.quadTo(quadPointX, quadPointY, startLeftX, 0) 
    			
    			myShape.closePath()
    			
    			// transformation into a shape
    			AffineTransform af = new AffineTransform()
    			shape = myShape.createTransformedShape(af)
    		}
    	}
    }
    

}