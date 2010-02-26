package org.ccci.debug;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

@Name("requestHistory")
@Scope(SESSION)
@Startup
@BypassInterceptors
public class RequestHistory implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int maxSizeOfHistory = 25;
    
    private final List<RequestEvent> recentRequests = Lists.newArrayList();
    private final Lock lock = new ReentrantLock();

    
    private List<String> extensionsToIgnore = Lists.newArrayList(".jpg", ".js", ".css", ".gif", ".ico");
    private List<String> pathsToIgnore = Lists.newArrayList("/a4j_");
    
    ParameterSanitizer sanitizer;
    
    @Create
    public void initSanitizer()
    {
        sanitizer = (ParameterSanitizer) Component.getInstance("parameterSanitizer");
        Preconditions.checkNotNull(sanitizer, "parameterSanitizer is not available!");
    }
    
    /**
     * note: Executed when seam contexts are not active 
     */
    public void newRequest(HttpServletRequest request)
    {
        String url = request.getRequestURL().toString();
        for (String extension : extensionsToIgnore)
        {
            if (url.endsWith(extension)) return;
        }
        for (String path : pathsToIgnore)
        {
            String servletPath = request.getServletPath();
            if (servletPath != null && servletPath.startsWith(path)) return;
        }
        
        /* 
         * Generally, session-scoped components are @Synchronized,
         * but this has @BypassInterceptors specified, so it isn't.
         */
        lock.lock();
        try
        {
            if (recentRequests.size() >= maxSizeOfHistory)
            {
                recentRequests.remove(0);
            }
            recentRequests.add(new RequestEvent(request, sanitizer));
        } finally {
            lock.unlock();
        }
    }

    public int getMaxSizeOfHistory()
    {
        return maxSizeOfHistory;
    }

    public void setSizeOfHistory(int sizeOfHistory)
    {
        this.maxSizeOfHistory = sizeOfHistory;
    }

    public List<RequestEvent> getRecentRequests()
    {
        return recentRequests;
    }

}
