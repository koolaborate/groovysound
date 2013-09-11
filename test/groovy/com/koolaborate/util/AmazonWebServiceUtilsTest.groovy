package com.koolaborate.util;

import com.amazonaws.a2s.AmazonA2SClient;
import com.amazonaws.a2s.model.Image;
import com.amazonaws.a2s.model.Item
import com.amazonaws.a2s.model.ItemSearchRequest;
import com.amazonaws.a2s.model.ItemSearchResponse;
import com.amazonaws.a2s.model.Items;
import com.koolaborate.test.ConstantsTest;

import static org.junit.Assert.*
import static org.mockito.Mockito.*

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

class AmazonWebServiceUtilsTest {
	AmazonWebServiceUtils amazonWebServiceUtils
	
	@Before
	void setup(){
		amazonWebServiceUtils = new AmazonWebServiceUtilsStub()
	}
	
	@Test
	void shouldGetAlbumArtImageIcon(){
		def imageIcon = amazonWebServiceUtils.getAlbumArtImageIcon(null, null)
		assert null == imageIcon
	}
	
	@Test
	void shouldCreateImageIcon(){
		def createImageIcon = amazonWebServiceUtils.createImageIcon(null)
		assert null == createImageIcon
		
		def urlStr = new File(ConstantsTest.LOCAL_PIC).toURI().toURL()
		createImageIcon = amazonWebServiceUtils.createImageIcon(urlStr.toString())
		assert null != createImageIcon
		
		createImageIcon = amazonWebServiceUtils.createImageIcon(ConstantsTest.REMOTE_PIC)
		assert null != createImageIcon
	}
	
	@Test
	void shouldTestGetAlbumArtImg(){
		BufferedImage bufferedImage
		bufferedImage = amazonWebServiceUtils.getAlbumArtImg(null, null)
		assert null == bufferedImage
		
		bufferedImage = amazonWebServiceUtils.getAlbumArtImg("a", "a")
		assert null != bufferedImage
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != amazonWebServiceUtils
		assert null != amazonWebServiceUtils.getAmazonA2SClient()
	}
	
	
	
}


class AmazonWebServiceUtilsStub extends AmazonWebServiceUtils{

	
	@Override
	protected def AmazonA2SClient getAmazonA2SClient(){
		AmazonA2SClient mockClient = mock(AmazonA2SClient.class)
		ItemSearchResponse responseMock = mock(ItemSearchResponse.class)
		
		List<Items> itemsList = new ArrayList<Items>()
		List<Item> itemList = new ArrayList<Item>()
		Items items = new Items()
		Item item = new Item()
		Image largeImage = new Image()
		
		largeImage.setURL(ConstantsTest.REMOTE_PIC)
		item.setLargeImage(largeImage)
		itemList.add(item)
		items.setItem(itemList)
		itemsList.add(items)
		
		when(responseMock.getItems()).thenReturn(itemsList)
		when(mockClient.itemSearch(anyObject())).thenReturn(responseMock)
		return mockClient
	}
}


