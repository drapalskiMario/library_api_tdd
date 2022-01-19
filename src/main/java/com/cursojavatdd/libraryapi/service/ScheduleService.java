package com.cursojavatdd.libraryapi.service;

import com.cursojavatdd.libraryapi.entity.Loan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleService {

    private static final String CRON_LATE_LOANS = "0 0 0 1/1 * *";

    @Value("${application.email.lateloans.message}")
    private String message;

    private LoanService loanService;
    private EmailService emailService;

    public ScheduleService(LoanService loanService, EmailService emailService) {
        this.loanService = loanService;
        this.emailService = emailService;
    }

    @Scheduled(cron = CRON_LATE_LOANS)
    public void sendEmailToLateLoans() {
        List<Loan> allLateLoans = this.loanService.getAllLateLoans();
        List<String> mailList = allLateLoans
                .stream()
                .map(loan -> loan.getCustomer())
                .collect(Collectors.toList());

        this.emailService.sendEmails(mailList, message);
    }
}
