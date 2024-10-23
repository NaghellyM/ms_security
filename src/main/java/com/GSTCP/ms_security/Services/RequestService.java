package com.GSTCP.ms_security.Services;

import com.GSTCP.ms_security.Entities.EmailContent;
import com.GSTCP.ms_security.Entities.TelegramContent;
import com.GSTCP.ms_security.Models.NotificationRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
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
        private String notificationServiceUrl;

        //funcion para enviar notificaciones, que recibe una request de notificacion del modelo
    public String sendNotification(NotificationRequest notificationRequest) {
        //Esto es de los headers del postman
        HttpHeaders headers = new HttpHeaders();
        //Esto es para definir que tipo de contenido va a tener mi carta para el ms de notificaciones
        //en este caso, json
        headers.setContentType(MediaType.APPLICATION_JSON);
        //Creamos una nueva request http con la notificacion
        HttpEntity<NotificationRequest> requestEntity = new HttpEntity<>(notificationRequest, headers);

        //retornamos el cuerpo, que es una request restTemplate(que se utiliza para hacer
        //solicitudes HTTP desde una aplicación en Java, como realizar llamadas a servicios REST.
        // Es muy util para el tema de interactuar con APIs externas)
        // de motodo post
        var response = restTemplate.exchange(notificationServiceUrl, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }
    



}
