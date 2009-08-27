package org.ccci.debug;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import javax.faces.event.PhaseId;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

/**
 * Constructed when an exception occurs.
 * A value object holding basic context information about where/when/to whom the
 * exception occurred.
 * 
 * This should represent the top-level exception, not necessarily the root exception.
 * 
 * @author Matt Drees
 *
 */
public class ExceptionEvent implements Serializable, Comparable<ExceptionEvent>
{
    private static final long serialVersionUID = 1L;

    private final Throwable throwable;
    private final String sessionId;
    private final DateTime occuredAt;
    private final String username;
    private final String phaseId;
    private final String conversationId;
    private final String userAgent;
    private final String remoteIPAddress;
    private final Map<String, String> additionalContextInformation = Maps.newLinkedHashMap();
    
    //TODO: add a unique eventId, that can be referenced later?
    
    /**
     * 
     * @param throwable
     * @param occuredAt
     * @param sessionId - not required
     * @param username - not required
     * @param phaseId - not required
     * @param userAgent 
     * @param remoteIPAddress 
     */
    public ExceptionEvent(Throwable throwable, DateTime occuredAt, String sessionId, String username, PhaseId phaseId, String conversationId, String userAgent, String remoteIPAddress)
    {
        Preconditions.checkNotNull(throwable, "throwable is null");
        Preconditions.checkNotNull(occuredAt, "occuredAt is null");
        
        this.throwable = throwable;
        this.occuredAt = occuredAt;
        this.sessionId = sessionId;
        this.username = username;
        this.phaseId = phaseId == null ? null : phaseId.toString();
        this.conversationId = conversationId;
        this.userAgent = userAgent;
        this.remoteIPAddress = remoteIPAddress;
    }
    
    public ExceptionLocation getExceptionLocation()
    {
        return new ExceptionLocation(ExceptionUtil.getRootThrowable(throwable));
    }

    public Throwable getThrowable()
    {
        return throwable;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public DateTime getOccuredAt()
    {
        return occuredAt;
    }

    public String getConversationId()
    {
        return conversationId;
    }

    /**
     * May be null, if the user was not logged in at the beginning of the request
     * @return
     */
    public String getUsername()
    {
        return username;
    }
    
    public String getUserAgent()
    {
        return userAgent;
    }

    public String getRemoteIPAddress()
    {
        return remoteIPAddress;
    }

    /**
     * the JSF PhaseID when this exception occured.  may be null, if outside a jsf phase or
     * in a non-jsf request
     * @return
     */
    public String getPhaseId()
    {
        return phaseId;
    }

    @Override
    public String toString()
    {
        return String.format("ExceptionEvent[throwable: %s, sessionId: %s, username: %s, occuredAt: %s, phaseId: %s, conversationId: %s]", 
            throwable, sessionId, username, occuredAt, phaseId, conversationId);
    }

    public Throwable getRootThrowable()
    {
        return ExceptionUtil.getRootThrowable(throwable);
    }

    public int compareTo(ExceptionEvent o)
    {
        Preconditions.checkNotNull(o, "other exceptionEvent is null");
        return occuredAt.compareTo(o.occuredAt);
    }

    public Map<String, String> getAdditionalContextInformation()
    {
        return Collections.unmodifiableMap(additionalContextInformation);
    }
    
    public void addContextInformation(String key, String value)
    {
        additionalContextInformation.put(key, value);
    }
    
}
