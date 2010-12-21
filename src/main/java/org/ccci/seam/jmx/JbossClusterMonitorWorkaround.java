package org.ccci.seam.jmx;

import static org.jboss.seam.ScopeType.APPLICATION;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.jmx.JBossClusterMonitor;

@Name("org.jboss.seam.jmx.jbossClusterMonitor")
@BypassInterceptors
@Scope(APPLICATION)
@Startup
@Install(precedence=Install.FRAMEWORK, classDependencies="org.jgroups.MembershipListener")
public class JbossClusterMonitorWorkaround extends JBossClusterMonitor
{

    @Override
    protected MBeanServer locateJBoss()
    {
        // JBSEAM-4029 [jsd] Addresses NPE
        for (Object o : MBeanServerFactory.findMBeanServer(null))
        {
            MBeanServer server = (MBeanServer) o;
            if ("jboss".equals(server.getDefaultDomain()))
            {
                return server;
            }
        }
        return null;
    }
}
