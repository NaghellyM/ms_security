package com.kmo.ms_security.Controllers;

import com.kmo.ms_security.Models.Role;
import com.kmo.ms_security.Repositories.RoleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/roles")
public class RoleController {
    @Autowired
    RoleRepository theRoleRepository;

    @GetMapping("")
    public List<Role> find() {
        return this.theRoleRepository.findAll();
    }

    @GetMapping("{id}")
    public ResponseEntity<Role> findById(@PathVariable String id) {
        Role theRole = this.theRoleRepository.findById(id).orElse(null);
        if (theRole != null) {
            return ResponseEntity.ok(theRole);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        Role theRole = this.theRoleRepository.findById(id).orElse(null);
        if (theRole != null) {
            this.theRoleRepository.delete(theRole);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public Role create(@RequestBody Role newRole) {
        return this.theRoleRepository.save(newRole);
    }

    @PutMapping("{id}")
    public ResponseEntity<Role> update(@PathVariable String id, @RequestBody Role newRole) {
        Role actualRole = this.theRoleRepository.findById(id).orElse(null);
        if (actualRole != null) {
            actualRole.setName(newRole.getName());
            actualRole.setDescription(newRole.getDescription());
            this.theRoleRepository.save(actualRole);
            return ResponseEntity.ok(actualRole);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
