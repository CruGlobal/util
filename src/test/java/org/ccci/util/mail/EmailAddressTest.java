package org.ccci.util.mail;

import javax.faces.FactoryFinder;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
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
    @SuppressWarnings("ConstantConditions")
    public void sendEmail() throws MessagingException
    {
        String mailServer = MailProperties.mailServer;
        String mailServerUsername = MailProperties.mailServerUsername;
        String mailServerPassword = MailProperties.mailServerPassword;
        String mailServerPort = MailProperties.mailServerPort;

        EmailAddress toEmailAddress = MailProperties.toEmailAddress;
        EmailAddress fromEmailAddress = MailProperties.fromEmailAddress;
        EmailAddress replyToEmailAddress = MailProperties.replyToEmailAddress;
        String name = MailProperties.emailSenderName;

        if (mailServer.equals("")) {
            String methodName = new Throwable().getStackTrace()[0].getMethodName();
            System.out.println(mailServer.equals("Test: " + methodName + " bypassed"));
            return;
        }

        initSeam();

        PasswordAuthentication passwordAuthentication =
                new PasswordAuthentication(mailServerUsername, mailServerPassword);

        String content = "<HTML><H1>This is a Header</H1></BODY></HTML>";

        MimeMultipart mp = new MimeMultipart();
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(content, "text/html; charset=UTF-8");
        mp.addBodyPart(htmlPart);


        MimeMailMessageFactory mimeMailMessageFactory =
            new MimeMailMessageFactory(mailServer, mailServerPort, passwordAuthentication);

        MimeMailMessage mailMessage = mimeMailMessageFactory.createApplicationMessage();
        mailMessage.setFrom(fromEmailAddress.toInternetAddress("Local Util Test Run"));
        mailMessage.addTo(toEmailAddress.toInternetAddress());
        mailMessage.setReplyTo(replyToEmailAddress.toInternetAddress(name));
        mailMessage.setContent(mp);
        mailMessage.setSubject("Конференция Сотрудников в ş”", "UTF-8");
        mailMessage.sendToAll();
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
