package com.ddcf.security.Interceptors;

import com.ddcf.security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

//decorador componente
@Component
public class SecurityInterceptor implements HandlerInterceptor {
    //configuura que puedo hacer las puertas entrada uy salida
    @Autowired
    private ValidatorsService validatorService; //este es nuetro vifilamnte o policia que vigila
    //analisis puerta de entrada
    //si nos piden ajustes con el policia aqui se hace
    @Override
    public boolean preHandle(HttpServletRequest request,//la carta viene rutas metodo token, informacion, OJO este es importante
                             HttpServletResponse response, //respuesta 200, 404, 500
                             Object handler)
            throws Exception {
        //si es true pasa  la puerta si es false no pasa la puerta
        boolean success=this.validatorService.validationRolePermission(request,request.getRequestURI(),request.getMethod());
        return success;
    }

    //puerta de salida
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Lógica a ejecutar después de que se haya manejado la solicitud por el controlador
    }
//se hace ajustes de salida en una interfaz
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // Lógica a ejecutar después de completar la solicitud, incluso después de la renderización de la vista
    }
}
