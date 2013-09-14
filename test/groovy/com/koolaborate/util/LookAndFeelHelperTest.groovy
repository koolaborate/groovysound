package com.koolaborate.util

import org.junit.Before;
import org.junit.Test;

class LookAndFeelHelperTest{
	LookAndFeelHelper helper
	
	@Before
	void setup(){
		helper = LookAndFeelHelper.getInstance()
	}
	
	@Test
	void shouldGetAllLookAndFeelNames(){
		List<String> names = helper.getAllLookAndFeelNames()
		assert null != names
		assert ["Metal", "Nimbus", "CDE/Motif", "Windows", "Windows Classic"] == names
		
		String value = helper.getClassNameForLnF(null)
		assert null == value
		
		value = helper.getClassNameForLnF("Metal")
		assert "javax.swing.plaf.metal.MetalLookAndFeel" == value
	}
	
	@Test
	void shouldTestCurrentLookName(){
		String name = helper.getCurrentLookName()
		assert null != name
		assert "Metal" == name
	}
}
