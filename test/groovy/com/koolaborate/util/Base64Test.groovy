package com.koolaborate.util

import org.junit.Before;
import org.junit.Test;

class Base64Test{
	Base64 base
	
	@Before
	void setup(){
		base = Base64.getInstance()
	}
	
	@Test
	void shouldEncodeBytes(){
		def encoded = base.encodeBytes(null)
		assert null == encoded
		
		byte[] bytes = []
		encoded = base.encodeBytes(bytes)
		assert "" == encoded
		
		bytes = ['a']
		encoded = base.encodeBytes(bytes)
		assert "YQ==" == encoded
		
		def decodedAgain = base.decodeBase64(encoded)
		assert bytes == decodedAgain
	}
	
	@Test
	void shouldDecodeBase64(){
		def decoded = base.decodeBase64(null)
		assert null == decoded
		
		decoded = base.decodeBase64("")
		assert null != decoded
		assert [] == decoded
		
		decoded = base.decodeBase64("a")
		assert [] == decoded
		
		// TODO don't be lazy, use byte comparison
		decoded = base.decodeBase64("this is just a test")
		assert [-74, 24, -84, -2, 43, 63, -114, -21, 45, -3, -81, -19, 122, -53, 109] == decoded
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != base
	}
}
