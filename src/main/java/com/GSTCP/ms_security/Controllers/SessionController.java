package com.GSTCP.ms_security.Controllers;

import com.GSTCP.ms_security.Models.Session;
import com.GSTCP.ms_security.Models.User;
import com.GSTCP.ms_security.Repositories.SessionRepository;
import com.GSTCP.ms_security.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


    @CrossOrigin
    @RestController
    @RequestMapping("/sessions") //Mucho ojo con esto Importante colocarlo porque es la ruta de acceso
    public class SessionController {

        @Autowired//fabricas de objetos
        private SessionRepository theSessionRepository; //referencia a la clase de seccionRepository porque no se hace una intancia de una clase interfaz

        //aqui llamamos al usuario o inyeccion de dependencias
        @Autowired //crea el objeto
        private UserRepository theUserRepository;


        @GetMapping("")
        public List<Session> find(){//metodo que retorna una lista de secciones y el find es el nombre del metodo
            return this.theSessionRepository.findAll();//finfAll es un metodo de la interfaz SeessionRepository
        }
        @GetMapping("{id}")
        public Session findById(@PathVariable String id){
            Session theSeccion=this.theSessionRepository.findById(id).orElse(null);
            return theSeccion;
        }
        @PostMapping
        public Session create(@RequestBody Session newSession){
            return this.theSessionRepository.save(newSession);
        }
        @PutMapping("{id}")
        public Session update(@PathVariable String id, @RequestBody Session newSession){
            Session actualSession=this.theSessionRepository.findById(id).orElse(null);
            if(actualSession!=null){
                actualSession.setToken(newSession.getToken());
                actualSession.setExpiration(newSession.getExpiration());

                this.theSessionRepository.save(actualSession);
                return actualSession;
            }else{
                return null;
            }
        }
        @DeleteMapping("{id}")
        public void delete(@PathVariable String id){
            Session theSession=this.theSessionRepository.findById(id).orElse(null);
            if (theSession!=null){
                this.theSessionRepository.delete(theSession);
            }
        }
        //implementar el match seccion con User
        @PostMapping("{session_id}/user/{user_id}")
        public Session matchUser(@PathVariable String session_id,
                                 @PathVariable String user_id) {
            Session theSession = this.theSessionRepository.findById(session_id).orElse(null);
            User theUser = this.theUserRepository.findById(user_id).orElse(null); // creo la variable theUser como objeto y busco el usuario por id
            if (theSession != null && theUser != null) {
                theSession.setUser((theUser));
                this.theSessionRepository.save(theSession);
                return theSession;
            } else {
                return null;
            }
        }

        // vamos hacer el empoint
        @GetMapping("user/{userId}")
        public List<Session> getSessionsByUser(@PathVariable String userId) {
            return this.theSessionRepository.getSessionsByUserId(userId);


        }

    }