package com.vuviet.jobhunter.controller;

import com.vuviet.jobhunter.service.EmailService;
import com.vuviet.jobhunter.service.SubscriberService;
import com.vuviet.jobhunter.util.annotation.ApiMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmailController {
    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send email")
//    @Scheduled(cron = )
    public String sendSimpleEmail(){
//        this.emailService.sendSimpleEmail();
//        this.emailService.sendEmailSync("vuvip2002z@gmail.com","test send email","<h1><b>Hello world</b></h1>",false,true);
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }
}
