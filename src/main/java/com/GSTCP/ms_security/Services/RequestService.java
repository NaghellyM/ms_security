package com.GSTCP.ms_security.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RequestService {
    @Autowired
    private RestTemplate restTemplate;

    public List<UserEntity> getUsers() {
        String url = "url para hacer la solicitud como en el postman/get-users";
        ResponseEntity<UserEntity[]> response = restTemplate.getForEntity(url,UserEntity[].class);
        UserEntity[] users = response.getBody();
        return Arrays.asList(users);
    }



}
