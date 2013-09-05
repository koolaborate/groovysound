package com.koolaborate.util

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.koolaborate.util.StringUtils;

class StringUtilsTest {
	StringUtils utils;
	
	@Before
	void setup(){
		utils = StringUtils.getInstance()
	}
	
	@Test
	void shouldTestGetFirstChar(){
		def firstChar = utils.getFirstChar(null)
		assert null == firstChar
		
		firstChar = utils.getFirstChar("")
		assert null == firstChar
		
		firstChar = utils.getFirstChar("some char")
		assert null != firstChar
		assert "s" == firstChar
		
		firstChar = utils.getFirstChar("5some char")
		assert null != firstChar
		assert "#" == firstChar
		
		firstChar = utils.getFirstChar("_some char")
		assert null != firstChar
		assert "_" == firstChar
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != utils
	}
	
	
	
}







