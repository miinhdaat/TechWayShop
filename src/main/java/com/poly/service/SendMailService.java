package com.poly.service;

import javax.mail.MessagingException;

import com.poly.entity.MailInfo;

import java.io.IOException;

public interface SendMailService {

	void run();

	void queue(String to, String subject, String body);

	void queue(MailInfo mail);

	void send(MailInfo mail) throws MessagingException, IOException;
}
