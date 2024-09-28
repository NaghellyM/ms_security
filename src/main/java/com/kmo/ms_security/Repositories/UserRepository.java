package com.kmo.ms_security.Repositories;

import com.kmo.ms_security.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository <User,String> {

}
