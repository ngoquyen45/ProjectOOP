package com.viettel.backend.service.common;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;

import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

import com.viettel.backend.domain.Client;

@Service
public class MailService extends AbstractService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private VelocityEngine velocityEngine;
    
    @Async
    public void sendEmail(final String toEmail) { 
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                message.setFrom("trungkh@viettel.com.vn");
                message.setTo(toEmail);
                Map<String, Object> model = new HashMap<String, Object>();
                Client client = new Client();
                client.setName("Test");
                model.put("client", client);
                String text = VelocityEngineUtils.mergeTemplateIntoString(
                        velocityEngine, "mail-template/template.vm", "UTF-8", model);
                message.setText(text, true);
            }
        };
        this.mailSender.send(preparator);
    }
    
}
