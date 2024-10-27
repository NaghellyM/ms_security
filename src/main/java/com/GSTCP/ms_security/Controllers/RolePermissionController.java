package com.GSTCP.ms_security.Controllers;

import com.GSTCP.ms_security.Models.Permission;
import com.GSTCP.ms_security.Models.Role;
import com.GSTCP.ms_security.Models.RolePermission;
import com.GSTCP.ms_security.Repositories.PermissionRepository;
import com.GSTCP.ms_security.Repositories.RolePermissionRepository;
import com.GSTCP.ms_security.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/role-permission") // Mapea las solicitudes a "/role-permission"
public class RolePermissionController {
    @Autowired// Inyecta el repositorio de RolePermission
    private RolePermissionRepository theRolePermissionRepository;
    @Autowired  // Inyecta el repositorio de Permission
    private PermissionRepository thePermissionRepository;
    @Autowired     // Inyecta el repositorio de Role
    private RoleRepository theRoleRepository;

    @ResponseStatus(HttpStatus.CREATED)  // Define el estado HTTP 201 (CREATED) para esta operación
    @PostMapping("role/{roleId}/permission/{permissionId}")   // Mapea las solicitudes POST a "role/{roleId}/permission/{permissionId}"
    public RolePermission create(@PathVariable String roleId,
                                 @PathVariable String permissionId){
        Role theRole=this.theRoleRepository.findById(roleId)
                                            .orElse(null);
        // Busca el permiso por su ID
        Permission thePermission=this.thePermissionRepository.findById((permissionId))
                                                                .orElse(null);
        // Si ambos, el rol y el permiso, existen
        if(theRole!=null && thePermission!=null){
            RolePermission newRolePermission=new RolePermission(); // Crea una nueva instancia de RolePermission
            newRolePermission.setRole(theRole); // Asigna el rol a la nueva instancia
            newRolePermission.setPermission(thePermission); // Asigna el permiso a la nueva instancia
            return this.theRolePermissionRepository.save(newRolePermission); // Guarda la nueva instancia en el repositorio y la retorna
        }else{
            return null;    // Si el rol o el permiso no existen, retorna null
        }
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)     // Define el estado HTTP 204 (NO CONTENT) para esta operación

    // Mapea las solicitudes DELETE a "{id}"
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        // Busca la relación RolePermission por su ID
        RolePermission theRolePermission = this.theRolePermissionRepository
                .findById(id)
                .orElse(null);
        // Si la relación existe, la elimina del repositorio
        if (theRolePermission != null) {
            this.theRolePermissionRepository.delete(theRolePermission);
        }
    }
    // Mapea las solicitudes GET a "role/{roleId}"
    @GetMapping("role/{roleId}")
    public List<RolePermission> findPermissionsByRole(@PathVariable String roleId){
        // Retorna la lista de permisos asociados a un rol específico
        return this.theRolePermissionRepository.getPermissionsByRole(roleId);
    }

}