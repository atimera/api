package com.atimera.api.services.impl;

import com.atimera.api.services.EmailService;
import com.sun.mail.smtp.SMTPTransport;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import static com.atimera.api.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.CC;
import static javax.mail.Message.RecipientType.TO;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {


    @Override
    public void sendNewPasswordEmail(String firstName, String password, String email)
            throws MessagingException {
        Message message = createEmail(firstName, password, email);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession()
                .getTransport(SIMPLE_EMAIL_TRANSFER_PROTOCOL);
        //smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        //sendmail();
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createEmail(String firstName, String password, String email) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(EMAIL_FROM));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        message.setSubject(EMAIL_SUBJECT);
        message.setText("Bonjour "
                + firstName + "\n \n Voici votre mot de passe: "
                + password + " \n \n Votre équipe support");
        message.setSentDate(new Date());
        message.saveChanges();

        return message;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.setProperty(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.setProperty(SMTP_AUTH, "true");
        //properties.setProperty(SMTP_PORT, SMTP_DEFAULT_PORT);
        properties.setProperty(SMTP_STARTTLS_ENABLE, "true");
        properties.setProperty(SMTP_STARTTLS_REQUIRED, "true");

        properties.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, null);
    }

    private void sendmail() throws AddressException, MessagingException, IOException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                //return new PasswordAuthentication("tutorialspoint@gmail.com", "<your password>");
                //return new PasswordAuthentication("atimera.dev@gmail.com", "DEVlaenzo2065368.");
                return new PasswordAuthentication("amdiatoutimera@gmail.com", "ajgbksmswdzpqcpa");
            }
        });
        Message msg = new MimeMessage(session);
        //msg.setFrom(new InternetAddress("tutorialspoint@gmail.com", false));
        //msg.setFrom(new InternetAddress("atimera.dev@gmail.com", false));
        msg.setFrom(new InternetAddress("amdiatoutimera@gmail.com", false));

        //msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("tutorialspoint@gmail.com"));
        //msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("atimera.dev@gmail.com"));
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse("amdiatoutimera@gmail.com"));
        msg.setSubject("Tutorials point email");
        msg.setContent("Tutorials point email", "text/html");
        msg.setSentDate(new Date());

        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent("Tutorials point email", "text/html");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
        MimeBodyPart attachPart = new MimeBodyPart();

        attachPart.attachFile("C:\\Users\\amdia\\Downloads\\easter.png");
        multipart.addBodyPart(attachPart);
        msg.setContent(multipart);
        Transport.send(msg);
    }
}
