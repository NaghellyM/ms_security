package com.GSTCP.ms_security.Configurations;

import com.GSTCP.ms_security.Interceptors.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;


//esta clase es el muro
@Configuration
@EnableWebSecurity

public class WebConfig implements WebMvcConfigurer {
    //implementamos la interfaz WebMvcConfigurer
    @Autowired
    private SecurityInterceptor securityInterceptor;

    //registro todos los interceptores que yo quiero colacar @Override es una sobreescritura
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //regestry es el proyecto
        //security intercepton inyecta y se va al paquete de los interceptores
        registry.addInterceptor(securityInterceptor)
                /*va a mirar el muro todo lo que tenga esa ruta lo tenga protegido el interceptor
                excluyo nas y otras quedo abiertas*/
                .addPathPatterns("/api/**")//las tiene en cuenta
                .excludePathPatterns("/api/public/**"); //las excluye


    }

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> {
                    //tiene la función de permitir el acceso público
                    // (sin autenticación) a las rutas específicas que se mencionan en el array de URLs.
                    //Ya sea solamente /, o /auth/google, etc. Comentarla si se quieren deshabilitar las rutas
                    request.requestMatchers("/", "/auth/google", "/auth/github", "/auth/callback/**").permitAll();
                    request.anyRequest().permitAll();
                })
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF
                .formLogin(AbstractHttpConfigurer::disable) // Deshabilita el inicio de sesión basado en formulario
                .httpBasic(AbstractHttpConfigurer::disable) // Deshabilita el inicio de sesión básico
                .build();
    }

}