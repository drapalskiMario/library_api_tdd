package com.cursojavatdd.libraryapi.service.impl;

import com.cursojavatdd.libraryapi.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    @Value("${application.email.default-emailsender}")
    private String emailSender;

    private JavaMailSender javaMailSender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmails(List<String> mailList, String message) {
        String[] mails = mailList.toArray(new String[mailList.size()]);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(emailSender);
        mailMessage.setSubject("Livro com empréstimo atrasado");
        mailMessage.setText(message);
        mailMessage.setTo(mails);

        javaMailSender.send(mailMessage);
    }
}
