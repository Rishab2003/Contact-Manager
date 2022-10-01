package com.smart.contactmanager.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;
import com.smart.contactmanager.dao.UserRepository;
import com.smart.contactmanager.entities.User;
import com.smart.contactmanager.helper.Message;

@Controller
public class HomeController {
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    
  

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("title", "Home - Contact Manager");
        return "home";
    }

    @GetMapping("/about")
    public String about(Model model){
        model.addAttribute("title", "Contact");
        return "about";
    }
    @GetMapping("/signup")
    public String signup(Model model){
        model.addAttribute("title", "Register - Contact Manager");
        model.addAttribute("user",new User());
        return "signup";
    }

    //handler for registering user
    @PostMapping("/do_register")    //"user" is used in modelattribut bcoz we have used th:object as user in signup.html
    public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1 ,@RequestParam( value = "agreement",defaultValue = "false") Boolean agreement,Model model,HttpSession session){
        try {
            if(!agreement){
                
                throw new Exception("Please agree to the terms and condition!!");
            }
            if(result1.hasErrors()){
                System.out.println(result1.toString());
                model.addAttribute("user",user);
                return "signup";
            }
    
            user.setEnabled(true);
            user.setRole("ROLE_USER");
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);
            System.out.println("Agreement"+agreement);
            System.out.println(user);
            model.addAttribute("user",new User());
            session.setAttribute("message", new Message("Successfully registered!!", "alert-success"));
            return "signup";
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
            model.addAttribute("user",user);
            session.setAttribute("message", new Message("Something went wrong"+e.getMessage(), "alert-danger"));
            
            return "signup";
        }
        
    }

    //handler for custom login
    @GetMapping("/signin")
    public String customLogin(Model model){
        model.addAttribute("title","Login page");
        return "login";
    }

}
