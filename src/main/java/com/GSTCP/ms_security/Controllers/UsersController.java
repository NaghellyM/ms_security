package com.GSTCP.ms_security.Controllers;
import com.GSTCP.ms_security.Models.User;
//import com.GSTCP.ms_security.Repositories.SessionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;
import com.GSTCP.ms_security.Services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin
@RestController
@RequestMapping("/users1") //Mucho ojo con esto Importante colocarlo porque es la ruta de acceso

public class UsersController {

    @Autowired//fabricas de objetos
    private UserRepository theUserRepository; //referencia a la clase de UserRepository porque no se hace una intancia de una clase interfaz

    /*hago la
    inyeccion de
    EncryptionService y
    poder asi
    usar el
    servicio y
    con @Autowired le
    digo que
    lo inyecte el EncruptionService*/

    @Autowired
    private EncryptionService theEncryptionService;

    //@Autowired
    //private SessionRepository theSessionRepository;

    @GetMapping("")
    public List<User> find() {
        return this.theUserRepository.findAll();
    }

    @GetMapping("{id}")
    public User findById(@PathVariable String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        return theUser;
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        /*Antes de meterla en la base de datos transformamos la contraseña que viene plana y hacemos que al nuevo usuario cambien
         la contraseña depuesS vamos al theEncryptionService
        y yllamamos al metodo convertSHA256 convertimos la contraseña a SHA256
         pero vamos al useario y con el get lo optenemos
         */
        newUser.setPassword(this.theEncryptionService.convertSHA256((newUser.getPassword())));
        /*hacemos un retorno en la cual a la base
        de datos de theUserRepository le vamos a guardar el nuevo usuario*/
        return this.theUserRepository.save(newUser);
    }

    @PutMapping("{id}")
    public User update(@PathVariable String id, @RequestBody User newUser) {
        User actualUser = this.theUserRepository.findById(id).orElse(null);
        if (actualUser != null) {
            actualUser.setName(newUser.getName());
            actualUser.setEmail(newUser.getEmail());
            //aqui hacemos la actualizacion de la contraseña pero ya en formato SHA256
            actualUser.setPassword((this.theEncryptionService.convertSHA256(newUser.getPassword())));
            this.theUserRepository.save(actualUser);
            return actualUser;
        } else {
            return null;
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        User theUser = this.theUserRepository.findById(id).orElse(null);
        if (theUser != null) {
            this.theUserRepository.delete(theUser);
        }
    }


}