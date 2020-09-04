/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fgengine.Managers;

import com.sendgrid.Content;
import com.sendgrid.Email;
import com.sendgrid.Mail;
import com.sendgrid.Method;
import com.sendgrid.Personalization;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import fgengine.Tables.Tables;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.Map;

/**
 *
 * @author mac
 */
public class EngineEmailManager {

    public static String PasswordResetEmail(String ToEmail, String Subject, String Code, String UserName, String Option, String UserType) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, IOException {
        String result = "failed";
        Mail mail = prepareEmail(ToEmail, Subject, Code, UserName, Option, UserType);
        String key = GetSendGridKey();
        SendGrid sg = new SendGrid(key);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            int code = response.getStatusCode();
            System.out.println(response.getBody());
            String body = response.getBody();
            System.out.println(response.getHeaders());
            Map header = response.getHeaders();
            Map me = header;
            result = "success";
        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.getMessage());
            throw ex;
        }
        return result;
    }

    public static String GetSendGridKey() throws ClassNotFoundException, SQLException, UnsupportedEncodingException {
        String result = DBManager.GetString(Tables.ParametersTable.SendGridKey, Tables.ParametersTable.Table, "where " + Tables.ParametersTable.ID + " = " + 1);
        return result;
    }

    public static Mail prepareEmail(String ToEmail, String Subject, String Code, String UserName, String Option, String UserType) {

        Mail mail = new Mail();
        Email fromEmail = new Email();
        fromEmail.setEmail("support@fyngram.com");
        fromEmail.setName("Fyngram");
        mail.setFrom(fromEmail);
        mail.setSubject(Subject);

        Personalization personalization = new Personalization();
        Email toEmail = new Email();
        toEmail.setEmail(ToEmail);
        toEmail.setName(UserName);
        personalization.addTo(toEmail);

        if (Option.equals("Password")) {
            personalization.addSubstitution("%name%", UserName);
            personalization.addSubstitution("%code%", Code);
            mail.addPersonalization(personalization);
            mail.setTemplateId("7a6f7866-464d-48e1-9792-5e310ad08977");
        } else if (Option.equals("Registration")) {
            personalization.addSubstitution("%name%", UserName);
            personalization.addSubstitution("%type%", UserType);
            personalization.addSubstitution("%code%", Code);
            mail.addPersonalization(personalization);
            mail.setTemplateId("cb6f6e9d-9367-47aa-9b7c-f6cb4fa17183");
        }

        Email replyTo = new Email();
        replyTo.setName("Fyngram");
        replyTo.setEmail("support@fyngram.com");
        mail.setReplyTo(replyTo);
        return mail;
    }

    public static String SendEmail(String ToEmail, String Body, String Subject) throws ClassNotFoundException, SQLException, UnsupportedEncodingException, IOException {
        String result = "failed";

        Email from = new Email("support@fyngram.com");
        Email to = new Email(ToEmail);
        Content content = new Content("text/plain", Body);
        Mail mail = new Mail(from, Subject, to, content);
        String key = GetSendGridKey();

        Request request = new Request();
        try {
            SendGrid sg = new SendGrid(key);
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            int code = response.getStatusCode();
            System.out.println(response.getBody());
            String body = response.getBody();
            System.out.println(response.getHeaders());
            Map header = response.getHeaders();
            Map me = header;
            result = "success";
        } catch (IOException ex) {
            ex.getStackTrace();
            System.out.println(ex.getMessage());
            throw ex;
        }
        return result;
    }
}
