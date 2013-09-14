package com.koolaborate.util;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/***********************************************************************************
 * ImageHelper                                                                     *
 ***********************************************************************************
 * A helper class that deals with images.                                          *
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
public class ImageHelper
{
	/**
	 * Constructor.
	 */
	public ImageHelper(){}

//	/**
//	 * Method creates a thumbnail view of the cover and saves it into the same folder as
//	 * "folder_small.jpg".
//	 * 
//	 * @param bigCover the album cover file
//	 * @throws IOException if the thumbnail image could not be written
//	 */
//	public void createSmallCover(File bigCover) throws IOException
//	{
//		if(!bigCover.exists()) return;
//		
//		BufferedImage cdCase = ImageIO.read(getClass().getResource("/images/emptycover.jpg"));
//		BufferedImage cover = ImageIO.read(bigCover);
//		BufferedImage stitch = ImageIO.read(getClass().getResource("/images/stitch.png"));
//		
//		BufferedImage preview = GraphicsUtilities.createCompatibleImage(80, 68);
//		Graphics2D g2d = preview.createGraphics();
//		g2d.clearRect(0, 0, preview.getWidth(), preview.getHeight());
//		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//		g2d.setComposite(AlphaComposite.SrcOver);
//		g2d.drawImage(cdCase, 0, 0, preview.getWidth(), preview.getHeight(), null);
//		g2d.drawImage(cover, 7, 1, 75, 65, null);
//		g2d.drawImage(stitch, 7, 1, 10, 65, null);
//
//		String destination = bigCover.getParent() + System.getProperty("file.separator") + "folder_small.jpg";
//		ImageIO.write(preview, "jpg", new File(destination));
//	}
	
	/**
	 * Method creates a thumbnail view of the cover and returns it.
	 * 
	 * @param bigCover the album cover file
	 * @return the small cover image
	 */
	public BufferedImage createSmallCover(File bigCover)
	{
		if(!bigCover.exists()) return null;
		
		BufferedImage preview = null;
		
		try
		{
			BufferedImage cdCase = ImageIO.read(getClass().getResource("/images/emptycover.jpg"));
			BufferedImage cover = ImageIO.read(bigCover);
			BufferedImage stitch = ImageIO.read(getClass().getResource("/images/stitch.png"));
			
			preview = GraphicsUtilities.getInstance().createCompatibleImage(80, 68);
			Graphics2D g2d = preview.createGraphics();
			g2d.clearRect(0, 0, preview.getWidth(), preview.getHeight());
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setComposite(AlphaComposite.SrcOver);
			g2d.drawImage(cdCase, 0, 0, preview.getWidth(), preview.getHeight(), null);
			g2d.drawImage(cover, 7, 1, 75, 65, null);
			g2d.drawImage(stitch, 7, 1, 10, 65, null);
//		String destination = bigCover.getParent() + System.getProperty("file.separator") + "folder_small.jpg";
//		ImageIO.write(preview, "jpg", new File(destination));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		return preview;
	}

	
	/**
	 * Creates a thumbnail view of the cover from a given BufferedImage and returns it.
	 * 
	 * @param image the original image (which might have to get resized)
	 * @return the small cover image
	 */
	public BufferedImage createSmallCover(BufferedImage image)
	{
		BufferedImage preview = null;
		try
		{
			BufferedImage cdCase = ImageIO.read(getClass().getResource("/images/emptycover.jpg"));
			BufferedImage cover = image;
			BufferedImage stitch = ImageIO.read(getClass().getResource("/images/stitch.png"));
			
			preview = GraphicsUtilities.getInstance().createCompatibleImage(80, 68);
			Graphics2D g2d = preview.createGraphics();
			g2d.clearRect(0, 0, preview.getWidth(), preview.getHeight());
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setComposite(AlphaComposite.SrcOver);
			g2d.drawImage(cdCase, 0, 0, preview.getWidth(), preview.getHeight(), null);
			g2d.drawImage(cover, 7, 1, 75, 65, null);
			g2d.drawImage(stitch, 7, 1, 10, 65, null);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return preview;
	}
}