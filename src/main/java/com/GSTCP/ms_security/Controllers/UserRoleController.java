package com.GSTCP.ms_security.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GSTCP.ms_security.Models.Role;
import com.GSTCP.ms_security.Models.User;
import com.GSTCP.ms_security.Models.UserRole;
import com.GSTCP.ms_security.Repositories.RoleRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;
import com.GSTCP.ms_security.Repositories.UserRoleRepository;

@CrossOrigin
@RestController
@RequestMapping("/user_role")

public class UserRoleController {

    // injecciones UserRrpository son repositorios
    @Autowired
    private UserRoleRepository theUserRoleRepository;

    // Agregamos 3 injecciones mas y por ende 2 repositorio
    // injeccion de UserRepository
    @Autowired
    private UserRepository theUserRepository;

    // injeccion de RoleRepository
    @Autowired
    private RoleRepository theRoleRepository;

    @GetMapping("user/{userId}") // Mapea las solicitudes HTTP GET a /user_role/role/{roleId} a este método
    public List<UserRole> getRolesByUser(@PathVariable String userId) { // Define un método que toma un parámetro de
                                                                        // ruta 'roleId'
        return this.theUserRoleRepository.getRolesByUser(userId); // Retorna una lista de objetos UserRole asociados con
                                                                  // el roleId dado
    }

    @GetMapping("role/{roleId}") // Mapea las solicitudes HTTP GET a /user_role/role/{roleId} a este método
    public List<UserRole> getUsersByRole(@PathVariable String roleId) { // Define un método que toma un parámetro de
                                                                        // ruta 'roleId'
        return this.theUserRoleRepository.getUsersByRole(roleId); // Retorna una lista de objetos UserRole asociados con
                                                                  // el roleId dado
    }
    // Hacemos conexiones en el controlador

    @PostMapping("user/{userId}/role/{roleId}") // Mapea las solicitudes HTTP POST a
                                                // /user_role/user/{userId}/role/{roleId} a este método
    public UserRole create(@PathVariable String userId, // Define un método que toma dos parámetros de ruta 'userId' y
                                                        // 'roleId'
            @PathVariable String roleId) {
        User theUser = this.theUserRepository.findById(userId).orElse(null); // Busca un usuario por su ID en el
                                                                             // repositorio, devuelve null si no se
                                                                             // encuentra
        Role theRole = this.theRoleRepository.findById(roleId).orElse(null); // Busca un rol por su ID en el
                                                                             // repositorio, devuelve null si no se
                                                                             // encuentra

        if (theUser != null && theRole != null) { // Verifica si tanto el usuario como el rol existen
            UserRole newUserRole = new UserRole(); // Crea una nueva instancia de UserRole
            newUserRole.setUser(theUser); // Asigna el usuario al nuevo UserRole
            newUserRole.setRole(theRole); // Asigna el rol al nuevo UserRole
            return this.theUserRoleRepository.save(newUserRole); // Guarda el nuevo UserRole en el repositorio y lo
                                                                 // retorna
        } else {
            return null; // Retorna null si el usuario o el rol no existen
        }
    }

    @DeleteMapping("{id}") // Mapea las solicitudes HTTP DELETE a /user_role/{id} a este método
    public void delete(@PathVariable String id) { // Define un método que toma un parámetro de ruta 'id'
        UserRole theUserRole = this.theUserRoleRepository.findById(id).orElse(null); // Busca un UserRole por su ID en
                                                                                     // el repositorio, devuelve null si
                                                                                     // no se encuentra
        if (theUserRole != null) { // Verifica si el UserRole existe
            this.theUserRoleRepository.delete(theUserRole); // Elimina el UserRole del repositorio
        }
    }

}
