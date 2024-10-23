package com.GSTCP.ms_security.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
public class Oauth2Service {

    @Autowired
    private RestTemplate restTemplate;
 
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${google.auth.uri}")
    private String googleAuthUri;

    @Value("${google.token.uri}")
    private String googleTokenUri;

    @Value("${google.user.info.uri}")
    private String googleUserInfoUri;

    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

            @Value("${github.auth.uri}")
    private String githubAuthUri;

    @Value("${github.token.uri}")
    private String githubTokenUri;

    @Value("${github.user.info.uri}")
    private String githubUserInfoUri;

    // Genera la URL de autenticación para Google
    public String getGoogleAuthUrl(String state) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(googleAuthUri)
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email")  // Scopes correctos
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent");

        return uriBuilder.toUriString();
    }

    //Método para convertir el codigo en un token

    public Map<String, Object> getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(googleTokenUri, request, Map.class);

        return response.getBody();
    }

    //Obtener informacion del usuario a partir del token

    public Map<String, Object> getGoogleUserInfo(String accessToken) {
        // Crear un objeto HttpHeaders para enviar el token de acceso en la cabecera
        HttpHeaders headers = new HttpHeaders();
        // Establecer el encabezado de autorización como "Bearer" con el token de acceso
        headers.setBearerAuth(accessToken);
        // Crear una entidad HTTP (request) sin cuerpo, solo con las cabeceras
        HttpEntity<Void> request = new HttpEntity<>(headers);
        // Realizar la solicitud GET a la URL de Google para obtener la información del usuario
        ResponseEntity<Map> response = restTemplate.exchange(
                googleUserInfoUri,   // URL para obtener la información del usuario de Google
                HttpMethod.GET,      // Método HTTP que será GET en este caso
                request,             // La entidad HTTP con las cabeceras (incluyendo el token)
                Map.class            // La clase esperada en la respuesta, que es un Map
        );
        // Retornar el cuerpo de la respuesta, que contiene la información del usuario
        return response.getBody();
    }

    // Genera la URL de autenticación para Github
    public String getGitHubAuthUrl(String state) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(githubAuthUri)
                .queryParam("client_id", githubClientId)
                .queryParam("redirect_uri", githubRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email")  // Scopes correctos
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent");

        return uriBuilder.toUriString();
    }

    public Map<String, Object> getGitHubAccessToken(String code) { //Recibe un código de autorizacion code es lo que nos manda el servidor de google
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", githubClientId);
        params.add("client_secret", githubClientSecret);
        params.add("redirect_uri", githubRedirectUri);
        params.add("grant_type", "authorization_code"); //Indica que queremos obtener un token.

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(githubTokenUri, request, Map.class);

        return response.getBody();
    }

    public Map<String, Object> getGithubUserInfo(String accessToken) {
        // Crear un objeto HttpHeaders para enviar el token de acceso en la cabecera
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        // Realizar la solicitud GET a la URL de Github para obtener la información del usuario
        ResponseEntity<Map> response = restTemplate.exchange(
                githubUserInfoUri,   // URL para obtener la información del usuario de Github
                HttpMethod.GET,      // Método HTTP que será GET en este caso
                request,             // La entidad HTTP con las cabeceras (incluyendo el token)
                Map.class            // La clase esperada en la respuesta, que es un Map
        );
        // Retornar el cuerpo de la respuesta, que contiene la información del usuario
        return response.getBody();
    }



}

