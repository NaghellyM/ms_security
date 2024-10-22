package com.GSTCP.ms_security.Services;

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
    //Claves de google
    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.url}")
    private String googleRedirectUri;

    @Value("${google.auth.uri}")
    private String googleOauth2Uri;

    @Value("${google.token.uri}")
    private String googleTokenUri;

    @Value("${google.user.info.uri}")
    private String googleUserInfoUri;

    private final RestTemplate restTemplate = new RestTemplate();

    //Aqui generamos la url de autenticación para google con algunos atributos (los ultimos 2 son por
    //temas de la libreria
    //Este metodo recibe el estado enviado por el endpoint ("google")
    public String getGoogleUrl(String state) {
        //El UriComponentsBuilder se usa para crear una url
        //la url la vamos creando paso por paso con los parametros
        //que definimos previemente
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(googleOauth2Uri)
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code") //La respuesta es un codigo
                .queryParam("scope", "openid profile email")  // Aquí definimos que queremos recibir del scope (la ventana emergente)
                                                                            // que configuramos en el apartado del Google Console. En este caso
                                                                            // pedimos el openid, profile y email
                .queryParam("state", state) //Aquí le decimos el estado del navegador en el que estoy
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent");
        //Y retornamos la url como un string
        return uriBuilder.toUriString();
    }

    //Aqui obtenemos el token que le solicitamos a google (lo genera)
    //Enviamos como parametro el codigo
    public Map<String, Object> getGoogleAccessToken(String code) {
        //Aquí en los headers definimos que va a ser un tipo de aplication para
        //la autenticación
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        //Definimos un nuevo map con los atributos del google oauth2
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        //nuevamente usamos restTemplate para hacer una solicitud a la API de google, con una url,
        // con una informacion (googleTokenUri) y va a devolver una clase mapeada
        ResponseEntity<Map> response = restTemplate.postForEntity(googleTokenUri, request, Map.class);
        //dentro de esta respuesta ya tengo mi access token
        System.out.println(response);
        return response.getBody();
    }

    // Obtiene información del usuario de Google
    // Se hace nuevamente una solicitud a Google
    public Map<String, Object> getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // headers: size = 1
        //Con un bearertoken
        //Para que agarre esa indormacion
        HttpEntity<Void> request = new HttpEntity<>(headers); // request: {Authorization=Bearer ya29.a0Aa...}
        //Y me la devuelva
        ResponseEntity<Map> response = restTemplate.exchange(googleUserInfoUri, HttpMethod.GET, request, Map.class);
        System.out.println(response);
        return response.getBody(); // response: "200 OK OK, {id=105115495175148508726, email=luran.me0704@gmail.com}"
    }





}
