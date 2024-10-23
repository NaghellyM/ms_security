package com.GSTCP.ms_security.Services;

import com.GSTCP.ms_security.Entities.EmailContent;
import com.GSTCP.ms_security.Entities.TelegramContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestService {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${api.ms-notifications}")
    private String notification;


    public void sendEmail(EmailContent content) {
        String endpointName = "sendemail";
        String url = notification + endpointName;
        restTemplate.postForObject(url, content, String.class);

    }

    public void sendTelegram(TelegramContent content) {
        String endpointName = "sendTelegram";
        String url = notification + endpointName;
        restTemplate.postForObject(url, content, String.class);
    }
    



}
