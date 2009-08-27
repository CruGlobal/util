package org.ccci.facelets.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ccci.facelets.navigation.RequestController;

 
public class RequestControlFilter implements Filter {
	protected FilterConfig config;
 
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}
 
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
 
		RequestController rc = (RequestController) request.getSession()
				.getAttribute("requestController");
 
		if (rc == null) {
			rc = new RequestController();
			request.getSession().setAttribute("requestController", rc);
		}
		
	    rc.control(request, response, chain, config);
	}
 
	
	
	public void destroy() {
	}
}
