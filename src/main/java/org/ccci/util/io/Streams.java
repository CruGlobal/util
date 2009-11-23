package org.ccci.util.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


import com.google.common.base.Preconditions;

public class Streams
{

    /**
     * Returns an {@link InputStream} whose byte value will be the given string when decoded according to the given
     * {@link Charset}
     * @param string
     * @param charset
     * @return
     */
    public static InputStream fromString(String string, Charset charset)
    {
        Preconditions.checkNotNull(string, "string is null");
        return new ByteArrayInputStream(string.getBytes(charset));
//        return new StringInputStream(string, charset);
    }

    /**
     * An unusable InputSream.  It's useful for passing a reference to an InputStream that wont' be used.  For example, mock
     * parameters.
     *  
     * @return
     */
    public static InputStream dummyStream()
    {
        return new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new UnsupportedOperationException("this is just a dummy InputStream");
            }
        };
    }
}

///**
// * somewhat from http://info.michael-simons.eu/2007/10/18/java-creating-a-stream-on-a-bytebuffer/
// * 
// * The idea is that the string can be decoded small bits at a time, so a huge byte array 
// * need not be created.
// * 
// * @author Matt Drees
// *
// */
//class StringInputStream extends InputStream
//{
//
//    final CharBuffer charBuffer;
//    final ByteBuffer byteBuffer;
//    final CharsetEncoder newEncoder;
//
//    public StringInputStream(CharSequence sequence, Charset charset)
//    {
//        charBuffer = CharBuffer.wrap(sequence);
//        byteBuffer = ByteBuffer.allocate(10000);
//        newEncoder = charset.newEncoder();
//    }
//
//    public int read() throws IOException {
//        if (charBuffer.hasRemaining())
//        {
//            //TODO:
//            throw new NotImplementedException();
//        }
//        else
//        {
//            return byteBuffer.hasRemaining() ? byteBuffer.get() : -1;
//        }
//    }
//
//    public int read(byte[] bytes, int off, int len) throws IOException {
//        if (charBuffer.hasRemaining())
//        {
//            //TODO:
//            throw new NotImplementedException();
//        }
//        else
//        {
//            // Read only what's left
//            len = Math.min(len, byteBuffer.remaining());
//            byteBuffer.get(bytes, off, len);            
//            return len == 0 ? -1 : len;
//        }
//    }
//}