package org.ccci.el;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.el.util.ReflectionUtil;

/**
 * Shutdown Reflection Util. https://jira.jboss.org/jira/browse/JBSEAM-3932
 * @author Alexander Schwartz (PLUS Finanzservice) 2009
 */
public class JbossELShutdownListener implements ServletContextListener {

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        ReflectionUtil.shutdown();
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

}
