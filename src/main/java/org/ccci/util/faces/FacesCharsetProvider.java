package org.ccci.util.faces;

import java.nio.charset.Charset;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;


/**
 * Allows Seam components to inject the {@link Charset} of the current request.
 * This is determined by calling {@link ExternalContext#getRequestCharacterEncoding()}.  If that
 * returns null, this factory returns the default <tt>Charset</tt>.
 * 
 * @author Matt Drees
 *
 */
@Name("facesCharsetProvider")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class FacesCharsetProvider
{

    @In
    FacesContext facesContext;

    @Factory(value = "requestCharset", autoCreate = true, scope = ScopeType.EVENT)
    public Charset getRequestCharset()
    {
        String requestCharacterEncoding = facesContext.getExternalContext().getRequestCharacterEncoding();
        return requestCharacterEncoding == null ? Charset.defaultCharset() : Charset.forName(requestCharacterEncoding);
    }
    
}
