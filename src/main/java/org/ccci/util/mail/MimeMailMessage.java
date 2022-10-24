package org.ccci.util.mail;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;

/**
 * <p>Simple API for emails. Sends either a separate email to each person that has been added via
 * {@link #addTo(InternetAddress...)}, or a single email to all recipients, depending on whether
 * {@link #sendToEach()} or {@link #sendToAll()} is called.
 *
 * <p>Adapted from old framework jar. Use by injecting a {@link MimeMailMessageFactory}, and calling
 * {@link MimeMailMessageFactory#createApplicationMessage()}.
 *
 * <p>Not threadsafe.
 *
 * @author Ben deVries
 */
public class MimeMailMessage {
    private final Session session;
    private final MimeMessage mimeMessage;

    private final Logger log = Logger.getLogger(MimeMailMessage.class);

    private final List<InternetAddress> toList = Lists.newArrayList();

    private boolean sent = false;

    /** whether this message is an application message, or a system message */
    private final boolean system;

    MimeMailMessage(Session session, boolean system)
    {
        this.session = session;
        this.mimeMessage = new MimeMessage(session);
        this.system = system;
    }

    /**
     * Add a person or people to the list of recipients
     * @param addresses array of recipients to be added to the email
     */
    public void addTo(InternetAddress... addresses)
    {
        checkNotSent();
        toList.addAll(Arrays.asList(addresses));
    }

    public void setFrom(InternetAddress address)
    {
        checkNotSent();
        Preconditions.checkNotNull(address.getAddress());

        try
        {
            mimeMessage.setFrom(address);
        } catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    public void setReplyTo(InternetAddress... internetAddresses)
    {
        checkNotSent();

        try
        {
            mimeMessage.setReplyTo(internetAddresses);
        }
        catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    public void setContent(MimeMultipart multipart)
    {
        checkNotSent();
        try
        {
            Preconditions.checkArgument(multipart.getCount() > 0);
            mimeMessage.setContent(multipart);
        }
        catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    public void setSubject(String subject)
    {
        setSubject(subject, "UTF-8");
    }

    public void setSubject(String subject, String charSet)
    {
        checkNotSent();
        Preconditions.checkNotNull(subject, "subject is null");
        Preconditions.checkNotNull(charSet, "charset is null");

        try
        {
            mimeMessage.setSubject(subject, charSet);
        }
        catch (MessagingException e)
        {
            throw Throwables.propagate(e);
        }
    }

    /**
     * send the message to each recipient individually
     * @throws SendFailedException - if the message could not be sent to one of the recipients.
     * @throws MessagingException - Any other error sending the message
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
                mimeMessage.setRecipient(Message.RecipientType.TO, address);
                mimeMessage.saveChanges();
                send(transport, mimeMessage);
                log.debug("sent to " + address);
            }
        }
        finally
        {
            transport.close();
        }
        sent = true;
    }

    private void send(Transport transport, MimeMessage message) throws MessagingException
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
     * @throws MessagingException - Any other error sending the message
     */
    public void sendToAll() throws MessagingException
    {
        checkNotSent();
        setJavaGeneratedHeader();

        Transport transport = session.getTransport();
        transport.connect();
        try
        {
            for (InternetAddress address : toList)
            {
                mimeMessage.addRecipient(Message.RecipientType.TO, address);
            }
            mimeMessage.saveChanges();
            send(transport, mimeMessage);
            log.debug("sent to " + toList);
        }
        finally
        {
            transport.close();
        }
        sent = true;
    }

    /**
     * important - set this so that our SMTP server
     * won't mess with this message
     */
    private void setJavaGeneratedHeader() throws MessagingException
    {
        mimeMessage.setHeader("X-JavaGenerated","Auto");
    }

    private void checkNotSent()
    {
        Preconditions.checkState(!sent, "already sent message!");
    }
}
