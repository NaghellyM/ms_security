package com.GSTCP.ms_security.Controllers;


import com.GSTCP.ms_security.Services.Oauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private Oauth2Service oauth2Service;

    //Generamos un estado único para cada solicitud de redirección a google y github
    private String state;

    //creamos el endPoint para redireccionar a google
    @GetMapping("/google")
    public RedirectView authenticateWithGoogle(){ //Recibo la sesion del navegador, en este caso de google
        // Genera un estado único para la solicitud de redirección a Google
        this.state = UUID.randomUUID().toString();
        // Obtiene la URL de autenticación de Google//Genero un estado único para la solicitud de redirección a google
        String authUrl = oauth2Service.getGoogleAuthUrl(this.state);
        // Redirecciona a la URL de autenticación de Google
        return new RedirectView(authUrl);
    }

    @GetMapping("/github")
    public RedirectView authenticateWithGitHub(){ //Recibo la sesion del navegador, en este caso de google
        this.state = UUID.randomUUID().toString(); //Genero un estado único para la solicitud de redirección a github
        String authUrl = oauth2Service.getGitHubAuthUrl(state); //Genero la url
        return new RedirectView(authUrl); //Url a la que quiero redireccionar
    }

    // Maneja la redirección cuando el usuario se haya autenticado
    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> callback(@PathVariable String provider, @RequestParam String code, @RequestParam String state) {
        // Verifica que el estado sea válido
        if (this.state == null || !this.state.equals(state)) {
            //La línea completa devuelve este ResponseEntity al cliente, indicando que la solicitud fue inválida.
            return ResponseEntity.badRequest().body("Estado inválido");
        }
        // Si el proveedor es Google
        if ("google".equalsIgnoreCase(provider)) {
            //Procedemos a intercambiar el code por un token de acceso.
            Map<String, Object> tokenResponse = oauth2Service.getGoogleAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");
            // Obtiene la información del usuario de Google
            Map<String, Object> userInfo = oauth2Service.getGoogleUserInfo(accessToken);
            return ResponseEntity.ok(userInfo);
        } else if ("github".equalsIgnoreCase(provider)) {
            //Procedemos a intercambiar el code por un token de acceso.
            Map<String, Object> tokenResponse = oauth2Service.getGitHubAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");
            // Obtiene la información del usuario de GitHub
            Map<String, Object> userInfo = oauth2Service.getGithubUserInfo(accessToken);
            return ResponseEntity.ok(userInfo); //devuelve este ResponseEntity al cliente, indicando que la solicitud fue exitosa y proporcionando la información del usuario.
        } else {
            return null; //retorna null
        }

    }
}

