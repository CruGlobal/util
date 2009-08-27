package org.ccci.seam.persistence;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.persistence.PersistenceProvider;

/**
 * 
 *  see http://jira.jboss.org/jira/browse/JBSEAM-3030
 *  
 * for either Toplink or Eclipselink (almost the same thing, really)
 * @author Matt Drees
 *
 */
@Name("org.jboss.seam.persistence.persistenceProvider")
@Scope(ScopeType.STATELESS)
@BypassInterceptors
@Install(precedence=Install.FRAMEWORK + 1)
public class NonHibernatePersistenceProvider extends PersistenceProvider
{

    @Override
    public void setFlushModeManual(EntityManager entityManager)
    {
        // don't throw an UnsupportedOperationException
    }
}
