package org.ccci.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import org.ccci.util.xml.XmlDocumentStreamConverter;
import org.w3c.dom.Document;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

/**
 * Creates SHA1 hashes for a couple of file types.
 * Currently supported
 *  org.w3c.dom.Document
 *  java.awt.image.BufferedImage
 *
 * Both of these files are written into ByteArrayOutputStreams then are converted into byte[]'s for the hash
 *
 * Created by ryancarlson on 3/18/14.
 */
public class ShaGenerator
{
	private ShaGenerator()
	{

	}

	/**
	 * Generate an SHA1 hash for an XML document
	 *
	 */
    public static String calculateHash(Document xmlFile)
    {
        try
        {
            return calculateHash(XmlDocumentStreamConverter.xmlToStream(xmlFile).toByteArray());
        }
        catch(Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

	/**
	 * Generate an SHA1 hash for a BufferedImage.
	 *
	 * @param imageType is required and must be recognized by ImageIO: [jpg, bmp, jpeg, wbmp, png, gif]
	 *                     (source: http://examples.javacodegeeks.com/desktop-java/imageio/list-read-write-supported-image-formats/)
	 */
	public static String calculateHash(BufferedImage bufferedImage, String imageType)
	{
		Preconditions.checkNotNull(imageType);

		try
		{
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ImageIO.write(bufferedImage, imageType, byteArrayOutputStream);

			String hash = calculateHash(byteArrayOutputStream.toByteArray());
			byteArrayOutputStream.close();

			return hash;
		}
		catch(Exception e)
		{
			throw Throwables.propagate(e);
		}
	}

    public static String calculateHash(byte[] bytes)
    {
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");

            messageDigest.update(bytes);

            byte[] messageDigestBytes = messageDigest.digest();

            StringBuffer hexString = new StringBuffer();

            for(int i=0; i < messageDigestBytes.length; i++)
            {
                hexString.append(Integer.toHexString(0xFF & messageDigestBytes[i]));
            }

            return hexString.toString();
        }
        catch(Exception e)
        {
            throw Throwables.propagate(e);
        }
    }

}
