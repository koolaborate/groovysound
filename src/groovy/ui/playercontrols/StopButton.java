package ui.playercontrols;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/***********************************************************************************
 * StopButton                                                                      *
 ***********************************************************************************
 * A rounded button to stop the playback.                                          *
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
public class StopButton extends JComponent
{
	private BufferedImage activeImg;
	private BufferedImage inActiveImg;
	private boolean active = false;
	private boolean pressed = false;
	private Shape shape, base;
	
	/**
	 * Constructor.
	 */
	public StopButton(BufferedImage activeImg, BufferedImage inActiveImg)
	{
		this.activeImg = activeImg;
		this.inActiveImg = inActiveImg;
		
        Dimension dim = new Dimension(activeImg.getWidth(), activeImg.getHeight());
        setPreferredSize(dim);
        setMaximumSize(dim);
        setMinimumSize(dim);
        setSize(dim);
        setBackground(Color.BLACK);
        setAlignmentY(JComponent.TOP_ALIGNMENT);
        initShape();

        addMouseListener(new MouseAdapter(){
        	@Override
        	public void mouseEntered(MouseEvent e)
        	{
        		active = true;
        		repaint();
        	}
        	
        	@Override
        	public void mouseExited(MouseEvent e)
        	{
        		active = false;
        		repaint();
        	}
        	
        	@Override
        	public void mouseClicked(MouseEvent e) 
        	{
        		// TODO there could also be a shadowed version of the button when it is clicked
        		// must also be implemented in the paintComponent() method
        		pressed = !pressed;
//        		repaint();
        	}
        });
	}
	
	
    protected void initShape() 
    {
    	if(shape == null)
    	{
    		if(!getBounds().equals(base)) 
    		{
    			base = getBounds();
    			
    			// coordinates
    			int centerY = 11;
    			int startLeftX = 1;
    			int endRightX = 21;
    			
    			GeneralPath myShape = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
    			// upper side
    			myShape.moveTo(startLeftX, 0);
    			myShape.lineTo(endRightX, 0);
    			// right arc
    			float quadPointX = 30;
    			float quadPointY = centerY;
    			myShape.quadTo(quadPointX, quadPointY, endRightX, endRightX); 
    			// lower side
    			myShape.lineTo(startLeftX, endRightX);
    			// left arc
    			quadPointX = 6;
    			quadPointY = centerY;
    			myShape.quadTo(quadPointX, quadPointY, startLeftX, 0); 
    			
    			myShape.closePath();
    			
    			// transformation into a shape
    			AffineTransform af = new AffineTransform();
    			shape = myShape.createTransformedShape(af);
    		}
    	}
    }


	public boolean isActive()
	{
		return active;
	}


	public void setActive(boolean active)
	{
		this.active = active;
	}


	public BufferedImage getActiveImg()
	{
		return activeImg;
	}


	public void setActiveImg(BufferedImage activeImg)
	{
		this.activeImg = activeImg;
	}


	public Shape getBase()
	{
		return base;
	}


	public void setBase(Shape base)
	{
		this.base = base;
	}


	public BufferedImage getInActiveImg()
	{
		return inActiveImg;
	}


	public void setInActiveImg(BufferedImage inActiveImg)
	{
		this.inActiveImg = inActiveImg;
	}


	public boolean isPressed()
	{
		return pressed;
	}


	public void setPressed(boolean pressed)
	{
		this.pressed = pressed;
	}


	public Shape getShape()
	{
		return shape;
	}


	public void setShape(Shape shape)
	{
		this.shape = shape;
	}
}