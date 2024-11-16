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

import com.GSTCP.ms_security.Models.Profile;
import com.GSTCP.ms_security.Models.User;
import com.GSTCP.ms_security.Repositories.ProfileRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("/profiles")
public class ProfileController {
    @Autowired
    private ProfileRepository theProfileRepository; // Aqui se esta usando una inyeccion de dependencias en la interfaz.
                                                    // En este caso esta adoptando los metdodos de la libreria que nos
                                                    // vana a ayudar a conectarnos con la bd de mongo

    @Autowired
    private UserRepository theUserRepository;

    @GetMapping("")
    public List<Profile> find() {
        return this.theProfileRepository.findAll(); // Este metodo es para que a la hora de que se use un "GET" general
                                                    // de los usuarios, simplemente nos muestre a todos
    }

    @GetMapping("{id}")
    public Profile findById(@PathVariable String id) { // Aqui es para devolver un unico usuario. El @PathVariable es la
                                                       // ruta, en este caso de donde va a conseguir el identificador
        Profile theProfile = this.theProfileRepository.findById(id).orElse(null);
        return theProfile;
    }

    @PostMapping
    public Profile create(@RequestBody Profile newUser) {
        return this.theProfileRepository.save(newUser);
    }

    @PutMapping("{id}") // Mapea las solicitudes HTTP PUT a la ruta "/profiles/{id}"
    public Profile update(@PathVariable String id, @RequestBody Profile newProfile) {
        // Busca el perfil actual en la base de datos por su ID
        Profile actualProfile = this.theProfileRepository.findById(id).orElse(null);
        // Si el perfil actual existe, actualiza sus datos
        if (actualProfile != null) {
            // Actualiza el número de teléfono del perfil
            actualProfile.setPhone(newProfile.getPhone());
            // Actualiza la foto del perfil
            actualProfile.setPhoto(newProfile.getPhoto());
            // Guarda los cambios en la base de datos
            this.theProfileRepository.save(actualProfile);
            // Devuelve el perfil actualizado
            return actualProfile;
            // Si el perfil no existe, devuelve null
        } else {
            return null;
        }
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable String id) {
        // Busca el perfil en la base de datos por su ID
        Profile theUser = this.theProfileRepository.findById(id).orElse(null);
        // Si el perfil existe, elimínalo
        if (theUser != null) {
            this.theProfileRepository.delete(theUser);
        }
    }

    @PostMapping("{profile_id}/user/{user_id}")
    public Profile matchUserWProfile(@PathVariable String profile_id,
            @PathVariable String user_id) {
        // Busca el perfil y el usuario en la base de datos por sus IDs
        Profile thisProfile = this.theProfileRepository.findById(profile_id).orElse(null);
        User thisUser = this.theUserRepository.findById(user_id).orElse(null);
        // Si ambos existen y el perfil no tiene usuario asignado, asigna el usuario al
        // perfil
        if (thisProfile != null && thisUser != null) {
            if (thisProfile.getUser() == null) {
                thisProfile.setUser(thisUser);
                this.theProfileRepository.save(thisProfile);
                return thisProfile;
            }
        } else {
            return null;
        }
        // System.out.println(thisProfile);
        return thisProfile;
    }

    @GetMapping("user/{user_id}")
    public List<Profile> getProfileByUser(@PathVariable String user_id) {
        // Devuelve la lista de perfiles asociados al usuario
        return this.theProfileRepository.getProfileByUser(user_id);
    }

}