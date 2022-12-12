package com.buxuesong.account.infrastructure.general.utils;

import com.buxuesong.account.infrastructure.general.config.MailConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
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
        // Config the sender
        InternetAddress form = new InternetAddress(mailConfig.getUsername());
        MimeMessageHelper msg = new MimeMessageHelper(message, true);
        File file = new File(mailConfig.getDbRealPath());
        msg.addAttachment("stock-and-fund.db", file);
        msg.setFrom(form);
        message.setFrom(form);
        // 邮件接收者
        InternetAddress[] iaToList = InternetAddress.parse(mailTo);
        msg.setTo(iaToList);
        // 邮件主题
        msg.setSubject(subject);
        // 邮件正文
        msg.setText(content.toString());
        // 邮件发送时间
        msg.setSentDate(new Date());
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
