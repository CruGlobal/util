package org.ccci.servlet;

import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.ccci.util.HttpRequests;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * A tool to be used mostly by servlet filters that need to either ignore or include certain paths or types of requests.
 * 
 * @author Matt Drees
 */
public class ServletRequestMatcher
{
    private final Pattern urlPattern;
    private final boolean matchNonHttpRequests;

    private ServletRequestMatcher(Pattern urlPattern, boolean matchNonHttpRequests)
    {
        this.urlPattern = urlPattern;
        this.matchNonHttpRequests = matchNonHttpRequests;
    }

    public boolean matches(ServletRequest request)
    {
        Preconditions.checkNotNull(request, "request is null");
        if (request instanceof HttpServletRequest)
        {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            String fullPath = HttpRequests.getFullPath(httpRequest);
            if (urlPattern.matcher(fullPath).matches())
                return true;
        }
        else
        {
            if (matchNonHttpRequests)
                return true;
        }
        return false;
    }
    
    public static class Builder
    {
        private boolean matchNonHttpRequests;
        private List<String> urlPatterns = Lists.newLinkedList() ;

        /**
         * by default, no paths are matched
         */
        public Builder matchingUrlPatterns(Iterable<String> urlPatterns)
        {
            Preconditions.checkNotNull(urlPatterns, "urlPatterns is null");
            Iterables.addAll(this.urlPatterns, urlPatterns);
            return this;
        }
        
        /**
         * a convenience shortcut for {@link #matchingUrlPatterns(Iterable)} with
         * path prefix match patterns. 
         */
        public Builder matchingPaths(Iterable<String> paths)
        {
            Preconditions.checkNotNull(urlPatterns, "paths is null");
            for (String path : paths)
            {
                Preconditions.checkNotNull(path, "path is null");
                urlPatterns.add(path + ".*");
            }
            return this;
        }
        
        /**
         * a convenience shortcut for {@link #matchingUrlPatterns(Iterable)} with
         * url extension match patterns. Each extension should look, for example, 
         * like '.jpg'.
         */
        public Builder matchingExtensions(Iterable<String> extensions)
        {
            Preconditions.checkNotNull(urlPatterns, "paths is null");
            for (String extension : extensions)
            {
                Preconditions.checkNotNull(extension, "extension is null");
                urlPatterns.add(".*" + extension.replace(".", "\\."));
            }
            return this;
        }
        
        /**
         * By default, don't match non-http requests 
         */
        public Builder matchNonHttpRequests()
        {
            matchNonHttpRequests = true;
            return this;
        }
        
        /** Create a the configured {@link ServletRequestMatcher} */
        public ServletRequestMatcher build()
        {
            String combinedPattern = Joiner.on("|").join(urlPatterns);
            return new ServletRequestMatcher(Pattern.compile(combinedPattern), matchNonHttpRequests);
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    
}
