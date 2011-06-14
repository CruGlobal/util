package org.ccci.util.seam;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.ccci.util.ValueLatch;
import org.ccci.util.contract.Preconditions;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.log.Log;

@Scope(ScopeType.APPLICATION)
@AutoCreate
@Install(precedence = Install.FRAMEWORK + 1)
@Startup
public class JndiSeamBridgeOverride
{
    
    @Logger Log log;
    
    private ValueExpression<Object> override;
    
    @Create
    public void init(Component component) throws NamingException
    {
        if (override != null)
        {
            log.info("mapping {0} to {1}", component.getName(), override.toString());
        }
        else
        {
            log.info("'override' has not been set for {0}", component.getName());
        }
    }
    
    @Unwrap
    public Object lookup() throws NamingException
    {
        if (override == null)
        {
            throw new IllegalStateException("not configured with a component to override!");
        }
        return override.getValue();
    }
    
    public ValueExpression<Object> getOverride()
    {
        return override;
    }

    public void setOverride(ValueExpression<Object> override)
    {
        this.override = override;
    }

    /** No-op; allows this to override a JndiSeamBridge definition, which does use this attribute */
    public void setSourceJndiName(String sourceJndiName)
    {
    }

    /** No-op; see above */
    public void setCache(boolean cache)
    {
    }
    
    
}
