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
    public void sendemail() throws MessagingException {

        initSeam();

        String mailServer = MailProperties.getMailServer();
        String mailServerUsername = MailProperties.getMailServerUsername();
        String mailServerPassword = MailProperties.getMailServerPassword();
        String mailServerPort = MailProperties.getMailServerPort();

        PasswordAuthentication passwordAuthentication =
                new PasswordAuthentication(mailServerUsername, mailServerPassword);

        MailMessageFactory mailMessageFactory =
                new MailMessageFactory(mailServer, mailServerPort, passwordAuthentication);

        MailMessage mailMessage = mailMessageFactory.createApplicationMessage();

        mailMessage.setFrom(EmailAddress.valueOf("event.registration.tool@cru.org"), "Local Util Test Run");
        mailMessage.addTo(EmailAddress.valueOf("ben.devries@cru.org"));
        mailMessage.setReplyTo(EmailAddress.valueOf("ben.devries@cru.org"), "Ben deVries");


        mailMessage.setMessage("Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 şşşşşşşş  ““”", "Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 şşşşşşşş  ““”", true);
        mailMessage.setCharset(Charsets.UTF_8);

        mailMessage.sendToAll();


        MailMessage mailMessage2 = mailMessageFactory.createApplicationMessage();

        mailMessage2.setFrom(EmailAddress.valueOf("event.registration.tool@cru.org"), "Local Util Test Run");
        mailMessage2.addTo(EmailAddress.valueOf("ben.devries@cru.org"));
        mailMessage2.setReplyTo(EmailAddress.valueOf("ben.devries@cru.org"), "Ben deVries");


        mailMessage2.setMessage("Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 şşşşşşşş  ““”", "Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 şşşşşşşş  ““”", true);
        mailMessage2.sendToAll();

        MailMessage mailMessage3 = mailMessageFactory.createApplicationMessage(Charsets.UTF_8);

        mailMessage3.setFrom(EmailAddress.valueOf("event.registration.tool@cru.org"), "Local Util Test Run");
        mailMessage3.addTo(EmailAddress.valueOf("ben.devries@cru.org"));
        mailMessage3.setReplyTo(EmailAddress.valueOf("ben.devries@cru.org"), "Ben deVries");


        mailMessage3.setMessage("Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 şşşşşşşş  ““”", "Конференция Сотрудников в Анталии 2023 /Staff Conference in Antalya 2023 şşşşşşşş  ““”", true);
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
