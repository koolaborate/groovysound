package com.koolaborate.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.amazonaws.a2s.AmazonA2SClient;
import com.amazonaws.a2s.AmazonA2SException;
import com.amazonaws.a2s.model.ItemSearchRequest;
import com.amazonaws.a2s.model.ItemSearchResponse;

/***********************************************************************************
 * AmazonWebServiceUtils                                                           *
 ***********************************************************************************
 * A utility class for the Amazon(R) web services.                                 *
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
public class AmazonWebServiceUtils {
	final static String accessKeyID = "";
	final static String accessKey   = "";
	
	static final Logger logger = Logger.getLogger(AmazonWebServiceUtils.class)

	protected AmazonWebServiceUtils(){

	}

	private static AmazonWebServiceUtils amazonWebServiceUtils;

	def static synchronized AmazonWebServiceUtils getInstance(){
		if(null == amazonWebServiceUtils){
			amazonWebServiceUtils = new AmazonWebServiceUtils()
		}

		return amazonWebServiceUtils
	}

	/**
	 * Retrieves an image icon as an album cover for the given album.
	 * 
	 * @param artist the artist of the album
	 * @param album the name of the album
	 * @return an ImageIcon containing the album cover image or <code>NULL</code>
	 * @throws Exception in case no image could be retrieved or no connection could be established
	 */
	def ImageIcon getAlbumArtImageIcon(String artist, String album) throws Exception {
		// build the search request that looks for images of music.
		ItemSearchRequest request = new ItemSearchRequest();
		request.setSearchIndex("Music");
		request.setResponseGroup(Arrays.asList("Images"));
		request.setArtist(artist);
		request.setTitle(album);

		// create a new amazon client using the access key. sign up for an
		// amazon web services account here:
		// https://aws-portal.amazon.com/gp/aws/developer/registration/index.html
		AmazonA2SClient client = new AmazonA2SClient(accessKeyID, accessKey);

		// create a search response from the search request.
		ItemSearchResponse response = client.itemSearch(request);

		// get the URL to the amazon image (if one was returned).
		String url = response.getItems().get(0).getItem().get(0).getLargeImage().getURL();

		// create an ImageIcon from the returned URL. if the URL is null, then
		// the icon returned will also be null.
		return createImageIcon(url);
	}


	/**
	 * Creates and returnes an ImageIcon of a given URL.
	 * 
	 * @param url the URL of the image
	 * @return the ImageIcon of this ressource
	 */
	def ImageIcon createImageIcon(String url) {
		ImageIcon icon = null;

		try {
			icon = url == null ? null : new ImageIcon(new URL(url))
		}
		catch(MalformedURLException e) {
			logger.error("Malformed url: " + e.getMessage(), e)
		}
		return icon;
	}


	/**
	 * Retrieves a buffered image as an album cover for the given album.
	 * 
	 * @param artist the name of the album artist
	 * @param album the name of the album
	 * @return a buffered image of the album cover image or <code>NULL</code>
	 * @throws AmazonA2SException in case the connection could not be established
	 * @throws IOException in case the image could not be loaded
	 */
	def BufferedImage getAlbumArtImg(String artist, String album) throws AmazonA2SException, IOException {
		BufferedImage bufferedImage = null;
		if (StringUtils.isBlank(artist) || StringUtils.isBlank(album)) return bufferedImage

		// build the search request that looks for images of music.
		ItemSearchRequest request = buildItemSearchRequest(artist, album);

		// create a new amazon client using the access key. sign up for an
		// amazon web services account here:
		// https://aws-portal.amazon.com/gp/aws/developer/registration/index.html
		AmazonA2SClient client = getAmazonA2SClient();

		// create a search response from the search request.
		ItemSearchResponse response = client.itemSearch(request);

		// get the URL to the amazon image (if one was returned).
		String urlString = response.getItems().get(0).getItem().get(0).getLargeImage().getURL();
		URL url = new URL(urlString);

		// create an ImageIcon from the returned URL. if the URL is null, then
		// the icon returned will also be null.
		bufferedImage = ImageIO.read(url);

		return bufferedImage;
	}

	protected ItemSearchRequest buildItemSearchRequest(String artist, String album) {
		ItemSearchRequest request = new ItemSearchRequest();
		request.setSearchIndex("Music");
		request.setResponseGroup(Arrays.asList("Images"));
		request.setArtist(artist);
		request.setTitle(album)
		return request
	}

	protected def AmazonA2SClient getAmazonA2SClient(){
		return getAmazonA2SClient(accessKeyID, accessKey);
	}

	protected def AmazonA2SClient getAmazonA2SClient(String accessKeyID, String accessKey){
		return new AmazonA2SClient(accessKeyID, accessKey);
	}
}



