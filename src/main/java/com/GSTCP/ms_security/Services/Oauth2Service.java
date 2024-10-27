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
        // Construye la URL de autenticación utilizando UriComponentsBuilder
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(googleAuthUri)
                // Establece el encabezado de autorización como "Bearer" con el token de acceso
                .queryParam("client_id", googleClientId) // Agrega el parámetro client_id
                .queryParam("redirect_uri", googleRedirectUri) // Agrega el parámetro redirect_uri
                .queryParam("response_type", "code") // Agrega el parámetro response_type con valor "code"
                .queryParam("scope", "openid profile email")  // Scopes correctos-Agrega el parámetro scope con los valores "openid profile email"
                .queryParam("state", state) // Agrega el parámetro state
                .queryParam("access_type", "offline")  // Agrega el parámetro access_type con valor "offline"
                .queryParam("prompt", "consent"); // Agrega el parámetro prompt con valor "consent"

        return uriBuilder.toUriString();  // Convierte la URL construida a una cadena y la devuelve
    }

    //Método para convertir el codigo en un token

    public Map<String, Object> getGoogleAccessToken(String code) {
        // Crea un objeto HttpHeaders y establece el tipo de contenido como application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Crea un objeto MultiValueMap para los parámetros de la solicitud
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);  // Agrega el parámetro code
        params.add("client_id", googleClientId); // Agrega el parámetro client_id
        params.add("client_secret", googleClientSecret);  // Agrega el parámetro client_secret
        params.add("redirect_uri", googleRedirectUri); // Agrega el parámetro redirect_uri
        params.add("grant_type", "authorization_code"); // Agrega el parámetro grant_type con valor "authorization_code"

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers); // Crea una entidad HTTP con los parámetros y las cabecer
        ResponseEntity<Map> response = restTemplate.postForEntity(googleTokenUri, request, Map.class); // Crea una entidad HTTP con los parámetros y las cabecer

        return response.getBody();     // Devuelve el cuerpo de la respuesta
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
        // Construye la URL de autenticación utilizando UriComponentsBuilder
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(githubAuthUri)
                .queryParam("client_id", githubClientId) // Agrega el parámetro client_id
                .queryParam("redirect_uri", githubRedirectUri) // Agrega el parámetro redirect_uri
                .queryParam("response_type", "code") // Agrega el parámetro response_type con valor "code"
                .queryParam("scope", "openid profile email")  // Scopes correctos -// Agrega el parámetro scope con los valores "openid profile email"
                .queryParam("state", state)  // Agrega el parámetro state
                .queryParam("access_type", "offline") // Agrega el parámetro access_type con valor "offline"
                .queryParam("prompt", "consent"); // Agrega el parámetro prompt con valor "consent"

        return uriBuilder.toUriString();
        // Convierte la URL construida a una cadena y la devuelve
    }
    // Método para convertir el código en un token
    public Map<String, Object> getGitHubAccessToken(String code) {   // Crea un objeto HttpHeaders y establece el tipo de contenido como application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Crea un objeto MultiValueMap para los parámetros de la solicitud
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code); // Agrega el parámetro code
        params.add("client_id", githubClientId);  // Agrega el parámetro client_id
        params.add("client_secret", githubClientSecret); // Agrega el parámetro client_secret
        params.add("redirect_uri", githubRedirectUri); // Agrega el parámetro redirect_uri
        params.add("grant_type", "authorization_code"); // Agrega el parámetro grant_type con valor "authorization_code"
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);   // Crea una entidad HTTP con los parámetros y las cabeceras
        ResponseEntity<Map> response = restTemplate.postForEntity(githubTokenUri, request, Map.class);  // Realiza una solicitud POST a githubTokenUri y obtiene la respuesta

        return response.getBody(); // Devuelve el cuerpo de la respuesta
    }

    public Map<String, Object> getGithubUserInfo(String accessToken) { // Obtener información del usuario a partir del token
        // Crear un objeto HttpHeaders para enviar el token de acceso en la cabecera
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);  // Establece el encabezado de autorización como "Bearer" con el token de acceso
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

