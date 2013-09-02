package helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
public class FileHelper
{
	/**
	 * Returns whether the file with the specified path exists or not.
	 * 
	 * @param path the path the file should be found under
	 * @return <code>true</code> if the file exists, <code>false</code> otherwise
	 */
	public static boolean testFileExists(String path)
	{
		File f = new File(path);
		return f.exists();
	}


	/**
	 * Creates a new file with the given path.
	 * 
	 * @param path the path of the file that shall be created
	 * @throws IOException if the file could not be created
	 */
	public static void createFile(String path) throws IOException 
	{
		File f = new File(path);
		f.createNewFile();
	}


	/**
	 * Removes the given file.
	 * 
	 * @param path the path to the file to be deleted
	 */
	public static void removeFile(String path)
	{
		File f = new File(path);
		if(f.exists() && f.isFile())
		{
			if(!f.delete()) f.deleteOnExit();
		}
		else if(f.exists() && f.isDirectory())
		{
			File[] files = f.listFiles();
			for(File file : files) file.delete();
			if(!f.delete()) f.deleteOnExit();
		}
	}


	/**
	 * Copies a file from the source to the destination.
	 * 
	 * @param source the source path
	 * @param destination the destination path
	 */
	public static void copyFile(String source, String destination)
	{
		FileInputStream  fis = null; 
		FileOutputStream fos = null; 
		
		try 
		{ 
			fis = new FileInputStream(source); 
			fos = new FileOutputStream(destination); 
			copy(fis, fos); 
		} 
		catch(IOException e ) 
		{ 
			e.printStackTrace(); 
		} 
		finally 
		{ 
			if(fis != null) 
			try 
			{ 
				fis.close(); 
			} 
			catch(IOException e){} 
			if(fos != null) 
			try 
			{ 
				fos.close(); 
			} 
			catch(IOException e){} 
		} 
	}
	private static void copy(InputStream istream, OutputStream out) throws IOException 
	{ 
		byte[] buffer = [0xFFFF]
		for(int len; (len = istream.read(buffer)) != -1;) out.write(buffer, 0, len); 
	} 
}
