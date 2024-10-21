package com.GSTCP.ms_security.Services;

import com.GSTCP.ms_security.Models.*;
import com.GSTCP.ms_security.Repositories.PermissionRepository;
import com.GSTCP.ms_security.Repositories.RolePermissionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;
import com.GSTCP.ms_security.Repositories.UserRoleRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;


@Service
public class ValidatorsService {
    //hacemos inyecciones de dependencias
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
    //devuelve un booleano si lo deja o no lo deja y va a nesecitar de lacarta, url, el mrthodo
    public boolean validationRolePermission(HttpServletRequest request,//request
                                            String url,//la url
                                            String method){ //metodo
        boolean success=false;//en un principio declaro la variable que me va a servir de respuesta
        User theUser=this.getUser(request);//analisa el paequete y obtiene un usuario porque nesecitamos los roles
        if(theUser!=null){//si el usuario no es nulo
            System.out.println("Antes URL "+url+" metodo "+method);
            url = url.replaceAll("[0-9a-fA-F]{24}|\\d+", "?");//esto me ayuda a generalizar las peticiones y lo remplazo por un interrogante
            System.out.println("URL "+url+" metodo "+method);//estos sale de request osea la carta

            Permission thePermission=this.thePermissionRepository.getPermission(url,method);
            //a este repositorio le digo si me da permiso que si por favor me obtiene un permiso asi
            //Aqui obtengo o cargo los roles del Usuarios desde UserRoleRepository
            List<UserRole> roles=this.theUserRoleRepository.getRolesByUser(theUser.get_id());
            //despues vamos a verificar si ese role tiene permiso osea verificar si existe la clase intermedia rolPermiso
            int i=0;
            while(i<roles.size() && success==false){//analizamos los roles de ese Usuario y recorremos todos los element¿os de esa clase iunermedia osea UserPersmission
                UserRole actual=roles.get(i);//UserRole acrual tiene por dentro un role
                Role theRole=actual.getRole();//saco el role le saco el rol de UserRole y lo obtengo
                if(theRole!=null && thePermission!=null){//verifico si el role y el permiso no son nulos para que no haya inconsistencias
                    System.out.println("Rol "+theRole.get_id()+ " Permission "+thePermission.get_id());
                    //le digo al repositorio si hay un match entre un rol y un permiso para eso mando el identidicador del rol y del permiso
                    RolePermission theRolePermission=this.theRolePermissionRepository.getRolePermission(theRole.get_id(),thePermission.get_id());
                    if (theRolePermission!=null){//di rolePERMISSION ES DISTINTO DE NULO
                        success=true;// si hay un match entre el rol y el permiso y levanta la bandera a true y cieera el ciclo while diciendo que se encontro el elemento
                    }
                }else{
                    success=false; //de lo contrario no lo encontro devuelve un falso y eso lo devuelve al muro
                }
                i+=1;
            }

        }
        return success;
    }
    //retorna un usuario y le va a entrar la request oseaa la carta
    public User getUser(final HttpServletRequest request) {
        User theUser=null; //EL USUARIO EN UN PRINCIPIO VA HACER NULO
        String authorizationHeader = request.getHeader("Authorization");//de la request optenemos el autorization optenga el toque y viene bearer" token
        System.out.println("Header "+authorizationHeader);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {//si VIENE UNA  authorizationHeader y empieza con LA APALBRA Bearer ES PORQUE SI MANDARON ALGO
            //Lo primero que se hace es obtener el token y lo recoramos para que despues de tal posicion obtenga ese contenido
            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            System.out.println("Bearer Token: " + token);//tokrn por dentro cifrado por dentro vien el usuario
            User theUserFromToken=jwtService.getUserFromToken(token);//jwt me ayuda a desifrar al usuario y me devuelve el usuario desde el token
            if(theUserFromToken!=null) {//si encontro el usuario del token
                /*Cargo de la base de datos de UserRepository que me busque a un usuario y que le busque
                 el identificador, que esta dado por el usuario que viene del token                                                                                                                          */
                theUser= this.theUserRepository.findById(theUserFromToken.get_id())//devolvemos el usuario al que corresponde en la base de datos
                        .orElse(null);

            }
        }
        return theUser;//devuelve el usuario bien formado
    }
}
