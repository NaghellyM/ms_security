package com.kmo.ms_security.Controllers;

import com.kmo.ms_security.Models.User;
import com.kmo.ms_security.Repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired//fabricas de objetos
    private UserRepository theUserRepository; //referencia a la clase de UserRepository porque no se hace una intancia de una clase interfaz

    @GetMapping("")
    public List<User> find(){
        return this.theUserRepository.findAll();
    }
    @GetMapping("{id}")
    public User findById(@PathVariable String id){
        User theUser=this.theUserRepository.findById(id).orElse(null);
        return theUser;
    }
    @PostMapping
    public User create(@RequestBody User newUser){
        return this.theUserRepository.save(newUser);
    }
    @PutMapping("{id}")
    public User update(@PathVariable String id, @RequestBody User newUser){
        User actualUser=this.theUserRepository.findById(id).orElse(null);
        if(actualUser!=null){
            actualUser.setName(newUser.getName());
            actualUser.setEmail(newUser.getEmail());
            actualUser.setPassword(newUser.getPassword());
            this.theUserRepository.save(actualUser);
            return actualUser;
        }else{
            return null;
        }
    }
    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        User theUser=this.theUserRepository.findById(id).orElse(null);
        if (theUser!=null){
            this.theUserRepository.delete(theUser);
        }
    }
}
