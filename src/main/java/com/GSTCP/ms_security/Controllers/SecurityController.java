package com.GSTCP.ms_security.Controllers;

import com.GSTCP.ms_security.Models.Session;
import com.GSTCP.ms_security.Models.User;
import com.GSTCP.ms_security.Repositories.SessionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;
//import com.GSTCP.ms_security.Repositories.UserRoleRepository;
import com.GSTCP.ms_security.Services.EncryptionService;
import com.GSTCP.ms_security.Services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

//Esta clase es la que se encarga de la seguridad del ciclo
@CrossOrigin //esto permite que el controlador maneje las peticiones de cualquier origen
@RestController //significa que los métodos de la clase devolverán datos directamente en el cuerpo de la respuesta HTTP en formato JSON o XML.
//aqui colocamos la ruta de la api
@RequestMapping("/api/public/security")
public class SecurityController {
    //se inyecta el repositorio de los usuarios para verificar si existe o no
    @Autowired
    private UserRepository theUserRepository;

    @Autowired
    private EncryptionService theEncryptionService;

    //este lo intanciamos para poder usarlo para generar el token
    @Autowired
    private JwtService theJwtService;

    //Hacemos la inyeccion de SessionRepository
    @Autowired
    private SessionRepository theSessionRepository;

    //@Autowired
    //private UserRoleRepository theUserRoleRepository;

    @PostMapping("/login")
    /* crea un HashMap que contendra un string y un objeto y la funcion la llamamos login
     luego va a recibir una instancia de usuario, requestBody se hace lo pega en el body de la aplocacioj */
     
    public HashMap<String,Object> login(@RequestBody User theNewUser, final HttpServletResponse response)throws IOException {
        // Crear un nuevo HashMap para almacenar la respuesta
        HashMap<String,Object> theResponse=new HashMap<>();
        //variable del toquen y asi obtenemos al usuraio ppor el correo electronico
        String token="";
        /*usuario busque por repositorio ppor correo electronico y me devuelve el usuario actual getUserEmail es un metodo
        de la interfaz UserRepository*/
        
        User theActualUser=this.theUserRepository.getUserByEmail(theNewUser.getEmail());//devuelve el usuario actual

        //hace la verificacion, el usuario existe veridique el que esta actual en la base de datos y traiga la contraseña
        // la clave llega plana y la compara pero como nno es igual la convierto a SHA256
        if(theActualUser!=null &&
           theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))){//contraseña plana y la convieto a SHA256 para hacer la verificacion si es igual
            //si eso es cierto le dice a JWT service le devuelve un token y le manda al usuario
            token=theJwtService.generateToken(theActualUser);//lo manda al JwtService para generar el token con el usruario actual, lo que dice es mandeme el usuario desde el toquen
            Session newSession = new Session(token, new Date());
            newSession.setUser(theActualUser);
            this.theSessionRepository.save(newSession);

            theActualUser.setPassword("");//aal usuario le elimio la contraseña temporal
            theResponse.put("token",token);//le colocamos el toquen y el usuario actual
            theResponse.put("user",theActualUser);//""""
            //theResponse.put("role",theActualUser.getRole());//le colocamos el rol del usuario
            //ahora hacemos la conexion de Session

            return theResponse;//y eso le devuelvo al usuario

        }else{
            //sino existe el sistema no esta autorizado
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return  theResponse;
        }

    }

} 
 
/*  SEGUNDO FACTOR DE AUTENTICACION
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

}*/

