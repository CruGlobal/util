package org.ccci.util.seam;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

public class JodaSeamFactories
{

    /**
     * Similar to {@link org.jboss.seam.framework.CurrentDatetime}
     * @author Matt Drees
     *
     */
    @Name("currentDateTime")
    @Install(precedence=Install.FRAMEWORK)
    @Scope(ScopeType.STATELESS)
    @AutoCreate
    @BypassInterceptors
    public static class CurrentDateTimeProvider
    {
       @Unwrap
       public DateTime getCurrentDateTime()
       {
           return new DateTime();
       }
    }

    @Name("currentLocalDate")
    @Install(precedence=Install.FRAMEWORK)
    @Scope(ScopeType.STATELESS)
    @AutoCreate
    @BypassInterceptors
    public static class CurrentLocalDateProvider
    {
        @Unwrap
        public LocalDate getCurrentLocalDate()
        {
            return new LocalDate();
        }
    }
    
    @Name("currentLocalTime")
    @Install(precedence=Install.FRAMEWORK)
    @Scope(ScopeType.STATELESS)
    @AutoCreate
    @BypassInterceptors
    public static class CurrentLocalTimeProvider
    {
        @Unwrap
        public LocalTime getCurrentLocalTime()
        {
            return new LocalTime();
        }
    }
   /* 
    @Name("requestId")
    @Install(precedence=Install.FRAMEWORK)
    @Scope(ScopeType.STATELESS)
    @AutoCreate
    @BypassInterceptors
    public static class RequestIdProvider
    {
       @Unwrap
       public Long getRequestId()
       {
           return System.currentTimeMillis();
       }
    }
    */
    
}
