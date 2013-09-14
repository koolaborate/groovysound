package com.koolaborate.util

import java.awt.Transparency;
import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*

class GraphicsUtilities2Test{
	GraphicsUtilities2 gu2
	
	@Before
	void setup(){
		gu2 = GraphicsUtilities2.getInstance()
	}
	
	@Test
	void shouldTestCreateCompatibleImage(){
		BufferedImage compatImage = gu2.createCompatibleImage(null, 0, 0)
		assert null == compatImage
		
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		try{
			compatImage = gu2.createCompatibleImage(bufferedImage, 0, 0)
			fail()
		}catch(IllegalArgumentException e){}
		assert null == compatImage
		
		compatImage = gu2.createCompatibleImage(bufferedImage, 10, 10)
		assert null != compatImage
		assert 10 == compatImage.width
		assert 10 == compatImage.height
		assert bufferedImage.getTransparency() == compatImage.getTransparency()
	}
	
	@Test
	void shouldTestCreateCompatibleTranslucentImage(){
		BufferedImage compatImage
		
		try{
			compatImage = gu2.createCompatibleTranslucentImage(0, 0)
			fail()
		}catch(IllegalArgumentException e){}
		
		compatImage = gu2.createCompatibleTranslucentImage(10, 10)
		assert null != compatImage
		assert 10 == compatImage.width
		assert 10 == compatImage.height
		assert Transparency.TRANSLUCENT == compatImage.getTransparency()
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != gu2
	}

}
