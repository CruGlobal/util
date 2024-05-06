package org.ccci.util.mail;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

/**
 * @deprecated Not suitable for HTML messages that use the UTF-8 charset, or have non-ascii subjects
 * <br> Use {@link MimeMailMessage} instead <br><br>
 *
 * <p>Simple API for emails. Sends either a separate email to each person that has been added via
 * {@link #addTo(EmailAddress, String)}, or a single email to all recipients, depending on whether
 * {@link #sendToEach()} or {@link #sendToAll()} is called.
 *
 * <p>Adapted from old framework jar. Use by injecting a {@link MailMessageFactory}, and calling
 * {@link MailMessageFactory#createApplicationMessage()}.
 *
 * <p>Should only be used for simple, plain text emails. HTML emails should use Seam / facelets
 * email templating (see http://docs.jboss.com/seam/2.0.2.GA/reference/en-US/html/mail.html ).
 *
 * <p>Nonetheless, this class supports sending html text messages if required.
 *
 * <p>Not threadsafe.
 *
 * @author Matt Drees
 * @author Lee Braddock
 */
@Deprecated
public class MailMessage
{
    private Session session;
    private Message message;

    private Logger log = Logger.getLogger(MailMessage.class);

    private List<InternetAddress> toList = Lists.newArrayList();
    private List<InternetAddress> ccList = Lists.newArrayList();
    private List<InternetAddress> bccList = Lists.newArrayList();

    private boolean sent = false;

    /** whether this message is an application message, or a system message */
    private final boolean system;
    
    MailMessage(Session session, boolean system)
    {
        this.session = session;
        message = new MimeMessage(session);
        this.system = system;
    }

    /**
     * Add a person to the list of recipients, using a personal name
     * @throws UnsupportedEncodingException
     */
    public void addTo(EmailAddress address, String name)
    {
        addTo(address, name, Message.RecipientType.TO);
    }

    /**
     * Add a person to the list of recipients, using a personal name
     */
    public void addTo(EmailAddress address, String name, Message.RecipientType recipientType)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        Preconditions.checkNotNull(name, "name is null");
        recipientTypeToList(recipientType).add(address.toInternetAddress(name));
    }

    private List<InternetAddress> recipientTypeToList(Message.RecipientType recipientType)
    {
        return recipientType == Message.RecipientType.CC ? ccList :
                recipientType == Message.RecipientType.BCC ? bccList : toList;
    }

    private void checkNotSent()
    {
        Preconditions.checkState(!sent, "already sent message!");
    }


    /**
     * Add a person to the list of recipients
     * @throws UnsupportedEncodingException 
     */
    public void addTo(EmailAddress address)
    {
        addTo(address, Message.RecipientType.TO);
    }

    public void addTo(EmailAddress address, Message.RecipientType recipientType)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        recipientTypeToList(recipientType).add(address.toInternetAddress());
    }

    /**
     * set the "from" address and name
     */
    public void setFrom(EmailAddress address, String name)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        Preconditions.checkNotNull(name, "name is null");
        try
        {
            message.setFrom(address.toInternetAddress(name));
        }
        catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }
    

    public void setFrom(EmailAddress address)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        try
        {
            message.setFrom(address.toInternetAddress());
        }
        catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /**
     * set the reply-to address and name
     */    
    public void setReplyTo(EmailAddress address, String name)
    {
        checkNotSent();
        Preconditions.checkNotNull(address, "address is null");
        Preconditions.checkNotNull(name, "name is null");
        InternetAddress addresses[] = new InternetAddress[1];
        addresses[0] = address.toInternetAddress(name);
        try
        {
            message.setReplyTo(addresses);
        }
        catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Set the subject and text message
     */
    public void setMessage(String subject, String body)
    {
    	setMessage(subject, body, false);
    }

    /**
     * Set the subject and text message
     */
    public void setMessage(String subject, String body, boolean html)
    {
        checkNotSent();
        Preconditions.checkNotNull(subject, "subject is null");
        Preconditions.checkNotNull(body, "body is null");
        try
        {
            message.setSubject(subject);
            message.setContent(body, "text/" + ((html) ? "html" : "plain"));
        }
        catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /**
     * send the message to each recipient individually
     * @throws SendFailedException - if the message could not be sent to one of the recipients.  
     * @throws MessagingException
     */    
    public void sendToEach() throws MessagingException 
    {
        checkNotSent();
        setJavaGeneratedHeader();
        
        Transport transport = session.getTransport();
        transport.connect();
        try
        {
            for (InternetAddress address : toList)
            {
                message.setRecipient(Message.RecipientType.TO, address);
                addRecipientsTo(message, ccList, Message.RecipientType.CC);
                addRecipientsTo(message, bccList, Message.RecipientType.BCC);
                message.saveChanges();
                send(transport, message);
                log.debug("sent to " + address);
            }

        }
        finally
        {
            transport.close();
        }
        sent = true;
    }

    private void send(Transport transport, Message message) throws MessagingException
    {
        if (system && transport instanceof IndirectingSMTPTransport)
        {
            ((IndirectingSMTPTransport)transport).sendMessageWithoutIndirection(message, message.getAllRecipients());
        }
        else
        {
            transport.sendMessage(message, message.getAllRecipients());
        }
    }
    
    /**
     * send the message to all recipients
     * @throws SendFailedException - if the message could not be sent to one of the recipients.  
     * @throws MessagingException
     */    
    public void sendToAll() throws MessagingException 
    {
        checkNotSent();
        setJavaGeneratedHeader();
        
        Transport transport = session.getTransport();
        transport.connect();
        try
        {
            addRecipientsTo(message, toList, Message.RecipientType.TO);
            addRecipientsTo(message, ccList, Message.RecipientType.CC);
            addRecipientsTo(message, bccList, Message.RecipientType.BCC);

            message.saveChanges();
            send(transport, message);
            log.debug("sent to " + toList);
        }
        finally
        {
            transport.close();
        }
        sent = true;
    }

    private void addRecipientsTo(
            Message message,
            List<InternetAddress> list,
            Message.RecipientType recipientType) throws MessagingException
     {
        for (InternetAddress address : list)
        {
            message.addRecipient(recipientType, address);
            log.debug("sending to " + address);
        }
    }

    /**
     * important - set this so that our SMTP server
     * won't mess with this message
     */
    private void setJavaGeneratedHeader() throws MessagingException
    {
        message.setHeader("X-JavaGenerated","Auto");
    }
}
