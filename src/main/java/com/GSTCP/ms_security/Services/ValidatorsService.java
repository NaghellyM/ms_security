package com.GSTCP.ms_security.Services;
import java.security.Permission;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import com.GSTCP.ms_security.Repositories.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import com.GSTCP.ms_security.Models.*;
import com.GSTCP.ms_security.Repositories.PermissionRepository;
import com.GSTCP.ms_security.Repositories.RolePermissionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service

public class ValidatorsService {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private PermissionRepository thePermissionRepository;
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private RolePermissionRepository theRolePermissionRepository;

    @Autowired
    private UserRoleRepository theUserRoleRepository;

    private static final String BEARER_PREFIX = "Bearer ";
    public boolean validationRolePermission(HttpServletRequest request,
                                            String url,
                                            String method){
        boolean success=false;
        User theUser=this.getUser(request);//analisa el paequete y obtiene un usuario
        if(theUser!=null){
            System.out.println("Antes URL "+url+" metodo "+method);
            url = url.replaceAll("[0-9a-fA-F]{24}|\\d+", "?");
            System.out.println("URL "+url+" metodo "+method);//estos sale de ruqeuest
            Permission thePermission=this.thePermissionRepository.getPermission(url,method);
            //Aqui obtengo los roles del Usuarios
            List<UserRole> roles=this.theUserRoleRepository.getRolesByUser(theUser.get_id());
            int i=0;
            while(i<roles.size() && success==false){
                UserRole actual=roles.get(i);
                Role theRole=actual.getRole();
                if(theRole!=null && thePermission!=null){
                    System.out.println("Rol "+theRole.get_id()+ " Permission "+thePermission.get_id());
                    RolePermission theRolePermission=this.theRolePermissionRepository.getRolePermission(theRole.get_id(),thePermission.get_id());
                    if (theRolePermission!=null){
                        success=true;
                    }
                }else{
                    success=false;
                }
                i+=1;
            }

        }
        return success;
    }
    public User getUser(final HttpServletRequest request) {
        User theUser=null;
        String authorizationHeader = request.getHeader("Authorization");
        System.out.println("Header "+authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            System.out.println("Bearer Token: " + token);
            User theUserFromToken=jwtService.getUserFromToken(token);
            if(theUserFromToken!=null) {
                theUser= this.theUserRepository.findById(theUserFromToken.get_id())//devolvemos el usuario al que corresponde en la base de datos
                        .orElse(null);

            }
        }
        return theUser;
    }
    
}
