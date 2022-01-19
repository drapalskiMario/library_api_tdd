package com.cursojavatdd.libraryapi.service;

import java.util.List;

public interface EmailService {
    void sendEmails(List<String> mailList, String message);
}
