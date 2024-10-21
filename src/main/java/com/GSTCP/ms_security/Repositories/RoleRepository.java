package com.GSTCP.ms_security.Repositories;
import com.GSTCP.ms_security.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role,String> {

}