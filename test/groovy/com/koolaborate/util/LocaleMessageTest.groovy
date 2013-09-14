package com.koolaborate.util

import org.junit.Before;
import org.junit.Test;

class LocaleMessageTest{
	LocaleMessage message
	static final String ERROR_29 = "error.29"
	
	@Before
	void setup(){
		message = LocaleMessage.getInstance()
	}
	
	@Test
	void shouldGetString(){
		String value = message.getString(ERROR_29)
		assert null != value
		assert "An error occurred while installing the plugin. Please ensure that you have installed the latest version of VibrantPlayer and try again." == value
		
		value = message.getString("shouldn't exist")
		assert "MISSING: shouldn't exist" == value
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != message
	}
}
