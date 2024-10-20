package com.ddcf.security.Controllers;

import com.ddcf.security.Models.Role;
import com.ddcf.security.Models.User;
import com.ddcf.security.Models.UserRole;
import com.ddcf.security.Repositories.RoleRepository;
import com.ddcf.security.Repositories.UserRepository;
import com.ddcf.security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user_role")

public class UserRoleController {

    //injecciones UserRrpository son repositorios
    @Autowired
    private UserRoleRepository theUserRoleRepository;

    //Agregamos 3 injecciones mas y por ende 2 repositorio
    //injeccion de UserRepository
    @Autowired
    private UserRepository theUserRepository;

    //injeccion de RoleRepository
    @Autowired
    private RoleRepository theRoleRepository;


    @GetMapping("user/{userId}")
    public List<UserRole> getRolesByUser(@PathVariable String userId){
        return this.theUserRoleRepository.getRolesByUser(userId);
    }

    @GetMapping("role/{roleId}")
    public List<UserRole> getUsersByRole(@PathVariable String roleId){
        return this.theUserRoleRepository.getUsersByRole(roleId);
    }
    //Hacemos conexiones en el controlador

    @PostMapping("user/{userId}/role/{roleId}")
    public UserRole create(@PathVariable String userId,
                            @PathVariable String roleId){
        User theUser=this.theUserRepository.findById(userId).orElse(null);
        Role theRole=this.theRoleRepository.findById(roleId).orElse(null);

      if(theUser != null && theRole != null){
          UserRole newUserRole = new UserRole();
          newUserRole.setUser(theUser);
          newUserRole.setRole(theRole);
          return this.theUserRoleRepository.save(newUserRole);
      }else{
          return null;
      }
    }
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        UserRole theUserRole=this.theUserRoleRepository.findById(id).orElse(null);
        if (theUserRole!=null) {
            this.theUserRoleRepository.delete(theUserRole);
        }
    }


    }




