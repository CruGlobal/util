package org.ccci.util.mail;

import com.google.common.base.Charsets;
import javax.faces.FactoryFinder;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockApplicationFactory;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class EmailAddressTest
{

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValidation_noExclamationMarks()
    {
        EmailAddress.valueOf("matt.drees!@ccci.org");
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testValidation_noTrailingSpaces()
    {
        EmailAddress.valueOf("matt.drees@ccci.org ");
    }

    @Test
    public void sendEmail() throws MessagingException {
        String mailServer = MailProperties.mailServer;
        String mailServerUsername = MailProperties.mailServerUsername;
        String mailServerPassword = MailProperties.mailServerPassword;
        String mailServerPort = MailProperties.mailServerPort;

        EmailAddress toEmailAddress = MailProperties.toEmailAddress;
        EmailAddress fromEmailAddress = MailProperties.fromEmailAddress;
        EmailAddress replyToEmailAddress = MailProperties.replyToEmailAddress;
        String name = MailProperties.emailSenderName;

        if (mailServer.equals("")) {
            // Test bypassed if not configured
            return;
        }

        initSeam();

        PasswordAuthentication passwordAuthentication =
                new PasswordAuthentication(mailServerUsername, mailServerPassword);

        MailMessageFactory mailMessageFactory =
                new MailMessageFactory(mailServer, mailServerPort, passwordAuthentication);

        MailMessage mailMessage = mailMessageFactory.createApplicationMessage();
        mailMessage.setFrom(fromEmailAddress, "Local Util Test Run");
        mailMessage.addTo(toEmailAddress);
        mailMessage.setReplyTo(replyToEmailAddress, name);
        mailMessage.setMessage("Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 ş  ”", "Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 ş  ”", true);
        mailMessage.setCharset(Charsets.UTF_8);
        mailMessage.sendToAll();

        MailMessage mailMessage2 = mailMessageFactory.createApplicationMessage();
        mailMessage2.setFrom(fromEmailAddress, "Local Util Test Run");
        mailMessage2.addTo(toEmailAddress);
        mailMessage2.setReplyTo(replyToEmailAddress, name);
        mailMessage2.setMessage("Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 ş  ”", "Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 ş  ”", true);
        mailMessage2.sendToAll();

        MailMessage mailMessage3 = mailMessageFactory.createApplicationMessage(Charsets.UTF_8);
        mailMessage3.setFrom(fromEmailAddress, "Local Util Test Run");
        mailMessage3.addTo(toEmailAddress);
        mailMessage3.setReplyTo(replyToEmailAddress, name);
        mailMessage3.setMessage("Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 ş  ”", "Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 ş  ”", true);
        mailMessage3.sendToAll();
    }

    private void initSeam()
    {
        MockServletContext servletContext = new MockServletContext();

        ServletLifecycle.beginApplication(servletContext);

        FactoryFinder.setFactory(FactoryFinder.APPLICATION_FACTORY, MockApplicationFactory.class.getName());
        new Initialization(servletContext).create().init();
        ((Init) servletContext.getAttribute(Seam.getComponentName(Init.class))).setDebug(false);
    }
}
