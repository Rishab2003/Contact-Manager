package com.smart.contactmanager.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.annotation.ModelAndViewResolver;

import com.smart.contactmanager.dao.ContactRepository;
import com.smart.contactmanager.dao.UserRepository;
import com.smart.contactmanager.entities.Contact;
import com.smart.contactmanager.entities.User;
import com.smart.contactmanager.helper.Message;

@Controller
@RequestMapping(value="/user")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;

    //add model attribute to all user handlers
    @ModelAttribute
    public void addCommonData(Model model,Principal principal){

        
        String username=principal.getName();    //get username(email)
        
        User user=userRepository.getUserByName(username);

        model.addAttribute("user",user);


    }


    @RequestMapping(value="/index")
    public String dashboard(Model model, Principal principal){      //principal to get details of user like username and password



        return "normal/user_dashboard";     //normal is the folder name
    }

    //open add form controller
    @GetMapping("/add_contact")
    public String openAddContactForm(Model model){

        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());   //adding new contact object
        return "normal/add_contact";
    }

    @PostMapping("/process-contact")
    public String processContact(@RequestParam("image") MultipartFile file,  
                                 Principal principal,
                                 @RequestParam("name") String name,
                                 @RequestParam("secondName") String secondName,
                                 @RequestParam("phoneNo") String phoneNo,
                                 @RequestParam("email") String email,
                                 @RequestParam("work") String work,
                                 @RequestParam("description") String description,
                                 HttpSession session)
    {
                                

        try{
            
        String username=principal.getName();
        User user=userRepository.getUserByName(username);
        Contact contact=new Contact(name,secondName,work,email,phoneNo,"contacts.png",description,user);

        boolean empty=false;
        //processing image file
        if(file.isEmpty()){
            System.out.println("file is empty");
            throw new Exception();
            
        }
        else{

            String filename= file.getOriginalFilename();
            
            
            contact.setImage(filename);
            
            
            File file1=new ClassPathResource("static/img/").getFile();

            Path path=Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());

            Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
            
            user.getContacts().add(contact);
            contact.setUser(user);
                    
            this.userRepository.save(user); 
           
            session.setAttribute("message", new Message("Contact added successfully", "success"));

        }
        

        
        
      
        }catch(Exception e){
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!!", "danger"));
        }
        
       
        return "normal/add_contact";

    }

    //show contacts
    //per page=5 [n]
    //current page = 0 [page]
    @GetMapping("/show-contacts/{page}")
    public String showContacts(@PathVariable("page") Integer page, Model model,Principal principal){
        model.addAttribute("title","Show Contacts");

        String username=principal.getName();    //to get email of current user
        User user=this.userRepository.getUserByName(username);

        Pageable pageable=PageRequest.of(page, 5);

        Page<Contact> contacts  = this.contactRepository.findContactsByUser(user.getId(),pageable);
    
        model.addAttribute("contacts",contacts);
        
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", contacts.getTotalPages());


        return "normal/show_contacts";

    }

    //show individual contact
    
    @GetMapping("/contact/{cId}")
    public String showContactDetail(@PathVariable("cId") Integer cId,Model model,Principal principal){

        Optional<Contact> contactOptional=this.contactRepository.findById(cId);
        Contact contact=contactOptional.get();

        String username = principal.getName();
        User user=this.userRepository.getUserByName(username);

        if(user.getId()==contact.getUser().getId())
        
            model.addAttribute("contact",contact);

        return "normal/contact_details";
    }

    //delete contact handler
    @GetMapping("/delete/{cId}")
    public String deleteContact(@PathVariable("cId") Integer cId,Model model,HttpSession session ){

        Optional<Contact> contactOptional=this.contactRepository.findById(cId);
        Contact contact=contactOptional.get();
        

        //this does not delete coz we r using cascadetype.all
        //so unlink the contact from user
        contact.setUser(null);

        this.contactRepository.delete(contact);

        //todo:remove image as well from contacts
    

        session.setAttribute("message",new Message("Contact deleted successfully", "success"));

        return "redirect:/user/show-contacts/0";

    }

    //opening form of update contact
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid") Integer cid,Model m){

        m.addAttribute("title","Update contact");

        Contact contact=this.contactRepository.findById(cid).get();
        m.addAttribute("contact",contact);
        return "normal/update_form";
    }

    //update handler
    @PostMapping("/process-update")
    public String updateHandler(@RequestParam("image") MultipartFile file,  
                                Principal principal,
                                @RequestParam("cId") Integer cId,
                                @RequestParam("name") String name,
                                @RequestParam("secondName") String secondName,
                                @RequestParam("phoneNo") String phoneNo,
                                @RequestParam("email") String email,
                                @RequestParam("work") String work,
                                @RequestParam("description") String description,
                                HttpSession session)
        {
        
        String username=principal.getName();
        User user=userRepository.getUserByName(username); 
        Contact contact=new Contact(cId,name,secondName,work,email,phoneNo,description,user);

        try {
            //image
            //old contact
            Contact contactold=this.contactRepository.findById(cId).get();

            if(!file.isEmpty())
            {
                

                //rewrite file

                //delete old photo
                File deletefile=new ClassPathResource("static/img").getFile();
                File filenew=new File(deletefile,contactold.getImage());
                
                filenew.delete();


                //update new photo
                File file1=new ClassPathResource("static/img/").getFile();

                Path path=Paths.get(file1.getAbsolutePath()+File.separator+file.getOriginalFilename());
                Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
                contact.setImage(file.getOriginalFilename());

            }
            else{

            }
           
            
    
            this.contactRepository.save(contact);
            session.setAttribute("message", new Message("Your contact is updated", "success"));

            
        } catch (Exception e) {
            // TODO: handle exception
        }
     
        return "redirect:/user/contact/"+contact.getcId();
    }

    //profile handler
    @GetMapping("/profile")
    public String yourProfile(Model model,Principal principal){
        model.addAttribute("title", "Profile page");

        String username=principal.getName();
        User user=userRepository.getUserByName(username); 
        model.addAttribute("user", user);
        return "normal/profile";
    }

}
