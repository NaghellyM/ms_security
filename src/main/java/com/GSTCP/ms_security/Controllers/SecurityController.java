package com.GSTCP.ms_security.Controllers;

import com.GSTCP.ms_security.Models.NotificationRequest;
import com.GSTCP.ms_security.Models.Session;
import com.GSTCP.ms_security.Models.User;
import com.GSTCP.ms_security.Repositories.SessionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;

import com.GSTCP.ms_security.Services.EncryptionService;
import com.GSTCP.ms_security.Services.JwtService;
import com.GSTCP.ms_security.Services.RequestService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
//import java.util.Random;

@CrossOrigin
@RestController
@RequestMapping("/security")
public class SecurityController {

    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private SessionRepository thSessionRepository;

    @Autowired
    private EncryptionService theEncryptionService;

    @Autowired
    private JwtService theJwtService;


    
    @Autowired
    private RequestService requestService;


    //private String verificationCode; // Almacena el código de verificación
    //private Object thSessionRepository;
@PostMapping("/login")
public HashMap<String, Object> login(@RequestBody User theNewUser, final HttpServletResponse response) throws IOException {
    HashMap<String, Object> theResponse = new HashMap<>();
    
    // Verificar si el usuario existe
    User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
    if (theActualUser == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
        return theResponse;
    }

    // Verificar si la contraseña coincide
    if (theActualUser != null
                && theActualUser.getPassword()
                    .equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
        // Generar y asignar el código de verificación
        String verificationCode = theEncryptionService.generateRandomCode(6);
        theActualUser.setVerificationCode(verificationCode);
        this.theUserRepository.save(theActualUser);  // Guardar el código de verificación

        // Enviar código por correo
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setRecipient(theActualUser.getEmail());
        notificationRequest.setSubject("Código de autentificación");
        notificationRequest.setBody_html("Su código de autentificación es: <b>" + verificationCode + "</b>");
        this.requestService.sendNotification(notificationRequest);

        theResponse.put("message", "Código de autentificación enviado al correo, por favor ingresarlo");
        theResponse.put("requires_2fa", true);
        return theResponse;
    } else {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Contraseña incorrecta");
        return theResponse;
    }
}

@PostMapping("/verify-2fa")
public HashMap<String, Object> verify2FA(@RequestBody HashMap<String, String> requestBody,
                                         final HttpServletResponse response) throws IOException {
    HashMap<String, Object> theResponse = new HashMap<>();
    String token = "";

    // Obtener email y código
    String email = requestBody.get("email");
    String code = requestBody.get("code");

    // Verificar si el usuario existe
    User theActualUser = this.theUserRepository.getUserByEmail(email);
    if (theActualUser == null) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
        return theResponse;
    }

    // Verificar si el código es correcto
    if (code != null && code.equals(theActualUser.getVerificationCode())) {
        token = theJwtService.generateToken(theActualUser);

        Session newSession = new Session(token, new Date());
        newSession.setUser(theActualUser);
        this.thSessionRepository.save(newSession);

        theActualUser.setVerificationCode("");  // Limpiar el código de verificación
        this.theUserRepository.save(theActualUser);

        theActualUser.setPassword("");  // Limpiar la contraseña en la respuesta
        theResponse.put("user", theActualUser);
        theResponse.put("token", token);
        return theResponse;
    } else {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Código de verificación incorrecto");
        return theResponse;
    }
}

}

