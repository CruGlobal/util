package org.ccci.facelets.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ccci.facelets.navigation.RequestController;
 
public class LoopBackServlet extends HttpServlet {
	private static final long serialVersionUID = 6L;
	 
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
 
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		super.service(request, response);
	}
 
	public void destroy() {
		super.destroy(); 
	}
 
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// retrieve the request controller from the session
		RequestController rc = (RequestController) request.getSession()
				.getAttribute("requestController");
 
		ServletOutputStream out = response.getOutputStream();
		out.print(rc.getResponseContent());
	}
 
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}
