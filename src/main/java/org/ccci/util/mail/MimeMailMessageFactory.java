package org.ccci.util.mail;

import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * <p>Used by client code to create {@link MimeMailMessage}s
 * @author Ben deVries
 */
@Name("mimeMailMessageFactory")
@Scope(ScopeType.STATELESS)
@AutoCreate
public class MimeMailMessageFactory {
    @In
    Session mailSession;

    /**
     * For usage by Seam, where a Seam-managed {@link Session} is configured
     */
    public MimeMailMessageFactory() {}

    /**
     * For usage outside of Seam
     */
    public MimeMailMessageFactory(String smtpHost)
    {
        this(smtpHost, (PasswordAuthentication)null);
    }

    public MimeMailMessageFactory(String smtpHost, String smtpPort)
    {
        this(smtpHost, smtpPort, null);
    }

    public MimeMailMessageFactory(String smtpHost, PasswordAuthentication passwordAuthentication)
    {
        Properties props = buildPropertiesWithProtocolAndHost(smtpHost);
        this.mailSession = getSession(props, passwordAuthentication);
    }

    public MimeMailMessageFactory(String smtpHost, String smtpPort, PasswordAuthentication passwordAuthentication)
    {
        Properties props = buildPropertiesWithProtocolAndHost(smtpHost);
        props.setProperty("mail.smtp.port", smtpPort);
        this.mailSession = getSession(props, passwordAuthentication);
    }

    private Properties buildPropertiesWithProtocolAndHost(String smtpHost)
    {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", smtpHost);
        return props;
    }

    /**
     * Gets a {@link Session} instance, with authentication (if required).
     */
    private Session getSession(Properties props, final PasswordAuthentication passwordAuthentication)
    {
        if(passwordAuthentication == null)
            return Session.getInstance(props);

        props.setProperty("mail.smtp.auth", "true");

        return Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return passwordAuthentication;
            }
        });
    }

    /**
     * Creates a {@link MimeMailMessage} suitable for application/business email messages.
     * These emails might be indirected by {@link MailIndirection}, if configured.
     */
    public MimeMailMessage createApplicationMessage()
    {
        checkTimeoutProperty();
        return new MimeMailMessage(mailSession, false);
    }

    private void checkTimeoutProperty()
    {
        Properties mailProperties = mailSession.getProperties();
        String transport = mailProperties.getProperty("mail.transport.protocol");
        setPropertyIfUnspecified(mailProperties, "mail." + transport + ".connectiontimeout", 5);
        setPropertyIfUnspecified(mailProperties, "mail." + transport + ".timeout", 15);
    }

    private void setPropertyIfUnspecified(Properties mailProperties, String timeoutProperty, long duration)
    {
        if (!mailProperties.containsKey(timeoutProperty))
        {
            mailProperties.setProperty(timeoutProperty, String.valueOf(TimeUnit.SECONDS.toMillis(duration)));
        }
    }

    /**
     * Creates a {@link MimeMailMessage} suitable for system/sysadmin email messages.  For example,
     * used by exception email reports.  These should be guaranteed to be delivered to the emailaddress,
     * even if {@link MailIndirection} is configured.
     */
    public MimeMailMessage createSystemMessage()
    {
        checkTimeoutProperty();
        return new MimeMailMessage(mailSession, true);
    }
}
