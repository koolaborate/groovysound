package com.koolaborate.util

import org.junit.Before;
import org.junit.Test;

class OSHelperTest{
	OSHelper helper
	
	@Before
	void setup(){
		helper = OSHelper.getInstance()
	}
	
	@Test
	void testVistaStyle(){
		assert !helper.isVistaStyleEnabled()
	}
	
	@Test
	void shouldGetJavaVersion(){
		float version = helper.getJavaVersion()
		assert 1.7f == version
	}
	
	@Test
	void shouldGetDefaultLnFName(){
		String fname = helper.getDefaultLnFName()
		assert null != fname
		assert "Windows" == fname
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != helper
	}
}
