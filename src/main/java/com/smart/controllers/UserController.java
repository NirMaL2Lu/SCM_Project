package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entites.Contact;
import com.smart.entites.User;
import com.smart.helper.Message;
@Controller
@RequestMapping("/user")
public class UserController {
	
	Logger logger = LoggerFactory.getLogger(UserController.class);
	
		@Autowired
        private	UserRepository userRepository;
		@Autowired
		private ContactRepository contactRepository;
		
		//method for adding common data to response
		@ModelAttribute
		public void commonData(Model model,Principal principal) {
			
			logger.info("Method : commonData starts");
			
			String userName = principal.getName();
			System.out.println("UserName : "+userName);
			
			User user = this.userRepository.getUserByUserName(userName);
			
			System.out.println("User : " +user);
			model.addAttribute("user", user);
		
			logger.info("Method : commonData ends");
		}
	//dashboard home
	@RequestMapping("/index")
	public String dashBoard(Model model,Principal principal) {
		
		logger.info("Method : dashBoard starts");
		
		model.addAttribute("title","User dashboard");
		
		logger.info("Method : dashBoard ends");
		
		return "normal/user_dashboard";
		
		
	}
	
	//open add contact handler
		@GetMapping("/add_contact")
		public String openAddContactForm(Model model) {
			
			logger.info("Method : openAddContactForm starts");
			
			model.addAttribute("title","Add Contact");
			model.addAttribute("contact",new Contact());
			
			logger.info("Method : openAddContactForm ends");
			
			return "normal/add_contact_from";
		}
		
		//process add contact handler
		@PostMapping("/process-contact")
		public String processContact(
				@ModelAttribute Contact contact,
				@RequestParam("profileImage") MultipartFile multipartFile,
				Principal principal,HttpSession session) {
			
			logger.info("Method : processContact starts");
			
			
			try {
				
				
				String name = principal.getName();
				User user = userRepository.getUserByUserName(name);
				
				/*
				 * if (3>2) { throw new Exception(); }
				 */
				//processing upload file
				
				if (multipartFile.isEmpty()) {
					//file is not found
					System.out.println("File is not found");
					contact.setImage("contact.png");
				}
				
				else {
					contact.setImage(multipartFile.getOriginalFilename());
					File saveFile = new ClassPathResource("static/img").getFile();
					
					Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+multipartFile.getOriginalFilename());
					
					Files.copy(multipartFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
					
					System.out.println("file is uploaded...."+path);
					System.out.println("file is uploaded....");
				}
				contact.setUser(user);
				user.getContacts().add(contact);
				this.userRepository.save(user);
				System.out.println("Contacts : "+contact);
				System.out.println("Added successfully to db");
				//success message
				session.setAttribute("message", new Message("Your contact is added..!! Add more..", "success"));
				
			} 
			
			catch (Exception e) {
				System.out.println("Error : "+e.getMessage());
				//error message
				session.setAttribute("message", new Message("Something went wrong..!! Try again..", "danger"));
				
			}
			
			logger.info("Method : processContact ends");
			
			return "normal/add_contact_from";
		}
		
		// show contact handler
		//per page =5[n] n=page
		// current page 0[n]
		@GetMapping("/showcontacts/{page}")
		public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
			
			logger.info("Method : showContacts starts");
			
			model.addAttribute("title","View Contacts");
			String userName = principal.getName();
			
			User user = this.userRepository.getUserByUserName(userName);
			Pageable pageable = PageRequest.of(page, 3);
			
			//this is for all contact in one page
//			List<Contact> contacts = this.contactRepository.findContactByUser(user.getId());
			
			//this is for pages of contacts
			Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
			
			model.addAttribute("contacts",contacts);
			
			model.addAttribute("currentPage",page);
			
			model.addAttribute("totalPages",contacts.getTotalPages());
			
			logger.info("Method : showContacts ends");
			
			return "normal/show_contact";
		}
		
		//show perticular contact details
		@GetMapping("/{cId}/contact")
		public String showContactDetails(@PathVariable("cId")Integer cId,Model model) {
			
			logger.info("Method : showContactDetails starts");
			
			
			System.out.println("CID :" + cId);
			
			Optional<Contact> contactOptional = this.contactRepository.findById(cId);
			Contact contact = contactOptional.get();
			model.addAttribute("contact",contact);
			
			logger.info("Method : showContactDetails ends");
			
			return "normal/contact_details";
		}
}
