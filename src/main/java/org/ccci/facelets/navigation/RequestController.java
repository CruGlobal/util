package org.ccci.facelets.navigation;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ccci.debug.BackButtonException;
import org.ccci.facelets.response.ResponseWrapper;
import org.ccci.util.Exceptions;

public class RequestController implements Serializable {
	static final long serialVersionUID = 42L;

	private long previousRequestId;
	private String responseContent;
 
	public synchronized long getPreviousRequestId() {
		return previousRequestId;
	}
 
	public synchronized String getResponseContent() {
		return responseContent;
	}
 
	public void control(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain, FilterConfig config)
			throws IOException, ServletException {
		long currentRequestId = 0;
 
		String requestId = request.getParameter("requestId");

		String[] ajaxReq = request.getParameterValues("AJAXREQUEST");
		
		
			
		if (requestId != null) {
			currentRequestId = Long.parseLong(requestId);
		}
 
		if (currentRequestId <= 0) {
			// this may be the request for the very first page of this
			// application or a page without the requestId param
			// hence, just continue
			chain.doFilter(request, response);
			return;
		}
 
		try {
			synchronized (this) {
				if (currentRequestId != previousRequestId  && ajaxReq == null ) {
					// this is the first and valid request for the current page
					this.responseContent = null;
 
					ResponseWrapper responseWrapper = new ResponseWrapper(
							response);
 
						chain.doFilter(request, responseWrapper);
					// by now we must have a valid response
 
					// get the response string from the response wrapper
					// and store it here
					this.responseContent = responseWrapper.getResponseContent();
					
					// save the current request-id
					this.previousRequestId = currentRequestId;
				}
				else if ( currentRequestId == previousRequestId  && ajaxReq == null  ){
					throw new BackButtonException();
				}
				else if ( ajaxReq != null)
				{
					this.responseContent = null;
					 
					ResponseWrapper responseWrapper = new ResponseWrapper(
							response);
					
					chain.doFilter(request, responseWrapper);
					
					this.responseContent = responseWrapper.getResponseContent();

				} 
			}
		} catch (Exception ex) {
			throw Exceptions.wrap(ex);
		} finally {
			// the valid response is given for the first request and any
			// subsequent requests
			RequestDispatcher rd = config.getServletContext().getRequestDispatcher("/loopBack");
			rd.forward(request, response);
		}
	}
}
