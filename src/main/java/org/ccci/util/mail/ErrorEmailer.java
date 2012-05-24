package org.ccci.util.mail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.ccci.util.Exceptions;

public class ErrorEmailer
{
    public static RuntimeException sendErrorToAdmin(Properties properties, Throwable t)
    {
        String fromEmail = properties.getProperty("errorReporting.fromEmail");
        String fromName = properties.getProperty("errorReporting.fromName");
        String smtpHost = properties.getProperty("errorReporting.smtpHost");
        String toEmailList = properties.getProperty("errorReporting.toEmailList");

        MailMessage mailMessage = new MailMessageFactory(smtpHost).createApplicationMessage();
    
        String to[] = toEmailList.split(",");
        for (String address : to)
        {
            mailMessage.addTo(EmailAddress.valueOf(address.trim()), "");
        }
    
        mailMessage.setFrom(EmailAddress.valueOf(fromEmail), fromName);
        
        StringWriter errors = new StringWriter();
        t.printStackTrace(new PrintWriter(errors));
    
        mailMessage.setMessage("Error from "+fromName, errors.toString(), false);
        
        try
        {
            mailMessage.sendToAll();
        }
        catch(Exception e)
        {
            throw Exceptions.wrapDontThrow(e);
        }
        return Exceptions.wrapDontThrow(t);
    }
}
