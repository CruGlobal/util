package org.ccci.facelets.tag.jsf;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.servlet.http.HttpServletRequest;

import org.ccci.util.Exceptions;
 
public class RequestIdTag extends UIOutput {
	
	private String paramName = "requestId";

	public RequestIdTag() {
		super();
	}
 
	public String getParamName() {
		return paramName;
	}
 
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}

	 public void encodeBegin(FacesContext context) throws IOException {
    	try {
    		final ResponseWriter writer = context.getResponseWriter();
    		
            Long requestId;
            
            
            HttpServletRequest currentRequest = (HttpServletRequest)context.getExternalContext().getRequest();
            
            requestId = (Long)context.getExternalContext().getRequestMap().get("requestId");
            

            if ( requestId == null  )
            {
            	requestId = System.currentTimeMillis();
            	currentRequest.setAttribute("requestId", requestId);
            }

            writer.startElement("input",null);
    		writer.writeAttribute("type","hidden",null);
    		writer.writeAttribute("name", paramName, "paramName");
    		writer.writeAttribute("value", requestId, "value");
    		writer.endElement("input");	 		
		} catch (IOException e) {
			throw Exceptions.wrap(e);
		}
    }

}
