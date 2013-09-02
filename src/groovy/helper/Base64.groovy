package helper;

import java.io.IOException;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/*
 * TODO: replace sun.misc.Base64*
 * reference: http://www.mkyong.com/java/access-restriction-the-type-base64encoder-is-not-accessible-due-to-restriction/
 */

/***********************************************************************************
 * Base64                                                                          *
 ***********************************************************************************
 * A simple helper class that deal with BASE64 conversion.                         *
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
public class Base64
{
	public static String encodeBytes(byte[] bytes)
	{
		return new BASE64Encoder().encode(bytes); 
	}

	public static byte[] decodeBase64(String base64) throws IOException
	{
		return new BASE64Decoder().decodeBuffer(base64);
	}
}