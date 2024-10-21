package com.GSTCP.ms_security.Repositories;


import com.GSTCP.ms_security.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
 //Esta consulta lo que hace es buscar un email y que me devuelva el usuario asociado
 @Query("{'email': ?0}")
 public User getUserByEmail(String email);
}