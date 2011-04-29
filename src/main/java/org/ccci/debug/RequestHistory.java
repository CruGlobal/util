package org.ccci.debug;

import static org.jboss.seam.ScopeType.SESSION;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;

import org.ccci.servlet.ServletRequestMatcher;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.intercept.PostActivate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Name("requestHistory")
@Scope(SESSION)
@Startup
@BypassInterceptors
public class RequestHistory implements Serializable
{
    private static final long serialVersionUID = 1L;

    private int maxSizeOfHistory = 25;
    
    private final List<RequestEvent> recentRequests = Lists.newLinkedList();
    private final Lock lock = new ReentrantLock();

    private static final ServletRequestMatcher ignoredRequestsMatcher = ServletRequestMatcher.builder()
        .matchingExtensions(Lists.newArrayList(".jpg", ".js", ".css", ".gif", ".ico"))
        .matchingPaths(Lists.newArrayList("/a4j_"))
        .build();
    
    transient ParameterSanitizer sanitizer;
    
    @Create
    @PostActivate
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
        if (ignoredRequestsMatcher.matches(request))
            return;
        
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
        lock.lock();
        try
        {
            return ImmutableList.copyOf(recentRequests);
        } finally {
            lock.unlock();
        }
    }

}
