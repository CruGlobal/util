package org.ccci.debug;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class FloodSummary
{
    
    private final List<ExceptionEvent> exceptionEvents = Lists.newArrayList();
    private final DateTime start;

    /**
     * @param exceptionEvents should be sorted by occuredAt
     */
    public FloodSummary(List<ExceptionEvent> exceptionEvents)
    {
        Preconditions.checkNotNull(exceptionEvents, "exceptionEvents is null");
        Preconditions.checkArgument(!exceptionEvents.isEmpty(), "exceptionEvents is empty");
        this.exceptionEvents.addAll(exceptionEvents);
        start = exceptionEvents.get(0).getOccuredAt();
    }
    
    public DateTime getStart()
    {
        return start;
    }
    
    public List<ExceptionEvent> getExceptionEvents()
    {
        return Collections.unmodifiableList(exceptionEvents);
    }
    
    public int getCountOfAffectedUsers()
    {
        return 0; //TODO:
    }
}
