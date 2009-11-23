package org.ccci.util.faces;

import java.io.IOException;
import java.io.PrintWriter;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.ccci.util.io.Downloader;
import org.ccci.util.servlet.ForwardingServletOutputStream;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("downloader")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class FacesDownloader implements Downloader
{
    @In
    FacesContext facesContext;

    /**
     * {@inheritDoc}
     * 
     * <p>
     * {@link FacesContext#responseComplete()} is called when the returned {@link ServletOutputStream}'s close() method is invoked.
     * </p>
     */
    @Override
    public ServletOutputStream downloadBinaryToClient(String contentType, String fileName) throws IOException
    {
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        return new ForwardingServletOutputStream(response.getOutputStream())
        {
            @Override
            public void close() throws IOException
            {
                super.close();
                facesContext.responseComplete();
            }
        };
    }

    
    /**
     * {@inheritDoc}
     * 
     * <p>
     * {@link FacesContext#responseComplete()} is called when the returned {@link PrintWriter}'s close() method is invoked.
     * </p>
     */
    @Override
    public PrintWriter downloadTextToClient(String contentType, String fileName) throws IOException
    {
        HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        return new PrintWriter(response.getWriter())
        {
            @Override
            public void close()
            {
                super.close();
                facesContext.responseComplete();
            }
        };
    }

}
