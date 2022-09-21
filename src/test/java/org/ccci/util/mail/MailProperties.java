package org.ccci.util.mail;

public class MailProperties {
    /*
     * WARNING: secure data should be deleted before uploading to github
     */
    public static String mailServer = "";
    public static String mailServerUsername = "";
    public static String mailServerPassword = "";
    public static String mailServerPort = "";

    public static EmailAddress toEmailAddress = new EmailAddress("");
    public static EmailAddress fromEmailAddress = new EmailAddress("");
    public static EmailAddress replyToEmailAddress = new EmailAddress("");
    public static String emailSenderName = "";
}
