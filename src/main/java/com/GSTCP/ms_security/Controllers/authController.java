package com.GSTCP.ms_security.Controllers;


import com.GSTCP.ms_security.Services.oauth2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/auth")
public class authController {
    @Autowired
    private oauth2Service oauth2Service;

    //Generamos un estado único para cada solicitud de redirección a google y github
    private String state;

    //creamos el endPoint para redireccionar a google
    @GetMapping("/google")
    public RedirectView authenticateWithGoogle(){ //Recibo la sesion del navegador, en este caso de google
        this.state = UUID.randomUUID().toString(); //Genero un estado único para la solicitud de redirección a google
        String authUrl = oauth2Service.getGoogleAuthUrl(this.state);
        return new RedirectView(authUrl); //Url a la que quiero red
    }

    @GetMapping("/github")
    public RedirectView authenticateWithGitHub(){ //Recibo la sesion del navegador, en este caso de google
        this.state = UUID.randomUUID().toString(); //Genero un estado único para la solicitud de redirección a github
        String authUrl = oauth2Service.getGitHubAuthUrl(state); //Genero la url
        return new RedirectView(authUrl); //Url a la que quiero redireccionar
    }

    //manejamos la redirección cuando se haya logueado
    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> callback(@PathVariable String provider, @RequestParam String code, @RequestParam String state) {
        if (this.state == null || !this.state.equals(state)) {
            return ResponseEntity.badRequest().body("Estado inválido");
        }

        if ("google".equalsIgnoreCase(provider)) {
            //Procedemos a intercambiar el code por un token de acceso.
            Map<String, Object> tokenResponse = oauth2Service.getGoogleAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");

            Map<String, Object> userInfo = oauth2Service.getGoogleUserInfo(accessToken);
            // Aquí puedes manejar la lógica de tu aplicación

            return ResponseEntity.ok(userInfo);
        } else if ("github".equalsIgnoreCase(provider)) {
            //Procedemos a intercambiar el code por un token de acceso.
            Map<String, Object> tokenResponse = oauth2Service.getGitHubAccessToken(code);
            String accessToken = (String) tokenResponse.get("access_token");

            Map<String, Object> userInfo = oauth2Service.getGithubUserInfo(accessToken);
            return ResponseEntity.ok(userInfo);
        } else {
            return null;
        }

    }
}

