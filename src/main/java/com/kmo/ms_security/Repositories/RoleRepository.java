package com.kmo.ms_security.Repositories;

import com.kmo.ms_security.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository <Role,String> {

}
