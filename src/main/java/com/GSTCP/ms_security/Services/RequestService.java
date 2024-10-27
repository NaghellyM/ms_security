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

// Anotación para indicar que esta clase es un servicio de Spring
@Service
public class RequestService {
    // Inyección de dependencia de RestTemplate
    @Autowired
    private RestTemplate restTemplate;


    // Inyección del valor de la propiedad 'api.ms-notifications' desde el archivo de configuración
    @Value("${api.ms-notifications}")
    private String notification;

    // Método para enviar un correo electrónico
    public void sendEmail(EmailContent content) {
        // Nombre del endpoint para enviar correos electrónicos
        String endpointName = "send-email";        // Envío de la solicitud POST al endpoint con el contenido del correo
        String url = notification + endpointName;
        // Envío de la solicitud POST al endpoint con el contenido del correo
        restTemplate.postForObject(url, content, String.class);

    }
    // Método para enviar un mensaje de Telegram
    public void sendTelegram(TelegramContent content) {
        // Nombre del endpoint para enviar mensajes de Telegram
        String endpointName = "sendTelegram";
        // Construcción de la URL completa del endpoint
        String url = notification + endpointName;
        // Envío de la solicitud POST al endpoint con el contenido del mensaje de Telegram
        restTemplate.postForObject(url, content, String.class);
    }

    // Variable para almacenar la URL del servicio de notificaciones
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
        System.out.println(requestEntity);
        //retornamos el cuerpo, que es una request restTemplate(que se utiliza para hacer
        //solicitudes HTTP desde una aplicación en Java, como realizar llamadas a servicios REST.
        // Es muy util para el tema de interactuar con APIs externas)
        // de motodo post
        var response = restTemplate.exchange(notification, HttpMethod.POST, requestEntity, String.class);
        return response.getBody();
    }
    



}
