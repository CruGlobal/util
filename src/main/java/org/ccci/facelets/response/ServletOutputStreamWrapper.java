package org.ccci.facelets.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
 
public class ServletOutputStreamWrapper extends ServletOutputStream {
	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
 
	@Override
	public String toString() {
		return baos.toString();
	}
 
	@Override
	public void write(int arg0) throws IOException {
		baos.write(arg0);		
	}
	
}