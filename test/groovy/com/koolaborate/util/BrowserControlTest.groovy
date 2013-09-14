package com.koolaborate.util

import org.junit.Before;
import org.junit.Test;

class BrowserControlTest{
	BrowserControl browserControl
	private static final String LINK = "http://www.impressive-artworx.de/albumplayer.php"
	
	@Before
	void setup(){
		browserControl = BrowserControl.getInstance()
	}
	
	@Test
	void shouldTestDisplayLink(){
		browserControl.displayURL(LINK)
	}
	
	@Test
	void shouldTestIsWindowsPlatform(){
		assert browserControl.isWindowsPlatform()
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != browserControl
	}
}
