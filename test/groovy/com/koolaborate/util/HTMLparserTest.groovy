package com.koolaborate.util

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

class HTMLparserTest{
	HtmlParser parser
	
	@Before
	void setup(){
		parser = HtmlParser.getInstance()
	}
	
	@Test
	void shouldGetCoverImagePathsFromGoogle(){
		URL[] urls = parser.getCoverImagePathsFromGoogle(null, null)
		assert null == urls
		
		urls = parser.getCoverImagePathsFromGoogle("", "")
		assert null != urls
		assert 2 == urls.length
		assert urls[0] == urls[1]
		assert null == urls[0]
		
		urls = parser.getCoverImagePathsFromGoogle("The Beatles", "Abbey Road")
		assert 2 == urls.length
		assert null != urls[0]
		assert null != urls[1]
		
	}
	
	// TODO look at, as this test case does not work for positive case
	@Test @Ignore
	void shouldGetArtistInfoFromWikipedia(){
		String artistInfo = parser.getArtistInfoFromWikipedia(null)
		assert null == artistInfo
		
		artistInfo = parser.getArtistInfoFromWikipedia("")
		assert null == artistInfo
		
		artistInfo = parser.getArtistInfoFromWikipedia("django")
		assert null != artistInfo
		assert "<HTML><HEAD></HEAD><BODY><i>No information found for artist 'django'.</i></BODY></HTML>" == artistInfo
		
		artistInfo = parser.getArtistInfoFromWikipedia("Beatles")
		assert null != artistInfo
	}
	
	@Test
	void shouldGetArtistImageFromGoogle(){
		URL url = parser.getArtistImageFromGoogle(null)
		assert null == url
		
		url = parser.getArtistImageFromGoogle("")
		assert null == url
		
		url = parser.getArtistImageFromGoogle("django")
		assert null != url
		assert "http" == url.protocol
		
		
	}
	
	// actually making a connection, probably move to integration, mock out calling object
	@Test
	void shouldGetArtistImagePathsFromGoogle(){
		URL[] urls = parser.getArtistImagePathsFromGoogle(null)
		assert null == urls
		
		urls = parser.getArtistImagePathsFromGoogle("")
		assert [null, null, null] == urls
		
		urls = parser.getArtistImagePathsFromGoogle("django")
		assert urls.length > 0 && urls.length <= 3
		
		for(URL url: urls){
			assert null != url.protocol
			assert "http" == url.protocol
		}
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != parser
	}
	
	
}







