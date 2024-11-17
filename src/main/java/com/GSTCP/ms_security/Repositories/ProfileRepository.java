package com.GSTCP.ms_security.Repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import com.GSTCP.ms_security.Models.Profile;

public interface ProfileRepository extends MongoRepository<Profile, String> {
    @Query("{'user.$id': ObjectId(?0)}")
    public List<Profile> getProfileByUser(String userId);

}