
// Oauth2Controller.java
package com.GSTCP.ms_security.Controllers;

import com.GSTCP.ms_security.Services.Oauth2Service;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/oauth2")
public class Oauth2Controller {

    @Autowired
    private Oauth2Service oauth2Service;

    @GetMapping("/google")
    public RedirectView authenticateWithGoogle(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        String authUrl = oauth2Service.getGoogleUrl(state);
        System.out.println("Google Auth URL: " + authUrl);
        return new RedirectView(authUrl);
    }

    @GetMapping("/github")
    public RedirectView authenticateWithGitHub(HttpSession session) {
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);
        String authUrl = oauth2Service.getGitHubUrl(state);
        System.out.println("GitHub Auth URL: " + authUrl);
        return new RedirectView(authUrl);
    }

    @GetMapping("/callback/{provider}")
    public ResponseEntity<?> callback(
            @PathVariable String provider,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String error_description,
            HttpSession session) {

        try {
            // Check for error parameters
            if (error != null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "error", error,
                                "description", error_description
                        ));
            }

            // Validate required parameters
            if (code == null || state == null) {
                return ResponseEntity.badRequest()
                        .body("Código o estado faltante en la respuesta");
            }

            // Validate state
            String sessionState = (String) session.getAttribute("oauth_state");
            if (sessionState == null || !sessionState.equals(state)) {
                return ResponseEntity.badRequest()
                        .body("Estado inválido o expirado");
            }

            // Process based on provider
            if ("google".equalsIgnoreCase(provider)) {
                Map<String, Object> tokenResponse = oauth2Service.getGoogleAccessToken(code);
                if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                    return ResponseEntity.badRequest()
                            .body("No se pudo obtener el token de acceso de Google");
                }

                String accessToken = (String) tokenResponse.get("access_token");
                Map<String, Object> userInfo = oauth2Service.getGoogleUserInfo(accessToken);
                return ResponseEntity.ok(userInfo);

            } else if ("github".equalsIgnoreCase(provider)) {
                Map<String, Object> tokenResponse = oauth2Service.getGitHubAccessToken(code);
                if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                    return ResponseEntity.badRequest()
                            .body("No se pudo obtener el token de acceso de GitHub");
                }

                String accessToken = (String) tokenResponse.get("access_token");
                Map<String, Object> userInfo = oauth2Service.getGitHubUserInfo(accessToken);
                return ResponseEntity.ok(userInfo);

            } else {
                return ResponseEntity.badRequest()
                        .body("Proveedor no soportado: " + provider);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error durante la autenticación: " + e.getMessage());
        } finally {
            // Clean up the session state
            session.removeAttribute("oauth_state");
        }
    }
}