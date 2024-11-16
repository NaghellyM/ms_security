package com.GSTCP.ms_security.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GSTCP.ms_security.Models.Role;
import com.GSTCP.ms_security.Repositories.RoleRepository;

@CrossOrigin
@RestController
@RequestMapping("/api/roles")

public class RoleController {
    @Autowired
    private RoleRepository theRoleRepository; // Aqui se esta usando una inyeccion de dependencias en la interfaz. En
                                              // este caso esta adoptando los metdodos de la libreria que nos vana a
                                              // ayudar a conectarnos con la bd de mongo

    @GetMapping("")
    public List<Role> find() {
        return this.theRoleRepository.findAll(); // Este metodo es para que a la hora de que se use un "GET" general de
                                                 // los usuarios, simplemente nos muestre a todos
    }

    @GetMapping("{id}")
    public Role findById(@PathVariable String id) { // Aqui es para devolver un unico usuario. El @PathVariable es la
                                                    // ruta, en este caso de donde va a conseguir el identificador
        Role theRole = this.theRoleRepository.findById(id).orElse(null);
        return theRole;
    }

    @PostMapping
    public Role create(@RequestBody Role newRole) {
        return this.theRoleRepository.save(newRole);
    }

    @PutMapping("{id}")
    public Role update(@PathVariable String id, @RequestBody Role newRole) {
        Role actualRole = this.theRoleRepository.findById(id).orElse(null);
        if (actualRole != null) {
            actualRole.setName(newRole.getName());
            actualRole.setDescription(newRole.getDescription());
            this.theRoleRepository.save(actualRole);
            return actualRole;
        } else {
            return null;
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        Role theUser = this.theRoleRepository.findById(id).orElse(null);
        if (theUser != null) {
            this.theRoleRepository.delete(theUser);
        }
    }

}
