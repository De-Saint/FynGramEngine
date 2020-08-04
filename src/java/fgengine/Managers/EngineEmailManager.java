/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author mac
 */
public class EngineEmailManager {
    
    /**
     *
     * @param ToEmail
     * @param Body
     * @param Subject
     * @return
     */
    public static String SendEmail(String ToEmail, String Body, String Subject) {
        String from = "info@thewealthmarket.com";
        String result = "success";
        final String username = "info@thewealthmarket.com";//change accordingly
        final String password = "@TheWM1234";//change accordingly
//        String host = "localhost";
        String host = "thewealthmarket.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.transport.protocol", "smtp");
//        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", 25);
        props.put("mail.smtp.user", username);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.from", from);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

//        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(username, password);
//            }
//        });
//
//        try {
//            // Create a default MimeMessage object.
//            Message message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(from));
//            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(ToEmail));
//            message.setSubject(Subject);
//            message.setContent(Body, "text/html");
//            // Send message
//            Transport.send(message);
//            System.out.println("Sent message successfully....");
//            result = "success";
//        } catch (MessagingException e) {
//            throw new RuntimeException(e);
//        }
        return result;
    }

}
