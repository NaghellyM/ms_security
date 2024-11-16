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

import com.GSTCP.ms_security.Models.Session;
import com.GSTCP.ms_security.Models.User;
import com.GSTCP.ms_security.Repositories.SessionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;

@CrossOrigin
@RestController
@RequestMapping("/sessions") // Mucho ojo con esto Importante colocarlo porque es la ruta de acceso
public class SessionController {

    @Autowired // fabricas de objetos
    private SessionRepository theSessionRepository; // referencia a la clase de seccionRepository porque no se hace una
                                                    // intancia de una clase interfaz

    // aqui llamamos al usuario o inyeccion de dependencias
    @Autowired // crea el objeto
    private UserRepository theUserRepository;

    @GetMapping("")
    public List<Session> find() {
        return this.theSessionRepository.findAll(); // Este metodo es para que a la hora de que se use un "GET" general
                                                    // de los usuarios, simplemente nos muestre a todos
    }

    @GetMapping("{id}") // Maps HTTP GET requests to /sessions/{id} to this method
    public Session findById(@PathVariable String id) { // Defines a method that takes a path variable 'id'
        Session theSeccion = this.theSessionRepository.findById(id).orElse(null); // Searches for a session by its ID in
                                                                                  // the repository, returns null if not
                                                                                  // found
        return theSeccion; // Returns the found session or null
    }

    @PostMapping // Maps HTTP POST requests to /sessions to this method
    public Session create(@RequestBody Session newSession) { // Defines a method that takes a request body 'newSession'
        return this.theSessionRepository.save(newSession); // Saves the new session in the repository and returns it
    }

    @PutMapping("{id}") // Maps HTTP PUT requests to /sessions/{id} to this method
    public Session update(@PathVariable String id, @RequestBody Session newSession) { // Defines a method that takes a
                                                                                      // path variable 'id' and a
                                                                                      // request body 'newSession'
        Session actualSession = this.theSessionRepository.findById(id).orElse(null); // Searches for a session by its ID
                                                                                     // in the repository, returns null
                                                                                     // if not found
        if (actualSession != null) { // Checks if the session exists
            actualSession.setToken(newSession.getToken()); // Updates the token of the existing session with the new
                                                           // token
            actualSession.setExpiration(newSession.getExpiration()); // Updates the expiration of the existing session
                                                                     // with the new expiration
            this.theSessionRepository.save(actualSession); // Saves the updated session in the repository
            return actualSession; // Returns the updated session
        } else { // If the session does not exist
            return null; // Returns null
        }
    }

    @DeleteMapping("{id}") // Maps HTTP DELETE requests to /sessions/{id} to this method
    public void delete(@PathVariable String id) { // Defines a method that takes a path variable 'id'
        Session theSession = this.theSessionRepository.findById(id).orElse(null); // Searches for a session by its ID in
                                                                                  // the repository, returns null if not
                                                                                  // found
        if (theSession != null) { // Checks if the session exists
            this.theSessionRepository.delete(theSession); // Deletes the session from the repository
        }
    }

    // Implement the match session with User
    @PostMapping("{session_id}/user/{user_id}") // Maps HTTP POST requests to /sessions/{session_id}/user/{user_id} to
                                                // this method
    public Session matchUser(@PathVariable String session_id, @PathVariable String user_id) { // Defines a method that
                                                                                              // takes two path
                                                                                              // variables 'session_id'
                                                                                              // and 'user_id'
        Session theSession = this.theSessionRepository.findById(session_id).orElse(null); // Searches for a session by
                                                                                          // its ID in the repository,
                                                                                          // returns null if not found
        User theUser = this.theUserRepository.findById(user_id).orElse(null); // Searches for a user by its ID in the
                                                                              // repository, returns null if not found
        if (theSession != null && theUser != null) { // Checks if both the session and the user exist
            theSession.setUser(theUser); // Sets the user to the session
            this.theSessionRepository.save(theSession); // Saves the updated session in the repository
            return theSession; // Returns the updated session
        } else { // If either the session or the user does not exist
            return null; // Returns null
        }
    }

    // Create the endpoint
    @GetMapping("user/{userId}") // Maps HTTP GET requests to /sessions/user/{userId} to this method
    public List<Session> getSessionsByUser(@PathVariable String userId) { // Defines a method that takes a path variable
                                                                          // 'userId'
        return this.theSessionRepository.getSessionsByUserId(userId); // Returns a list of sessions associated with the
                                                                      // given userId
    }

}