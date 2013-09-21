package com.koolaborate.util;

import org.apache.commons.lang3.StringUtils


/***********************************************************************************
 * FileHelper                                                                      *
 ***********************************************************************************
 * A helper class that deals with various file issues.                             *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.0                                                                    *
 *********************************************************************************** 
 * This file is part of VibrantPlayer.                                             *
 *                                                                                 *
 *  VibrantPlayer is free software: you can redistribute it and/or modify          *
 *  it under the terms of the Lesser GNU General Public License as published by    *
 *  the Free Software Foundation, either version 3 of the License, or              *
 *  (at your option) any later version.                                            *
 *                                                                                 *
 *  VibrantPlayer is distributed in the hope that it will be useful,               *
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Lesser            *
 *  GNU General Public License for more details.                                   *
 *                                                                                 *
 *  You should have received a copy of the Lesser GNU General Public License       *
 *  along with VibrantPlayer. If not, see <http://www.gnu.org/licenses/>.          *
 ***********************************************************************************/
public class FileHelper {
	// TODO make java 7 compliant
	// ref: http://www.java7developer.com/blog/?p=334
	
	private static FileHelper fileHelperInstance
	private FileHelper(){

	}
	
	def static synchronized FileHelper getInstance(){
		if(null == fileHelperInstance){
			fileHelperInstance = new FileHelper()
		}

		return fileHelperInstance
	}
	

	/*
	 * TODO rewrite to java 7 spec
	 */



	/**
	 * Removes the given file.
	 * 
	 * @param path the path to the file to be deleted
	 */
	
	// refactor, use Files and Paths from nio
	public void removeFile(String path) {
		if(StringUtils.isBlank(path)) return
		
		File f = new File(path)
		if(f.exists() && f.isFile()) {
			if(!f.delete()){
				f.deleteOnExit()
			}
		}
		else if(f.exists() && f.isDirectory()) {
			File[] files = f.listFiles()
			for(File file : files) {
				file.delete()
			}
			
			if(!f.delete()) {
				f.deleteOnExit()
			}
		}
	}


	/**
	 * Copies a file from the source to the destination.
	 * 
	 * @param source the source path
	 * @param destination the destination path
	 */
	public void copyFile(String source, String destination) {
		if(null == source || null == destination) return
		FileInputStream  fis = null;
		FileOutputStream fos = null;

		// TODO to try with resource
		try {
			fis = new FileInputStream(source)
			fos = new FileOutputStream(destination)
			copy(fis, fos)
		}
		catch(IOException e ) {
			e.printStackTrace();
		} finally {
			if(fis != null){
				try {
					fis.close()
				} catch(IOException e){}
			}
				
			if(fos != null){
				try {
					fos.close()
				} catch(IOException e){}
			}
				
		}
	}

	protected void copy(InputStream istream, OutputStream out) throws IOException {
		if(null == istream || null == out) return
		
		byte[] buffer = [0xFFFF]
		for(int len; (len = istream.read(buffer)) != -1;){
			out.write(buffer, 0, len);
		}
	}
}
