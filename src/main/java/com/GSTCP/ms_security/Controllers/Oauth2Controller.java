package com.GSTCP.ms_security.Controllers;

import com.GSTCP.ms_security.Services.Oauth2Service;
import com.GSTCP.ms_security.Services.RequestService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {
    @SuppressWarnings("unused")
    
    @Autowired
    private RequestService theRequestService;

    @Autowired
    private Oauth2Service theOauth2Service;

    //Endpoint para iniciar autenticación con google
    @GetMapping("/google")
    //Toma como parametro la sesion hecha con google
    public RedirectView authenticateWithGoogle(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        //aqui creamos una url con la cual accederemos a la ventana
        //de autentificación con nuestro servicio
        String authUrl = theOauth2Service.getGoogleUrl(state);
        System.out.println(authUrl);
        //Ya cuando recibimos esa url, utilizamos el metodo new RedirectView
        //para redireccionar al usuario a la url (Ya como tal la ventana)
        return new RedirectView(authUrl);
    }

    // Endpoint de callback para manejar la respuesta de Google y GitHub
    //Antes cuando configuramos las APIs de Google y GitHub definiamos
    //una ruta, pues aquí vamos a configurarla
    @GetMapping("/callback/{provider}")
                                        //Google me devuelve todos los parametros que hay aquí abajo
    public ResponseEntity<?> callback(@PathVariable String provider,
                                      @RequestParam String code,
                                      @RequestParam String state,
                                      HttpSession session) {
        String sessionState = (String) session.getAttribute("oauth_state");
        if (sessionState == null || !sessionState.equals(state)) {
            return ResponseEntity.badRequest().body("Estado inválido");
        }
        //Aquí miramos de donde proviene la autenticacion
        //en este caso de google
        if ("google".equalsIgnoreCase(provider)) {
            // Intercambiar código por token
            //Lo que pasa es que obtenemos un codigo con una llave cifrada por google,
            //la cual (por obviedad) no tenemos, por lo cual le solicitamos a google
            // que nos de el token
            //Por lo cual aquí google está solicitando nuestro backend
            //Luego de que ya obtuve el token, me crea un mapa con un String y un objeto
            Map<String, Object> tokenResponse = theOauth2Service.getGoogleAccessToken(code); //Este es el codigo que cambiamos por un token de acceso
            //De este objeto obtengo el accessToken
            String accessToken = (String) tokenResponse.get("access_token");

            // Obtener información del usuario
            // Esto es por medio de llamar a google y que me de la información de ese token
            Map<String, Object> userInfo = theOauth2Service.getGoogleUserInfo(accessToken);

            System.out.println(userInfo);
            // Aquí puedes manejar la lógica de tu aplicación, como crear o buscar un usuario
            return ResponseEntity.ok(userInfo);

        } else{
            System.out.println("no se pudo");
            return ResponseEntity.badRequest().body("Proveedor no soportado.");
        }


    }


}
