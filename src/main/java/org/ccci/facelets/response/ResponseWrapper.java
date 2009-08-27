package org.ccci.facelets.response;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
 
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.ccci.util.Exceptions;

 
public class ResponseWrapper extends HttpServletResponseWrapper {
	private ServletOutputStreamWrapper sosw;
	private PrintWriter pw;
 
	public ResponseWrapper(HttpServletResponse response) {
		super(response);
		sosw = new ServletOutputStreamWrapper();
		try {
			pw = new PrintWriter(new OutputStreamWriter( sosw, "utf8" ));
		} catch (UnsupportedEncodingException e) {
			throw Exceptions.wrap(e);
		}
	}
 
	public ServletOutputStream getOutputStream() throws java.io.IOException {
		return sosw;
	}
 
	public PrintWriter getWriter() throws java.io.IOException {
		return pw;
	}
 
	public String getResponseContent() {
		pw.flush();	
		return sosw.toString();
		
	}
}