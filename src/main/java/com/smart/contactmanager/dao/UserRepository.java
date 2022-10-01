package com.smart.contactmanager.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.smart.contactmanager.entities.User;

public interface UserRepository extends CrudRepository<User,Integer>{
    
    @Query("select u from User u where u.email = :email")
    public User getUserByName(@Param("email") String email);
}
