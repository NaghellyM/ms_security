//PARA PODER USAR REQUESTSERVICE INYECTADO
package com.GSTCP.ms_security.Configurations;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration // Indica que esta clase es una clase de configuración de Spring
public class RestTemplateConfig {

    @Bean  // Define un bean que será administrado por el contenedor de Spring
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        // Construye y devuelve una instancia de RestTemplate utilizando el RestTemplateBuilder
        return builder.build();
    }

}