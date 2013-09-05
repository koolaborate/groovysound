package com.koolaborate.util;

import com.amazonaws.a2s.AmazonA2SClient;
import static org.junit.Assert.*
import static org.mockito.Mockito.*

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

class AmazonWebServiceUtilsTest {
	AmazonWebServiceUtils amazonWebServiceUtils;
	
	@Before
	void setup(){
		amazonWebServiceUtils = AmazonWebServiceUtils.getInstance()
	}
	
	@Test
	void shouldTestGetAlbumArtImg(){
		BufferedImage bufferedImage
		bufferedImage = amazonWebServiceUtils.getAlbumArtImg(null, null)
		assert null == bufferedImage
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != amazonWebServiceUtils
	}
}
