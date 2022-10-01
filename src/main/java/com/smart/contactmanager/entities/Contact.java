package com.smart.contactmanager.entities;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "CONTACT")
public class Contact {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;

    private String name;
    private String secondName;
    private String work;
    private String email;
    private String phoneNo;
    
    @Value("${some.key:contacts.png}")
    private String image;
    
    @Column(length = 1000)
    private String description;

 
    @ManyToOne
    private User user;


    public int getcId() {
        return cId;
    }
    public void setcId(int cId) {
        this.cId = cId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSecondName() {
        return secondName;
    }
    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }
    public String getWork() {
        return work;
    }
    public void setWork(String work) {
        this.work = work;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNo() {
        return phoneNo;
    }
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Contact() {
    }
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
    public Contact( String name, String secondName, String work, String email, String phoneNo, String image,
            String description, User user) {
       
        this.name = name;
        this.secondName = secondName;
        this.work = work;
        this.email = email;
        this.phoneNo = phoneNo;
        this.image = image;
        this.description = description;
        this.user = user;
    }

    //constructor with cid
    public Contact(int cId, String name, String secondName, String work, String email, String phoneNo, String image,
    String description, User user) {
    this.cId = cId;
    this.name = name;
    this.secondName = secondName;
    this.work = work;
    this.email = email;
    this.phoneNo = phoneNo;
    this.image = image;
    this.description = description;
    this.user = user;
    }

    //constructor without image
    public Contact(int cId, String name, String secondName, String work, String email, String phoneNo,
            String description, User user) {
        this.cId = cId;
        this.name = name;
        this.secondName = secondName;
        this.work = work;
        this.email = email;
        this.phoneNo = phoneNo;
        this.description = description;
        this.user = user;
    }


    


}
