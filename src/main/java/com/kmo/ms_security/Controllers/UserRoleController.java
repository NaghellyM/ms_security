package com.kmo.ms_security.Controllers;

import com.kmo.ms_security.Models.Role;
import com.kmo.ms_security.Models.User;
import com.kmo.ms_security.Models.UserRole;
import com.kmo.ms_security.Repositories.RoleRepository;
import com.kmo.ms_security.Repositories.UserRepository;
import com.kmo.ms_security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/user_role")
public class UserRoleController {

    @Autowired
    UserRepository theUserRepository;

    @Autowired
    RoleRepository theRoleRepository;

    @Autowired
    UserRoleRepository theUserRoleRepository;



    @PostMapping ("user/{userId}/role/{roleId}")
    public UserRole create (@PathVariable String userId,  @PathVariable String roleId){
        User theUser = this.theUserRepository.findById(userId).orElse(null);
        Role thrRole = this.theRoleRepository.findById(roleId).orElse(null);
        if (theUser != null && thrRole != null){
            UserRole newUserRole = new UserRole();

            //N-N relationship links
            newUserRole.setUser(theUser);
            newUserRole.setRole(thrRole);
            this.theUserRoleRepository.save(newUserRole);
            return newUserRole;
        } else {
            return null;
        }
    }

    @GetMapping ("user/{userId}")
    public List<UserRole> getRolesByUser(@PathVariable String userId){
        return this.theUserRoleRepository.getRolesByUser(userId);
    }

    @GetMapping("role/{roleId}")
    public List<UserRole> getUserByRole(@PathVariable String roleId){
        return this.theUserRoleRepository.getUsersByUser(roleId);
    }


    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        User theUser=this.theUserRepository.findById(id).orElse(null);
        if (theUser!=null){
            this.theUserRepository.delete(theUser);
        }
    }

}
