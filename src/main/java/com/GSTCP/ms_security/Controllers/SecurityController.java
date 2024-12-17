package com.GSTCP.ms_security.Controllers;

import com.GSTCP.ms_security.Models.NotificationRequest;
import com.GSTCP.ms_security.Models.Permission;
import com.GSTCP.ms_security.Models.Session;
import com.GSTCP.ms_security.Models.User;
import com.GSTCP.ms_security.Repositories.SessionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;

import com.GSTCP.ms_security.Services.EncryptionService;
import com.GSTCP.ms_security.Services.JwtService;
import com.GSTCP.ms_security.Services.RequestService;
import com.GSTCP.ms_security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
//import java.util.Random;
import java.util.Scanner;

@CrossOrigin
@RestController
@RequestMapping("/api/public/security")
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

    @Autowired
    private ValidatorsService theValidatorsService;
    @PostMapping("/login")
    public HashMap<String,Object> login(@RequestBody User theNewUser,
                                        final HttpServletResponse response)throws IOException {
        HashMap<String,Object> theResponse=new HashMap<>();
        String token="";
        User theActualUser=this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        if(theActualUser!=null &&
                theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))){
            token=theJwtService.generateToken(theActualUser);
            theActualUser.setPassword("");
            theResponse.put("token",token);
            theResponse.put("user",theActualUser);
            return theResponse;
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return  theResponse;
        }

    }
    // private String verificationCode; // Almacena el código de verificación
    // private Object thSessionRepository;

    /*@PostMapping("/login1")
    public HashMap<String, Object> login(@RequestBody User theNewUser, final HttpServletResponse response)
            throws IOException {
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
            this.theUserRepository.save(theActualUser); // Guardar el código de verificación

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
    }*/


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

            theActualUser.setVerificationCode(""); // Limpiar el código de verificación
            this.theUserRepository.save(theActualUser);

            theActualUser.setPassword(""); // Limpiar la contraseña en la respuesta
            theResponse.put("user", theActualUser);
            theResponse.put("token", token);



            return theResponse;
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Código de verificación incorrecto");
            return theResponse;
        }
    }
    


//     //! LOGIN BÁSICO -
@PostMapping("/login")
public HashMap<String, Object> loginWithout2FA(@RequestBody User theNewUser, final HttpServletResponse response)
        throws IOException {
    HashMap<String, Object> theResponse = new HashMap<>();

    // Obtener el token de reCAPTCHA del objeto User
    String recaptchaToken = theNewUser.getCaptchaToken();
    System.out.println("Token de reCAPTCHA: " + recaptchaToken);

    // Validar el token de reCAPTCHA
    boolean isCaptchaValid = validateRecaptcha(recaptchaToken);
    if (!isCaptchaValid) {
        System.out.println("Problema con el captcha");
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        theResponse.put("message", "Captcha inválido.");
        return theResponse;
    }

    // Verificar si el usuario existe
    User theActualUser = this.theUserRepository.getUserByEmail(theNewUser.getEmail());
    if (theActualUser == null) {
        System.out.println("Usuario no encontrado");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuario no encontrado");
        return theResponse;
    }

    // Verificar si la contraseña coincide
    if (theActualUser.getPassword()
            .equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))) {
        // Generar token directamente
        String token = theJwtService.generateToken(theActualUser);

        // Crear sesión y asociarla al usuario
        Session newSession = new Session(token, new Date());
        newSession.setUser(theActualUser);
        this.thSessionRepository.save(newSession);

        // Limpiar la contraseña del usuario antes de enviarlo como respuesta
        theActualUser.setPassword("");
        theResponse.put("user", theActualUser);
        theResponse.put("token", token);
        return theResponse;
    } else {
        System.out.println("Contraseña incorrecta");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Contraseña incorrecta");
        return theResponse;
    }
}

// Método para validar el reCAPTCHA
public static boolean validateRecaptcha(String recaptchaToken) {
    // String RECAPTCHA_SECRET = "6LdDHpsqAAAAALxj4nWrV5XO1pZsFZP-EEVuQnWv"; // Configura tu clave secreta aquí
        String RECAPTCHA_SECRET = "6LfZi50qAAAAAELwmd35fymbEvJr4Ydr5l6w3m23"; // Configura tu clave secreta aquí

    String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    if (recaptchaToken == null || recaptchaToken.isEmpty()) {
        System.out.println("El token de reCAPTCHA está vacío o es nulo");
        return false;
    }

    try {
        // Crear conexión HTTP
        URL url = new URL(RECAPTCHA_VERIFY_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        // Crear el cuerpo de la solicitud
        String postData = "secret=" + RECAPTCHA_SECRET + "&response=" + recaptchaToken;

        // Escribir datos en el cuerpo
        try (OutputStream os = conn.getOutputStream()) {
            os.write(postData.getBytes());
            os.flush();
        }

        // Leer la respuesta
        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }

                // Parsear la respuesta JSON
                String jsonResponse = response.toString();
                System.out.println("Respuesta de Google: " + jsonResponse);

                // Verificar el campo "success" en la respuesta JSON
                return jsonResponse.contains("\"success\": true");
            }
        } else {
            System.out.println("Error en la respuesta del servidor de Google. Código: " + responseCode);
            return false;
        }
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    // creamos un nuevo empoint donde se conecta con
    // /api/public/security/permissions-validation en
    // ojo: es un metodo post porque si uno manda por get no se puede enviar un body
    // entonces pailas importante para la vida laboral

    // sustentacion cual es el usuario que mas a utulizado el sevicio de peliculas
    // sustentacion que enpoint mas a usado el rol con el permiso de administracion
    @PostMapping("/permissions-validation")
    // boolean puede entrar o no true o false
    public boolean permissionsValidation(final HttpServletRequest request,
            @RequestBody Permission thePermission) {
        // venir y hacer el cruce busca el rol del usuario y verifica si tiene
        // permiso(mando la request donde viene el token del usuario, mando la url y el
        // metodo al que quiero acceder la cual es el que manda el cliente)
        boolean success = this.theValidatorsService.validationRolePermission(request, thePermission.getUrl(),
                thePermission.getMethod());
        return success;
    }

}
