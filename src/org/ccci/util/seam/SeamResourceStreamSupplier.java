package org.ccci.util.seam;

import java.io.InputStream;

import org.ccci.util.io.ResourceStreamSupplier;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.ResourceLoader;

import com.google.common.base.Preconditions;

/**
 * 
 * @author Matt Drees
 */
//TODO: remove if not used
@Name("resourceStreamSupplier")
@AutoCreate
@Scope(ScopeType.STATELESS)
public class SeamResourceStreamSupplier implements ResourceStreamSupplier
{

    @In
    ResourceLoader resourceLoader;

    @Override
    public InputStream getResourceAsStream(String name)
    {
        InputStream resourceAsStream = resourceLoader.getResourceAsStream(name);
        Preconditions.checkNotNull(resourceAsStream, "resource %s is not available on classpath or servlet context path", name);
        return resourceAsStream;
    }

}
