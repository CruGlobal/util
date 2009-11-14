package org.ccci.debug;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryNotificationInfo;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;

import com.google.common.base.Preconditions;

/**
 * This memory warning system will call the listener when we exceed the percentage of available memory specified.
 * There should only be one instance of this object created, since the usage threshold can only be set to one
 * number.
 * 
 * From http://www.javaspecialists.co.za/archive/Issue092.html
 * 
 * TODO: this shouldn't be deployed with a business app. Should be either part of the appserver, or part of a
 * utility app deployed to the appserver.
 * 
 */
public class MemoryWarningSystem
{

    private   MemoryThresholdExceededNotificationListener notificationListener;
    
    private static final class MemoryThresholdExceededNotificationListener implements NotificationListener
    {
        private final Listener listener;
        
        public MemoryThresholdExceededNotificationListener(Listener listener)
        {
            this.listener = listener;
        }

        public void handleNotification(Notification n, Object hb)
        {
            if (n.getType().equals(MemoryNotificationInfo.MEMORY_THRESHOLD_EXCEEDED))
            {
                long maxMemory = tenuredGenPool.getUsage().getMax();
                long usedMemory = tenuredGenPool.getUsage().getUsed();
                listener.memoryUsageLow(usedMemory, maxMemory);
            }
        }
    }

    public interface Listener
    {
        public void memoryUsageLow(long usedMemory, long maxMemory);
    }


    public synchronized void startup(Listener listener)
    {
        Preconditions.checkState(notificationListener == null, "alreday started!");
        NotificationEmitter emitter = getEmitter();
        MemoryThresholdExceededNotificationListener tempListener = new MemoryThresholdExceededNotificationListener(listener);
        emitter.addNotificationListener(tempListener, null, null);
        //don't assign to field until after addNotificationListener() 
        notificationListener = tempListener;
    }

    private NotificationEmitter getEmitter()
    {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        NotificationEmitter emitter = (NotificationEmitter) mbean;
        return emitter;
    }

    public synchronized void shutdown()
    {
        Preconditions.checkState(notificationListener != null, "either not started, or already stopped!");
        try
        {
            getEmitter().removeNotificationListener(notificationListener);
        }
        catch (ListenerNotFoundException e)
        {
            throw org.ccci.util.Exceptions.wrap(e);
        }
        notificationListener = null;
    }
    
    private static final MemoryPoolMXBean tenuredGenPool = findTenuredGenPool();

    public static void setPercentageUsageThreshold(double percentage)
    {
        if (percentage <= 0.0 || percentage > 1.0) { throw new IllegalArgumentException("Percentage not in range"); }
        long maxMemory = tenuredGenPool.getUsage().getMax();
        long warningThreshold = (long) (maxMemory * percentage);
        tenuredGenPool.setUsageThreshold(warningThreshold);
    }

    /**
     * Tenured Space Pool can be determined by it being of type HEAP and by it being possible to set the usage
     * threshold.
     */
    private static MemoryPoolMXBean findTenuredGenPool()
    {
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans())
        {
            // I don't know whether this approach is better, or whether
            // we should rather check for the pool name "Tenured Gen"?
            if (pool.getType() == MemoryType.HEAP && pool.isUsageThresholdSupported()) { return pool; }
        }
        throw new AssertionError("Could not find tenured space");
    }

}
