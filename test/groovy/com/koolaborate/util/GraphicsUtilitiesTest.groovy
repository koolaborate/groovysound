package com.koolaborate.util

import java.awt.GraphicsConfiguration;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel
import java.awt.image.WritableRaster;

import static org.mockito.Mockito.*

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.koolaborate.test.ConstantsTest;

import static org.junit.Assert.*

class GraphicsUtilitiesTest{
	GraphicsUtilities gu
	
	@Before
	void setup(){
		gu = GraphicsUtilities.getInstance()
	}
	
	@Test
	void shouldTestCreateColorModelCompatibleImage(){
		BufferedImage compatImage = gu.createColorModelCompatibleImage(null)
		assert null == compatImage
		
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR) 
		compatImage = gu.createColorModelCompatibleImage(bufferedImage)
		assert null != compatImage
		assert bufferedImage.getColorModel() == compatImage.getColorModel()
	}
	
	@Test
	void shouldCreateCompatibleImage(){
		BufferedImage compatImage = gu.createCompatibleImage(null, 5, 5)
		assert null == compatImage
		
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		compatImage = gu.createCompatibleImage(bufferedImage, 5, 5)
		assert null != compatImage
		assert 5 == compatImage.width
		assert 5 == compatImage.height
		assert bufferedImage.getTransparency() == compatImage.getTransparency()
		
	}
	
	@Test
	void shouldTestCreateTranslucentCompatibleImage(){
		try{
			gu.createTranslucentCompatibleImage(0, 0)
			fail()
		}catch(IllegalArgumentException e){}
		
		def bufferedImage = gu.createTranslucentCompatibleImage(1, 1)
		assert null != bufferedImage
		assert Transparency.TRANSLUCENT == bufferedImage.getTransparency()
		
		try{
			gu.createCompatibleImage(0, 0)
			fail()
		}catch(IllegalArgumentException e){}
		
		bufferedImage = gu.createCompatibleImage(10, 10)
		assert 10 == bufferedImage.width
		assert 10 == bufferedImage.height
	}
	
	@Test
	void shouldLoadCompatImage(){
		BufferedImage compatImage = gu.loadCompatibleImage(null)
		assert null == compatImage
		
		URL url = new URL(new URL("file:"), ConstantsTest.LOCAL_PIC)
		compatImage = gu.loadCompatibleImage(url)
		assert null != compatImage
	}
	
	@Test
	void shouldToCompatibleImage(){
		BufferedImage bufferedImage = mock(BufferedImage.class)
		BufferedImage compatImage = gu.toCompatibleImage(null)
		assert null == compatImage

		GraphicsConfiguration config = ReflectionTestUtils.getField(gu, "CONFIGURATION")
		when(bufferedImage.getColorModel()).thenReturn(config.getColorModel())
		
		compatImage = gu.toCompatibleImage(bufferedImage)
		assert bufferedImage == compatImage
		
		bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		compatImage = gu.toCompatibleImage(bufferedImage)
		assert bufferedImage != compatImage
		assert bufferedImage.getTransparency() == compatImage.getTransparency()
		assert bufferedImage.width == compatImage.width
		assert bufferedImage.height == compatImage.height
	}
	
	@Test
	void shouldCreateThumbnailFastBasedOnLength(){
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		def newLength = 5
		BufferedImage thumbnail = gu.createThumbnailFast(null, newLength)
		assert null == thumbnail
		
		thumbnail = gu.createThumbnailFast(bufferedImage, newLength)
		assert null != thumbnail
		assert newLength == thumbnail.width
		assert newLength == thumbnail.height
	}
	
	@Test
	void shouldCreateThumbnailFast(){
		assert null == gu.createThumbnailFast(null, 1, 1)
		
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		def newWidth = 5
		def newHeight = 5
		BufferedImage thumbnail = gu.createThumbnailFast(bufferedImage, newWidth, newHeight)
		assert null != thumbnail
	}
	
	@Test
	void shouldCreateThumbnailForSquare(){
		BufferedImage thumbnail = gu.createThumbnail(null, 0)
		assert null == thumbnail
		
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		def newLength = 5
		thumbnail = gu.createThumbnail(bufferedImage, newLength)
		assert null != thumbnail
		assert newLength == thumbnail.width
		assert newLength == thumbnail.height
		
		assert 100 == bufferedImage.width
		assert 100 == bufferedImage.height
		
		bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		newLength = -5
		try{
			thumbnail = gu.createThumbnail(bufferedImage, newLength)
			fail()
		}catch(IllegalArgumentException e){}
	}
	
	@Test
	void shouldCreateThumbnail(){
		BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		def newWidth = 5
		def newHeight = 5
		BufferedImage thumbnail = gu.createThumbnail(bufferedImage, newWidth, newHeight)
		assert null != thumbnail
		assert newWidth == thumbnail.width
		assert newHeight == thumbnail.height
		assert 100 == bufferedImage.width
		assert 100 == bufferedImage.height
		
		newWidth = 500
		newHeight = 500
		thumbnail = gu.createThumbnail(bufferedImage, newWidth, newHeight)
		assert null != thumbnail
		assert bufferedImage.width == thumbnail.width
		assert bufferedImage.height == thumbnail.height
		
		bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)
		newWidth = -1
		newHeight = -1
		try{
			thumbnail = gu.createThumbnail(bufferedImage, newWidth, newHeight)
			fail()
		}catch(IllegalArgumentException e){}
		
		thumbnail = gu.createThumbnail(null, newWidth, newHeight)
		assert null == thumbnail
	}
	
	@Test
	void shouldGetPixels(){
		int[] pixels = null
		int[] answer = null
		answer = gu.getPixels(null, 0, 0, 0, 0, pixels)
		assert null != answer
		assert [] == answer
		
		BufferedImage bufferedImage = null
		
		def x = 2; def y = 2
		def w = 2; def h = 2
		pixels = [1, 1, 1, 1, 1, 1]
		bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		answer = gu.getPixels(bufferedImage, 0, 0, w, h, pixels)
		assert [-16777216, -16777216, -16777216, -16777216, 1, 1] == answer
	}
	
	@Test
	void shouldSetPixels(){
		BufferedImage bufferedImage = null
		gu.setPixels(bufferedImage, 0, 0, 0, 0, null)
		
		int w = 1
		int h = 1
		int[] pixels = [0]
		bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR)
		gu.setPixels(bufferedImage, 0, 0, w, h, null)
		try{
			w = 3; h = 3
			pixels = [1, 1, 1, 1]
			gu.setPixels(bufferedImage, 0, 0, w, h, pixels)
			fail()
		}catch(IllegalArgumentException e){}
		
		int x = 1
		int y = 1
		w = 2; h = 2
		pixels = [1, 1, 1, 1]
		def rgb = null
		try{
			rgb = bufferedImage.getRGB(x, y, w, h, pixels, 0, w)
			fail()
		}catch(ArrayIndexOutOfBoundsException e){}
		
		x = 2; y = 2
		w = 2; h = 2
		pixels = [1, 1, 1, 1, 1, 1]
		bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_3BYTE_BGR)
		gu.setPixels(bufferedImage, 0, 0, w, h, pixels)
		rgb = bufferedImage.getRGB(x, y, w, h, pixels, 0, w)
		assert [-16777216, -16777216, -16777216, -16777216, 1, 1] == rgb  // TODO do actual byte comparison 
		
		
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != gu
	}
	
	
	
	
}

class GraphicsUtilitiesStub extends GraphicsUtilities{
	boolean rasterSet = false
	
	@Override
	def void setRaster(BufferedImage bufferedImage, int x, int y, int w, int h, int[] pixels){
		rasterSet = true
	}
}







