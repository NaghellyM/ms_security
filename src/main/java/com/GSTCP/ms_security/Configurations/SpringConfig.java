package com.GSTCP.ms_security.Configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // Indica que esta clase es una clase de configuración de Spring
@EnableWebSecurity // Habilita la seguridad web en la aplicación
public class SpringConfig {

    // Define un bean que será administrado por el contenedor de Spring
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> // Configura la autorización de solicitudes HTTP
                request.anyRequest().permitAll() // Permite todas las solicitudes sin restricción
                )
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita la protección CSRF
                .formLogin(AbstractHttpConfigurer::disable) // Deshabilita el formulario de inicio de sesión
                .httpBasic(AbstractHttpConfigurer::disable) // Deshabilita la autenticación HTTP básica
                .build(); // Construye y devuelve la cadena de filtros de seguridad
    }
}