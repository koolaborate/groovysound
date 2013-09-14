package com.koolaborate.util

import java.awt.image.BufferedImage

import org.junit.Before
import org.junit.Test

import com.koolaborate.test.ConstantsTest

class ImageHelperTest{
	ImageHelper helper
	
	@Before
	void setup(){
		helper = new ImageHelper()
	}
	
	@Test
	void shouldCreateSmallCover(){
		File image = null
		BufferedImage smallCover = helper.createSmallCover(image)
		assert null == smallCover
		
		image = new File("")
		smallCover = helper.createSmallCover(image)
		assert null == smallCover
		
		image = new File(ConstantsTest.LOCAL_PIC)
		smallCover = helper.createSmallCover(image)
		assert null != smallCover
		
		image = new File(ConstantsTest.LOCAL_PIC + " does not exist")
		smallCover = helper.createSmallCover(image)
		assert null == smallCover
		
		
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != helper
	}
	
	
	
	
}







