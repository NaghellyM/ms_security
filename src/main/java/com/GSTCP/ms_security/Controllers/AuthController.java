package com.GSTCP.ms_security.Controllers;


import com.GSTCP.ms_security.Entities.EmailContent;
import com.GSTCP.ms_security.Entities.Recipient;
import com.GSTCP.ms_security.Entities.TelegramContent;
import com.GSTCP.ms_security.Services.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private RequestService requestService;

    @PostMapping("/login")
    public ResponseEntity<String> sendEmail(@RequestBody EmailContent emailcontent) {
        // Aquí emailContent.getRecipients() es una lista de objetos Recipient
        for (Recipient recipient : emailcontent.getRecipients()) {
            System.out.println("Recipient name: " + recipient.getName());
            System.out.println("Recipient email: " + recipient.getEmail());
        }

        // Llamar al servicio para enviar el email
        requestService.sendEmail(emailcontent);

        return new ResponseEntity<>("Email sent", HttpStatus.OK);
    }

    @PostMapping("/telegram")
    public ResponseEntity<String> sendTelegram(@RequestBody TelegramContent telegramcontent) {
        System.out.println("Telegram message: " + telegramcontent.getMessage());
        // Llamar al servicio para enviar el mensaje de Telegram
        requestService.sendTelegram(telegramcontent);

        return new ResponseEntity<>("Telegram sent", HttpStatus.OK);
    }


}
