package com.koolaborate.util

import java.awt.Window;

import org.junit.Before;
import org.junit.Test;

class WindowShaperTest{
	WindowShaper shaper
	
	@Before
	void setup(){
		shaper = new WindowShaper()
	}
	
	@Test
	void shouldTestShapeWindow(){
		assert !shaper.shapeWindow(null, null)
		
		Window window = new Window()
		assert shaper.shapeWindow(window, null)
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != shaper
	}
}
