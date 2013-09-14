package com.koolaborate.converter

import java.awt.BorderLayout;
import java.awt.Color;

import org.junit.Before;
import org.junit.Test;

import com.koolaborate.test.ConstantsTest;

import static org.junit.Assert.*

class MP3ToWAVConverterTest{
	MP3ToWAVConverter converter
	Convertable convertable
	
	@Before
	void setup(){
		converter = new MP3ToWAVConverter()
		convertable = converter
		
		File file = new File(ConstantsTest.MP3_DEST_FOLDER)
		assert file.isDirectory()
		
		File[] files = file.listFiles()
		if(null != files && files.length > 0){
			for(File f: files){
				f.delete()
			}
		}
	}
	
	@Test
	void shouldInit(){
		try{
			convertable.init(null, null)
			fail()
		}catch(IllegalArgumentException e){}
		
		try{
			convertable.init("", "")
			fail()
		}catch(IllegalArgumentException e){}
		
		convertable.init(ConstantsTest.MP3_SRC_FOLDER, ConstantsTest.MP3_DEST_FOLDER)
	}
	
	@Test
	void shouldInitCheckFields(){
		assert null == converter.language
		assert null == converter.destinationFolder
		assert null == converter.dialog
		assert null == converter.mp3s
		converter.init(ConstantsTest.MP3_SRC_FOLDER, ConstantsTest.MP3_DEST_FOLDER)
		assert "en" == converter.language
		assert "testresource\\groovy\\temp\\com\\koolaborate\\converter\\mp3\\dest" == converter.destinationFolder.toString()
		
		assert null != converter.dialog
		assert null != converter.dialog.getLayout()
		assert converter.dialog.getLayout() instanceof BorderLayout
		assert Color.WHITE == converter.dialog.contentPane.background
		assert [] != converter.mp3s
		assert 1 == converter.mp3s.length
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != converter
		assert null != convertable
	}
}
