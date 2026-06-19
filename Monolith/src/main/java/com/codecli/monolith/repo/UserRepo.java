package com.codecli.monolith.repo;

import com.codecli.monolith.Models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<User, String> {

    @Query("SELECT u from User u where u.email = ?1")
    Optional<User> findByEmail(String email);


}
