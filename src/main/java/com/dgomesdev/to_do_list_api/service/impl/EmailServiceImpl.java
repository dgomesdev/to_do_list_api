package com.dgomesdev.to_do_list_api.service.impl;

import com.dgomesdev.to_do_list_api.service.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendWelcomeMail(String to, String username) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom("Dgomes Dev <" + from + ">");
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject("Welcome " + username + "!");
            String content = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <p>You have registered successfully!,</p>
                    <p>Thank you for downloading the Task List App.</p>
                    <br>
                    <p>Best regards,</p>
                    <p>Danilo Gomes<br>Dgomes Dev - Android developer</p>
                </body>
                </html>
                """;

            message.setContent(content, "text/html");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendResetPasswordMail(String to, String username, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom("Dgomes Dev <" + from + ">");
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject("Reset password");

            String content = """
                <html>
                <body style="font-family: Arial, sans-serif; line-height: 1.6;">
                    <p>Hello <strong>%s</strong>,</p>
                    <p>You can click the button below to reset yout password(valid for 15 minutes):</p>
                    <div style="margin: 10px 0;">
                        <a href="https://to-do-list-api-dgomesdev.up.railway.app/reset-password?code=%s" style="background-color: #3DDC84; color: #FFFFFF; text-decoration: none; padding: 10px 20px; border-radius: 5px; font-weight: bold;">
                            Reset password
                        </a>
                    </div>
                    <p>You can also copy and paste the code if the button doesn't work.</p>
                    <p><strong>%s</strong></p>
                    <br>
                    <p>Best regards,</p>
                    <p>Danilo Gomes<br>Dgomes Dev - Android developer</p>
                </body>
                </html>
                """.formatted(username, code, code);

            message.setContent(content, "text/html");

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

}
