package de.solidsearch.bot.utils;

import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service("mailService")
public class MailService {
     
    @Autowired
    private JavaMailSenderImpl mailSender;
     
    public void sendMail(String from, String to, String bcc, String subject, String body) {
         
        SimpleMailMessage message = new SimpleMailMessage();
          
        message.setFrom(from);
        message.setTo(to);
        if (bcc != null)
        message.setBcc(bcc);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
         
    }
    
    public void sendHTMLMail(String from, String[] to, String bcc, String subject, String htmlBody) {
        
    	MimeMessage mimeMessage = mailSender.createMimeMessage();
    	MimeMessageHelper helper;
		try
		{
			helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			mimeMessage.setHeader("Content-Type", "text/html; charset=utf-8");
			mimeMessage.setContent(htmlBody, "text/html; charset=utf-8");
	    	helper.setTo(to);
	    	helper.setSubject(MimeUtility.encodeText(subject, "utf-8", "B"));
	    	helper.setFrom(from);
	    	mailSender.send(mimeMessage);
	    	
		} catch (Exception e)
		{
			e.printStackTrace();
		}
         
    }
    
     
}
