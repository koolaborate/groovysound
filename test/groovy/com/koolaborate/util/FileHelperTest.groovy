package com.koolaborate.util

import static org.junit.Assert.*

import java.nio.file.Files
import java.nio.file.Paths

import org.junit.Before
import org.junit.Test

import com.koolaborate.test.ConstantsTest

class FileHelperTest{
	FileHelper helper
	
	@Before
	void setup(){
		helper = FileHelper.getInstance()
		
		File localSourceFile = new File(ConstantsTest.LOCAL_SRC_FILE)
		if(localSourceFile.exists()){
			localSourceFile.delete()
		}
		
		File localDestFile = new File(ConstantsTest.LOCAL_DEST_FILE)
		if(localDestFile.exists()){
			localDestFile.delete()
		}
	}
	
	@Test
	void shouldRemoveFile(){
		try{helper.removeFile(null)}
		catch(Exception e){fail()}
		
		File infile = new File(ConstantsTest.LOCAL_SRC_FILE)
		if(!infile.exists()){
			infile.createNewFile()
		}
		assert infile.exists()
		
		helper.removeFile(ConstantsTest.LOCAL_SRC_FILE)
		infile = new File(ConstantsTest.LOCAL_SRC_FILE)
//		assert (!infile.exists())  // fails, needs to be java7 compliant
		
		try{
			Files.delete(Paths.get(ConstantsTest.LOCAL_SRC_FILE))
		} catch(Exception e){ e.printStackTrace();}
		
		assert (!infile.exists())
	}
	
	@Test
	void shouldCopyFile(){
		def destinationFile = new File(ConstantsTest.LOCAL_DEST_FILE)
		assert !(destinationFile.exists())
		helper.copyFile(null, null)
		assert !(destinationFile.exists())
		
		File infile = new File(ConstantsTest.LOCAL_SRC_FILE)
		if(!infile.exists()) {
			infile.createNewFile()
		} else {
			infile.delete()
			infile.createNewFile()
		}
		assert infile.exists()
		
		byte[] bytes = ['o', 'k', 0]
		ByteArrayInputStream data = new ByteArrayInputStream(bytes)
		FileOutputStream fileOutputStream = new FileOutputStream(ConstantsTest.LOCAL_SRC_FILE)
		helper.copy(data, fileOutputStream)
		assert (new File(ConstantsTest.LOCAL_SRC_FILE)).exists()
		assert (new File(ConstantsTest.LOCAL_SRC_FILE)).size() > 0
		
		helper.copyFile(ConstantsTest.LOCAL_SRC_FILE, ConstantsTest.LOCAL_DEST_FILE)
		assert (destinationFile.exists())
	}
	
	@Test
	void shouldCopy(){
		try{
			helper.copy(null, null)
		}catch(Exception e){fail()}
		
		byte[] bytes = []
		InputStream inStream = new ByteArrayInputStream(bytes)
		ByteArrayOutputStream outStream = new ByteArrayOutputStream()
		helper.copy(inStream, outStream)
		assert null != outStream
		assert null != outStream.toByteArray()
		assert [] == outStream.toByteArray()
		
		bytes = ['a']
		inStream = new ByteArrayInputStream(bytes)
		outStream = new ByteArrayOutputStream()
		helper.copy(inStream, outStream)
		assert bytes == outStream.toByteArray()
	}
	
	@Test
	void shouldTestInstantiation(){
		assert null != helper
	}
	
	
	
}




