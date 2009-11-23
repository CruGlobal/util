package org.ccci.util.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * For use in a servlet environment.  Implementation will look up appropriate response object to back Writer/Stream
 * implementations.
 * 
 * @author Matt Drees
 */
public interface Downloader
{

    /**
     * Returns a {@link PrintWriter} that can be used by client code to download a text-based document (for example, a Comma-Separated Value file)
     * to 
     * the user's browser. 
     * 
     * @param contentType for example, "text/csv" for a CSV file
     * @param fileName for example "myoutput.csv"
     * @return a {@link PrintWriter} to which the document should be written.  The caller should call {@link PrintWriter#checkError()} 
     *  afterwards to flush and check for errors.
     * @throws IOException if an {@link IOException} occurs 
     */
    PrintWriter downloadTextToClient(String contentType, String fileName) throws IOException;
    
    /** exactly like {@link #downloadTextToClient(String, String)}, but for binary content */
    OutputStream downloadBinaryToClient(String contentType, String fileName) throws IOException;

}
