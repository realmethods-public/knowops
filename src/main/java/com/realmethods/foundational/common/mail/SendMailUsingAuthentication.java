package com.realmethods.foundational.common.mail;

import java.util.*;
import java.util.logging.Logger;

import javax.mail.*;
import javax.mail.internet.*;


/**
 * Send email class that uses authentication
 * @author realMethods
 *
 */
public class SendMailUsingAuthentication
{
    public SendMailUsingAuthentication() {
    	// intentionally empty
    }
        
    public void sendEmail(String [] recipients, String subject, String message) throws MessagingException {
    	sendEmail( recipients, subject, message, false );
    }
    
    public void sendEmail(String [] recipients, String subject, String message, boolean htmlType ) throws MessagingException {
    	String logMsg = "Email parameters are host:" + SMTP_HOST_NAME + ", user:" + SMTP_AUTH_USER + ", pw: " + SMTP_AUTH_PWD;
    	
    	LOGGER.info(logMsg);

    	boolean debug = true;

        //Set the host smtp address
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST_NAME);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.enable", SMTP_ENABLE_SSL);
        if ( SMTP_DEBUG == null || SMTP_DEBUG.isEmpty() )
        	props.put("mail.debug", false);
        else
        	props.put("mail.debug", SMTP_DEBUG);
        
        Authenticator auth = new SMTPAuthenticator();
        Session session = Session.getDefaultInstance(props, auth);

        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(session);

        // set the from and to address...the from has to be the authuser address
        InternetAddress addressFrom = new InternetAddress(SMTP_AUTH_USER);
        msg.setFrom(addressFrom);

        InternetAddress [] addressTo = new InternetAddress[ recipients.length ];

        for (int i = 0; i < recipients.length; i++)
        {
            addressTo[ i ] = new InternetAddress(recipients[ i ]);
        }

        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, !htmlType ? "text/plain" : "text/html");
        Transport.send(msg);
    }

    /**
     * SimpleAuthenticator is used to do simple authentication
     * when the SMTP server requires it.
     */
    private class SMTPAuthenticator
        extends javax.mail.Authenticator
    {
    	@Override
        public PasswordAuthentication getPasswordAuthentication()
        {
            String username = SMTP_AUTH_USER;
            String password = SMTP_AUTH_PWD;

            return new PasswordAuthentication(username, password);
        }
    }
    
    // attributes
    private static final String SMTP_HOST_NAME 	= System.getProperty("smtp.host.name");
    private static final String SMTP_AUTH_USER 	= System.getProperty("smtp.auth.user");
    private static final String SMTP_AUTH_PWD 	= System.getProperty("smtp.auth.pwd");
    private static final String SMTP_ENABLE_SSL = System.getProperty("smtp.enable.ssl");
    private static final String SMTP_PORT 		= System.getProperty("smtp.port");
    
    
    private static final String SMTP_DEBUG 		= System.getProperty("smtp.debug");

    private static final Logger LOGGER 			= Logger.getLogger(SendMailUsingAuthentication.class.getName());	

}
