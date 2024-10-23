package com.GSTCP.ms_security.Controllers;
import com.GSTCP.ms_security.Models.NotificationRequest;
import com.GSTCP.ms_security.Models.User;
//import com.GSTCP.ms_security.Repositories.SessionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;
import com.GSTCP.ms_security.Services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.GSTCP.ms_security.Services.RequestService;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/api/users") //Mucho ojo con esto Importante colocarlo porque es la ruta de acceso

public class UsersController {

    @Autowired//fabricas de objetos
    private UserRepository theUserRepository; //referencia a la clase de UserRepository porque no se hace una intancia de una clase interfaz
    
    @Autowired
    private RequestService requestService;

    /*hago la
    inyeccion de
    EncryptionService y
    poder asi
    usar el
    servicio y
    con @Autowired le
    digo que
    lo inyecte el EncruptionService*/

    @Autowired
    private EncryptionService theEncryptionService;

    //@Autowired
    //private SessionRepository theSessionRepository;

    @GetMapping("")
    public List<User> find() {
        return this.theUserRepository.findAll();
    }

    @GetMapping("{id}")
    public User findById(@PathVariable String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        return theUser;
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        /*Antes de meterla en la base de datos transformamos la contraseña que viene plana y hacemos que al nuevo usuario cambien
         la contraseña depuesS vamos al theEncryptionService
        y yllamamos al metodo convertSHA256 convertimos la contraseña a SHA256
         pero vamos al useario y con el get lo optenemos
         */
        newUser.setPassword(this.theEncryptionService.convertSHA256((newUser.getPassword())));
        /*hacemos un retorno en la cual a la base
        de datos de theUserRepository le vamos a guardar el nuevo usuario*/
        return this.theUserRepository.save(newUser);
    }

    @PutMapping("{id}")
    public User update(@PathVariable String id, @RequestBody User newUser) {
        User actualUser = this.theUserRepository.findById(id).orElse(null);
        if (actualUser != null) {
            actualUser.setName(newUser.getName());
            actualUser.setEmail(newUser.getEmail());
            //aqui hacemos la actualizacion de la contraseña pero ya en formato SHA256
            actualUser.setPassword((this.theEncryptionService.convertSHA256(newUser.getPassword())));
            this.theUserRepository.save(actualUser);
            return actualUser;
        } else {
            return null;
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        if (theUser != null) {
            this.theUserRepository.delete(theUser);
        }
    }

    @PostMapping("/new-password")
    public ResponseEntity sendNewPassword(@RequestBody User theNewUser) {
        //Obtenemos el usuario por medio del email con el repo
        User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());

        System.out.println(theActualUser.getEmail());

        // Generar un código aleatorio de 10 caracteres
        String newPassword = this.theEncryptionService.generateRandomCode(10);
        System.out.println(newPassword);
        //Le asignamos la contraseña cifrada al usuario
        theActualUser.setPassword(theEncryptionService.convertSHA256(newPassword));
        //guardamos el usuario
        this.theUserRepository.save(theActualUser);

        System.out.println(theActualUser.getPassword());
        // en esta linea creamos el cuerpo del correo
        String bodyHtml = "<!DOCTYPE html>" +
                "<html lang='es'>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body {font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;}" +
                ".card {background-color: white; border-radius: 8px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); max-width: 600px; margin: 0 auto; padding: 20px; text-align: center;}" +
                ".card h1 {color: #333; font-size: 24px;}" +
                ".card p {color: #666; font-size: 16px;}" +
                ".card h2 {color: #007BFF; font-size: 20px; margin-top: 20px;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='card'>" +
                "<h1>Tu nueva contraseña</h1>" +
                "<p>Esta es tu nueva contraseña para el sistema:</p>" +
                "<h2>" + newPassword + "</h2>" +
                "</div>" +
                "</body>" +
                "</html>";

        // Creamos una solicitud de notificación
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setSubject("Solicitud de nueva contraseña");
        notificationRequest.setRecipient(theActualUser.getEmail());
        notificationRequest.setBody_html(bodyHtml);

        // Enviamos el correo usando el servicio
        String response = this.requestService.sendNotification(notificationRequest);

        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}