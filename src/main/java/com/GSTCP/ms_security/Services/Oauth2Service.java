package com.GSTCP.ms_security.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

@Service
public class Oauth2Service {
    // Google Properties
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

    // GitHub Properties
    @Value("${spring.security.oauth2.client.registration.github.client-id}")
    private String gitHubClientId;

    @Value("${spring.security.oauth2.client.registration.github.client-secret}")
    private String gitHubClientSecret;

    @Value("${spring.security.oauth2.client.registration.github.redirect-uri}")
    private String gitHubRedirectUri;

    @Value("${github.auth.uri}")
    private String gitHubOauth2Uri;

    @Value("${github.token.uri}")
    private String gitHubTokenUri;

    @Value("${github.user.info.uri}")
    private String gitHubUserInfoUri;

    private final RestTemplate restTemplate = new RestTemplate();

    // Google Methods
    public String getGoogleUrl(String state) {
        return UriComponentsBuilder.fromHttpUrl(googleOauth2Uri)
                .queryParam("client_id", googleClientId)
                .queryParam("redirect_uri", googleRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "openid profile email")
                .queryParam("state", state)
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .toUriString();
    }

    public Map<String, Object> getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(googleTokenUri, request, Map.class);
            System.out.println("Google Token Response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error getting Google access token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Object> getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    googleUserInfoUri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            System.out.println("Google User Info Response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error getting Google user info: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // GitHub Methods
    public String getGitHubUrl(String state) {
        String finalUrl = UriComponentsBuilder.fromHttpUrl(gitHubOauth2Uri)
                .queryParam("client_id", gitHubClientId)
                .queryParam("redirect_uri", gitHubRedirectUri)
                .queryParam("scope", "user:email")
                .queryParam("state", state)
                .toUriString();

        System.out.println("Generated GitHub URL: " + finalUrl);
        return finalUrl;
    }

    public Map<String, Object> getGitHubAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("Accept", "application/json");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", gitHubClientId);
        params.add("client_secret", gitHubClientSecret);
        params.add("redirect_uri", gitHubRedirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(gitHubTokenUri, request, Map.class);
            System.out.println("GitHub Token Response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error getting GitHub access token: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public Map<String, Object> getGitHubUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "Spring-Boot-Application");

        HttpEntity<Void> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    gitHubUserInfoUri,
                    HttpMethod.GET,
                    request,
                    Map.class
            );
            System.out.println("GitHub User Info Response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Error getting GitHub user info: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
}