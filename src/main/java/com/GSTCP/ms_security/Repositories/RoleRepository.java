package com.GSTCP.ms_security.Repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.GSTCP.ms_security.Models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {

}