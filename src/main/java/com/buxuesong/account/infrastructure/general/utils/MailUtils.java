package com.buxuesong.account.infrastructure.general.utils;

import com.buxuesong.account.infrastructure.general.config.MailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Date;
import java.util.Properties;

@Component
@RequiredArgsConstructor
public class MailUtils {

    private final MailConfig mailConfig;

    /**
     * Common send email
     *
     * @param subject
     * @param content
     * @throws Exception
     */
    public void sendMail(String subject, String content, String mailTo) throws Exception {
        MimeMessage message = createMail();
        BodyPart messageBodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        // Config the sender
        InternetAddress form = new InternetAddress(mailConfig.getUsername());
        message.setFrom(form);
        // 配置邮件
        InternetAddress[] iaToList = InternetAddress.parse(mailTo);
        message.setRecipients(Message.RecipientType.TO, iaToList);
        message.setSentDate(new Date());
        message.setSubject(subject);
        message.setText(content.toString());
        // 设置邮件内容为html
        messageBodyPart.setContent(content.toString(), "text/html;charset=utf-8");
        multipart.addBodyPart(messageBodyPart);
        // 增加db文件附件发送
        MimeBodyPart mailArchieve = new MimeBodyPart();
        FileDataSource fds = new FileDataSource(mailConfig.getDbRealPath());
        mailArchieve.setDataHandler(new DataHandler(fds));
        mailArchieve.setFileName(MimeUtility.encodeText(fds.getName(), "UTF-8", "B"));
        multipart.addBodyPart(mailArchieve);

        message.setContent(multipart);
        // Start to send
        Transport.send(message);
    }

    /**
     * Common send email
     *
     * @param subject
     * @param content
     * @throws Exception
     */
    public void sendMailNoArchieve(String subject, String content) throws Exception {
        MimeMessage message = createMail();
        BodyPart messageBodyPart = new MimeBodyPart();
        Multipart multipart = new MimeMultipart();
        // Config the sender
        InternetAddress form = new InternetAddress(mailConfig.getUsername());
        message.setFrom(form);
        // 配置邮件
        InternetAddress[] iaToList = InternetAddress.parse(mailConfig.getToAddress());
        message.setRecipients(Message.RecipientType.TO, iaToList);
        message.setSentDate(new Date());
        message.setSubject(subject);
        message.setText(content.toString());
        // 设置邮件内容为html
        messageBodyPart.setContent(content.toString(), "text/html;charset=utf-8");
        multipart.addBodyPart(messageBodyPart);

        message.setContent(multipart);
        // Start to send
        Transport.send(message);
    }

    /**
     * Send warning email to BFF BE
     *
     * @param subject
     * @param content
     * @throws Exception
     */
    public void sendMail(String subject, String content) throws Exception {
        sendMail(subject, content, mailConfig.getToAddress());
    }

    private MimeMessage createMail() {

        final Properties props = new Properties();
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.port", mailConfig.getSmtpPort());
        props.setProperty("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", mailConfig.getSmtpHost());
        props.put("mail.user", mailConfig.getUsername());
        props.put("mail.password", mailConfig.getPassword());

        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mailConfig.getUsername(),
                    mailConfig.getPassword());
            }
        };
        // Use the properties to create mail session
        Session mailSession = Session.getInstance(props, authenticator);
        // Create email information
        // If we open debug mode
//        mailSession.setDebug(true);
        MimeMessage message = new MimeMessage(mailSession);

        return message;
    }
}
