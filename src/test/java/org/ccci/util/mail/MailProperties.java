package org.ccci.util.mail;

/**
 * Must be added to git ignore, properties file for tests
 *
 * @author Ben deVries
 */
public class MailProperties {

	public static String getMailServer()
	{
		return "";
	}
	public static String getMailServerUsername()
	{
		return "";
	}
	public static String getMailServerPassword()
	{
		return "";
	}
	public static String getMailServerPort()
	{
		return "";
	}

	public static String getEmailSenderName() {
		return "";
	}
	public static EmailAddress getFromEmailAddress() {
		return new EmailAddress("");
	}
	public static EmailAddress getToEmailAddress() {
		return new EmailAddress("");
	}

}
