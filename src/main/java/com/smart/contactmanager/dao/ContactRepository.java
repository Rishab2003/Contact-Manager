package com.smart.contactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.contactmanager.entities.Contact;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

    //created contatct repo to use pagination
    //pagination method
    //befor list were used. now use page to implement pagination
    // pageable contains 2 info. 1.current page  2.contact per page
    @Query("from Contact as c where c.user.id=:userID")
    public Page<Contact> findContactsByUser(@Param("userID")int userID, Pageable pageable);
    
}
