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

@CrossOrigin//voy a mandar preguntas desde mi cumputaador a mi mismo computador
@RestController//gestionar todo lo que son apis
@RequestMapping("/users") //Mucho ojo con esto Importante colocarlo porque es la ruta de acceso

public class UsersController {

    @Autowired//fabricas de objetos cuando arranque el proyecto crea el objeto
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

    @GetMapping("{id}") // Mapea las solicitudes HTTP GET a /users/{id} a este método
    public User findById(@PathVariable String id) { // Define un método que toma un parámetro de ruta 'id'
        User theUser = this.theUserRepository.findById(id).orElse(null);// Busca un usuario por su ID en el repositorio, devuelve null si no se encuentra
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

    @PutMapping("{id}")// Mapea las solicitudes HTTP PUT a /users/{id} a este método
    public User update(@PathVariable String id, @RequestBody User newUser) {// Define un método que toma un parámetro de ruta 'id' y un cuerpo de solicitud 'newUser'
        User actualUser = this.theUserRepository.findById(id).orElse(null);// Busca un usuario por su ID en el repositorio, devuelve null si no se encuentra
        if (actualUser != null) {// Verifica si el usuario actual existe
            actualUser.setName(newUser.getName()); // Actualiza el nombre del usuario actual con el nuevo nombre
            actualUser.setEmail(newUser.getEmail());// Actualiza el email del usuario actual con el nuevo email
            //aqui hacemos la actualizacion de la contraseña pero ya en formato SHA256
            actualUser.setPassword((this.theEncryptionService.convertSHA256(newUser.getPassword()))); // Guarda el usuario actualizado en el repositorio
            this.theUserRepository.save(actualUser); // Guarda el usuario actualizado en el repositorio
            return actualUser; // Retorna el usuario actualizado
        } else {// Si el usuario no existe
            return null;  // Retorna null
        }
    }

    @DeleteMapping("{id}") // Mapea las solicitudes HTTP DELETE a /users/{id} a este método
    public void delete(@PathVariable String id) { // Define un método que toma un parámetro de ruta 'id'
        User theUser = this.theUserRepository.findById(id).orElse(null); // Busca un usuario por su ID en el repositorio, devuelve null si no se encuentra
        if (theUser != null) { // Verifica si el usuario existe
            this.theUserRepository.delete(theUser); // Elimina el usuario del repositorio
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
                "body {font-family: 'Helvetica', 'Arial', sans-serif; background-color: #f0f2f5; padding: 20px; margin: 0;}" +
                ".card {background-color: #ffffff; border-radius: 10px; box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1); max-width: 600px; margin: 0 auto; padding: 40px; text-align: center;}" +
                ".card h1 {color: #2d3748; font-size: 26px; font-weight: bold; margin-bottom: 20px;}" +
                ".card p {color: #4a5568; font-size: 18px; line-height: 1.6; margin-bottom: 30px;}" +
                ".card h2 {color: #38a169; font-size: 22px; margin-top: 20px; background-color: #f0fff4; padding: 15px; border-radius: 5px; display: inline-block;}" +
                ".footer {color: #718096; font-size: 14px; margin-top: 40px;}" +
                ".footer a {color: #3182ce; text-decoration: none;}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='card'>" +
                "<h1>Tu nueva contraseña</h1>" +
                "<p>Hola, hemos generado una nueva contraseña para tu cuenta. Utiliza la siguiente contraseña para acceder al sistema. Recuerda cambiarla después de iniciar sesión.</p>" +
                "<h2>" + newPassword + "</h2>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Si no solicitaste este cambio, por favor <a href='#'>contacta a soporte</a>.</p>" +
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