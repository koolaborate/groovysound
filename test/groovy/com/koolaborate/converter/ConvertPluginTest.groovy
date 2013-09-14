package com.koolaborate.converter

import java.awt.image.BufferedImage;

import org.junit.Before;
import org.junit.Test;

class ConvertPluginTest{
	ConvertPlugin plugin
	
	@Before
	void setup(){
		plugin = new ConvertPlugin()
	}
	
	@Test
	void shouldGetIcon(){
		BufferedImage icon = plugin.getIcon()
		assert null != icon
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != plugin
	}
}
