package mp3;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import javax.imageio.ImageIO;
import org.apache.log4j.Logger;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.TagFieldKey;
import org.jaudiotagger.tag.datatype.DataTypes;
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC;

/***********************************************************************************
 * CoverHelper                                                                     *
 ***********************************************************************************
 * A helper class that extracts an embedded cover image from an mp3 file and saves *
 * it in case there does not yet exist a cover image for the album.                *
 ***********************************************************************************
 * (c) Impressive Artworx, 2k8                                                     * 
 * @author Manuel Kaess                                                            *
 * @version 1.1                                                                    *
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
public class CoverHelper
{
	/**
	 * Reads and saves the embedded cover image of a mp3 file. The cover is saved in case
	 * there does not yet exist a cover image for the album. 
	 * 
	 * @param mp3Filename the full path to the mp3 file from which the cover image shall
	 *        be read
	 * @return <code>true</code> if the album cover could be retrieved and was saved. Will
	 *         only be the case if there has not yet been a cover image for this album.
	 *         Otherwise, <code>false</code> is returned
	 */
	public static boolean saveEmbeddedCover(String mp3Filename)
	{
		final Logger log = Logger.getLogger(CoverHelper.class.getName());
		
		File mp3File = new File(mp3Filename);
		if(!mp3File.exists() || !mp3File.isFile())
		{
			return false;
		}
		
		// check if there is already a cover image present
		File folder = mp3File.getParentFile();
		String coverFilePath = folder.getAbsolutePath() + File.separator + "folder.jpg";
		File coverFile = new File(coverFilePath);
		if(coverFile.exists()) return false;
		
		// no cover image yet exists: retrieve the cover image and save it
		try
		{
			MP3File af = (MP3File)AudioFileIO.read(mp3File);
			BufferedImage coverImage = null;
			TagField imageField = af.getTag().get(TagFieldKey.COVER_ART).get(0);
			if(imageField instanceof AbstractID3v2Frame)
			{
				FrameBodyAPIC imageFrameBody = 
					(FrameBodyAPIC)((AbstractID3v2Frame)imageField).getBody();
				if(!imageFrameBody.isImageUrl())
				{
					byte[] imageRawData = 
						(byte[])imageFrameBody.getObjectValue(DataTypes.OBJ_PICTURE_DATA);
					coverImage = ImageIO.read(new ByteArrayInputStream(imageRawData));
					// save the cover image if it could be retrieved
					if(coverImage != null)
					{
						ImageIO.write(coverImage, "jpg", coverFile);
					}
					else
						return false;
				}
			}	
		}
		catch(Exception ex)
		{
			log.debug("Error retrieving and saving cover image from file " + mp3Filename +
					". Error message: " + ex.getMessage());
			return false;
		}
		
		return true;
	}
}