package org.ccci.debug;

import static org.jboss.seam.ScopeType.STATELESS;

import java.io.Serializable;
import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

import org.ccci.util.mail.EmailAddress;
import org.ccci.util.mail.MailMessage;
import org.ccci.util.mail.MailMessageFactory;
import org.ccci.util.seam.Components;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.log.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Reports exceptions to developers via email.
 * Configured in components.xml, for example: 
 *  <component name="exceptionReporter">
 *    <property name="recipients">
 *      <value>matt.drees@ccci.org</value>
 *    </property>
 *  </component>
 *  
 *  Can be temporarily disabled, if desired:
 *  <component name="exceptionReporter">
 *  ...
 *  <property name="enabled">false</property>
 *  </component>
 *  
 *  
 * @author Matt Drees
 *
 */
@Name("exceptionReporter")
@Scope(STATELESS)
@BypassInterceptors
public class ExceptionReporter implements Serializable
{
    private static final long serialVersionUID = 1L;

    @Logger Log log;
    
    private List<EmailAddress> recipients = Lists.newArrayList();
    
    private EmailAddress sender;

    private boolean configured = false;
    
    private boolean enabled = true;
    
    @Observer("org.jboss.seam.postInitialization")
    public void validate()
    {
        if (enabled && configured)
        {
            Preconditions.checkState(!recipients.isEmpty(), "must specify at least one recipient for exception emails in components.xml");
            Preconditions.checkState(sender != null, "must specify sender address for exception emails in components.xml");
        }
    }
    
    public static ExceptionReporter instance()
    {
        return Components.getStatelessComponent(ExceptionReporter.class);
    }

    public void reportExceptions()
    {
        sendReport(createExceptionReport());
    }

    public void reportLogMessages()
    {
        sendReport(createErrorLogReport());
        
    }

    /**
     * Sends the given {@link Report}.
     * Will not throw mail exceptions, etc; rather, will log exceptions and return.
     * @param report
     */
    public void sendReport(Report report)
    {
        if (!enabled) return;
        if (recipients.isEmpty())
        {
            log.warn("exception reporter enabled, but no recipients configured in components.xml");
            return;
        }
        MailMessageFactory factory = Components.getStatelessComponent(MailMessageFactory.class);
        MailMessage message;
        try
        {
            message = factory.createSystemMessage();
            String reportAsString = makeOutlookFriendly(report.getReportAsString());
            message.setMessage(report.getSummary(), reportAsString);
            for (EmailAddress recipient : recipients)
            {
                message.addTo(recipient);
            }
            message.setFrom(sender);
        }
        catch (Exception e)
        {
            log.error("could not construct multipart MimeMessage; exception report not sent", e);
            return;
        }
        
        try
        {
            message.sendToAll();
        }
        catch (MessagingException e)
        {
            log.error("could not get send exception report", e);
            return;
        }
    }

    


    /**
     * Outlook sometimes strips newlines.  This can be disabled in Outlook, but to avoid that,
     * a tab character can be added to the end of each line
     * see http://www.masternewmedia.org/newsletter_publishing/newsletter_formatting/remove_line_breaks_issue_Microsoft_Outlook_2003_when_publishing_text_newsletters_20051217.htm
     * @param reportAsString
     * @return
     */
    private String makeOutlookFriendly(String string)
    {
        String lineSeparator = System.getProperty("line.separator");
        return string.replaceAll(lineSeparator, "\t" + lineSeparator);
    }

    private ExceptionReport createExceptionReport()
    {
        ExceptionContext exceptionContext = ExceptionContext.getCurrentInstance();

        ExceptionReport report = new ExceptionReport();
        report.setExceptionEvent(exceptionContext.getPrimaryException());
        report.setHandledExceptionEvent(exceptionContext.getPrimaryHandledException());
        report.setLoggingLayout(exceptionContext.getLoggingLayout());
        
        report.setThisRequestEvent(new RequestEvent(exceptionContext.getRequest()));
        HttpSession session = exceptionContext.getSession();
        if (session != null)
        {
            report.getRecentRequestEvents().addAll(
                Components.getSessionScopedComponentOutsideSessionContext(RequestHistory.class,
                    session).getRecentRequests());
        }

        report.getUnexpectedExceptionsByLocation().putAll(exceptionContext.getUnexpectedExceptionsByLocation());
        report.getLoggingEvents().addAll(exceptionContext.getLoggingEvents());
        
        AdditionalExceptionDetailsPrinter printer = AdditionalExceptionDetailsPrinter.instance();
        report.getOtherExceptionDetails().addAll(printer.getAdditionalDetails(report.getExceptionEvent().getThrowable()));
        return report;
    }


    private Report createErrorLogReport()
    {
        ExceptionContext exceptionContext = ExceptionContext.getCurrentInstance();

        ErrorLogReport report = new ErrorLogReport();
        report.setLoggingLayout(exceptionContext.getLoggingLayout());
        
        report.setThisRequestEvent(new RequestEvent(exceptionContext.getRequest()));
        HttpSession session = exceptionContext.getSession();
        if (session != null)
        {
            report.getRecentRequestEvents().addAll(
                Components.getSessionScopedComponentOutsideSessionContext(RequestHistory.class,
                    session).getRecentRequests());
        }

        report.getLoggingEvents().addAll(exceptionContext.getLoggingEvents());
        report.setUsername(exceptionContext.getUsername());
        return report;    
    }

    //configured in components.xml
    public void setRecipients(List<String> recipients)
    {
        configured  = true;
        this.recipients = Lists.newArrayList();
        for (String recipient : recipients)
        {
            this.recipients.add(EmailAddress.valueOf(recipient));
        }
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    //configured in components.xml
    public void setSender(String sender)
    {
        configured = true;
        this.sender = EmailAddress.valueOf(sender);
    }

}
