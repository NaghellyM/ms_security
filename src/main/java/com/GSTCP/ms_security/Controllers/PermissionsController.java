package com.GSTCP.ms_security.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.GSTCP.ms_security.Models.Permission;
import com.GSTCP.ms_security.Repositories.PermissionRepository;

@CrossOrigin
@RestController // Define esta clase como un controlador REST
@RequestMapping("/permissions") // Mapea las solicitudes a "/api/permissions"

public class PermissionsController {

    // Inyecta la dependencia PermissionRepository
    @Autowired
    private PermissionRepository thePermissionRepository;

    // Maneja las solicitudes GET para recuperar todos los permisos
    @GetMapping("")
    // Devuelve la lista de todos los permisos
    public List<Permission> findAll() {
        return this.thePermissionRepository.findAll();
    }

    // Maneja las solicitudes POST para crear un nuevo permiso
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Permission create(@RequestBody Permission theNewPermission) {
        // Guarda el nuevo permiso y lo devuelve
        return this.thePermissionRepository.save(theNewPermission);
    }

    // Maneja las solicitudes DELETE para eliminar un permiso por ID
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        // Encuentra el permiso por ID
        Permission thePermission = this.thePermissionRepository
                .findById(id)
                .orElse(null);
        // Si el permiso existe, elimínalo
        if (thePermission != null) {
            // elimina el objeto thePermission de la base de datos
            this.thePermissionRepository.delete(thePermission);
        }
    }
}